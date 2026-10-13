package dev.rhaaaaaast1.foodindustry.client;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.item.DrinkableBottleItem;
import dev.rhaaaaaast1.foodindustry.common.registry.ModFluids;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.Map;

/**
 * 客户端专属初始化。
 *
 * <p>这个类只允许在物理客户端被加载（由主类用 {@code FMLEnvironment.dist.isClient()} 判定后调用），
 * 因此可以放心 import 客户端包。</p>
 *
 * <p><b>贴图策略</b>：所有流体都**引用** NeoForge 自带的牛奶贴图（16×512 的 32 帧动画、几乎是灰度图），
 * 颜色完全由 {@code getTintColor()} 决定 —— 零自带素材，还能靠染色区分每一种流体。
 * 瓶装则继承原版药水模型，同样靠染色区分。注意这里是**引用**不是复制：
 * 原版/加载器的美术素材不允许随模组分发。</p>
 */
public final class FoodIndustryClient {

    /** 牛奶贴图（NeoForge 自带，随加载器一起分发）。 */
    private static final ResourceLocation MILK_STILL =
            ResourceLocation.fromNamespaceAndPath("neoforge", "block/milk_still");
    private static final ResourceLocation MILK_FLOWING =
            ResourceLocation.fromNamespaceAndPath("neoforge", "block/milk_flowing");

    /**
     * 每种流体的染色（按流体名）。
     *
     * <p>颜色属于**客户端表现**，所以集中放在这里，不混进注册代码；流体和瓶子的液体层共用同一张表。</p>
     */
    private static final Map<String, Integer> TINTS = Map.ofEntries(
            Map.entry("goat_milk", 0xFFF4EDDA),
            // 汁类
            Map.entry("blueberry_juice", 0xFF5A6FB8),
            Map.entry("strawberry_juice", 0xFFE0556B),
            Map.entry("pineapple_juice", 0xFFE8C24A),
            Map.entry("white_grape_juice", 0xFFD8E07A),
            Map.entry("red_grape_juice", 0xFF9A3AA8),
            Map.entry("carrot_juice", 0xFFE07A2A),
            Map.entry("soy_milk", 0xFFF2EAD2),
            Map.entry("coffee", 0xFF4A2A18),
            // 酱类（比对应的汁更深）
            Map.entry("blueberry_jam", 0xFF3B4A85),
            Map.entry("chili_sauce", 0xFFB4341F),
            Map.entry("strawberry_jam", 0xFFA8283C),
            Map.entry("pineapple_jam", 0xFFC08A22),
            Map.entry("grape_jam", 0xFF6B2A80),
            Map.entry("peanut_butter", 0xFFC08A4A),
            // 食用油
            Map.entry("cooking_oil", 0xFFE8D24A),
            // 酱油：深褐近黑（发酵产物，比咖啡更暗更红）
            Map.entry("soy_sauce", 0xFF3A1F0E),
            // 醋：琥珀褐（比酱油浅、更偏红黄 —— 和酱油瓶贴图的换色方向一致）
            Map.entry("vinegar", 0xFF9A5A18),
            // compat
            Map.entry("tomato_sauce", 0xFFC63A22),
            Map.entry("chorus_sauce", 0xFF9A5AA8));

    public static void init(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(FoodIndustryClient::onClientSetup);
        modEventBus.addListener(FoodIndustryClient::registerFluidExtensions);
        modEventBus.addListener(FoodIndustryClient::registerItemColors);
        // 粪便投掷物的渲染器（纯逻辑实体，直接用原版的投掷物渲染器，和雪球同一个）
        modEventBus.addListener(FoodIndustryClient::registerEntityRenderers);
        // 菜单界面（发酵木桶）
        modEventBus.addListener(FoodIndustryClient::registerScreens);
        // 物品提示统一处理器（士腻架的玩梗提示 + 被停用内容的说明）
        ItemTooltipHandler.register();
        // 让模组列表里的配置按钮打开 NeoForge 自带的配置界面
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    /** 实体渲染器注册：粪便投掷物没有自定义模型，用原版 {@code ThrownItemRenderer} 即可。 */
    private static void registerEntityRenderers(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(dev.rhaaaaaast1.foodindustry.common.registry.ModEntities.MANURE_PROJECTILE.get(),
                net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
    }

    /**
     * 菜单界面注册：把 MenuType 绑到对应的 Screen。
     *
     * <p><b>目前没有菜单</b> —— 唯一的菜单（发酵木桶）已暂时移除。
     * 以后加 GUI 时在这里写
     * {@code event.register(ModMenus.XXX.get(), XxxScreen::new);}。</p>
     */
    private static void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        // 暂无菜单
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        // 流体自身的渲染层：和原版水一样用半透明（供储罐/管道等渲染流体时使用）
        event.enqueueWork(() -> {
            for (ModFluids.MilkLike milkLike : ModFluids.allMilkLike()) {
                ItemBlockRenderTypes.setRenderLayer(milkLike.source().get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(milkLike.flowing().get(), RenderType.translucent());
            }
        });
        FoodIndustry.LOGGER.debug("[{}] 客户端初始化完成（{} 种流体）",
                FoodIndustry.MODID, ModFluids.allMilkLike().size());
    }

    /**
     * 流体外观：只能通过 {@link RegisterClientExtensionsEvent} 注册 —— 旧的 {@code initializeClient}
     * 已废弃，两者同时用会抛"重复注册客户端扩展"。
     */
    private static void registerFluidExtensions(RegisterClientExtensionsEvent event) {
        for (ModFluids.MilkLike milkLike : ModFluids.allMilkLike()) {
            final int tint = tintOf(milkLike.name());
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return MILK_STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return MILK_FLOWING;
                }

                @Override
                public int getTintColor() {
                    return tint;
                }
            }, milkLike.type().value());
        }
    }

    /**
     * 瓶子的液体层染色：原版药水模型是 {@code layer0 = 液体层}、{@code layer1 = 瓶身}，
     * 所以只染第 0 层，瓶身返回 -1（不染色）。
     */
    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for (ModFluids.MilkLike milkLike : ModFluids.allMilkLike()) {
            if (milkLike.bottle() == null || !(milkLike.bottle().get() instanceof DrinkableBottleItem)) {
                continue;
            }
            // ⚠️ 有自己专用贴图的瓶子（果汁/咖啡/酱油）**跳过染色**。
            //    它们已改用 item/generated + 用户画好的成品图，贴图里颜色是**画上去的**；
            //    再叠一层染色会把颜色乘歪（而且药水模型的 layer0 液体层也不存在了）。
            //    没有专用贴图的瓶子（豆浆、山羊奶）仍走"药水模型 + 染色"。
            if (dev.rhaaaaaast1.foodindustry.data.ModItemModelProvider.HAS_OWN_TEXTURE
                    .contains(milkLike.name())) {
                continue;
            }
            final int tint = tintOf(milkLike.name());
            event.register((stack, layer) -> layer == 0 ? tint : -1, milkLike.bottle().get());
        }
    }

    private static int tintOf(String name) {
        return TINTS.getOrDefault(name, 0xFFFFFFFF);
    }

    private FoodIndustryClient() {
    }
}
