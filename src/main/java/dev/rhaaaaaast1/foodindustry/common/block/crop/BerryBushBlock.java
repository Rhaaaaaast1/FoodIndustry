package dev.rhaaaaaast1.foodindustry.common.block.crop;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 甜浆果式灌木（茶、咖啡、蓝莓）。
 *
 * <p>生长方式与原版 {@code SweetBerryBushBlock} 一致：{@code AGE} 0..3、上方亮度 ≥ 9 时
 * 以 1/5 概率长一阶、右键收获（age &gt; 1 即可收，满熟多给一个）并**重置到 age 1**、
 * 骨粉 +1。与原版唯一的差别：**没有"走过去被扎"的减速与伤害**（茶/咖啡丛不该扎人），
 * 若要还原原版行为，补一个 {@code entityInside} 覆写即可。</p>
 *
 * <p>差异全部走构造参数：产物物品 + **土壤谓词**。咖啡只需传"只能种在灰化土"的谓词，
 * 不需要额外开子类。</p>
 */
public class BerryBushBlock extends BushBlock implements BonemealableBlock {

    /** codec 用。正常注册的方块都走带参数的构造器，见下方说明。 */
    public static final MapCodec<BerryBushBlock> CODEC = simpleCodec(BerryBushBlock::new);

    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    private final Supplier<? extends Item> fruit;
    private final Predicate<BlockState> soil;

    /**
     * codec 专用构造器：{@code simpleCodec} 需要一个只吃 {@code Properties} 的构造器。
     * 它构造出的实例只在"有人从数据包解码这个方块"时才会出现（正常游戏不会），
     * 因此这里给一套无害的默认值。
     */
    public BerryBushBlock(Properties properties) {
        this(properties, () -> Items.SWEET_BERRIES, state -> state.is(BlockTags.DIRT));
    }

    /**
     * @param fruit 收获时掉落的物品
     * @param soil  能种植其上的土壤（咖啡传 {@code state -> state.is(Blocks.PODZOL)}）
     */
    public BerryBushBlock(Properties properties, Supplier<? extends Item> fruit, Predicate<BlockState> soil) {
        super(properties);
        this.fruit = fruit;
        this.soil = soil;
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int age = state.getValue(AGE);
        if (age == 0) {
            return SAPLING_SHAPE;
        }
        return age < MAX_AGE ? MID_GROWTH_SHAPE : super.getShape(state, level, pos, context);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return soil.test(state);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < MAX_AGE && level.getRawBrightness(pos.above(), 0) >= 9
                && CommonHooks.canCropGrow(level, pos, state, random.nextInt(5) == 0)) {
            BlockState grown = state.setValue(AGE, age + 1);
            level.setBlock(pos, grown, 2);
            CommonHooks.fireCropGrowPost(level, pos, state);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(grown));
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean mature = state.getValue(AGE) == MAX_AGE;
        return !mature && stack.is(Items.BONE_MEAL)
                ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
                : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (age > 1) {
            boolean mature = age == MAX_AGE;
            int count = 1 + level.random.nextInt(2) + (mature ? 1 : 0);
            popResource(level, pos, new ItemStack(fruit.get(), count));
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            BlockState harvested = state.setValue(AGE, 1);
            level.setBlock(pos, harvested, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, harvested));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(fruit.get());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(AGE, Math.min(MAX_AGE, state.getValue(AGE) + 1)), 2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}
