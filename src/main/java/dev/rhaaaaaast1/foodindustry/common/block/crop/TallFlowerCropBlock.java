package dev.rhaaaaaast1.foodindustry.common.block.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/**
 * "开花型"作物：长到最大 age 后，再过一个随机刻就**整株变成原版的高大双格植物**（目前用于向日葵）。
 *
 * <p><b>为什么需要这个类</b>：原版向日葵是**两格高**的 {@code DoublePlantBlock}，一个方块没法用 age
 * 表达"它已经长成向日葵了" ✗，所以用这个中间形态承载"从种子长成向日葵"的过程 ✓。
 * <b>原版的向日葵方块本身不重复注册</b> ✓ —— 我们只注册中间形态的作物方块 ✓。</p>
 *
 * <p>行为：</p>
 * <ul>
 *   <li>age 0..7 正常生长（同 {@link SeedCropBlock}，原版 8 阶），骨粉只能催熟、**到顶就拒绝**（与原版作物一致 ✓）；</li>
 *   <li>到 age 7（最大阶）之后再过一个**自然随机刻** → 用 {@link DoublePlantBlock#placeAt} 原地变成目标植物
 *       （上下两格一起 ✓）；</li>
 *   <li>**上面没有空间就不开花** ✓（保持成熟状态，等腾出空间再开 ✓）；</li>
 *   <li>因为开花发生在最大 age 之后，所以必须覆盖 {@link #isRandomlyTicking} ✓（见那里的注释 ✓）。</li>
 * </ul>
 */
public class TallFlowerCropBlock extends SeedCropBlock {

    /** 成熟后要变成的原版方块（向日葵）。 */
    private final Supplier<? extends Block> matureBlock;

    public TallFlowerCropBlock(Properties properties, Supplier<? extends Item> seeds,
                               Supplier<? extends Block> matureBlock) {
        super(properties, seeds);
        this.matureBlock = matureBlock;
    }

    /** 成熟之后：不再继续"生长"，而是开花。 */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.isMaxAge(state)) {
            this.bloom(level, pos);
            return;
        }
        super.randomTick(state, level, pos, random);
    }

    /**
     * ⚠️ **必须覆盖这个方法**。
     *
     * <p>原版 {@code CropBlock} 的实现是 {@code return !isMaxAge(state)} —— 也就是长到最大 age 之后
     * <b>就不再接收随机刻了</b>（原版作物到顶只需要等人来收，不需要再 tick）✗。
     * 而我们的"开花"恰恰发生在最大 age **之后** ✓，所以必须让它继续接收随机刻，
     * 否则成熟后会**永远不开花** ✗（这是个很容易漏掉、而且静默失效的坑）。</p>
     */
    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    /**
     * 原地开花成两格高的原版植物。
     *
     * <p>上方必须是空气（或者可替换方块）—— 否则这次不开，等下一次随机刻再看 ✓。</p>
     */
    private void bloom(ServerLevel level, BlockPos pos) {
        if (!this.canBloomAt(level, pos)) {
            return;
        }
        DoublePlantBlock.placeAt(level, this.matureBlock.get().defaultBlockState(), pos, Block.UPDATE_ALL);
    }

    private boolean canBloomAt(BlockGetter level, BlockPos pos) {
        BlockPos above = pos.above();
        return level.getBlockState(above).canBeReplaced();
    }
}
