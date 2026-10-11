package dev.rhaaaaaast1.foodindustry.common.block.crop;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.function.Supplier;

/**
 * 本模组 **4 阶作物**（age 0..3）的公共骨架。
 *
 * <p>原先 {@link RegrowingCropBlock}、{@link TallFlowerCropBlock}、{@link RopeCropBlock}、
 * {@link RopeVineBlock} 各自把下面四个方法抄了一遍 ✗（合计 15 份样板），改一次规则要动四处 ✓。
 * 统一收到这里之后，子类只写自己**特有**的行为（反复收获 / 开花 / 爬绳 等）✓。</p>
 *
 * <p>{@link #AGE} 与 {@link #MAX_AGE} 定义在本类，子类可以直接用 {@code XxxBlock.AGE} 访问
 * （静态成员会被继承 ✓），所以外部调用点一行都不用改 ✓。</p>
 */
public abstract class FourStageCropBlock extends CropBlock {

    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final int MAX_AGE = 3;

    private final Supplier<? extends Item> seeds;

    protected FourStageCropBlock(Properties properties, Supplier<? extends Item> seeds) {
        super(properties);
        this.seeds = seeds;
    }

    /** 必须换掉原版的 {@code AGE_7}，否则 {@code state.getValue(AGE)} 会抛异常。 */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.seeds.get();
    }
}
