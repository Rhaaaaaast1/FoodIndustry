package dev.rhaaaaaast1.foodindustry.common.block.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * 攀藤作物的**底部作物**（黄瓜、白葡萄、红葡萄）。
 *
 * <p>行为：种在耕地上，按**原版作物规则**生长（{@code age 0..7}，与原版小麦/胡萝卜相同）。
 * 只要上方是 Farmer's Delight 的绳子，
 * 就会把那一格替换成自己的藤方块 {@link RopeVineBlock}，并沿着绳子一节节往上长
 * （最多 {@link RopeChain#MAX_VINE_SEGMENTS} 节）。成熟后右键收获并重置到 0；
 * 不搭绳子也能照常收获，只是产量少。</p>
 *
 * <p>差异全部走构造参数：种子、产物、对应的藤方块。</p>
 */
public class RopeCropBlock extends SeedCropBlock {

    /**
     * 藤的生长阶 —— **就是原版的 {@code CropBlock.AGE}（{@code AGE_7}，0..7）**。
     * 保留 {@code VINE_AGE} 这个名字只是因为读起来更直观（"藤的年龄"）。
     */
    public static final IntegerProperty VINE_AGE = CropBlock.AGE;

    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    private final Supplier<? extends Item> produce;
    private final Supplier<? extends Block> vine;

    public RopeCropBlock(Properties properties,
                         Supplier<? extends Item> seeds,
                         Supplier<? extends Item> produce,
                         Supplier<? extends Block> vine) {
        super(properties, seeds);
        this.produce = produce;
        this.vine = vine;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /** 满熟之后仍然要随机刻，才能继续往绳子上爬。 */
    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 原版生长（含光照判定、CommonHooks 事件）
        super.randomTick(state, level, pos, random);

        if (level.getRawBrightness(pos, 0) >= 9) {
            climbRopeAbove(level, pos);
        }
    }

    /** 上方是绳子、且整条株还没到高度上限时，把上方那格绳子换成藤。 */
    public void climbRopeAbove(ServerLevel level, BlockPos pos) {
        BlockPos above = pos.above();
        if (!RopeChain.isRope(level.getBlockState(above))) {
            return;
        }
        if (!RopeChain.canGrowOneMore(level, pos)) {
            return;
        }
        level.setBlockAndUpdate(above, vine.get().defaultBlockState());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        if (this.isMaxAge(state)) {
            // 统一走 CropHarvest：1~2 个产物；**只回退一阶**（不缩回小苗）
            return CropHarvest.pick(level, pos, state, VINE_AGE, CropBlock.MAX_AGE - 1,
                    this.produce.get(), 1, 2);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    /** 骨粉除了催熟，也有概率催着往上爬一节。 */
    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        super.performBonemeal(level, random, pos, state);
        if (random.nextFloat() < 0.3F) {
            climbRopeAbove(level, pos);
        }
    }
}
