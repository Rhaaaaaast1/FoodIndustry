package dev.rhaaaaaast1.foodindustry.common.block.crop;

import dev.rhaaaaaast1.foodindustry.common.tag.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 攀藤作物共用的链条判断逻辑。
 *
 * <p>结构：底部是种在耕地上的 {@link RopeCropBlock}（作物本体），往上每一节绳子被替换成
 * 一节 {@link RopeVineBlock}（藤）。整条株的合法性由"每格只看自己下方那一格"逐级传导，
 * 所以这里不递归整条链，只做局部判断与高度计数。</p>
 */
public final class RopeChain {

    /** 最多几节藤（不含底部作物）。 */
    public static final int MAX_VINE_SEGMENTS = 3;

    /** 计数时的安全上限，防止异常存档导致死循环。 */
    private static final int SAFETY_LIMIT = 64;

    private RopeChain() {
    }

    /** 上方那格是不是可以让我们攀上去的绳子（Farmer's Delight 的 {@code #farmersdelight:ropes}）。 */
    public static boolean isRope(BlockState state) {
        return state.is(ModTags.Blocks.ROPES);
    }

    /** 这一格是不是本模组的攀藤作物/藤（用于链条计数）。 */
    public static boolean isChainBlock(BlockState state) {
        return state.getBlock() instanceof RopeCropBlock || state.getBlock() instanceof RopeVineBlock;
    }

    /**
     * 从 {@code pos} 往下数：自己 + 下方连续的攀藤方块一共几格。
     *
     * @return 至少 1（含自己）
     */
    public static int chainLength(LevelReader level, BlockPos pos) {
        int length = 1;
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (length < SAFETY_LIMIT) {
            cursor.move(net.minecraft.core.Direction.DOWN);
            if (!isChainBlock(level.getBlockState(cursor))) {
                break;
            }
            length++;
        }
        return length;
    }

    /**
     * 现在还能不能再往上长一节藤。
     *
     * <p>总计最多 {@link #MAX_VINE_SEGMENTS} 节藤，因此当"自己及以下一共已有 N 格"时，
     * 只要 {@code N <= MAX_VINE_SEGMENTS} 就还能加一节。</p>
     */
    public static boolean canGrowOneMore(LevelReader level, BlockPos pos) {
        return chainLength(level, pos) <= MAX_VINE_SEGMENTS;
    }
}
