package dev.rhaaaaaast1.foodindustry.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

/**
 * 蛋糕方块：原版蛋糕的行为 + 用刀切下一片。
 *
 * <p>命名与农夫乐事的 {@code PieBlock} 同一路子：直接说清它是什么方块，
 * 不再加 "Sliced" 之类的前缀（我们每种蛋糕都可切，没有"不可切"的变体）。</p>
 *
 * <h2>默认行为 100% 来自原版 {@link net.minecraft.world.level.block.CakeBlock}</h2>
 * <ul>
 *   <li>吃：{@code useWithoutItem} → {@code eat(...)}，内部 {@code getFoodData().eat(2, 0.1F)}、
 *       发 {@code GameEvent.EAT}、吃完发 {@code BLOCK_DESTROY}；</li>
 *   <li>逐口缩小的碰撞箱：{@code SHAPE_BY_BITE}；</li>
 *   <li>比较器：{@code (7 - bites) * 2}。</li>
 * </ul>
 * <p>以上全部继承，一个字都没改。</p>
 *
 * <h2>唯一新增：刀切</h2>
 * <p>判定直接**套农夫乐事的代码**
 * {@code vectorwing.farmersdelight.common.utility.ItemUtils.isKnife(stack)}，
 * 它是这个语义的权威实现：</p>
 * <pre>{@code
 * isValidTool(stack, KnifeItem.KNIFE_HARVEST, ModTags.Items.KNIVES)
 *   = stack.canPerformAction(KNIFE_HARVEST) || stack.is(farmersdelight:tools/knives)
 * }</pre>
 * <p>注意第二项只是**兜底**，真正的判据是"能不能执行刀收割动作" —— 只查标签会漏掉
 * 那些通过 {@code ItemAbility} 暴露刀能力的物品。</p>
 *
 * <p>为什么刀切要写在子类里：农夫乐事对小刀切蛋糕是**硬编码**的 ——
 * {@code KnifeItem.KnifeEvents.onCakeInteraction} 里写死 {@code if (block == Blocks.CAKE)}，
 * 只认原版蛋糕，而且掉的是它自己的 {@code farmersdelight:cake_slice}。
 * 我们套用它的**判定**（isKnife），产出**我们自己的**切片。</p>
 */
public class CakeBlock extends net.minecraft.world.level.block.CakeBlock {

    /** 农夫乐事的切片音效；用字符串解析而不是引用它的常量类。 */
    private static final ResourceLocation FD_SLICE_SOUND =
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.food.slice");

    private final Supplier<Item> slice;

    public CakeBlock(Properties properties, Supplier<Item> slice) {
        super(properties);
        this.slice = slice;
    }

    public Item getSlice() {
        return this.slice.get();
    }

    /**
     * 手持物品右键：只在拿刀时抢先处理，其余一律交回默认交互。
     *
     * <p>（曾经这里还有一条"拿蜡烛插蛋糕"的分支，因为机制没跑通已经删掉。）</p>
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (vectorwing.farmersdelight.common.utility.ItemUtils.isKnife(held)) {
            return cutSlice(level, pos, state, player, held);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * 刀切：**一刀一片**，任何咬数都能切，最后一片切完方块消失。
     *
     * <p>与 {@code eat} 完全同构（同样的 {@code bites + 1} / 移除条件），
     * 区别只是切下来那片变成掉落物，而不是喂给玩家。总产出恒为 7 片，与"吃 7 口"等价。</p>
     *
     * <p>两端都会执行（NeoForge 客户端走 prediction），所以**不要**加
     * {@code isClientSide} 短路 —— 那会让音效与统计在两边各结算一次。</p>
     */
    private ItemInteractionResult cutSlice(Level level, BlockPos pos, BlockState state, Player player, ItemStack knife) {
        int bites = state.getValue(BITES);
        if (bites < 6) {
            level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
        } else {
            level.removeBlock(pos, false);
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }

        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5,
                new ItemStack(this.getSlice()));
        entity.setDeltaMovement(-player.getDirection().getStepX() * 0.15, 0.05,
                -player.getDirection().getStepZ() * 0.15);
        level.addFreshEntity(entity);

        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(FD_SLICE_SOUND);
        if (sound != null) {
            level.playSound(null, pos, sound, SoundSource.PLAYERS, 0.8F, 0.8F);
        }
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        knife.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        player.awardStat(Stats.ITEM_USED.get(knife.getItem()));
        return ItemInteractionResult.SUCCESS;
    }
}
