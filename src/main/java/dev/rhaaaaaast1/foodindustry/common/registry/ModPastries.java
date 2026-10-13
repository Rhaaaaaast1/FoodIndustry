package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.FoodValues;
import dev.rhaaaaaast1.foodindustry.common.block.CakeBlock;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.block.PieBlock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 糕点线：14 种蛋糕 + 8 种派，以及它们各自的切片。
 *
 * <p>两者注册流程完全一样（先切片物品 → 再方块 → 最后方块物品），只是方块实现和
 * 切片数值不同，所以合成一个注册器、共用同一套登记逻辑 —— 拆成两个类会出现
 * 26 行逐字重复，以后改一处忘另一处。</p>
 *
 * <h2>蛋糕 vs 派的差异（这是它们仍是两种方块的原因）</h2>
 * <table>
 *   <tr><th></th><th>蛋糕</th><th>派</th></tr>
 *   <tr><td>方块类</td><td>{@link CakeBlock}（本模组：原版 {@code CakeBlock} + 刀切）</td>
 *       <td>{@link PieBlock}（<b>农夫乐事的</b>：刀切/音效/粒子全是它的代码）</td></tr>
 *   <tr><td>口数</td><td>7（{@code bites} 0..6）</td><td>4（{@code bites} 0..3）</td></tr>
 *   <tr><td>模型父级</td><td>{@code minecraft:block/cake_sliceN}</td>
 *       <td>{@code farmersdelight:block/template_pie_sliceN}</td></tr>
 *   <tr><td>切片数值</td><td>2 / 0.5（{@link FoodValues#CAKE_SLICE}）</td>
 *       <td>3 / 0.3（{@link FoodValues#PIE_SLICE}）</td></tr>
 * </table>
 */
public final class ModPastries {

    /** 蛋糕口味：方块 {@code <口味>_cake}，切片 {@code <口味>_cake_slice}。 */
    public static final List<String> CAKE_FLAVOURS = List.of(
            "apple", "blueberry", "carrot", "chocolate", "chorus", "coffee", "grape",
            "honey", "lemon", "matcha", "orange", "pineapple", "strawberry", "sweet_berry");

    /** 派口味：方块 {@code <口味>_pie}，切片 {@code <口味>_pie_slice}。 */
    public static final List<String> PIE_FLAVOURS = List.of(
            "bacon", "carrot", "fish", "pineapple",
            "blueberry", "grape", "strawberry", "matcha");

    public static final DeferredRegister.Blocks PASTRY_BLOCKS =
            DeferredRegister.createBlocks(FoodIndustry.MODID);

    public static final Map<String, DeferredBlock<CakeBlock>> CAKES = new LinkedHashMap<>();
    public static final Map<String, DeferredBlock<PieBlock>> PIES = new LinkedHashMap<>();
    /** 全部糕点切片：key 是 {@code <口味>_cake} 或 {@code <口味>_pie}。 */
    public static final Map<String, DeferredItem<Item>> SLICES = new LinkedHashMap<>();
    public static final List<DeferredItem<BlockItem>> BLOCK_ITEMS = new ArrayList<>();

    static {
        // ---------------- 蛋糕 ----------------
        registerSlices(CAKE_FLAVOURS, "_cake", FoodValues.CAKE_SLICE);
        for (String flavour : CAKE_FLAVOURS) {
            registerCake(flavour);
        }
        registerBlockItems(CAKE_FLAVOURS, "_cake", CAKES);

        // ---------------- 派 ----------------
        registerSlices(PIE_FLAVOURS, "_pie", FoodValues.PIE_SLICE);
        for (String flavour : PIE_FLAVOURS) {
            registerPie(flavour);
        }
        registerBlockItems(PIE_FLAVOURS, "_pie", PIES);
    }

    /** 第一趟：注册切片物品。方块构造时要通过 supplier 拿到它。 */
    private static void registerSlices(List<String> flavours, String suffix, FoodProperties food) {
        for (String flavour : flavours) {
            String key = flavour + suffix;
            SLICES.put(key, ModItems.registerWithTab(key + "_slice",
                    () -> new Item(new Item.Properties().food(food))));
        }
    }

    /** 第二趟（蛋糕）：原版蛋糕行为 + 刀切，见 {@link CakeBlock}。 */
    private static void registerCake(String flavour) {
        String id = flavour + "_cake";
        CAKES.put(flavour, PASTRY_BLOCKS.registerBlock(id,
                props -> new CakeBlock(props, SLICES.get(id)),
                BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));
    }

    /** 第二趟（派）：直接复用农夫乐事的 {@link PieBlock}。 */
    private static void registerPie(String flavour) {
        String id = flavour + "_pie";
        PIES.put(flavour, PASTRY_BLOCKS.registerBlock(id,
                props -> new PieBlock(props, SLICES.get(id)),
                BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));
    }

    /** 第三趟：方块物品（放进创造标签）。 */
    private static <B extends Block> void registerBlockItems(
            List<String> flavours, String suffix, Map<String, DeferredBlock<B>> blocks) {
        for (String flavour : flavours) {
            DeferredBlock<B> block = blocks.get(flavour);
            BLOCK_ITEMS.add(ModItems.registerWithTab(flavour + suffix,
                    () -> new BlockItem(block.get(), new Item.Properties())));
        }
    }

    /** 类初始化钩子：主类在挂注册表之前调用，触发上面的静态初始化。 */
    public static void bootstrap() {
        // 空方法即可
    }

    /**
     * 糕点的**方块物品**集合，给物品模型生成器用。
     *
     * <p>这些物品用 {@code item/<名字>} 图标（同原版蛋糕／农夫乐事的派），
     * 不是"方块物品用方块贴图"，所以生成器必须跳过，否则会去找并不存在的
     * {@code block/<名字>} 贴图而让 runData 失败。</p>
     */
    public static Set<Item> blockItems() {
        Set<Item> out = new HashSet<>();
        for (DeferredItem<BlockItem> item : BLOCK_ITEMS) {
            out.add(item.get());
        }
        return out;
    }

    private ModPastries() {
    }
}
