package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 实体注册表。目前只有一个：粪便投掷物。
 *
 * <p>投掷物本身是纯逻辑、没有自定义模型，所以客户端渲染直接用原版的
 * {@code ThrownItemRenderer}（和雪球、鸡蛋、末影珍珠同一个渲染器），
 * 在 {@code FoodIndustryClient} 里注册。</p>
 */
public final class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, FoodIndustry.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<ManureProjectile>> MANURE_PROJECTILE =
            ENTITY_TYPES.register("manure", () -> EntityType.Builder
                    .<ManureProjectile>of(ManureProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("manure"));

    /**
     * 粪便投掷物：命中生物 → 10 秒反胃；命中方块 → 只留粒子。
     *
     * <p>结构与原版雪球（{@code Snowball}）一致，只是命中效果不同。
     * 命中后调用 {@code discard()} 把自己移除 —— 雪球也是这么做的。</p>
     */
    public static class ManureProjectile extends ThrowableItemProjectile {

        /** 反胃时长：10 秒 = 200 tick。 */
        public static final int NAUSEA_TICKS = 200;

        public ManureProjectile(EntityType<? extends ManureProjectile> entityType, Level level) {
            super(entityType, level);
        }

        public ManureProjectile(Level level, LivingEntity shooter) {
            super(ModEntities.MANURE_PROJECTILE.get(), shooter, level);
        }

        @Override
        protected Item getDefaultItem() {
            return ModItems.MANURE.get();
        }

        @Override
        protected void onHitEntity(EntityHitResult result) {
            super.onHitEntity(result);
            Entity target = result.getEntity();
            // 命中生物 → 反胃（时长走配置，默认 10 秒）。
            // 注意：反胃的常量名是 CONFUSION，注册名才是 "nausea"。
            if (target instanceof LivingEntity living) {
                int ticks = dev.rhaaaaaast1.foodindustry.common.Config.MANURE_NAUSEA_SECONDS.get() * 20;
                living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, ticks, 0), this.getOwner());
            }
            // 糊在生物身上也溅开（和砸方块用同一个事件）
            if (!this.level().isClientSide()) {
                this.level().broadcastEntityEvent(this, SPLAT_EVENT);
            }
            this.discard();
        }

        @Override
        protected void onHitBlock(net.minecraft.world.phys.BlockHitResult result) {
            super.onHitBlock(result);
            // 命中方块 → 广播"炸开"事件，两端一起出粒子（见 handleEntityEvent）
            if (!this.level().isClientSide()) {
                this.level().broadcastEntityEvent(this, SPLAT_EVENT);
            }
            this.discard();
        }

        /** 自定的实体事件编号（原版雪球用 3，我们换个值避免语义混淆）。 */
        private static final byte SPLAT_EVENT = 4;

        /**
         * 处理实体事件 —— 粪块砸地"四溅炸开"的粒子在这里生成。
         *
         * <p>粒子用的是 {@code ItemParticleOption(ParticleTypes.ITEM, 粪便物品)}：
         * 也就是把物品贴图拍碎成小碎片喷出来，和雪球/鸡蛋碎掉是同一种粒子。
         * **不新增粒子类型**，所以不需要贴图、不需要在客户端注册粒子工厂，
         * 而且碎片自动带粪便自己的颜色。</p>
         *
         * <p>为什么要走实体事件而不是直接在 {@code onHitBlock} 里 addParticle：
         * {@code onHitBlock} 只在服务端跑到（投掷物的命中判定在服务端做），
         * 而 {@code addParticle} 只在客户端有视觉效果。走
         * {@code broadcastEntityEvent} 会把事件发给所有能看到这个实体的客户端，
         * 由客户端那份实体实例来生成粒子 —— 两端各司其职，这也是原版雪球的做法。</p>
         *
         * <p><b>命中方块和命中生物都用这一个事件</b>（原版雪球也是命中两者都发同一个
         * {@code handleEntityEvent(3)}），所以两种情况下粒子表现完全一致。</p>
         */
        @Override
        public void handleEntityEvent(byte id) {
            if (id != SPLAT_EVENT) {
                super.handleEntityEvent(id);
                return;
            }

            RandomSource random = this.level().getRandom();
            ParticleOptions splat = new ItemParticleOption(ParticleTypes.ITEM, this.getItem());

            // 炸开：粒子从命中点向四周 + 略微向上飞散
            for (int i = 0; i < 20; i++) {
                double dx = (random.nextDouble() - 0.5) * 0.5;
                double dy = random.nextDouble() * 0.4 + 0.05;
                double dz = (random.nextDouble() - 0.5) * 0.5;
                this.level().addParticle(splat, this.getX(), this.getY(), this.getZ(), dx, dy, dz);
            }
            // 贴地的一圈"溅开"
            for (int i = 0; i < 12; i++) {
                double angle = random.nextDouble() * Math.PI * 2.0;
                double speed = 0.15 + random.nextDouble() * 0.25;
                this.level().addParticle(splat, this.getX(), this.getY() + 0.02, this.getZ(),
                        Math.cos(angle) * speed, 0.02, Math.sin(angle) * speed);
            }
        }
    }

    private ModEntities() {
    }
}
