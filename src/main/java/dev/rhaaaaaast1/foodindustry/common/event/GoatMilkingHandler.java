package dev.rhaaaaaast1.foodindustry.common.event;

import dev.rhaaaaaast1.foodindustry.common.registry.ModFluids;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * 挤山羊奶。
 *
 * <p>原版只有牛能挤奶（{@code Cow.mobInteract} 里硬编码的），山羊不行，所以这里补上：
 * <b>手持空桶右键成年山羊</b> → 得到山羊奶桶。</p>
 *
 * <p>实现方式完全照原版挤牛奶的逻辑走，因此行为也一致：</p>
 * <ul>
 *   <li>用 {@link ItemUtils#createFilledResult} 替换手上的物品 —— 所以**创造模式不会消耗空桶** ✓；</li>
 *   <li>羊羔不给奶 ✓；</li>
 *   <li>不做冷却（原版挤牛奶也没有冷却，限制来自"每桶要一个空桶"）✓。</li>
 * </ul>
 *
 * <p>用 {@code PlayerInteractEvent.EntityInteract} 而不是 mixin：这个方法客户端和服务端都会触发，
 * 所以只有服务端那一侧做实际改动，客户端侧只返回交互结果。</p>
 */
public final class GoatMilkingHandler {

    /** 在模组构造器里调用，挂到游戏事件总线（注意不是 mod 事件总线）。 */
    public static void register() {
        NeoForge.EVENT_BUS.addListener(GoatMilkingHandler::onEntityInteract);
    }

    private static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Goat goat)) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        if (!held.is(Items.BUCKET) || goat.isBaby()) {
            return;
        }

        // 拦截这次交互，并告诉客户端"成功了"（否则会出现挥手/重复触发的不同步）
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
        if (player.level().isClientSide) {
            return;
        }

        player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
        ItemStack filled = ItemUtils.createFilledResult(held, player,
                new ItemStack(ModFluids.GOAT_MILK.bucket().get()));
        player.setItemInHand(event.getHand(), filled);
    }

    private GoatMilkingHandler() {
    }
}
