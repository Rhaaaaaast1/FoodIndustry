package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 状态效果注册表。
 *
 * <p>饱和/营养这类"标准食物效果"可以直接复用 Farmer's Delight 的
 * {@code farmersdelight:nourishment}，不必自己注册；只有本模组独有的效果才放这里。</p>
 */
public final class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, FoodIndustry.MODID);

    private ModEffects() {
    }
}
