Add-Type -AssemblyName System.Drawing
$out = Join-Path $env:USERPROFILE 'Desktop\foodindustry_textures_blockbench_redrawn'
New-Item -ItemType Directory -Force $out | Out-Null
function Col($h){[Drawing.ColorTranslator]::FromHtml($h)}
function FillRect($g,$x,$y,$w,$h,$c){$b=New-Object Drawing.SolidBrush (Col $c);$g.FillRectangle($b,$x,$y,$w,$h);$b.Dispose()}
function SetPixel($g,$x,$y,$c){FillRect $g $x $y 1 1 $c}
function I($name,$kind,$base,$light,$accent){
 $bmp=New-Object Drawing.Bitmap 16,16; $g=[Drawing.Graphics]::FromImage($bmp); $g.SmoothingMode=0; $g.Clear([Drawing.Color]::Transparent); $e='#251d18'
 switch($kind){
  'round' {FillRect $g 3 5 10 8 $e;FillRect $g 4 4 8 9 $e;FillRect $g 5 5 6 7 $base;FillRect $g 6 5 3 2 $light;SetPixel $g 9 8 $accent;SetPixel $g 5 9 $accent}
  'fruit' {FillRect $g 2 6 12 7 $e;FillRect $g 4 4 8 9 $e;FillRect $g 4 6 8 6 $base;FillRect $g 5 5 4 2 $light;SetPixel $g 6 8 $accent;SetPixel $g 9 9 $accent;FillRect $g 7 2 2 3 '#4f8f3d';SetPixel $g 5 3 '#75ad52';SetPixel $g 10 3 '#75ad52'}
  'leaf' {FillRect $g 3 5 10 8 $e;FillRect $g 5 5 7 7 $base;FillRect $g 6 4 3 2 $light;FillRect $g 6 7 4 1 $accent;SetPixel $g 9 9 $light}
  'sushi' {FillRect $g 2 4 12 8 $e;FillRect $g 4 5 8 6 $base;FillRect $g 5 6 6 1 $light;FillRect $g 5 7 6 3 '#f4e8c4';SetPixel $g 7 8 $accent;SetPixel $g 10 6 $light}
  'slice' {FillRect $g 2 5 12 7 $e;FillRect $g 3 6 10 5 $base;FillRect $g 4 6 3 2 $light;SetPixel $g 7 8 $accent;SetPixel $g 10 7 $light}
  'tofu' {FillRect $g 2 3 12 9 $e;FillRect $g 3 4 10 7 $base;FillRect $g 4 5 4 2 $light;SetPixel $g 8 7 $accent;SetPixel $g 10 9 $light}
 }
 $g.Dispose(); $bmp.Save((Join-Path $out ($name+'.png')),[Drawing.Imaging.ImageFormat]::Png);$bmp.Dispose()
}
$defs=@{
 coffee=@('round','#5b321d','#a76b3a','#d78e49'); pineapple=@('fruit','#d19a26','#f2c33e','#a86d1e'); corn=@('fruit','#e3b52c','#f7d963','#a57720'); peanuts=@('round','#b9783e','#dfad6d','#8b522a'); soybeans=@('round','#8e914e','#c9c982','#666a36'); chili_pepper=@('fruit','#bc302d','#ef5a45','#8e2026'); blueberries=@('round','#3d4d98','#7892d2','#b6c9ee'); strawberries=@('fruit','#c8323d','#f05c62','#ffb06f'); tea_leaves=@('leaf','#4d873f','#91bd5c','#b7d47a'); cucumber=@('fruit','#3d823e','#7bab4f','#b6d86e'); red_grapes=@('round','#663a91','#ab72c6','#c394df'); white_grapes=@('round','#c9c37b','#f3e9ad','#fff2c1'); squid=@('slice','#606d9f','#a4b0d1','#cbd5e6'); squid_tentacles=@('leaf','#59658e','#a5b1d0','#d0d7e6'); guardian_meat=@('slice','#3e8a76','#82c4a6','#b3ddc6'); guardian_meat_slice=@('slice','#448d79','#8dc8aa','#dca06f'); pufferfish_slice=@('slice','#c58c2e','#efc85d','#fff0a0'); goat_milk_cheese=@('slice','#e8d39a','#fff2c7','#c5a96e'); tropical_fish_sushi=@('sushi','#d67837','#f0a75c','#74c0b2'); squid_sushi=@('sushi','#68739e','#9eaccb','#d7d0bb'); guardian_sushi=@('sushi','#4c927d','#8bc8a4','#d5d2ba'); pufferfish_sushi=@('sushi','#bf8b31','#e9c35d','#fff0a1'); tofu=@('tofu','#e4d5b5','#fff1d2','#bda982'); dried_tofu=@('tofu','#9c6d49','#d19a68','#6d4934')
}
foreach($d in $defs.GetEnumerator()){I $d.Key $d.Value[0] $d.Value[1] $d.Value[2] $d.Value[3]}
Set-Content (Join-Path $out 'README.txt') 'Detailed Blockbench-style 16x16 pixel art redraws. Transparent PNGs with layered shading and highlights.' -Encoding UTF8
Write-Output "Created $($defs.Count) detailed textures in $out"
