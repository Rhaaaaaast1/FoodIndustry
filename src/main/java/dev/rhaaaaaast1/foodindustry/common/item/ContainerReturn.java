package dev.rhaaaaaast1.foodindustry.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 用完饮品/食物后把容器还给玩家。
 *
 * <p>写法对齐原版 {@code HoneyBottleItem}：手上那份**空了**就直接变成容器；
 * 没空（比如堆叠里还有）就额外塞一个进背包，背包满了掉在地上。
 * 创造模式（{@code hasInfiniteMaterials}）不额外给，因为本来就不消耗。</p>
 */
public final class ContainerReturn {

    /**
     * @param used      刚才用掉的那一份（可能已经被 {@code super.finishUsingItem} 消耗掉）
     * @param entity    使用者
     * @param container 要还的容器（空瓶 / 空桶 / 空碗）
     * @return 应该放回手里的物品
     */
    public static ItemStack giveBack(ItemStack used, LivingEntity entity, ItemStack container) {
        if (used.isEmpty()) {
            return container;
        }
        if (entity instanceof Player player && !player.hasInfiniteMaterials()) {
            ItemStack extra = container.copy();
            if (!player.getInventory().add(extra)) {
                player.drop(extra, false);
            }
        }
        return used;
    }

    private ContainerReturn() {
    }
}
