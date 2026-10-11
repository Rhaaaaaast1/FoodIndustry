package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import dev.rhaaaaaast1.foodindustry.common.tag.ModTags;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

/**
 * 进度（advancement）生成。
 *
 * <p>目前只有一条：<b>本模组版的"野生作物"</b>。它是农夫乐事
 * {@code farmersdelight:main/get_fd_seed}（中文名同样是"野生作物"）的复制品，
 * 但**完全落在 {@code foodindustry} 命名空间里，不覆盖也不引用 FD 的任何文件**——
 * 玩家装不装 FD 都能拿到这条进度，FD 更新也不会跟我们打架。</p>
 *
 * <p>判定用的是 {@code #farmersdelight:wild_crops} 物品标签
 * （见 {@link dev.rhaaaaaast1.foodindustry.data.ModItemTagsProvider}）：
 * 我们注册的 12 种野生作物已经在里面，标签在游戏里还会和 FD 自己的 7 种并集，
 * 所以这一条判定同时覆盖 12 + 7 = 19 种野生作物，玩家捡到任何一种都会弹。</p>
 *
 * <p>语言键 {@code advancement.foodindustry.wild_crops.title/description}
 * 由手写资源 {@code assets/foodindustry/lang/*.json} 提供。</p>
 */
public final class ModAdvancementProvider implements AdvancementProvider.AdvancementGenerator {

    /** 这条进度的 id：{@code foodindustry:wild_crops}。 */
    private static final ResourceLocation WILD_CROPS_ID =
            ResourceLocation.fromNamespaceAndPath(FoodIndustry.MODID, "wild_crops");

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver,
                         ExistingFileHelper existingFileHelper) {
        // 注意：Advancement.Builder.save() 内部已经调用过 saver.accept()，
        // 这里**不能再**套一层 saver.accept(...)，否则会 "Duplicate advancement"。
        net.minecraft.advancements.Advancement.Builder.advancement()
                // 故意不给 parent：这样它是一条独立进度，
                // 不会挤进农夫乐事的成就树里（用户要求"完全不影响 FD"）。
                .display(new ItemStack(ModItems.WILD_CHILI.get()),
                        Component.translatable("advancement.foodindustry.wild_crops.title"),
                        Component.translatable("advancement.foodindustry.wild_crops.description"),
                        // 没有 parent 的进度不画背景，这个参数用不到
                        null,
                        AdvancementType.TASK,
                        true,   // show_toast
                        true,   // announce_to_chat
                        false)  // hidden（隐藏的进度不在界面显示，我们不做隐藏）
                .addCriterion("has_wild_crop",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                ItemPredicate.Builder.item().of(ModTags.Items.WILD_CROPS)))
                .save(saver, WILD_CROPS_ID.toString());
    }
}
