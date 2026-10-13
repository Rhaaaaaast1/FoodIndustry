package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块实体注册表。
 *
 * <p><b>目前是空的</b> —— 唯一的方块实体（发酵木桶）已暂时移除。
 * 注册表本身保留着：一旦有新的方块实体，在这里加一行注册即可，
 * 且 {@code FoodIndustry} 里的挂载顺序不用动。</p>
 *
 * <p>⚠️ 恢复发酵木桶时要注意**注册顺序**：它的工厂要引用
 * {@code ModBlocks.FERMENTATION_BARREL.get()}，所以必须在
 * {@code ModBlocks.BLOCKS.register(...)} 之后挂载。</p>
 */
public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FoodIndustry.MODID);

    private ModBlockEntities() {
    }
}
