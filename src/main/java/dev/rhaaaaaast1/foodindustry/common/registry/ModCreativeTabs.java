package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 创造模式标签。
 *
 * <p>注册名直接用 mod id（{@code foodindustry:foodindustry}）。内容来自
 * {@link ModItems#creativeTabItems()}，所以新增物品只要用
 * {@code ModItems.registerWithTab(...)} 就会自动出现在这里。</p>
 */
public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FoodIndustry.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_TABS.register(
            FoodIndustry.MODID,
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + FoodIndustry.MODID))
                    .icon(() -> new ItemStack(ModItems.CORN.get()))
                    .displayItems((parameters, output) ->
                            ModItems.visibleCreativeTabItems().forEach(item -> output.accept(item.get())))
                    .build());

    private ModCreativeTabs() {
    }
}
