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

/**
 * 碗装的酱（可以吃）。
 *
 * <p>和瓶装的写法一致：先 {@code super.finishUsingItem(...)} 结算饥饿值，再还一个**空碗**。
 * 动画用 {@code EAT}（拿勺子吃）而不是 {@code DRINK}；时长由 {@code eatTicks} 决定 ——
 * 酱类比汁类更黏稠，所以更长。</p>
 */
public class BowlFoodItem extends Item {

    private final int eatTicks;

    public BowlFoodItem(int eatTicks, Properties properties) {
        super(properties);
        this.eatTicks = eatTicks;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return this.eatTicks;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    /** 吃完：先结算食物，再还空碗。 */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        // ⚠️ 必须先走原版：饥饿值与食物效果都在这一步生效
        super.finishUsingItem(stack, level, entity);
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        return ContainerReturn.giveBack(stack, entity, new ItemStack(Items.BOWL));
    }
}
