package dev.rhaaaaaast1.foodindustry.common.block.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * 成熟作物"右键采摘"的**统一实现**。
 *
 * <p>本模组有三处都要这个行为：绳作物的底部作物（{@link RopeCropBlock}）、绳上的藤
 * （{@link RopeVineBlock}）、以及可反复收获的作物（{@link RegrowingCropBlock}）。
 * 原先三段代码各写一遍 ✗，规则一改就容易漏掉其中一个 ✓，所以抽到这里统一维护 ✓。</p>
 *
 * <p>行为：掉 {@code minCount~maxCount} 个产物 → 播放采摘音效 → 把 age 回退到 {@code resetAge}
 * （植株保留、继续结果 ✓，而不是整株消失 ✗）。</p>
 */
public final class CropHarvest {

    /**
     * @param ageProperty 这个方块的 age 属性
     * @param resetAge    采摘后回退到哪个 age（一般传 {@code MAX_AGE - 1}，这样植株看起来不会缩水 ✓）
     * @param produce     产物
     * @param minCount    最少掉几个
     * @param maxCount    最多掉几个（等于 minCount 时就是固定数量 ✓，例如菠萝）
     */
    public static InteractionResult pick(Level level, BlockPos pos, BlockState state,
                                         IntegerProperty ageProperty, int resetAge,
                                         Item produce, int minCount, int maxCount) {
        int span = Math.max(1, maxCount - minCount + 1);
        int count = minCount + level.random.nextInt(span);
        // Block.popResource 自己是 public static，而且内部会判断客户端/服务端
        Block.popResource(level, pos, new ItemStack(produce, count));
        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        level.setBlock(pos, state.setValue(ageProperty, resetAge), 2);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private CropHarvest() {
    }
}
