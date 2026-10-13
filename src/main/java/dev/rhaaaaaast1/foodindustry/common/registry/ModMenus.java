package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 菜单（GUI）注册表。
 *
 * <p><b>目前是空的</b> —— 唯一的菜单（发酵木桶）已暂时移除。</p>
 *
 * <p>留个参考：以后加 GUI 时用 NeoForge 的 {@link IMenuTypeExtension#create}
 * 而不是原版 {@code new MenuType<>(factory, FeatureFlagSet)} —— 前者让菜单能从
 * 网络包读参数（比如传方块位置来定位方块实体）。</p>
 */
public final class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, FoodIndustry.MODID);

    private ModMenus() {
    }
}
