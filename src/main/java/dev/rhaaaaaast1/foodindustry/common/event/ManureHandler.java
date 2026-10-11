package dev.rhaaaaaast1.foodindustry.common.event;

import dev.rhaaaaaast1.foodindustry.common.Config;
import dev.rhaaaaaast1.foodindustry.common.tag.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * 动物定期产粪便。
 *
 * <p>挂 {@link EntityTickEvent.Post}：它对**每个实体每 tick 触发一次**，
 * 服务端执行（用 {@code level.isClientSide} 挡掉客户端）。</p>
 *
 * <p>计时不用全局计数器，而是把**每只动物自己的进度**写进它的持久化数据
 * （{@code addAdditionalSaveData} 之外用 {@link Entity#getPersistentData()}，
 * 存档会一起保存）。这样：</p>
 * <ul>
 *   <li>每只动物各自计时，不会因为区块卸载而错乱；</li>
 *   <li>服务器重启后进度还在，不会"一重启就立刻产一堆"。</li>
 * </ul>
 *
 * <p>哪些动物会产由实体类型标签 {@code foodindustry:manure_producers} 决定，
 * 间隔由 {@code animals.manureIntervalTicks} 配置（默认 5 分钟）。</p>
 */
public final class ManureHandler {

    /** 每只动物记录自己"距离下次产出还有多少 tick"用的 NBT 键。 */
    private static final String TIMER_KEY = "foodindustry:manure_timer";

    /**
     * 每隔多少 tick 才做一次结算。
     *
     * <p><b>这是性能关键。</b>{@code Entity#getPersistentData()} 每次调用都会把那份
     * CompoundTag **反序列化一次**，然后才读得到那个 int。若每 tick 每只动物都读，
     * 100 只动物 = 每秒 2000 次 NBT 反序列化，白烧 CPU。</p>
     *
     * <p>把结算门控到每 20 tick（1 秒）一次，NBT 读取直接降到 1/20；
     * 产出间隔是几分钟级的，晚 1 秒产出对玩法毫无影响。</p>
     */
    private static final int SAMPLE_INTERVAL = 20;

    /** 服务端 tick 计数器，用来做上面那个门控。 */
    private static int tickCounter = 0;

    /** 在模组构造器里调用，挂到游戏事件总线（不是 mod 事件总线）。 */
    public static void register() {
        NeoForge.EVENT_BUS.addListener(ManureHandler::onServerTick);
        NeoForge.EVENT_BUS.addListener(ManureHandler::onEntityTick);
    }

    /** 每 tick 只做一件事：累加计数器，决定这一 tick 要不要做结算。 */
    private static void onServerTick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
        tickCounter++;
    }

    private static void onEntityTick(EntityTickEvent.Post event) {
        // 门控：只有每 SAMPLE_INTERVAL 个 tick 才真正结算一次
        if (tickCounter % SAMPLE_INTERVAL != 0) {
            return;
        }

        Entity entity = event.getEntity();
        Level level = entity.level();

        // 只在服务端做事（服务端才有 ServerTickEvent 推动 tickCounter，
        // 但客户端也会派发本事件，所以这层判断不能省）
        if (level.isClientSide() || entity instanceof Player) {
            return;
        }
        // 只处理"会产的动物"（标签决定）：getType() 只返回字段、标签判定是哈希查表，很便宜
        if (!entity.getType().is(ModTags.EntityTypes.MANURE_PRODUCERS)) {
            return;
        }
        if (entity instanceof net.minecraft.world.entity.AgeableMob ageable && ageable.isBaby()) {
            return;
        }

        CompoundTag data = entity.getPersistentData();
        int remaining = data.getInt(TIMER_KEY);
        if (remaining <= 0) {
            // 第一次见到这只动物：从满间隔开始计时（不要立刻产）。
            // 配置项 get() 内部有 cachedValue，命中缓存时没有解析开销，但也没必要每 tick 读。
            remaining = Config.MANURE_INTERVAL_TICKS.get();
        }

        remaining -= SAMPLE_INTERVAL;
        if (remaining > 0) {
            data.putInt(TIMER_KEY, remaining);
            return;
        }

        // 到点了：产一份，计时重置
        data.putInt(TIMER_KEY, Config.MANURE_INTERVAL_TICKS.get());
        dropManure(level, entity);
    }

    private static void dropManure(Level level, Entity entity) {
        ItemStack stack = new ItemStack(dev.rhaaaaaast1.foodindustry.common.registry.ModItems.MANURE.get());
        ItemEntity drop = new ItemEntity(level,
                entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 0.5,
                entity.getY() + 0.1,
                entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 0.5,
                stack);
        drop.setDefaultPickUpDelay();
        level.addFreshEntity(drop);
    }

    private ManureHandler() {
    }
}
