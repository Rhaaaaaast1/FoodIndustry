package dev.rhaaaaaast1.foodindustry.common.block.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * 攀藤作物的**藤**：长在绳子上，取代被攀爬的那一格绳子。
 *
 * <p>要点：</p>
 * <ul>
 *   <li>支撑来自下方 —— 只能是同株的底部作物 {@link RopeCropBlock} 或另一节藤；</li>
 *   <li>失去支撑（比如底下作物被挖了）时整条藤逐级掉落，并把格子**还原成绳子**；</li>
 *   <li>玩家直接破坏时同样还原绳子，不会把绳子"吃掉"；</li>
 *   <li>成熟后右键收获产物，并重置到 0 重新结果。</li>
 * </ul>
 */
public class RopeVineBlock extends SeedCropBlock {

    /** 藤的生长阶 —— **就是原版的 {@code CropBlock.AGE}（{@code AGE_7}，0..7）**。 */
    public static final IntegerProperty VINE_AGE = CropBlock.AGE;

    private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    private final Supplier<? extends Item> produce;
    private final Supplier<? extends Block> baseCrop;
    private final Supplier<? extends Block> rope;

    /**
     * @param baseCrop 允许长在自己下方的底部作物
     * @param rope     失去支撑/被破坏后还原成的绳子（一般是 {@code farmersdelight:rope}）
     */
    public RopeVineBlock(Properties properties,
                         Supplier<? extends Item> seeds,
                         Supplier<? extends Item> produce,
                         Supplier<? extends Block> baseCrop,
                         Supplier<? extends Block> rope) {
        super(properties, seeds);
        this.produce = produce;
        this.baseCrop = baseCrop;
        this.rope = rope;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    // ------------------------------------------------------------------ 支撑

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(baseCrop.get()) || state.is(this);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return mayPlaceOn(level.getBlockState(pos.below()), level, pos.below());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
                                     BlockPos currentPos, BlockPos facingPos) {
        // 下方支撑没了就安排一次 tick，在那里处理掉落与还原绳子
        if (!state.canSurvive(level, currentPos)) {
            level.scheduleTick(currentPos, this, 1);
        }
        return state;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
            restoreRope(level, pos);
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        restoreRope(level, pos);
    }

    /** 把这一格还原成绳子。 */
    public void restoreRope(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            level.setBlockAndUpdate(pos, rope.get().defaultBlockState());
        }
    }

    // ------------------------------------------------------------------ 生长与收获

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        if (level.getRawBrightness(pos, 0) >= 9) {
            climbRopeAbove(level, pos);
        }
    }

    /** 上方还是绳子、且整条株没到上限时，再往上长一节。 */
    public void climbRopeAbove(ServerLevel level, BlockPos pos) {
        BlockPos above = pos.above();
        if (!RopeChain.isRope(level.getBlockState(above))) {
            return;
        }
        if (!RopeChain.canGrowOneMore(level, pos)) {
            return;
        }
        level.setBlockAndUpdate(above, this.defaultBlockState());
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

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        super.performBonemeal(level, random, pos, state);
        if (random.nextFloat() < 0.3F) {
            climbRopeAbove(level, pos);
        }
    }
}
