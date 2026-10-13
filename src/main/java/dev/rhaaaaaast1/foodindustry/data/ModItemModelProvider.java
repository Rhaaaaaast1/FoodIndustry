package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.item.BowlFoodItem;
import dev.rhaaaaaast1.foodindustry.common.item.DrinkableBottleItem;
import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 物品模型生成。
 *
 * <p>遍历的是**全部已注册物品**（不是创造标签）：桶按需求隐藏、不进创造物品栏，
 * 但它们同样需要模型，只遍历标签会漏掉。</p>
 *
 * <p>贴图规则体现一条约定：<b>原版已有的素材一律引用原版，不复制、不自画</b>。</p>
 * <ul>
 *   <li>本模组的桶 → 原版牛奶桶图标（{@code minecraft:item/milk_bucket}）；</li>
 *   <li>本模组的瓶 → 直接继承原版药水模型（瓶身 + 可染色的液体层，颜色在客户端按流体染色）；</li>
 *   <li>野生作物的方块物品 → 用方块贴图；</li>
 *   <li>其余（种子、产物）→ 用 {@code item/<名字>} 自己的贴图。</li>
 * </ul>
 */
public class ModItemModelProvider extends ItemModelProvider {

    /**
     * 这些物品有**自己画好的专用贴图**（用户提供的成品图，不是可染色的遮罩）。
     *
     * <p>它们不能继承 {@code item/potion} / {@code item/mushroom_stew}：
     * 那两个模型的轮廓（细颈药水瓶、蘑菇煲的碗）会把自己画的图裁掉一大块，
     * 而且客户端的染色表还会再叠一层色。所以改用 {@code item/generated} + 自己的贴图。</p>
     *
     * <p>名单之外的液体物品（豆浆、山羊奶）**没有专用贴图**，
     * 继续走"原版模型 + 代码染色"。</p>
     */
    public static final java.util.Set<String> HAS_OWN_TEXTURE = java.util.Set.of(
            "blueberry_juice", "strawberry_juice", "pineapple_juice",
            "white_grape_juice", "red_grape_juice", "coffee", "soy_sauce",
            // 醋：外观就是酱油瓶换色（由 tools/gen-bottle-recolor.py 从 soy_sauce 生成）
            "vinegar",
            // 食用油：同样"酱油瓶换色"。⚠️ 这里的名字必须写**物品注册名** cooking_oil_bottle
            // （不是流体名 cooking_oil）—— 生成器第 73 行取的是注册名，写错就静默走
            // "药水模型 + 染色"分支，模型变成 {"parent":"minecraft:item/potion"}，
            // 贴图也不会被引用（这个坑真踩过）。
            "cooking_oil_bottle",
            // 胡萝卜汁没有成品图，是用 melon_juice 做色相偏移生成的
            // （见 tools/gen-recolored-items.py），但它同样有了自己的贴图，所以也走 item/generated。
            "carrot_juice",
            "blueberry_jam", "strawberry_jam", "pineapple_jam", "grape_jam", "peanut_butter",
            // 辣酱：按需求从"碗装 + 蘑菇煲模型"改成"罐装果酱外观"，
            // 贴图由 tools/gen-recolored-items.py 从 grape_jam 改色生成。
            "chili_sauce");

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, FoodIndustry.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // 蛋糕的方块物品用 item/<名字> 图标（同原版蛋糕），不是"方块物品 = 方块贴图"，
        // 所以跳过生成，否则会去找并不存在的 block/<名字> 贴图，让 runData 直接失败。
        java.util.Set<Item> cakeItems = dev.rhaaaaaast1.foodindustry.common.registry.ModPastries.blockItems();

        // 派同理（图标照农夫乐事的派）
        cakeItems.addAll(dev.rhaaaaaast1.foodindustry.common.registry.ModPastries.blockItems());

        ModItems.ITEMS.getEntries().forEach(holder -> {
            Item item = holder.get();
            String name = BuiltInRegistries.ITEM.getKey(item).getPath();

            if (cakeItems.contains(item)) {
                // 模型由 tools/gen-cakes.py 生成（item/generated + item/<名字>）
                return;
            }

            // 这里**不要**加"贴图不存在就跳过"之类的守卫：
            // 模型生成器自身会校验 layer0 指向的贴图（缺了会以
            // "Texture ... does not exist in any known resource pack" 直接让 runData 失败），
            // 而 ExistingFileHelper 对"位于主资源里、尚未被扫描到的贴图"并不可靠 ——
            // 加守卫会把 28 个老物品的模型误删。
            // 依赖 Create 的两件也照常生成模型：它们的贴图在我们自己的资源里，
            // 模型必须无条件生成，jar 才不会因打包环境不同而不一致。
            if (item instanceof BowlFoodItem) {                if (HAS_OWN_TEXTURE.contains(name)) {
                    // 有专用贴图（果汁/果酱这类）→ 直接用画好的成品图，**不要再继承蘑菇煲模型**，
                    // 否则蘑菇煲的碗形会把图裁掉一大块。
                    simpleItem(item);
                } else {
                    // 碗装：引用原版蘑菇煲的图标（碗 + 内容物），内容物靠代码染色
                    withExistingParent(name, mcLoc("item/mushroom_stew"));
                }
            } else if (item instanceof DrinkableBottleItem) {
                if (HAS_OWN_TEXTURE.contains(name)) {
                    // 有专用贴图 → 直接用成品图（同理：药水瓶的细颈形状会裁掉自画的瓶身，
                    // 而且染色会再叠一层色）
                    simpleItem(item);
                } else {
                    // 瓶：继承原版药水模型（layer0 = 液体层可染色，layer1 = 瓶身）
                    withExistingParent(name, mcLoc("item/potion"));
                }
            } else if (item instanceof BucketItem) {
                // 桶：引用原版牛奶桶的图标
                withExistingParent(name, mcLoc("item/generated")).texture("layer0", mcLoc("item/milk_bucket"));
            } else if (item instanceof BlockItem && !(item instanceof ItemNameBlockItem)) {
                // 方块物品（野生作物）：用方块的贴图
                // 注意：种子用的 ItemNameBlockItem **也继承自 BlockItem**，但它应该用物品贴图
                withExistingParent(name, mcLoc("item/generated")).texture("layer0", modLoc("block/" + name));
            } else {
                simpleItem(item);
            }
        });
    }

    /** 生成 {@code item/generated} + 单层纹理（{@code item/<名字>}）的模型。 */
    protected void simpleItem(Item item) {
        String name = BuiltInRegistries.ITEM.getKey(item).getPath();
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name));
    }
}
