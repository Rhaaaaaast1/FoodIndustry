package dev.rhaaaaaast1.foodindustry.compat.registry;

import dev.rhaaaaaast1.foodindustry.common.registry.ModFluids;
import net.neoforged.fml.ModList;

import java.util.List;

/**
 * 兼容流体（放在 {@code compat} 包里，和本模组自己的流体分开维护）。
 *
 * <p><b>这两种都只注册流体，不做任何物品</b> —— 因为对方模组已经有对应的**物品**了：</p>
 * <ul>
 *   <li>番茄酱：Farmer's Delight 有 {@code farmersdelight:tomato_sauce}；</li>
 *   <li>紫颂酱：End's Delight 有 {@code ends_delight:chorus_sauce}。</li>
 * </ul>
 * <p>物品沿用它们的，我们只补上它们**没有的流体**（两者都完全没有流体系统）。
 * id 都在**我们自己的命名空间**下 ✓ —— 没有借用它们的命名空间，也没有使用它们的任何代码或素材。</p>
 *
 * <h2>可见性 / 可用性</h2>
 * <p>NeoForge 没有"条件注册"能力，注册表里漏掉一个 id 会让任何引用它的代码直接炸，所以这两种流体
 * <b>总是注册</b>。它们没有容器物品，因此本来就不会出现在创造物品栏里；真正决定"装了对方模组才
 * 提供内容"的是**配方上的 {@code neoforge:mod_loaded} 条件**（见 {@link #isEndsDelightLoaded()}）。</p>
 */
public final class CompatFluids {

    /** End's Delight 的模组 id。 */
    private static final String ENDS_DELIGHT = "ends_delight";

    /**
     * 番茄酱流体：本模组是 <b>Farmer's Delight 的附属</b>，而 FD 是**硬依赖**
     * （{@code neoforge.mods.toml} 里 {@code type="required"}），所以它无需任何条件判断。
     */
    public static final ModFluids.MilkLike TOMATO_SAUCE = ModFluids.compatFluidOnly("tomato_sauce");

    /** 紫颂酱流体：只有装了 End's Delight 才应该提供内容（配方侧加 {@code mod_loaded} 条件）。 */
    public static final ModFluids.MilkLike CHORUS_SAUCE = ModFluids.compatFluidOnly("chorus_sauce");

    /** 本模块注册的全部流体。 */
    public static final List<ModFluids.MilkLike> ALL = ModFluids.compatAll();

    /** 类初始化钩子：主类在注册注册表之前调用，确保上面的流体已排队。 */
    public static void bootstrap() {
        // 空方法即可 —— 调用它就足够触发这个类的静态初始化。
    }

    /** End's Delight 是否已安装（配方条件等地方用）。 */
    public static boolean isEndsDelightLoaded() {
        return ModList.get().isLoaded(ENDS_DELIGHT);
    }

    private CompatFluids() {
    }
}
