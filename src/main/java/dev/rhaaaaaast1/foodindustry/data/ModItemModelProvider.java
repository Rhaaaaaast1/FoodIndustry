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
            if (item instanceof BowlFoodItem) {
                // 碗装：引用原版蘑菇煲的图标（碗 + 内容物）
                withExistingParent(name, mcLoc("item/mushroom_stew"));
            } else if (item instanceof DrinkableBottleItem) {
                // 瓶：继承原版药水模型（layer0 = 液体层可染色，layer1 = 瓶身）
                withExistingParent(name, mcLoc("item/potion"));
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
