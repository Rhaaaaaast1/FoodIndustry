Add-Type -AssemblyName System.Drawing
$out = Join-Path $env:USERPROFILE 'Desktop\foodindustry_textures_16x16_farmer_style'
New-Item -ItemType Directory -Force $out | Out-Null
$items = @{
 coffee=@('bean','#4a2a18','#a96b3b'); pineapple=@('fruit','#d79b24','#70a94a'); corn=@('corn','#e1b52f','#75a448'); peanuts=@('nut','#b8793d','#e6b476'); soybeans=@('bean','#8d9250','#d7d39b'); chili_pepper=@('pepper','#bd302c','#71a247'); blueberries=@('berry','#3b4d99','#8da4df'); strawberries=@('berry','#c9333e','#76a84a'); tea_leaves=@('leaf','#4d843e','#a9ca70'); cucumber=@('cuke','#3c813d','#a7c86c'); red_grapes=@('grape','#673c96','#b07bce'); white_grapes=@('grape','#d7cf8a','#f3e9ae'); squid=@('sea','#64709f','#b3bdd8'); squid_tentacles=@('tentacle','#59648c','#a5b1d0'); guardian_meat=@('meat','#438f7c','#a5d1bc'); guardian_meat_slice=@('slice','#438f7c','#d3a06d'); pufferfish_slice=@('slice','#c28a27','#ecd06e'); goat_milk_cheese=@('cheese','#e9d9a6','#fff3cf'); tropical_fish_sushi=@('sushi','#d97839','#75c2ba'); squid_sushi=@('sushi','#65719e','#d9cfb8'); guardian_sushi=@('sushi','#4b927e','#d9cfb8'); pufferfish_sushi=@('sushi','#bf892f','#d9cfb8'); tofu=@('tofu','#e6d9bb','#fff6dd'); dried_tofu=@('tofu','#9d6d48','#d2a16e')
}
function C($hex){ return [Drawing.ColorTranslator]::FromHtml($hex) }
function Put($bmp,$x,$y,$hex){ if($x -ge 0 -and $x -lt 16 -and $y -ge 0 -and $y -lt 16){$bmp.SetPixel($x,$y,(C $hex))} }
function Rect($bmp,$x0,$y0,$x1,$y1,$hex){ for($y=$y0;$y -le $y1;$y++){for($x=$x0;$x -le $x1;$x++){Put $bmp $x $y $hex}} }
function Draw($bmp,$kind,$base,$hi){
  $edge='#231e1a';
  switch($kind){
    'bean' { Rect $bmp 4 4 11 11 $edge; Rect $bmp 5 4 10 10 $base; Rect $bmp 6 5 7 10 $hi; Rect $bmp 8 6 9 7 $hi }
    'fruit' { Rect $bmp 3 6 12 11 $edge; Rect $bmp 4 5 11 11 $base; Rect $bmp 5 4 9 5 $base; Rect $bmp 7 3 8 4 $hi; Rect $bmp 6 7 10 8 $hi; Rect $bmp 4 9 5 10 $hi }
    'corn' { Rect $bmp 4 3 11 12 $edge; Rect $bmp 5 4 10 11 $base; Rect $bmp 6 5 7 10 $hi; Rect $bmp 9 4 10 6 $hi; Rect $bmp 3 2 4 7 $hi; Rect $bmp 11 2 12 7 $hi }
    'nut' { Rect $bmp 4 3 11 12 $edge; Rect $bmp 5 4 10 11 $base; Rect $bmp 6 6 7 9 $hi; Rect $bmp 8 5 9 6 $hi }
    'berry' { Rect $bmp 4 5 11 11 $edge; Rect $bmp 5 4 10 10 $base; Rect $bmp 6 5 7 6 $hi; Rect $bmp 8 8 9 9 $hi; Rect $bmp 7 3 8 4 $hi }
    'leaf' { Rect $bmp 4 5 11 11 $edge; Rect $bmp 5 5 10 10 $base; Rect $bmp 6 4 8 5 $hi; Rect $bmp 6 7 8 8 $hi; Rect $bmp 9 6 10 7 $hi }
    'cuke' { Rect $bmp 3 4 11 12 $edge; Rect $bmp 4 5 10 11 $base; Rect $bmp 5 6 6 7 $hi; Rect $bmp 8 8 9 9 $hi; Rect $bmp 4 10 5 10 $hi }
    'grape' { Rect $bmp 4 5 11 11 $edge; Rect $bmp 5 5 10 10 $base; Rect $bmp 6 4 7 5 $hi; Rect $bmp 8 7 9 8 $hi; Rect $bmp 7 3 8 4 $hi }
    'sea' { Rect $bmp 3 4 12 11 $edge; Rect $bmp 4 5 11 10 $base; Rect $bmp 5 6 7 7 $hi; Rect $bmp 9 8 10 9 $hi; Rect $bmp 6 4 8 5 $hi }
    'tentacle' { Rect $bmp 3 4 12 9 $edge; Rect $bmp 4 5 11 8 $base; Rect $bmp 4 9 5 12 $base; Rect $bmp 7 9 8 12 $base; Rect $bmp 10 9 11 12 $base; Rect $bmp 5 5 6 6 $hi }
    'meat' { Rect $bmp 3 5 12 10 $edge; Rect $bmp 4 6 11 9 $base; Rect $bmp 5 7 7 8 $hi; Rect $bmp 9 6 10 7 $hi }
    'slice' { Rect $bmp 2 5 13 10 $edge; Rect $bmp 3 6 12 9 $base; Rect $bmp 5 6 6 7 $hi; Rect $bmp 8 8 9 9 $hi; Rect $bmp 11 6 12 7 $hi }
    'cheese' { Rect $bmp 3 4 12 11 $edge; Rect $bmp 4 5 11 10 $base; Rect $bmp 6 6 7 7 $hi; Rect $bmp 9 8 10 9 $hi }
    'sushi' { Rect $bmp 2 4 13 11 $edge; Rect $bmp 4 5 11 10 $base; Rect $bmp 5 6 10 9 '#f3e7c4'; Rect $bmp 6 7 7 8 $hi; Rect $bmp 9 6 10 7 $hi }
    'tofu' { Rect $bmp 2 3 13 11 $edge; Rect $bmp 3 4 12 10 $base; Rect $bmp 5 5 6 6 $hi; Rect $bmp 9 8 10 9 $hi; Rect $bmp 7 4 9 4 $hi }
  }
}
foreach($kv in $items.GetEnumerator()){
  $bmp=New-Object Drawing.Bitmap 16,16
  for($y=0;$y -lt 16;$y++){for($x=0;$x -lt 16;$x++){$bmp.SetPixel($x,$y,[Drawing.Color]::Transparent)}}
  Draw $bmp $kv.Value[0] $kv.Value[1] $kv.Value[2]
  $bmp.Save((Join-Path $out ($kv.Key+'.png')),[Drawing.Imaging.ImageFormat]::Png); $bmp.Dispose()
}
Set-Content (Join-Path $out 'README.txt') '16x16 original pixel art icons, inspired by Farmer''s Delight item readability. Transparent PNGs; no source textures copied.' -Encoding UTF8
Write-Output "Created $($items.Count) textures in $out"
