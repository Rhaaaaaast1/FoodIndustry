package dev.rhaaaaaast1.foodindustry.compat.jei;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.ResourceLocation;

/**
 * JEI 集成。
 *
 * <p><b>目前是空插件</b> —— 唯一的配方类别（"发酵"，发酵木桶用）已随木桶一起
 * 暂时移除。这里保留 {@code @JeiPlugin} 骨架，以后加配方类别时直接往
 * {@code registerCategories} / {@code registerRecipes} 里填即可。</p>
 *
 * <h2>恢复发酵木桶时要做的事</h2>
 * <ol>
 *   <li>{@code registerCategories} —— {@code addRecipeCategories(new FermentingRecipeCategory(...))}；</li>
 *   <li>{@code registerRecipes} —— 从 {@code RecipeManager} 取出
 *       {@code foodindustry:fermenting} 的配方交给 JEI；</li>
 *   <li>{@code registerRecipeCatalysts} —— 把发酵木桶登记为该配方的催化剂；</li>
 *   <li>{@code registerGuiHandlers} —— 用 {@code addRecipeClickArea} 把界面上的
 *       进度箭头区域变成"可点击查看配方"。<b>这是正解</b>：JEI 会自动处理鼠标事件，
 *       不用自己写点击逻辑、也不用反射调 JEI 内部 API。</li>
 * </ol>
 *
 * <h2>为什么要单独一个类、并且用 @JeiPlugin</h2>
 * <p>JEI 是软依赖（{@code compileOnly} + 运行期由玩家自行安装）。
 * {@code @JeiPlugin} 标注的类**只在 JEI 存在时**由 JEI 反射加载，所以没装 JEI 的
 * 玩家永远不会触发本类的类加载，不会 {@code NoClassDefFoundError}。</p>
 */
@JeiPlugin
public class FoodIndustryJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID =
            ResourceLocation.fromNamespaceAndPath(FoodIndustry.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }
}
