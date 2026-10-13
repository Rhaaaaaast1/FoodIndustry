param(
    [string]$DesktopOutput = ''
)

Add-Type -AssemblyName System.Drawing

$requestedOutput = if ([string]::IsNullOrWhiteSpace($DesktopOutput)) {
    Join-Path $env:USERPROFILE 'Desktop\foodindustry_textures_detailed_16x16'
} else { $DesktopOutput }
$DesktopOutput = $requestedOutput
$baseOutput = $DesktopOutput
$suffix = 2
while (Test-Path -LiteralPath $DesktopOutput) {
    $DesktopOutput = "${baseOutput}_$suffix"
    $suffix++
}
New-Item -ItemType Directory -Path $DesktopOutput | Out-Null

function Hex([string]$value) {
    return [Drawing.ColorTranslator]::FromHtml($value)
}

function New-Palette([string]$base, [string]$shadow, [string]$light, [string]$highlight,
                     [string]$accent = '#ffffff', [string]$leaf = '#4f873c',
                     [string]$outline = '#2b2524') {
    return @{
        'o' = (Hex $outline); 'd' = (Hex $shadow); 's' = (Hex $shadow)
        'b' = (Hex $base); 'm' = (Hex $base); 'l' = (Hex $light)
        'h' = (Hex $highlight); 'a' = (Hex $accent); 'g' = (Hex $leaf)
        'w' = (Hex '#f3e5c7'); 'r' = (Hex '#efe1bd'); 'n' = (Hex '#1d3430')
        'f' = (Hex '#d98a45'); 'k' = (Hex '#16191d'); 'p' = (Hex '#8a4d68')
    }
}

function Pad-Row([string]$row) {
    if ($row.Length -gt 16) { return $row.Substring(0,16) }
    return $row.PadRight(16, '.')
}

function Add-Sprite([hashtable]$table, [string]$name, [string[]]$rows, [hashtable]$palette) {
    if ($rows.Count -ne 16) { throw "$name must have 16 rows" }
    $table[$name] = @{ Rows = @($rows | ForEach-Object { Pad-Row $_ }); Palette = $palette }
}

$sprites = @{}

# Farmer's Delight reference traits used here: near-black one-pixel contour,
# compact silhouettes, warm top-left highlights, and two or three material tones.
Add-Sprite $sprites 'coffee' @(
    '........h.......','.......l........','......oo........','.....obbo.......','....obbbbo......','...obllbbbo.....','...obbbbbbo.....','...obbbbbbo.....','....obbbbo......','.....oooo.......','....oooooo......','...oobbbbboo....','....oobbo.......','................','................','................'
) (New-Palette '#75452c' '#4b2a20' '#b9784c' '#e1b276' '#f0d29b' '#4d7d3a')

Add-Sprite $sprites 'pineapple' @(
    '.......g........','......ggg.......','.....ggggg......','....ggg.ggg.....','....oobbo.......','...obbbbo.......','..obabbbbo......','..obbbabbo......','..obabbbbo......','...obbbbo.......','...oobbo........','....oooo........','................','................','................','................'
) (New-Palette '#d39a25' '#91651d' '#edc348' '#ffe584' '#9b681e' '#4b9142')

Add-Sprite $sprites 'corn' @(
    '......g.........','.....ggg........','....ggbg........','...ggbbbo.......','...gbabboo......','...gobbbboo.....','...gobabboo.....','...gobbaboo.....','...gobbbboo.....','....gbbbbo......','.....ggg........','......g.........','................','................','................','................'
) (New-Palette '#d7a829' '#96721f' '#edcf56' '#fff09b' '#a67a1e' '#4e873e')

Add-Sprite $sprites 'peanuts' @(
    '.......g........','.....oobbbo.....','....obllbbo.....','....obbbdbo.....','.....obbo.......','...oobbbo.......','..obllbbbo......','..obbbdbo.......','....obbo........','.......oobbbo...','......obllbbo...','......obbbdbo...','.......obbo.....','................','................','................'
) (New-Palette '#b9793f' '#774a2b' '#dba66a' '#f3ca8d' '#8d522a' '#657d3e')

Add-Sprite $sprites 'soybeans' @(
    '....oobbo......','...obllbbo.....','...obbbdbo.....','....obbo.......','......oobbo.....','.....obllbbo....','.....obbbdbo....','......obbo......','..oobbo.........','.obllbbo........','.obbbdbo........','..obbo..........','................','................','................','................'
) (New-Palette '#98974b' '#5f6133' '#c6c879' '#e5e3a2' '#6f7139' '#63853e')

Add-Sprite $sprites 'chili_pepper' @(
    '.......gg.......','......ggg.......','.......g........','.....oob.........','...oobbbb.......','..obbbbbbo......','..obabbbbo......','...obbbbo.......','....obbbbo......','.....obbo.......','......oo........','................','................','................','................','................'
) (New-Palette '#bd302d' '#731f25' '#e75b42' '#ff9470' '#f2c55d' '#4b8d3d')

Add-Sprite $sprites 'blueberries' @(
    '......g.........','...oobbo........','..obllbbo.......','..obbbdbo.......','...obbo.........','........oobbo...','.......obllbbo..','.......obbbdbo..','........obbo....','..oobbo.........','.obllbbo........','.obbbdbo........','..obbo..........','................','................','................'
) (New-Palette '#3e4c98' '#292d65' '#7188c9' '#b4c9f0' '#d5dff7' '#4d813d')

Add-Sprite $sprites 'strawberries' @(
    '....g..g........','...gggggg.......','....g..g........','..oobbbbbo......','.obabbbabbo.....','obbbbbbbbbo.....','obbbabbbdbo.....','.obbbbbdbo......','..obbbbo........','......g.........','.....ggg........','......g.........','................','................','................','................'
) (New-Palette '#c83240' '#7e222f' '#ed5c63' '#ffab88' '#f4d06c' '#3f873c')

Add-Sprite $sprites 'tea_leaves' @(
    '.......g........','.....oobbo......','....obllbbo.....','...obbbdbo......','..oobbo.........','....oobbo.......','...obllbbo......','...obbbdbo......','.....obbo.......','...oobbo........','..obllbbo.......','..obbbdbo.......','...obbo.........','................','................','................'
) (New-Palette '#4c823d' '#2f5b32' '#82ae51' '#b9d378' '#d5df95' '#6e9e45')

Add-Sprite $sprites 'cucumber' @(
    '........g.......','......oogg.......','....oobbbbo......','...obllbbbo.....','...obbbbbbo.....','....obabbo......','.....obbo.......','......obbo......','.......obbo.....','........obbo....','.........oo.....','................','................','................','................','................'
) (New-Palette '#3d833f' '#285832' '#76a84d' '#b8d86b' '#d4e48d' '#4b8f43')

Add-Sprite $sprites 'red_grapes' @(
    '.......g........','......ggg.......','.....oobbbo.....','....obllbbbo....','....obbbdbo.....','.....obbo.......','..oobbbo........','.obllbbbo.......','.obbbdbo........','..obbo..........','.....oobbbo.....','....obllbbbo....','....obbbdbo.....','.....obbo.......','................','................'
) (New-Palette '#663c91' '#3e245e' '#9a62bd' '#d1a0df' '#e4c4e9' '#47823d')

Add-Sprite $sprites 'white_grapes' @(
    '.......g........','......ggg.......','.....oobbo......','....obllbbo.....','....obbbdbo.....','.....obbo.......','..oobbo.........','.obllbbo........','.obbbdbo........','..obbo..........','.....oobbo......','....obllbbo.....','....obbbdbo.....','.....obbo.......','................','................'
) (New-Palette '#c9c27a' '#817843' '#e4dc9b' '#fff4c7' '#fffbe0' '#54873e')

Add-Sprite $sprites 'squid' @(
    '.......oo.......','.....oobboo.....','....obllbbbo....','...obbbbbbbbo...','..obbbabbbbbo...','..obbbbbbdbbo...','...obbbbbbo.....','....obbbbbo.....','...oobbbboo.....','..obbbobbbbo....','.obbbo..obbbo...','..oob....oob....','................','................','................','................'
) (New-Palette '#5d6c9a' '#38415f' '#91a3ca' '#c8d7ea' '#dfb7c7' '#4a7d7d')

Add-Sprite $sprites 'squid_tentacles' @(
    '....oobbbbbo....','...obllbbllbo...','...obbbbbbbo....','....obbbbbo.....','...oobbo.oob....','..obbo...obbo...','..obbo...obbo...','.obbo.....obbo..','obbo.......obbo.','oob.........oob.','................','................','................','................','................','................'
) (New-Palette '#59678f' '#343d5c' '#8d9ec3' '#c8d4e8' '#dce4f1' '#4d7c88')

Add-Sprite $sprites 'guardian_meat' @(
    '....oobbbbbo....','...obllbbllbo...','..obbbabbbbbo...','..obbbbbbbdbo...','...obbbbbo......','....obbbbo......','.....obbo.......','....oobbo.......','...obbbdbo......','...obllllbo.....','....obbbbo......','................','................','................','................','................'
) (New-Palette '#418a79' '#28564f' '#7fc3a4' '#bee7c9' '#e1b36a' '#3f9a78')

Add-Sprite $sprites 'guardian_meat_slice' @(
    '....oobbbbbo....','...obllllbbo....','..obbbbdbbbo....','..obbbabbbo.....','...obbbbo.......','....obbo........','.....oo.........','...oobbo........','..obllbbo.......','..obbbdbo.......','...obbo.........','................','................','................','................','................'
) (New-Palette '#478f7b' '#2d5c51' '#8fc9aa' '#d3edcf' '#d5a06b' '#3f9a78')

Add-Sprite $sprites 'cooked_guardian_meat_slice' @(
    '....oobbbbbo....','...obllllbbo....','..obbbadbbbo....','..obbbabbbo.....','...obbbbo.......','....obbo........','.....oo.........','...oobbo........','..obllbbo.......','..obbbdbo.......','...obbo.........','................','................','................','................','................'
) (New-Palette '#8c5b3c' '#543529' '#bf8253' '#e9bd79' '#f0d28f' '#5e8b5a')

Add-Sprite $sprites 'pufferfish_slice' @(
    '....oobbbbbo....','...obllabllbo...','..obbbbbbbbbo...','..obabbbabdbo...','...obbbbbo......','....obbbbo......','.....obbo.......','....oobbo.......','...obllbbo......','...obbbdbo......','....obbo........','................','................','................','................','................'
) (New-Palette '#c4872e' '#7c4d24' '#e9b84e' '#fff0a0' '#e65b36' '#4b893c')

Add-Sprite $sprites 'goat_milk_cheese' @(
    '.....oooo.......','....obbbbbo.....','...obllllbbo....','..obbbabbbbbo...','..obbbbbbbdbo...','...obbbbbbo.....','....obbbbo......','.....obbo.......','................','................','................','................','................','................','................','................'
) (New-Palette '#e3cf96' '#9e8250' '#f7e8b9' '#fff8db' '#bd9f5f' '#6e8b4b')

function Add-Sushi([hashtable]$table, [string]$name, [string]$fishBase, [string]$fishLight, [string]$accent) {
    $palette = New-Palette '#2c2827' '#1b2022' '#6c6a61' '#a7a493' $accent '#34705f'
    $palette['n'] = Hex '#152e2c'; $palette['r'] = Hex '#f0e3bd'; $palette['f'] = Hex $fishBase; $palette['p'] = Hex $fishLight
    Add-Sprite $table $name @(
        '......oooo......','....oonnnno.....','...onnnnnnno....','..onnrrrrnnno...','..onrrrprrrno...','.onrrrfffrrno...','.onrrffaffrno...','..onrrrprrno....','..onnrrrrnno....','...onnnnnno.....','....oonnoo.....','......oooo......','................','................','................','................'
    ) $palette
}
Add-Sushi $sprites 'tropical_fish_sushi' '#d8793e' '#f1a15d' '#69b6a2'
Add-Sushi $sprites 'squid_sushi' '#67749f' '#a8b6d3' '#dbd1b5'
Add-Sushi $sprites 'guardian_sushi' '#4a927c' '#8dc7a3' '#d5d2ba'
Add-Sushi $sprites 'pufferfish_sushi' '#c58a2f' '#e8be53' '#f6e58e'

Add-Sprite $sprites 'tofu' @(
    '....oooooooo....','...obllllllbo...','..obbbbbbbbbbo...','..obbbabbbbdbo...','..obbbbbbbbbbo...','...obllllllbo....','....obbbbbo.....','.....obbbbo.....','......obbo.......','................','................','................','................','................','................','................'
) (New-Palette '#dfd1b0' '#9f8c6d' '#f5e8c7' '#fff9e6' '#b69661' '#789052')

Add-Sprite $sprites 'dried_tofu' @(
    '....oooooooo....','...obllllllbo...','..obbbbbbbbbbo...','..obbbabbbbdbo...','..obbbbbbbbbbo...','...obllllllbo....','....obbbbbo.....','.....obbbbo.....','......obbo.......','................','................','................','................','................','................','................'
) (New-Palette '#9d6c48' '#5e3d30' '#cf9968' '#e8bd83' '#6e4331' '#6f8147')

# Additional code-facing variants and processing stages.
Add-Sprite $sprites 'coffee_cherries' @(
    '.......g........','.....oobbbo.....','....obllbbbo....','....obbbdbo.....','.....obbo.......','...oobbbo.......','..obllbbbo......','..obbbdbo.......','...obbo.........','........oobbbo..','.......obllbbbo.','.......obbbdbo..','........obbo....','................','................','................'
) (New-Palette '#8f2f34' '#55232d' '#c9534b' '#ef9a67' '#f3c25e' '#4e883e')
Add-Sprite $sprites 'raw_coffee_beans' @(
    '....oobbo......','...obllbbo.....','...obbbdbo.....','....obbo.......','......oobbo.....','.....obllbbo....','.....obbbdbo....','......obbo......','..oobbo.........','.obllbbo........','.obbbdbo........','..obbo..........','................','................','................','................'
) (New-Palette '#6f432a' '#3e281f' '#aa7046' '#d9a06b' '#f0ca8d' '#557b3e')
Add-Sprite $sprites 'roasted_coffee_beans' @(
    '....oobbo......','...obllbbo.....','...obbbdbo.....','....obbo.......','......oobbo.....','.....obllbbo....','.....obbbdbo....','......obbo......','..oobbo.........','.obllbbo........','.obbbdbo........','..obbo..........','................','................','................','................'
) (New-Palette '#4c2a1d' '#2a1b18' '#79503a' '#b87a4e' '#e0a36b' '#557b3e')
Add-Sprite $sprites 'coffee_powder' @(
    '.....oooo.......','....obbbbbo.....','...obllllbbo....','..obbbbbbdbbo...','..obbbabbbbo....','...obbbbbbo.....','....obbbbo......','.....oooo.......','................','................','................','................','................','................','................','................'
) (New-Palette '#503023' '#2e1d1a' '#7d4e37' '#b77950' '#dca16b' '#557b3e')

$rendered = @{}
foreach ($entry in $sprites.GetEnumerator()) {
    $name = $entry.Key; $data = $entry.Value
    $bmp = New-Object Drawing.Bitmap 16,16,([Drawing.Imaging.PixelFormat]::Format32bppArgb)
    for ($y=0; $y -lt 16; $y++) {
        $row = $data.Rows[$y]
        for ($x=0; $x -lt 16; $x++) {
            $ch = $row[$x]
            if ($ch -eq '.') { $bmp.SetPixel($x,$y,[Drawing.Color]::FromArgb(0,0,0,0)); continue }
            if (-not $data.Palette.ContainsKey([string]$ch)) { throw "Unknown palette key '$ch' in $name" }
            $bmp.SetPixel($x,$y,$data.Palette[[string]$ch])
        }
    }
    $outPath = Join-Path $DesktopOutput ($name + '.png')
    $bmp.Save($outPath,[Drawing.Imaging.ImageFormat]::Png)
    $rendered[$name] = $bmp.Clone()
    $bmp.Dispose()
}

# Make a nearest-neighbor review board for visual inspection.
$names = @($rendered.Keys | Sort-Object)
$cols = 6; $cellW = 96; $cellH = 116
$board = New-Object Drawing.Bitmap ($cols*$cellW),([math]::Ceiling($names.Count/$cols)*$cellH)
$g = [Drawing.Graphics]::FromImage($board)
$g.Clear((Hex '#202020'))
$g.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$g.PixelOffsetMode = [Drawing.Drawing2D.PixelOffsetMode]::Half
$font = New-Object Drawing.Font('Arial',8)
for ($i=0; $i -lt $names.Count; $i++) {
    $x = ($i % $cols) * $cellW; $y = [math]::Floor($i/$cols) * $cellH
    $g.DrawImage($rendered[$names[$i]],$x+16,$y+4,80,80)
    $g.DrawString($names[$i],$font,[Drawing.Brushes]::White,$x+3,$y+88)
}
$font.Dispose(); $g.Dispose()
$board.Save((Join-Path $DesktopOutput 'preview.png'),[Drawing.Imaging.ImageFormat]::Png)
$board.Dispose()
foreach ($bmp in $rendered.Values) { $bmp.Dispose() }

$readme = @"
Food Industry detailed 16x16 pixel textures
============================================
Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
Reference: libs/FarmersDelight-1.21.1-1.3.4.jar (actual item sprites inspected locally)
Style: native 16x16 grid, one-pixel contour, compact silhouette, warm top-left lighting,
       layered material shading and small pixel accents. No scaling, blur or antialiasing.

The folder contains standalone PNGs plus preview.png. The project directory is never
modified by this script. Requested sushi, cheese and tofu IDs are included as standalone
textures even when the current Java registry does not yet declare those items.
If the requested output folder already exists, a numbered new folder is created
automatically so existing files are preserved.
"@
Set-Content (Join-Path $DesktopOutput 'README.txt') $readme -Encoding UTF8
Write-Output "Generated $($rendered.Count) textures in $DesktopOutput"
