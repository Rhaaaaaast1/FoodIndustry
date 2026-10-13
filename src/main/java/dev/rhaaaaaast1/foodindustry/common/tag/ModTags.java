package dev.rhaaaaaast1.foodindustry.common.tag;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * 本模组使用的标签常量。
 *
 * <p>注意：Farmer's Delight 明确要求 add-on **复制** 自己需要的 TagKey，而不是引用它的常量，
 * 因此这里用相同的标签 id 自己声明一份。这样即使将来 FD 改名/移动常量，我们的代码也不会编译不过。</p>
 */
public final class ModTags {

    private ModTags() {
    }

    public static final class Blocks {

        /** Farmer's Delight 的绳子标签：我们的攀藤作物认这个标签里的方块。 */
        public static final TagKey<Block> ROPES = farmersDelight("ropes");

        /** 原版作物方块标签，方便别的模组按统一规则识别。 */
        public static final TagKey<Block> CROPS = vanilla("crops");

        /** Farmer's Delight 的野生作物标签（方块）：把我们的野生作物挂进去，别的模组就能统一识别。 */
        public static final TagKey<Block> WILD_CROPS = farmersDelight("wild_crops");

        private Blocks() {
        }

        private static TagKey<Block> farmersDelight(String path) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("farmersdelight", path));
        }

        private static TagKey<Block> vanilla(String path) {
            return BlockTags.create(ResourceLocation.withDefaultNamespace(path));
        }
    }

    public static final class Items {

        /** 通用作物标签，别的模组（含 Create、FD 附属）用它来识别"这是作物"。 */
        public static final TagKey<Item> CROPS = common("crops");

        /** 通用种子标签。 */
        public static final TagKey<Item> SEEDS = common("seeds");

        /** 通用水果标签。 */
        public static final TagKey<Item> FRUITS = common("fruits");

        /** 通用蔬菜标签。 */
        public static final TagKey<Item> VEGETABLES = common("vegetables");

        /** Farmer's Delight 的刀标签（真实路径是 {@code farmersdelight:tools/knives}）：砧板配方用它判定工具。 */
        public static final TagKey<Item> KNIVES = farmersDelight("tools/knives");

        /** Farmer's Delight 的野生作物标签（物品）。 */
        public static final TagKey<Item> WILD_CROPS = farmersDelight("wild_crops");

        /** 通用"奶类饮品"标签 {@code c:drinks/milk}（原版牛奶桶也在里面）。 */
        public static final TagKey<Item> DRINKS_MILK = common("drinks/milk");

        /** 通用"奶桶"标签 {@code c:buckets/milk}。 */
        public static final TagKey<Item> BUCKETS_MILK = common("buckets/milk");

        /** 通用"桶"标签 {@code c:buckets}：我们所有的桶都挂上。 */
        public static final TagKey<Item> BUCKETS = common("buckets");

        /** 通用"饮品"标签 {@code c:drinks}：汁类、豆浆、山羊奶挂上（酱类和食用油不挂）。 */
        public static final TagKey<Item> DRINKS = common("drinks");

        /** 通用"食物"标签 {@code c:foods}：碗装的酱挂上。 */
        public static final TagKey<Item> FOODS = common("foods");

        /** 单独一种作物的标签：{@code c:crops/corn}。 */
        public static TagKey<Item> crop(String name) {
            return common("crops/" + name);
        }

        /** 单独一种种子的标签：{@code c:seeds/corn}。 */
        public static TagKey<Item> seed(String name) {
            return common("seeds/" + name);
        }

        /** 单独一种水果的标签：{@code c:fruits/grape}。 */
        public static TagKey<Item> fruit(String name) {
            return common("fruits/" + name);
        }

        private Items() {
        }

        /** 通用物品标签：{@code c:<path>}。 */
        private static TagKey<Item> common(String path) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
        }

        /** Farmer's Delight 命名空间下的物品标签（按 FD 要求自己声明一份）。 */
        private static TagKey<Item> farmersDelight(String path) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("farmersdelight", path));
        }
    }

    public static final class EntityTypes {

        /**
         * **会产粪便的动物** —— 本模组自己的实体类型标签。
         *
         * <p>默认内容（牛/猪/羊/鸡等常见家畜）在 {@code ModEntityTagsProvider} 里生成；
         * 整合包或玩家可以用数据包继续往里加动物。</p>
         *
         * <p>注意：{@code EntityTypeTags} 只提供常量、**没有** {@code create} 方法，
         * 所以这里用通用的 {@link TagKey#create} 配 {@code Registries.ENTITY_TYPE}。</p>
         */
        public static final TagKey<net.minecraft.world.entity.EntityType<?>> MANURE_PRODUCERS =
                TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath(FoodIndustry.MODID, "manure_producers"));

        private EntityTypes() {
        }
    }

    public static final class Fluids {

        /**
         * 通用水标签 {@code c:water}：发酵配方用它当"水"这一项。
         *
         * <p>NeoForge 自带这个标签（它 jar 里的 {@code data/c/tags/fluid/water.json} 内容 =
         * {@code minecraft:water} + {@code minecraft:flowing_water}），所以**不需要**本模组再生成
         * 一份；这里按本文件的惯例自己声明常量，而不是引用 {@code net.neoforged.neoforge.common.Tags}
         * —— 和 {@link #MILK} 一样，避免将来 NeoForge 改名/挪位置时我们的代码跟着编译不过。</p>
         *
         * <p>用标签而不是硬写 {@code minecraft:water}：整合包里若有别的模组也提供"水"
         * （或把它的水流体挂进 {@code #c:water}），配方不用改就能匹配上。</p>
         */
        public static final TagKey<Fluid> WATER = common("water");

        /** 通用牛奶标签 {@code c:milk}：山羊奶也放进去，别的模组按"牛奶"取用时能接受它。 */
        public static final TagKey<Fluid> MILK = common("milk");

        /** 本模组自己的山羊奶标签（老名字，保留兼容）。 */
        public static final TagKey<Fluid> GOAT_MILK = mod("goat_milk");

        /** 本模组某种流体自己的标签：{@code foodindustry:<名字>}。 */
        public static TagKey<Fluid> mod(String path) {
            return FluidTags.create(ResourceLocation.fromNamespaceAndPath(FoodIndustry.MODID, path));
        }

        private Fluids() {
        }

        private static TagKey<Fluid> common(String path) {
            return FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
        }
    }
}
