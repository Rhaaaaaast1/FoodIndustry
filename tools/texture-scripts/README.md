# 贴图工作脚本（历史遗留）

这里的三个 PowerShell 脚本是**早期**画 16×16 物品贴图时用的，用 `System.Drawing` 逐像素
`SetPixel`/`Rect` 拼出图形。它们当初的输出目录都在**桌面上**（`$env:USERPROFILE\Desktop\...`），
所以放在仓库哪个位置都能跑，移动不影响。

**当前的工作流程已经不用这些脚本了** —— 现在贴图由 `E:\dev\foodindustry-tools\` 下的一组
Python 脚本生成（`gen-recolored-items.py`、`gen-eggplant.py`、`gen-chili-sauce.py`、
`gen-bottle-recolor.py` 等），做法是拿现有贴图按色相/明度规则改色，比逐像素硬画好维护得多。

留着它们的唯一理由是保留"这些贴图当初是怎么来的"这段过程记录。

| 文件 | 作用 |
| --- | --- |
| `make_food_textures.ps1` | 最早的版本：按"农夫乐事风格"画一批物品贴图，输出到桌面 `foodindustry_textures_16x16_farmer_style` |
| `make_detailed_food_textures.ps1` | 第二版：加了分层明暗与高光，输出到桌面 `foodindustry_textures_detailed_16x16`（可用 `-DesktopOutput` 参数改目录） |
| `make_blockbench_redrawn.ps1` | 第三版：模仿 Blockbench 手绘风格的重新绘制，输出到桌面 `foodindustry_textures_blockbench_redrawn` |
| `cucumber.ong` | **扩展名写错了**，实际是 512×512 的 PNG（`89 50 4E 47` 开头）。黄瓜的参考图，与仓库里 16×16 的 `cucumber.png` 不是同源，应该是喂给 AI 的素材。 |

> 注意：这些脚本重复运行会**覆盖**桌面上的输出目录，不会碰仓库里的
> `src/main/resources/assets/foodindustry/textures/`。
