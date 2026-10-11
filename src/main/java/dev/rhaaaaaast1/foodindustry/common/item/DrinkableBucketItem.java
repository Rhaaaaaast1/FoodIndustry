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
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.EffectCures;

/**
 * 可饮用的流体桶（目前只有山羊奶）。
 *
 * <p>右键 = **喝**，而不是把液体倒在地上 —— 和原版牛奶桶一致。喝完可以选择性地清除
 * **可被对应效果治愈的**状态效果。</p>
 *
 * <p><b>为什么不用原版的 {@code MilkBucketItem}</b>：它并不继承 {@link BucketItem}，
 * 而 NeoForge 的 {@code FluidBucketWrapper} 对它是**硬编码返回 `minecraft:milk`** 的。
 * 用它的话，我们的山羊奶桶一进储罐/管道就会被当成 NeoForge 的牛奶。
 * 继承 {@link BucketItem} 则走正常分支，流体能力正确地返回**我们自己的山羊奶**。</p>
 *
 * <p>另外：NeoForge 只给"精确类等于 {@code BucketItem}"的物品自动挂流体能力，
 * 所以这个子类必须在 {@code ModCapabilities} 里自己补注册一次。</p>
 */
public class DrinkableBucketItem extends BucketItem {

    private final boolean clearsEffects;
    private final int drinkTicks;

    /**
     * @param content       桶里装什么流体
     * @param clearsEffects 喝完是否清除"可被牛奶治愈"的效果（山羊奶为 true，对齐原版牛奶）
     * @param drinkTicks    喝一口要多久
     * @param properties    物品属性
     */
    public DrinkableBucketItem(Fluid content, boolean clearsEffects, int drinkTicks, Properties properties) {
        super(content, properties);
        this.clearsEffects = clearsEffects;
        this.drinkTicks = drinkTicks;
    }

    /** 右键直接开始喝，不走"放置流体"。 */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
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

    /**
     * 喝完：先结算食物（山羊奶没有饥饿值，这一步等于空转但保持一致）、再清效果、还空桶。
     *
     * <p>清效果用的是 {@link EffectCures#MILK} —— 和原版牛奶一样**只清"可被牛奶治愈"的效果**，
     * 不会把夜视、力量这类有益效果一起清掉。</p>
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        super.finishUsingItem(stack, level, entity);
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        if (!level.isClientSide && this.clearsEffects) {
            entity.removeEffectsCuredBy(EffectCures.MILK);
        }
        return ContainerReturn.giveBack(stack, entity, new ItemStack(Items.BUCKET));
    }
}
