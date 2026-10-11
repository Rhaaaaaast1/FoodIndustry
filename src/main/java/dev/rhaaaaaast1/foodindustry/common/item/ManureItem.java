package dev.rhaaaaaast1.foodindustry.common.item;

import dev.rhaaaaaast1.foodindustry.common.registry.ModEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 粪便：**既是肥料也是投掷物**。
 *
 * <p>继承 {@link BoneMealItem}，所以"对方块右键当骨粉用"是原版自己的代码
 * （{@code BoneMealItem.useOn} → {@code BoneMealItem.applyBonemeal}），
 * 与骨粉同效力，一行都不用写。</p>
 *
 * <p>投掷：对空气右键（或不指向可施肥方块时）即抛出，命中生物给对方
 * **10 秒反胃**（时长见 {@code ModEntities.ManureProjectile}）。</p>
 *
 * <p>投掷写法与**原版雪球**逐行一致（{@code SnowballItem.use}）：
 * new 出来 → {@code setItem} → {@code shootFromRotation} → 加入世界。</p>
 */
public class ManureItem extends BoneMealItem {

    public ManureItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL,
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            ModEntities.ManureProjectile projectile = new ModEntities.ManureProjectile(level, player);
            projectile.setItem(stack);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(projectile);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        stack.consume(1, player);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
