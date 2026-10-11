package dev.rhaaaaaast1.foodindustry.common.block.crop;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;

import java.util.function.Supplier;

/**
 * 普通作物。
 *
 * <p>生长、掉落、骨粉、光照需求等**全部沿用原版 {@link CropBlock} 的逻辑**，本类只做一件事：
 * 把"种子物品"从写死的常量改成构造器注入 —— 这样 6 种普通作物（玉米、花生、辣椒、草莓、
 * 菠萝、大豆）可以共用同一个类，行为改动只需要改这一处。</p>
 *
 * <p>注意：不要覆写 {@code getShape}，直接继承原版按 age 变化的形状即可。</p>
 */
public class SeedCropBlock extends CropBlock {

    private final Supplier<? extends Item> seeds;

    public SeedCropBlock(Properties properties, Supplier<? extends Item> seeds) {
        super(properties);
        this.seeds = seeds;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seeds.get();
    }
}
