package dev.rhaaaaaast1.foodindustry.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.EffectCures;

/**
 * 可饮用的瓶装流体（汁类 / 食用油）。
 *
 * <p>右键 = 喝；喝完还一个**玻璃瓶**。喝的速度由 {@code drinkTicks} 决定 ——
 * 汁类是 32 tick；{@code drinkTicks <= 0}（食用油，只是原料）则不响应右键。</p>
 *
 * <p><b>关键点</b>：{@code finishUsingItem} 里**必须先调 {@code super}**，原版的饥饿值结算就在
 * {@code Item.finishUsingItem} 里（它调用 {@code LivingEntity.eat}）；不调的话食物数值完全不生效。
 * 还容器的写法对齐原版 {@code HoneyBottleItem}。</p>
 */
public class DrinkableBottleItem extends Item {

    private final int drinkTicks;
    private final boolean clearsEffects;

    public DrinkableBottleItem(int drinkTicks, boolean clearsEffects, Properties properties) {
        super(properties);
        this.drinkTicks = drinkTicks;
        this.clearsEffects = clearsEffects;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (this.drinkTicks <= 0) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return this.drinkTicks;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    /** 喝完：先结算食物，再清效果（如果需要）、还玻璃瓶。 */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        // ⚠️ 必须先走原版：饥饿值与食物效果都在这一步生效
        super.finishUsingItem(stack, level, entity);
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        if (!level.isClientSide && this.clearsEffects) {
            entity.removeEffectsCuredBy(EffectCures.MILK);
        }
        return ContainerReturn.giveBack(stack, entity, new ItemStack(Items.GLASS_BOTTLE));
    }
}
