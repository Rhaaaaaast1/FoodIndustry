package dev.rhaaaaaast1.foodindustry.common;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

/**
 * 全部食物数值集中在这里。
 *
 * <p>布局参照农夫乐事的 {@code common/FoodValues.java}：注册表里只写
 * {@code food("corn", FoodValues.VEGETABLE)}，不在这里之外出现
 * {@code new FoodProperties.Builder()}。</p>
 *
 * <p><b>每个数值都标了出处。</b>改数值时请连出处一起改，不要凭感觉调 ——
 * 这些数字要么对齐原版、要么对齐某个参照模组，改之前先确认对齐对象是否也变了。</p>
 *
 * <p>⚠️ 容易搞错的一点：{@code saturationModifier} 是**修饰值**，不是最终饱和度。
 * 真实饱和度 = {@code nutrition × modifier × 2}（见 {@code FoodConstants.saturationByModifier}）。</p>
 */
public final class FoodValues {

    // ------------------------------------------------------------------
    //  基础档（作物产物）
    // ------------------------------------------------------------------

    /** 蔬菜：2 点 / 0.3。 */
    public static final FoodProperties VEGETABLE = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.3F).build();

    /** 水果：2 点 / 0.2。 */
    public static final FoodProperties FRUIT = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.2F).build();

    /** 浆果：1 点 / 0.1。 */
    public static final FoodProperties BERRY = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.1F).build();

    /**
     * 嗑着玩的种籽零食（葵花籽）：营养价值极低但吃得快。
     * 出处：对齐原版最寒酸的那档（干海带 1 点 / 0.1，且 {@code fast()}）。
     */
    public static final FoodProperties SEED_SNACK = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.1F).fast().build();

    // ------------------------------------------------------------------
    //  海产物
    // ------------------------------------------------------------------

    /**
     * 生鱼片：1 点 / 0.1，且 {@code fast()}。
     * 出处：农夫乐事 {@code FoodValues.COD_SLICE} / {@code SALMON_SLICE}（两者数值相同）。
     */
    public static final FoodProperties RAW_FISH_SLICE = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.1F).fast().build();

    /**
     * 熟鱼片：3 点 / 0.5，且 {@code fast()}。
     * 出处：农夫乐事 {@code FoodValues.COOKED_COD_SLICE}
     * （熟三文鱼片是 3 / 0.8，这里取保守的那一档）。
     */
    public static final FoodProperties COOKED_FISH_SLICE = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.5F).fast().build();

    /** 守卫者肉（整块）：3 点 / 0.3。出处：**原版生牛肉** {@code Foods.BEEF}。 */
    public static final FoodProperties GUARDIAN_MEAT = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.3F).build();

    /**
     * 生河豚肉片：1 点 / 0.1 + {@code fast()}，并**保留原版河豚的毒性**。
     *
     * <p>饥饿值出处同 {@link #RAW_FISH_SLICE}；毒性是额外保留的机制（原版河豚本身有毒）。
     * 嫌麻烦可以删掉下面那行 {@code effect(...)}，删掉后行为就与农夫乐事的生鱼片完全一致。</p>
     */
    public static final FoodProperties PUFFERFISH_SLICE_RAW = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.1F).fast()
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 300, 0), 1.0F)
            .build();

    // ------------------------------------------------------------------
    //  蛋糕线
    // ------------------------------------------------------------------

    /**
     * 蛋糕切片：2 点 / 0.5（真实饱和度 2.0）、{@code fast()}。
     * 出处：KubeJS 参照实现里的 {@code foodBuilder.saturation(0.5)}。
     *
     * <p>注意原版蛋糕片的修饰值是 {@code 0.1}（真实饱和度 0.4），
     * 这里刻意用 0.5 —— 是参照实现的原意，不是笔误。</p>
     */
    public static final FoodProperties CAKE_SLICE = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.5F).fast().build();

    // ------------------------------------------------------------------
    //  派线
    // ------------------------------------------------------------------

    /**
     * 派切片：3 点 / 0.3、{@code fast()}，**不带效果**。
     * 出处：农夫乐事 {@code FoodValues.PIE_SLICE}（3 / 0.3 + fast），
     * 但按需求**去掉了它的速度效果**。
     */
    public static final FoodProperties PIE_SLICE = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.3F).fast().build();

    // ------------------------------------------------------------------
    //  与 Create 联动的两件
    // ------------------------------------------------------------------

    /** 蜜渍浆果：7 点 / 0.8（真实饱和度 11.2）。出处：KubeJS 参照实现的 {@code kubejs:honeyed_berries}。 */
    public static final FoodProperties HONEYED_BERRIES = new FoodProperties.Builder()
            .nutrition(7).saturationModifier(0.8F).build();

    /** 巧克力包层苹果：8 点 / 0.8125（真实饱和度 13.0）。出处：KubeJS 参照实现的 {@code kubejs:chocolate_glazed_apple}。 */
    public static final FoodProperties CHOCOLATE_GLAZED_APPLE = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.8125F).build();

    // ------------------------------------------------------------------
    //  玩梗物品
    // ------------------------------------------------------------------

    /**
     * 士腻架：6 点 / 0.8。出处：Create {@code AllItems.SWEET_ROLL}（甜甜卷）。
     *
     * <p>{@code alwaysEdible()} 是玩梗需求"饿不饿都能吃"，与饥饿值无关。</p>
     */
    public static final FoodProperties SLICKERS = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.8F).alwaysEdible().build();

    private FoodValues() {
    }
}
