package dev.rhaaaaaast1.foodindustry;

import com.mojang.logging.LogUtils;
import dev.rhaaaaaast1.foodindustry.client.FoodIndustryClient;
import dev.rhaaaaaast1.foodindustry.common.CommonSetup;
import dev.rhaaaaaast1.foodindustry.common.Config;
import dev.rhaaaaaast1.foodindustry.common.event.GoatMilkingHandler;
import dev.rhaaaaaast1.foodindustry.common.registry.ModBlocks;

import dev.rhaaaaaast1.foodindustry.common.registry.ModCapabilities;

import dev.rhaaaaaast1.foodindustry.common.registry.ModCreativeTabs;
import dev.rhaaaaaast1.foodindustry.common.registry.ModEffects;
import dev.rhaaaaaast1.foodindustry.common.registry.ModEntities;
import dev.rhaaaaaast1.foodindustry.common.registry.ModFluids;
import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import dev.rhaaaaaast1.foodindustry.common.registry.ModPastries;
import dev.rhaaaaaast1.foodindustry.data.FoodIndustryDataGenerators;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

/**
 * 模组主入口。
 *
 * <p>职责仅限：注册配置、按客户端/服务端分流、把所有 {@code DeferredRegister} 挂到 mod 事件总线。
 * 具体内容放在 {@code common.registry} 与 {@code common.block}/{@code common.item} 下，客户端代码只放
 * {@code client} 包，避免在两端都会加载的类里引用客户端专属类型。</p>
 */
@Mod(FoodIndustry.MODID)
public class FoodIndustry {

    /** 模组 id，必须与 neoforge.mods.toml 里的 modId、以及 gradle.properties 的 mod_id 一致。 */
    public static final String MODID = "foodindustry";

    public static final Logger LOGGER = LogUtils.getLogger();

    public FoodIndustry(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(CommonSetup::init);
        // 能力注册：给"继承 BucketItem 的自定义桶"补上流体能力（NeoForge 只认精确类）
        modEventBus.addListener(ModCapabilities::register);
        // 数据生成（gradlew runData）只在 datagen 时触发，正常游戏不会执行
        modEventBus.addListener(FoodIndustryDataGenerators::gatherData);

        // 游戏事件（不是 mod 事件）：挤山羊奶等
        GoatMilkingHandler.register();
        // 动物定期产粪便
        dev.rhaaaaaast1.foodindustry.common.event.ManureHandler.register();

        // 客户端专属的初始化全部关在 client 包里，专用服务器不会加载那个类
        if (FMLEnvironment.dist.isClient()) {
            FoodIndustryClient.init(modEventBus, modContainer);
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        // 所有注册表统一在这里挂到 mod 事件总线
        ModBlocks.BLOCKS.register(modEventBus);
        // 糕点线（14 蛋糕 + 8 派）：先 bootstrap 触发静态初始化
        // （它内部会往 ModItems 注册切片与方块物品），再挂方块注册表
        ModPastries.bootstrap();
        ModPastries.PASTRY_BLOCKS.register(modEventBus);
        // ⚠️ 流体必须排在物品之前：山羊奶桶的工厂要 .get() 已经注册好的流体实例
        ModFluids.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
    }

    /** 拼本模组命名空间下的 ResourceLocation。 */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
