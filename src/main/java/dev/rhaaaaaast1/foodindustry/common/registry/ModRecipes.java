package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 配方系统注册表（{@code RecipeType} + {@code RecipeSerializer}）。
 *
 * <p><b>目前是空的</b> —— 唯一的自定义配方（{@code foodindustry:fermenting}，
 * 发酵木桶用）已随木桶一起暂时移除。</p>
 *
 * <p>以后加自定义配方时注意两者的分工：</p>
 * <ul>
 *   <li>{@code RecipeType} —— 用来把配方分组（{@code RecipeManager.byType}），
 *       方块实体调 {@code getRecipeFor(type, input, level)} 时认的是它，
 *       JEI/REI 分类也按它归类；它**不出现在 JSON 里**；</li>
 *   <li>{@code RecipeSerializer} —— 负责 JSON 的读写，JSON 里的
 *       {@code "type": "foodindustry:xxx"} 认的是**它**。</li>
 * </ul>
 */
public final class ModRecipes {

    /** 配方类型（原版注册表 {@code minecraft:recipe_type}）。 */
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, FoodIndustry.MODID);

    /** 配方序列化器（原版注册表 {@code minecraft:recipe_serializer}）。 */
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, FoodIndustry.MODID);

    /** 把两个注册表挂到 mod 事件总线。 */
    public static void register(IEventBus modEventBus) {
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }

    private ModRecipes() {
    }
}
