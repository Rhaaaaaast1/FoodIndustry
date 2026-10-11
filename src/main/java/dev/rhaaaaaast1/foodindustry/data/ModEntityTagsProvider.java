package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.tag.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 实体类型标签生成。
 *
 * <p>目前只有一个标签：{@code foodindustry:manure_producers} —— 会定期产粪便的动物。
 * 默认放常见家畜；整合包或玩家可以用数据包继续往里加（比如别的模组的牛）。</p>
 */
public class ModEntityTagsProvider extends EntityTypeTagsProvider {

    public ModEntityTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                 @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FoodIndustry.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 只放**家畜/农场动物**。故意不放猫、狼、鹦鹉这类宠物 ——
        // 它们往地上拉粪不合直觉（真要加，用数据包往这个标签里塞即可）。
        tag(ModTags.EntityTypes.MANURE_PRODUCERS).add(
                EntityType.COW,
                EntityType.MOOSHROOM,      // 蘑菇牛
                EntityType.PIG,
                EntityType.SHEEP,
                EntityType.CHICKEN,
                EntityType.RABBIT,
                EntityType.GOAT,
                EntityType.HORSE,
                EntityType.DONKEY,
                EntityType.MULE,
                EntityType.LLAMA,
                EntityType.TRADER_LLAMA,
                EntityType.CAMEL);
    }
}
