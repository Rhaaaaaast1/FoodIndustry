package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.item.BowlFoodItem;
import dev.rhaaaaaast1.foodindustry.common.item.DrinkableBottleItem;
import dev.rhaaaaaast1.foodindustry.common.item.DrinkableBucketItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 流体注册表。
 *
 * <p><b>每种流体 = FluidType + 源头流体 + 流动流体 + 一个容器物品</b>，全部是"牛奶型"：
 * 密度/黏度 1024、**不做可放置的液体方块**（和牛奶一样倒不到地上）。容器按用途分三种：</p>
 *
 * <ul>
 *   <li>{@link Container#BUCKET 桶} —— 真正的**流体容器**（能接/能倒），目前只有山羊奶；桶不进创造物品栏；</li>
 *   <li>{@link Container#BOTTLE 瓶} —— **汁类与食用油**的形态，能喝（食用油不能喝，是原料）；</li>
 *   <li>{@link Container#BOWL 碗} —— **酱类**的形态，能拿勺子吃。</li>
 * </ul>
 *
 * <p><b>⚠️ 注册顺序</b>：桶的工厂里要 {@code source().get()}（{@code BucketItem} 需要一个真实的
 * {@code Fluid} 实例），所以主类必须**先注册 {@link #FLUID_TYPES}/{@link #FLUIDS}、再注册
 * {@link ModItems#ITEMS}**。</p>
 */
public final class ModFluids {

    /** 流体类型注册表（NeoForge 的注册表）。 */
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, FoodIndustry.MODID);

    /** 流体本体注册表（原版注册表）。 */
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, FoodIndustry.MODID);

    // ⚠️ 这两个列表必须声明在下面所有流体字段之前：Java 按源码顺序执行静态初始化，
    //    流体字段的初始化会往列表里 add，声明晚了就是 null → NPE。
    /** 本模组自己的全部流体。 */
    private static final List<MilkLike> MAIN = new ArrayList<>();
    /** compat 模块的流体。 */
    private static final List<MilkLike> COMPAT = new ArrayList<>();

    // ==================================================================
    //  食物数值
    // ==================================================================

    /**
     * 汁类：**对齐 Create 的"建筑工茶饮"**（饥饿值 1、饱和度 0.6）。
     *
     * <p>数值来源是公开资料，不是 Create 的源码（该仓库明确不允许 AI 接触其代码）：
     * MC百科"建筑工茶饮"页面标注 饥饿值 1 / 饱和度 0.6。</p>
     */
    private static final FoodProperties JUICE_FOOD = food(1, 0.6F);

    // ==================================================================
    //  本模组自己的流体
    // ==================================================================

    /** 山羊奶：可喝、只清"可被牛奶治愈"的效果（和原版牛奶一致）、**没有饥饿值**、只有桶形态。 */
    public static final MilkLike GOAT_MILK = milkLike("goat_milk", Drinkable.MILK, Container.BUCKET, null);

    // ---- 汁类：瓶装、能喝、饥饿值对齐建筑工茶饮 ----
    public static final MilkLike BLUEBERRY_JUICE = milkLike("blueberry_juice", Drinkable.JUICE, Container.BOTTLE, JUICE_FOOD);
    public static final MilkLike STRAWBERRY_JUICE = milkLike("strawberry_juice", Drinkable.JUICE, Container.BOTTLE, JUICE_FOOD);
    public static final MilkLike PINEAPPLE_JUICE = milkLike("pineapple_juice", Drinkable.JUICE, Container.BOTTLE, JUICE_FOOD);
    /** 白葡萄汁：和红葡萄汁分开注册（后面还要做葡萄酒，两者不能合并）。 */
    public static final MilkLike WHITE_GRAPE_JUICE = milkLike("white_grape_juice", Drinkable.JUICE, Container.BOTTLE, JUICE_FOOD);
    public static final MilkLike RED_GRAPE_JUICE = milkLike("red_grape_juice", Drinkable.JUICE, Container.BOTTLE, JUICE_FOOD);
    public static final MilkLike CARROT_JUICE = milkLike("carrot_juice", Drinkable.JUICE, Container.BOTTLE, JUICE_FOOD);
    public static final MilkLike SOY_MILK = milkLike("soy_milk", Drinkable.JUICE, Container.BOTTLE, JUICE_FOOD);
    /**
     * 咖啡：由咖啡粉 + 水在 **Create 混合盆**里**加热**搅拌得到（1000 mB/次）。
     * 瓶装可喝，饥饿值同汁类。
     */
    public static final MilkLike COFFEE = milkLike("coffee", Drinkable.JUICE, Container.BOTTLE, JUICE_FOOD);

    // ---- 酱类：碗装、能拿勺子吃、吃得更慢（64 tick）----
    public static final MilkLike BLUEBERRY_JAM = milkLike("blueberry_jam", Drinkable.THICK, Container.BOWL, food(6, 0.5F));
    public static final MilkLike CHILI_SAUCE = milkLike("chili_sauce", Drinkable.THICK, Container.BOWL, food(5, 0.4F));
    public static final MilkLike STRAWBERRY_JAM = milkLike("strawberry_jam", Drinkable.THICK, Container.BOWL, food(6, 0.5F));
    public static final MilkLike PINEAPPLE_JAM = milkLike("pineapple_jam", Drinkable.THICK, Container.BOWL, food(6, 0.5F));
    /** 葡萄酱暂时只做一种（汁才分红白）。 */
    public static final MilkLike GRAPE_JAM = milkLike("grape_jam", Drinkable.THICK, Container.BOWL, food(6, 0.6F));
    public static final MilkLike PEANUT_BUTTER = milkLike("peanut_butter", Drinkable.THICK, Container.BOWL, food(7, 0.7F));

    /** 食用油：纯原料，**不能喝**，只有瓶装。 */
    public static final MilkLike COOKING_OIL = milkLike("cooking_oil", Drinkable.NONE, Container.BOTTLE, null);

    // ==================================================================
    //  注册
    // ==================================================================

    /**
     * 把两个注册表挂到 mod 事件总线；**必须在物品注册表之前调用**。
     *
     * <p>这里顺手触发一次 {@code CompatFluids} 的类初始化：它的流体字段是在类初始化时
     * 排队注册的，必须赶在注册事件之前完成，否则会错过注册窗口（NeoForge 不允许注册事件
     * 之后再添加对象）。</p>
     */
    public static void register(IEventBus modEventBus) {
        dev.rhaaaaaast1.foodindustry.compat.registry.CompatFluids.bootstrap();
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
    }

    /** 本模组 + compat 模块的全部"牛奶型"流体（供标签、能力、客户端染色统一遍历）。 */
    public static List<MilkLike> allMilkLike() {
        List<MilkLike> all = new ArrayList<>(MAIN);
        all.addAll(COMPAT);
        return List.copyOf(all);
    }

    /** compat 模块的流体（给 {@code CompatFluids} 用）。 */
    public static List<MilkLike> compatAll() {
        return List.copyOf(COMPAT);
    }

    /**
     * 只注册**流体本身**、不做任何物品的 compat 流体。
     *
     * <p>用于"对方模组已经有对应物品"的情况（番茄酱在 Farmer's Delight、紫颂酱在 End's Delight）——
     * 物品沿用它们的，我们不重复造，只补上它们没有的流体。</p>
     *
     * <p>注意：NeoForge 没有"条件注册"能力，所以这类流体**总是**注册；要靠配方上的
     * {@code neoforge:mod_loaded} 条件来控制"装了对方模组才提供内容"。</p>
     */
    public static MilkLike compatFluidOnly(String name) {
        return register(COMPAT, name, Drinkable.NONE, Container.NONE, null, false);
    }

    // ==================================================================
    //  内部实现
    // ==================================================================

    /** 本模组自己的流体：容器物品一律进创造物品栏（桶除外，桶按需求隐藏）。 */
    private static MilkLike milkLike(String name, Drinkable drinkable, Container container, @Nullable FoodProperties food) {
        return register(MAIN, name, drinkable, container, food, true);
    }

    /**
     * 一把注册完一种"牛奶型"流体的全部部件。
     *
     * <p>这里的 {@code Pending} 小对象是为了打破循环依赖：流体属性需要"桶"，而桶的构造又需要
     * 流体实例。两个 lambda 捕获的是 {@code Pending} <b>对象本身</b>，等到 RegisterEvent 真正
     * 执行时才读它的字段 —— 那时字段已经赋值完毕，所以能拿到正确的 holder。</p>
     */
    private static MilkLike register(List<MilkLike> sink, String name, Drinkable drinkable,
                                     Container container, @Nullable FoodProperties food, boolean visible) {
        final class Pending {
            DeferredHolder<FluidType, FluidType> type;
            DeferredHolder<Fluid, FlowingFluid> source;
            DeferredHolder<Fluid, FlowingFluid> flowing;
            @Nullable DeferredItem<Item> bucket;
            @Nullable DeferredItem<Item> bottle;
            @Nullable DeferredItem<Item> bowl;

            BaseFlowingFluid.Properties properties() {
                BaseFlowingFluid.Properties props =
                        new BaseFlowingFluid.Properties(this.type, this.source, this.flowing);
                if (this.bucket != null) {
                    // 有桶才挂桶：没有桶的流体按 NeoForge 规则 getBucket() 返回空气，
                    // 于是"空桶接不出它" —— 这正是我们要的（汁/酱不该能装桶）。
                    props.bucket(this.bucket);
                }
                return props;
            }

            MilkLike build() {
                return new MilkLike(name, drinkable, type, source, flowing, bucket, bottle, bowl);
            }
        }

        Pending pending = new Pending();
        pending.type = FLUID_TYPES.register(name, () -> new FluidType(milkLikeTypeProperties()));
        pending.source = FLUIDS.register(name, () -> new BaseFlowingFluid.Source(pending.properties()));
        pending.flowing = FLUIDS.register("flowing_" + name, () -> new BaseFlowingFluid.Flowing(pending.properties()));

        switch (container) {
            case BUCKET -> pending.bucket = registerContainer(name + "_bucket", false,
                    () -> makeBucket(pending.source.get(), drinkable, food));
            case BOTTLE -> pending.bottle = registerContainer(bottleItemName(name), visible,
                    () -> makeBottle(drinkable, food));
            case BOWL -> pending.bowl = registerContainer(name, visible,
                    () -> makeBowl(drinkable, food));
            case NONE -> {
                // 不注册容器物品
            }
        }

        MilkLike milkLike = pending.build();
        sink.add(milkLike);
        return milkLike;
    }

    /** 桶一律隐藏（按需求"桶装形态在创造模式物品栏不显示"），瓶和碗默认显示。 */
    private static DeferredItem<Item> registerContainer(String itemName, boolean visible, java.util.function.Supplier<Item> factory) {
        return visible ? ModItems.registerWithTab(itemName, factory) : ModItems.registerHidden(itemName, factory);
    }

    /**
     * 瓶装物品的注册名。
     *
     * <p>按 Farmer's Delight 的惯例，**物品名里不写容器**：花生酱就是 {@code peanut_butter}、
     * 蓝莓汁就是 {@code blueberry_juice}（"形式上是碗装/瓶装，但名字没必要写"）。
     * 桶是例外 —— 原版就叫 {@code milk_bucket}，所以保留 {@code _bucket}。
     * 食用油的瓶装按需求**保持 {@code cooking_oil_bottle} 不变**。</p>
     */
    private static String bottleItemName(String fluidName) {
        return "cooking_oil".equals(fluidName) ? fluidName + "_bottle" : fluidName;
    }

    private static Item makeBucket(Fluid fluid, Drinkable drinkable, @Nullable FoodProperties food) {
        Item.Properties properties = new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1);
        if (food != null) {
            properties.food(food);
        }
        return new DrinkableBucketItem(fluid, drinkable.clearsEffects, drinkable.ticks, properties);
    }

    private static Item makeBottle(Drinkable drinkable, @Nullable FoodProperties food) {
        Item.Properties properties = new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16);
        if (food != null) {
            properties.food(food);
        }
        return new DrinkableBottleItem(drinkable.ticks, drinkable.clearsEffects, properties);
    }

    private static Item makeBowl(Drinkable drinkable, @Nullable FoodProperties food) {
        Item.Properties properties = new Item.Properties().craftRemainder(Items.BOWL).stacksTo(16);
        if (food != null) {
            properties.food(food);
        }
        return new BowlFoodItem(drinkable.ticks, properties);
    }

    private static FoodProperties food(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
    }

    /** 和牛奶同款的流体类型：密度/黏度 1024 + 原版桶音效。 */
    private static FluidType.Properties milkLikeTypeProperties() {
        return FluidType.Properties.create()
                .density(1024)
                .viscosity(1024)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
    }

    /** 一种"牛奶型"流体的全部部件（桶/瓶/碗三者至多一个，取决于 {@link Container}）。 */
    public record MilkLike(String name,
                           Drinkable drinkable,
                           DeferredHolder<FluidType, FluidType> type,
                           DeferredHolder<Fluid, FlowingFluid> source,
                           DeferredHolder<Fluid, FlowingFluid> flowing,
                           @Nullable DeferredItem<Item> bucket,
                           @Nullable DeferredItem<Item> bottle,
                           @Nullable DeferredItem<Item> bowl) {

        /** 这个流体注册出来的容器物品（可能为空 —— 没有容器的流体只能靠管道/储罐搬运）。 */
        public List<DeferredItem<Item>> containers() {
            List<DeferredItem<Item>> list = new ArrayList<>(3);
            if (this.bucket != null) {
                list.add(this.bucket);
            }
            if (this.bottle != null) {
                list.add(this.bottle);
            }
            if (this.bowl != null) {
                list.add(this.bowl);
            }
            return list;
        }
    }

    /** 容器形态。 */
    public enum Container {
        /** 桶：真正的流体容器（能接/能倒），不进创造物品栏。 */
        BUCKET,
        /** 瓶：能喝（汁类、食用油）。 */
        BOTTLE,
        /** 碗：能吃的酱。 */
        BOWL,
        /** 不注册容器物品。 */
        NONE
    }

    /** 饮用/食用方式。 */
    public enum Drinkable {
        /** 不能喝（食用油等纯原料）。 */
        NONE(false, 0, false),
        /** 像牛奶：能喝、喝完只清"可被牛奶治愈"的效果。 */
        MILK(true, 32, true),
        /** 汁类：能喝、不清效果、32 tick。 */
        JUICE(true, 32, false),
        /** 酱类：拿勺子吃，更黏稠、64 tick。 */
        THICK(true, 64, false);

        private final boolean drinkable;
        private final int ticks;
        private final boolean clearsEffects;

        Drinkable(boolean drinkable, int ticks, boolean clearsEffects) {
            this.drinkable = drinkable;
            this.ticks = ticks;
            this.clearsEffects = clearsEffects;
        }

        /** 算不算"饮品"（标签用：汁类和山羊奶是饮品，酱类和食用油不是）。 */
        public boolean isDrink() {
            return this == MILK || this == JUICE;
        }
    }

    private ModFluids() {
    }
}
