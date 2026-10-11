package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.registry.ModFluids;
import dev.rhaaaaaast1.foodindustry.common.tag.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 流体标签生成。
 *
 * <p>注意两点：</p>
 * <ul>
 *   <li>1.21 里流体的标签目录是 <b>单数</b> {@code data/<命名空间>/tags/fluid/}（方块/物品才是复数）；</li>
 *   <li>一个流体有"静止 + 流动"两个 id，**两个都要写进标签**，否则别的模组按流动态去找会找不到。</li>
 * </ul>
 *
 * <p>每种流体都会生成 {@code foodindustry:<名字>} 标签；山羊奶额外挂进 {@code c:milk}，
 * 这样别的模组按"牛奶"取用时也能接受它。</p>
 */
public class ModFluidTagsProvider extends FluidTagsProvider {

    public ModFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FoodIndustry.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (ModFluids.MilkLike milkLike : ModFluids.allMilkLike()) {
            tag(ModTags.Fluids.mod(milkLike.name()))
                    .add(milkLike.source().get(), milkLike.flowing().get());
        }

        tag(ModTags.Fluids.MILK)
                .add(ModFluids.GOAT_MILK.source().get(), ModFluids.GOAT_MILK.flowing().get());
    }
}
