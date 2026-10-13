package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.Config;
import dev.rhaaaaaast1.foodindustry.common.FoodValues;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 物品注册表（所有 BlockItem 也在这里注册）。
 *
 * <p>种植用物品的三种形态：</p>
 * <ul>
 *   <li><b>独立种子</b>：在工作台用产物合成（辣椒、草莓、黄瓜、白/红葡萄、蓝莓、茶）</li>
 *   <li><b>产物即种子</b>：花生、大豆、咖啡豆直接种</li>
 *   <li><b>切出来的种植材料</b>：玉米粒（砧板切玉米）、菠萝头（砧板切菠萝，与野生掉落的菠萝苗区分）</li>
 * </ul>
 */
public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FoodIndustry.MODID);

    /** 会出现在本模组创造标签里的物品（注册顺序即展示顺序）。 */
    private static final Set<Supplier<? extends Item>> CREATIVE_TAB_ITEMS = new LinkedHashSet<>();

    /**
     * {@link #CREATIVE_TAB_ITEMS} 的子集：**依赖其他模组**的联动物品
     * （目前是依赖 Create 的巧克力包层苹果 / 蜜渍浆果）。
     *
     * <p>它们和普通物品一样注册并进 {@code CREATIVE_TAB_ITEMS}，
     * 但 {@link #visibleCreativeTabItems()} 会在依赖缺失时把它们剔掉。</p>
     *
     * <p><b>注意：这里只记录"哪些物品是联动物品"，不记录"当时对方在不在"。</b>
     * 依赖是否安装必须在**用到的时候**现问 {@code ModList}：
     * 本模组曾经把 {@code isLoaded} 的结果存成静态常量，结果在游戏测试服务端上，
     * 类初始化时与测试执行时看到的 mod 列表不一致，导致"装了 Create 的物品却被隐藏"。</p>
     */
    private static final Set<Supplier<? extends Item>> CREATE_COMPAT_ITEMS = new LinkedHashSet<>();


    /** 联动物品依赖的模组 id。 */
    private static final String CREATE_MOD_ID = "create";

    /** 配置未加载时的回退值，必须与 {@code Config.AUTO_DISABLE_CONFLICTING_CONTENT} 的默认值一致。 */
    private static final boolean AUTO_DISABLE_FALLBACK = true;



    // 食物数值统一放在 common/FoodValues.java（对照农夫乐事的同布局）

    // ==================================================================
    //  独立种子（工作台用产物合成）
    // ==================================================================
    public static final DeferredItem<ItemNameBlockItem> CHILI_SEEDS = seed("chili_seeds", () -> ModBlocks.CHILI.get());
    /** 茄子种子：和辣椒一样在工作台用产物合成（1 个茄子 → 若干种子）。 */
    public static final DeferredItem<ItemNameBlockItem> EGGPLANT_SEEDS = seed("eggplant_seeds", () -> ModBlocks.EGGPLANT.get());
    public static final DeferredItem<ItemNameBlockItem> STRAWBERRY_SEEDS = seed("strawberry_seeds", () -> ModBlocks.STRAWBERRY_BUSH.get());
    public static final DeferredItem<ItemNameBlockItem> CUCUMBER_SEEDS = seed("cucumber_seeds", () -> ModBlocks.CUCUMBER.get());
    public static final DeferredItem<ItemNameBlockItem> WHITE_GRAPE_SEEDS = seed("white_grape_seeds", () -> ModBlocks.WHITE_GRAPE.get());
    public static final DeferredItem<ItemNameBlockItem> RED_GRAPE_SEEDS = seed("red_grape_seeds", () -> ModBlocks.RED_GRAPE.get());
    public static final DeferredItem<ItemNameBlockItem> BLUEBERRY_SEEDS = seed("blueberry_seeds", () -> ModBlocks.BLUEBERRY_BUSH.get());
    public static final DeferredItem<ItemNameBlockItem> TEA_SEEDS = seed("tea_seeds", () -> ModBlocks.TEA_BUSH.get());
    /** 葵花籽：砧板切向日葵得一组；能嗑（饥饿值极低），也能种出原版向日葵。 */
    public static final DeferredItem<ItemNameBlockItem> SUNFLOWER_SEEDS =
            plantableFood("sunflower_seeds", () -> ModBlocks.SUNFLOWER_CROP.get(), FoodValues.SEED_SNACK);

    // ==================================================================
    //  砧板切出来的种植材料
    // ==================================================================
    /** 玉米粒：砧板切玉米得到，用于种玉米。 */
    public static final DeferredItem<ItemNameBlockItem> CORN_KERNELS = seed("corn_kernels", () -> ModBlocks.CORN.get());
    /** 菠萝头：砧板切菠萝得到，用于种菠萝。 */
    public static final DeferredItem<ItemNameBlockItem> PINEAPPLE_CROWN = seed("pineapple_crown", () -> ModBlocks.PINEAPPLE.get());
    /** 菠萝苗：**野生菠萝**掉落（与菠萝头是不同的物品），同样能种。 */
    public static final DeferredItem<ItemNameBlockItem> PINEAPPLE_SAPLING = seed("pineapple_sapling", () -> ModBlocks.PINEAPPLE.get());

    // ==================================================================
    //  产物即种子（直接种）
    // ==================================================================
    public static final DeferredItem<ItemNameBlockItem> PEANUTS =
            plantableFood("peanuts", () -> ModBlocks.PEANUT.get(), FoodValues.VEGETABLE);
    public static final DeferredItem<ItemNameBlockItem> SOYBEANS =
            plantableFood("soybeans", () -> ModBlocks.SOYBEAN.get(), FoodValues.VEGETABLE);
    /**
     * 生咖啡豆：既是**咖啡丛的种植物**，也是**砧板切咖啡果**的产物。
     * 生豆要经过烟熏炉烘烤 → 石磨磨粉 → 与水加热混合，才是能喝的咖啡。
     */
    public static final DeferredItem<ItemNameBlockItem> RAW_COFFEE_BEANS =
            seed("raw_coffee_beans", () -> ModBlocks.COFFEE_BUSH.get());

    // ==================================================================
    //  产物
    // ==================================================================
    public static final DeferredItem<Item> CORN = food("corn", FoodValues.VEGETABLE);
    public static final DeferredItem<Item> CHILI_PEPPER = food("chili_pepper", FoodValues.VEGETABLE);
    public static final DeferredItem<Item> STRAWBERRIES = food("strawberries", FoodValues.BERRY);
    public static final DeferredItem<Item> PINEAPPLE = food("pineapple", FoodValues.FRUIT);
    public static final DeferredItem<Item> PINEAPPLE_SLICE = food("pineapple_slice", FoodValues.BERRY);
    public static final DeferredItem<Item> CUCUMBER = food("cucumber", FoodValues.VEGETABLE);
    public static final DeferredItem<Item> WHITE_GRAPES = food("white_grapes", FoodValues.BERRY);
    public static final DeferredItem<Item> RED_GRAPES = food("red_grapes", FoodValues.BERRY);
    public static final DeferredItem<Item> BLUEBERRIES = food("blueberries", FoodValues.BERRY);

    /** 茶叶是加工原料（以后泡茶用），不做成食物。 */
    public static final DeferredItem<Item> TEA_LEAVES = plain("tea_leaves");

    /**
     * 咖啡果：**咖啡丛与野生咖啡的收获物**。
     * 它不能直接种，要在**砧板**上切成 8 颗生咖啡豆（见 {@link #RAW_COFFEE_BEANS}）。
     */
    public static final DeferredItem<Item> COFFEE_CHERRIES = plain("coffee_cherries");

    /** 烘焙咖啡豆：生豆进**烟熏炉**烤出来（时间/经验与普通熟食一致）。 */
    public static final DeferredItem<Item> ROASTED_COFFEE_BEANS = plain("roasted_coffee_beans");

    /** 咖啡粉：烘焙豆用 **Create 石磨** 磨成（1 豆 → 1 粉），再与水加热混合成咖啡。 */
    public static final DeferredItem<Item> COFFEE_POWDER = plain("coffee_powder");

    // ==================================================================
    //  野生作物的方块物品
    // ==================================================================
    public static final DeferredItem<BlockItem> WILD_CORN = wildCrop("wild_corn", () -> ModBlocks.WILD_CORN.get());
    public static final DeferredItem<BlockItem> WILD_PEANUT = wildCrop("wild_peanut", () -> ModBlocks.WILD_PEANUT.get());
    public static final DeferredItem<BlockItem> WILD_CHILI = wildCrop("wild_chili", () -> ModBlocks.WILD_CHILI.get());
    public static final DeferredItem<BlockItem> WILD_STRAWBERRY = wildCrop("wild_strawberry", () -> ModBlocks.WILD_STRAWBERRY.get());
    public static final DeferredItem<BlockItem> WILD_PINEAPPLE = wildCrop("wild_pineapple", () -> ModBlocks.WILD_PINEAPPLE.get());
    public static final DeferredItem<BlockItem> WILD_SOYBEAN = wildCrop("wild_soybean", () -> ModBlocks.WILD_SOYBEAN.get());
    public static final DeferredItem<BlockItem> WILD_CUCUMBER = wildCrop("wild_cucumber", () -> ModBlocks.WILD_CUCUMBER.get());
    public static final DeferredItem<BlockItem> WILD_WHITE_GRAPE = wildCrop("wild_white_grape", () -> ModBlocks.WILD_WHITE_GRAPE.get());
    public static final DeferredItem<BlockItem> WILD_RED_GRAPE = wildCrop("wild_red_grape", () -> ModBlocks.WILD_RED_GRAPE.get());
    public static final DeferredItem<BlockItem> WILD_TEA = wildCrop("wild_tea", () -> ModBlocks.WILD_TEA.get());
    public static final DeferredItem<BlockItem> WILD_BLUEBERRY = wildCrop("wild_blueberry", () -> ModBlocks.WILD_BLUEBERRY.get());
    public static final DeferredItem<BlockItem> WILD_COFFEE = wildCrop("wild_coffee", () -> ModBlocks.WILD_COFFEE.get());

    // ==================================================================
    //  海产物（本批：热带鱼 / 守卫者 / 鱿鱼 / 河豚 / 蜜渍浆果 / 士腻架）
    // ==================================================================
    /** 热带鱼片：切热带鱼得到；生吃一般，做熟更好。 */
    public static final DeferredItem<Item> TROPICAL_FISH_SLICE = food("tropical_fish_slice", FoodValues.RAW_FISH_SLICE);
    /** 熟热带鱼片：营火/熔炉/烟熏。 */
    public static final DeferredItem<Item> COOKED_TROPICAL_FISH_SLICE =
            food("cooked_tropical_fish_slice", FoodValues.COOKED_FISH_SLICE);
    /** 守卫者肉（整块）：守卫者掉落（掉落表待接）。数值同原版生牛肉。 */
    public static final DeferredItem<Item> GUARDIAN_MEAT = food("guardian_meat", FoodValues.GUARDIAN_MEAT);
    /** 守卫者肉片：守卫者肉切出来的薄片；生食数值同农夫乐事的生鱼片。 */
    public static final DeferredItem<Item> GUARDIAN_MEAT_SLICE = food("guardian_meat_slice", FoodValues.RAW_FISH_SLICE);
    /** 熟守卫者肉片。 */
    public static final DeferredItem<Item> COOKED_GUARDIAN_MEAT_SLICE =
            food("cooked_guardian_meat_slice", FoodValues.COOKED_FISH_SLICE);
    /** 鱿鱼：整只，可再切/做菜。 */
    public static final DeferredItem<Item> SQUID = food("squid", FoodValues.RAW_FISH_SLICE);
    /** 鱿鱼须：切鱿鱼得到。 */
    public static final DeferredItem<Item> SQUID_TENTACLES = food("squid_tentacles", FoodValues.RAW_FISH_SLICE);
    /** 熟鱿鱼须。 */
    public static final DeferredItem<Item> COOKED_SQUID_TENTACLES =
            food("cooked_squid_tentacles", FoodValues.COOKED_FISH_SLICE);
    /**
     * 发光鱿鱼：整只。数值与普通鱿鱼一致（它是同一个生物的发光变种，
     * 不是"更高级的食材"；发光墨囊那类额外产物以后要加再说）。
     */
    public static final DeferredItem<Item> GLOW_SQUID = food("glow_squid", FoodValues.RAW_FISH_SLICE);
    /** 发光鱿鱼须：切发光鱿鱼得到。 */
    public static final DeferredItem<Item> GLOW_SQUID_TENTACLES =
            food("glow_squid_tentacles", FoodValues.RAW_FISH_SLICE);
    /** 河豚肉片：**生吃有毒**，做熟再吃（见 {@link #FoodValues.PUFFERFISH_SLICE_RAW} 的说明）。 */
    public static final DeferredItem<Item> PUFFERFISH_SLICE = food("pufferfish_slice", FoodValues.PUFFERFISH_SLICE_RAW);
    /** 熟河豚肉片：做熟就没毒了。 */
    public static final DeferredItem<Item> COOKED_PUFFERFISH_SLICE =
            food("cooked_pufferfish_slice", FoodValues.COOKED_FISH_SLICE);
    /** 河豚毒腺：**不可食用**的材料，用来做剧毒之箭之类。 */
    public static final DeferredItem<Item> PUFFERFISH_POISON_GLAND = plain("pufferfish_poison_gland");

    // ==================================================================
    //  食材扩充（贴图由用户提供；数值出处见 FoodValues 的注释）
    // ==================================================================

    // ---- 鱼头：切鱼剩下的部分，不可食用，用来熬高汤/堆肥 ----
    /** 鳕鱼头。 */
    public static final DeferredItem<Item> COD_HEAD = plain("cod_head");
    /** 鲑鱼头。 */
    public static final DeferredItem<Item> SALMON_HEAD = plain("salmon_head");

    // ---- 蛙腿 / 海龟肉 ----
    /** 生蛙腿。 */
    public static final DeferredItem<Item> FROG_LEG = food("frog_leg", FoodValues.RAW_FROG_LEG);
    /** 熟蛙腿。 */
    public static final DeferredItem<Item> COOKED_FROG_LEG =
            food("cooked_frog_leg", FoodValues.COOKED_FROG_LEG);
    /** 生海龟肉。 */
    public static final DeferredItem<Item> TURTLE_MEAT = food("turtle_meat", FoodValues.RAW_MEAT);
    /** 熟海龟肉。 */
    public static final DeferredItem<Item> COOKED_TURTLE_MEAT =
            food("cooked_turtle_meat", FoodValues.COOKED_MEAT);

    // ---- 寿司：四种鱼各一种 ----
    /** 河豚寿司。 */
    public static final DeferredItem<Item> PUFFERFISH_SUSHI =
            food("pufferfish_sushi", FoodValues.SUSHI);
    /** 鱿鱼寿司。 */
    public static final DeferredItem<Item> SQUID_SUSHI = food("squid_sushi", FoodValues.SUSHI);
    /** 守卫者寿司。 */
    public static final DeferredItem<Item> GUARDIAN_SUSHI =
            food("guardian_sushi", FoodValues.SUSHI);
    /** 热带鱼寿司。 */
    public static final DeferredItem<Item> TROPICAL_FISH_SUSHI =
            food("tropical_fish_sushi", FoodValues.SUSHI);

    // ---- 蔬菜与豆制品 ----
    /** 茄子（作物产物）。 */
    public static final DeferredItem<Item> EGGPLANT = food("eggplant", FoodValues.EGGPLANT);
    /** 黄瓜丝：砧板切黄瓜得到，做凉菜用。 */
    public static final DeferredItem<Item> CUCUMBER_SHREDS = plain("cucumber_shreds");
    /** 豆腐。 */
    public static final DeferredItem<Item> TOFU = food("tofu", FoodValues.TOFU);
    /** 豆腐干：豆腐再加工。 */
    public static final DeferredItem<Item> DRIED_TOFU = food("dried_tofu", FoodValues.DRIED_TOFU);
    /** 羊奶干酪。 */
    public static final DeferredItem<Item> GOAT_MILK_CHEESE = plain("goat_milk_cheese");
    /** 米粉：需要再煮，所以不可直接食用。 */
    public static final DeferredItem<Item> RICE_NOODLES = plain("rice_noodles");

    // ---- 调味料 ----
    // ⚠️ 注意：`vinegar` / `coffee` / `peanut_butter` / 各种 *jam / juice / soy_sauce
    //    **不在这个文件里注册** —— 它们是 ModFluids 的流体，由 `MilkLike` 自动生成
    //    同名物品（瓶装或碗装）。在这里再写一次会 "Duplicate registration"。
    /** 辣椒粉。 */
    public static final DeferredItem<Item> CHILI_POWDER = plain("chili_powder");
    /** 干辣椒。 */
    public static final DeferredItem<Item> DRIED_CHILI = plain("dried_chili");

    // ---- 五种茶（都是制茶原料，不可直接吃）----
    /** 绿茶茶叶。 */
    public static final DeferredItem<Item> GREEN_TEA_LEAVES = plain("green_tea_leaves");
    /** 白茶茶叶。 */
    public static final DeferredItem<Item> WHITE_TEA_LEAVES = plain("white_tea_leaves");
    /** 黄茶茶叶。 */
    public static final DeferredItem<Item> YELLOW_TEA_LEAVES = plain("yellow_tea_leaves");
    /** 乌龙茶茶叶。 */
    public static final DeferredItem<Item> OOLONG_TEA_LEAVES = plain("oolong_tea_leaves");
    /** 红茶茶叶。 */
    public static final DeferredItem<Item> BLACK_TEA_LEAVES = plain("black_tea_leaves");
    /** 蜜渍浆果：与 Create 联动（蜂蜜流体）。数值对齐 Create 的巧克力包层浆果。 */
    public static final DeferredItem<Item> HONEYED_BERRIES =
            createCompat("honeyed_berries", () -> new Item(new Item.Properties().food(FoodValues.HONEYED_BERRIES)));
    /** 士腻架：玩梗物品，提示登记在 {@code common.ModTooltips}；数值对齐 Create 的甜甜卷。 */
    public static final DeferredItem<Item> SLICKERS = food("slickers", FoodValues.SLICKERS);
    /**
     * 巧克力包层苹果：与 Create 联动（用 Create 的巧克力流体）。注册名就是
     * {@code foodindustry:chocolate_glazed_apple}，**不是** {@code create:*} 里的任何东西。
     *
     * <p>Create 自己那两件是 {@code create:chocolate_glazed_berries}（巧克力包层浆果）和
     * {@code create:honeyed_apple}（蜂蜜苹果），与我们的"巧克力苹果 + 蜜渍浆果"正好互补，
     * 命名空间也不同，不构成同名冲突。</p>
     *
     * <p>没装 Create 时：本件与 {@link #HONEYED_BERRIES} 都不进创造标签、不生成模型与语言键，
     * 玩家界面上看不到（注册表里仍然存在，原理见
     * {@link dev.rhaaaaaast1.foodindustry.compat.registry.CompatFluids} 的说明）。</p>
     */
    public static final DeferredItem<Item> CHOCOLATE_GLAZED_APPLE =
            createCompat("chocolate_glazed_apple",
                    () -> new Item(new Item.Properties().food(FoodValues.CHOCOLATE_GLAZED_APPLE)));

    // ==================================================================
    //  粪便（肥料 + 投掷物）
    // ==================================================================

    /**
     * 粪便：**对着方块右键当骨粉用**（继承 {@link ManureItem} → 原版 {@code BoneMealItem} 的逻辑），
     * **对着空气右键丢出去**，命中生物给 10 秒反胃。
     *
     * <p>来源：动物每过一段时间自己产一次，见 {@code common/event/ManureHandler}。</p>
     */
    public static final DeferredItem<Item> MANURE = registerWithTab("manure",
            () -> new dev.rhaaaaaast1.foodindustry.common.item.ManureItem(new Item.Properties()));

    // ==================================================================
    //  发酵木桶（暂时移除，见 ModBlocks 末尾的说明）
    // ==================================================================

    // ==================================================================
    //  流体的桶与瓶
    // ==================================================================
    // 注意：桶和瓶都**不在这里手写**，而是在 ModFluids 里随流体一起注册
    // （ModFluids.milkLike(...) 会调用本类的 registerHidden / registerWithTab）。
    // 这样每种流体的"类型 + 源头 + 流动 + 桶 + 瓶"始终在一起，不会两处对不上。

    // ==================================================================
    //  注册辅助
    // ==================================================================

    /** 种子：用 {@link ItemNameBlockItem}，这样名字取自物品自己（与原版种子一致）。 */
    private static DeferredItem<ItemNameBlockItem> seed(String name, Supplier<? extends Block> block) {
        return registerWithTab(name, () -> new ItemNameBlockItem(block.get(), new Item.Properties()));
    }

    /** 既能种、又能吃的产物（花生、大豆）。 */
    private static DeferredItem<ItemNameBlockItem> plantableFood(String name, Supplier<? extends Block> block,
                                                                 FoodProperties food) {
        return registerWithTab(name, () -> new ItemNameBlockItem(block.get(), new Item.Properties().food(food)));
    }

    /** 普通食物。 */
    private static DeferredItem<Item> food(String name, FoodProperties food) {
        return registerWithTab(name, () -> new Item(new Item.Properties().food(food)));
    }

    /** 普通材料（不可食用）。 */
    private static DeferredItem<Item> plain(String name) {
        return registerWithTab(name, () -> new Item(new Item.Properties()));
    }

    /**
     * 与 Create 联动的物品（巧克力包层苹果、蜜渍浆果用它的巧克力/蜂蜜流体）。
     *
     * <p><b>为什么"没装 Create 就看不见"要这样做：</b>NeoForge 没有条件注册能力
     * （注册表里少一个 id，任何引用它的配方/标签/进度都会直接崩），所以物品**总是注册**。
     * 让它"只在装了 Create 时存在"是这样组合出来的：</p>
     * <ol>
     *   <li>不进创造标签 —— 见 {@link ModCreativeTabs}，它读的是
     *       {@link #visibleCreativeTabItems()} 这个带条件的视图；</li>
     *   <li>不生成语言键 —— 见语言文件（没装 Create 时名字会退化成 id）；</li>
     *   <li>配方挂 {@code neoforge:mod_loaded: create} 条件。</li>
     * </ol>
     * <p>于是没装 Create 时，玩家在 JEI、创造栏、配方书里都见不到它们。</p>
     *
     * <p>⚠️ 这里**只**把它登记为联动物品，不判断 Create 在不在；
     * 判断放在使用时（{@link #dependencyMissing()}），原因见 {@link #CREATE_COMPAT_ITEMS} 的注释。</p>
     */
    private static DeferredItem<Item> createCompat(String name, Supplier<Item> factory) {
        DeferredItem<Item> item = ITEMS.register(name, factory);
        CREATIVE_TAB_ITEMS.add(item);
        CREATE_COMPAT_ITEMS.add(item);
        return item;
    }

    /** 联动物品依赖的模组当前是否缺失（现问现答，不缓存）。 */
    private static boolean dependencyMissing() {
        return !ModList.get().isLoaded(CREATE_MOD_ID);
    }

    /** 野生作物的方块物品。 */
    private static DeferredItem<BlockItem> wildCrop(String name, Supplier<? extends Block> block) {
        return registerWithTab(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    /** 注册物品并加入创造标签。 */
    public static <T extends Item> DeferredItem<T> registerWithTab(String name, Supplier<T> factory) {
        DeferredItem<T> item = ITEMS.register(name, factory);
        CREATIVE_TAB_ITEMS.add(item);
        return item;
    }

    /** 注册物品但不放进创造标签（例如仅由合成/掉落获得的中间产物）。 */
    public static <T extends Item> DeferredItem<T> registerHidden(String name, Supplier<T> factory) {
        return ITEMS.register(name, factory);
    }

    /** 供 {@link ModCreativeTabs} 读取，外部只读。 */
    public static Set<Supplier<? extends Item>> creativeTabItems() {
        return Collections.unmodifiableSet(CREATIVE_TAB_ITEMS);
    }

    /**
     * 创造标签里**实际显示**的物品，以及 tooltip 里"停用说明"的判定依据（唯一来源）。
     *
     * <p>联动物品在"依赖的模组没装 + 兼容开关打开"时被剔除：它们既进不了创造标签，
     * 也会在提示里写明原因，而不是让玩家对着一件"拿不到原料、也没有配方"的物品发愣。</p>
     *
     * <p>依赖是否安装与配置值都在**调用时**才读，不做任何缓存 —— 见
     * {@link #CREATE_COMPAT_ITEMS} 的注释（缓存过，踩过坑）。</p>
     */
    public static Set<Supplier<? extends Item>> visibleCreativeTabItems() {
        if (!autoDisableConflictingContent() || !dependencyMissing()) {
            return creativeTabItems();
        }
        Set<Supplier<? extends Item>> visible = new LinkedHashSet<>(CREATIVE_TAB_ITEMS);
        visible.removeAll(CREATE_COMPAT_ITEMS);
        return Collections.unmodifiableSet(visible);
    }

    /**
     * 这件物品当前是否**不可用**（联动物品缺依赖、且兼容开关打开）。
     *
     * <p>创造标签过滤与 tooltip 说明都走这一个判定，避免两处各判一套导致
     * "标签里看不到、提示里也不说"这种让人困惑的状态。</p>
     */
    public static boolean isUnavailable(ItemStack stack) {
        return autoDisableConflictingContent()
                && dependencyMissing()
                && CREATE_COMPAT_ITEMS.stream().anyMatch(supplier -> stack.is(supplier.get()));
    }

    /**
     * 安全读取 {@code compat.autoDisableConflictingContent}。
     *
     * <p><b>为什么不能直接 {@code Config.….get()}：</b>
     * {@code ModConfigSpec} 在**配置尚未加载**时 {@code get()} 会抛
     * {@code IllegalStateException: Cannot get config value before config is loaded}；
     * 而且 {@code getDefault()} 也走同一道加载检查、同样会抛。
     * 创造标签的构建时机可能早于配置加载（例如 datagen、以及不加载 mod 配置的
     * 测试服务端环境），所以这里回退到一个**常量默认值**。</p>
     *
     * <p>⚠️ 若把 {@code Config} 里那个配置项的 {@code .define(..., true)} 默认值改了，
     * 这里的 {@code AUTO_DISABLE_FALLBACK} 必须同步改 —— 两处不一致会在
     * "配置未加载"的窗口期产生不同行为。</p>
     */
    private static boolean autoDisableConflictingContent() {
        try {
            return Config.AUTO_DISABLE_CONFLICTING_CONTENT.get();
        } catch (IllegalStateException notLoadedYet) {
            return AUTO_DISABLE_FALLBACK;
        }
    }

    /** {@link #isUnavailable} 为真时，说明原因的语言键。 */
    public static String unavailableReasonKey() {
        return "tooltip.foodindustry.disabled.create";
    }

    private ModItems() {
    }
}
