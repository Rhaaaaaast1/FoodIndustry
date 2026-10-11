package dev.rhaaaaaast1.foodindustry.common.block.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

/**
 * 可反复收获的作物（目前用于菠萝）。
 *
 * <p>与普通作物（{@link SeedCropBlock}）的区别只有一处**收获方式**：</p>
 * <ul>
 *   <li>成熟后**右键收获**：只掉产物，**植株保留**，并回退一阶（{@link #MAX_AGE} - 1）继续结果
 *       —— 因为只退一阶，植株看上去一直是"长成的形态"，不会缩回小苗；</li>
 *   <li>直接把植株挖掉：只在成熟时掉一份产物，**不掉种植物品**
 *       （菠萝头要靠砧板切菠萝得到，野生菠萝则掉菠萝苗）。</li>
 * </ul>
 *
 * <p>只有 4 个生长阶段（{@code age 0..3}），比原版作物的 8 阶段少，成熟更快。</p>
 */
public class RegrowingCropBlock extends FourStageCropBlock {

    private final Supplier<? extends Item> produce;
    /** 一次采摘给几个：菠萝是 1/1（"无论如何只长一个"），辣椒是 1/2。 */
    private final int minPick;
    private final int maxPick;

    public RegrowingCropBlock(Properties properties,
                              Supplier<? extends Item> seeds,
                              Supplier<? extends Item> produce,
                              int minPick, int maxPick) {
        super(properties, seeds);
        this.produce = produce;
        this.minPick = minPick;
        this.maxPick = maxPick;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        if (this.isMaxAge(state)) {
            // 统一走 CropHarvest：只回退一阶，植株保持"已长成"的形态，接着重新结果
            return CropHarvest.pick(level, pos, state, AGE, MAX_AGE - 1,
                    this.produce.get(), this.minPick, this.maxPick);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
