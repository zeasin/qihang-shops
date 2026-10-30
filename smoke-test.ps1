$ErrorActionPreference = 'Continue'
$base = 'http://127.0.0.1:18098'
$script:pass = 0; $script:fail = 0
function Check($name, $cond, $extra) {
    if ($cond) { Write-Output ("  PASS  " + $name + "  " + $extra); $script:pass++ }
    else { Write-Output ("  FAIL  " + $name + "  " + $extra); $script:fail++ }
}
function Sql($q) {
    $o = docker exec my-mysql8 mysql -uroot -pAndy_123 -N -e $q 2>$null | Out-String
    if (-not $o) { return '' }
    $lines = $o -split "`n" | Where-Object { $_ -and $_ -notmatch 'Warning' }
    if (-not $lines) { return '' }
    return ($lines | Select-Object -First 1).Trim()
}

Write-Output '=== 1. login ==='
$loginBody = @{ username='admin'; password='admin123'; code=''; uuid='' } | ConvertTo-Json
$login = Invoke-RestMethod -Uri "$base/login" -Method Post -Body $loginBody -ContentType 'application/json'
$token = $login.token
Check 'login' ($login.code -eq 200 -and $token) ("code=" + $login.code)
if (-not $token) { Write-Output 'login failed, abort'; exit 1 }
$hdr = @{ Authorization = "Bearer $token" }

Write-Output ''
Write-Output '=== 2. dynamic routers ==='
$routers = Invoke-RestMethod -Uri "$base/getRouters" -Headers $hdr
$json = ($routers.data | ConvertTo-Json -Depth 12 -Compress)
Check 'getRouters' ($routers.code -eq 200 -and $json.Length -gt 50)
Check 'shop/index'            ($json -match 'shop/index')
Check 'shop/platform/index'   ($json -match 'shop/platform/index')
Check 'order/index'           ($json -match 'order/index')
Check 'profit/order'          ($json -match 'profit/order')

Write-Output ''
Write-Output '=== 3. @PreAuthorize protected endpoints ==='
foreach ($t in @(
    @{n='/shop/list';             u="$base/shop/list"},
    @{n='/order/list';            u="$base/order/list"},
    @{n='/system/menu/list';      u="$base/system/menu/list"},
    @{n='/system/dict/data/list'; u="$base/system/dict/data/list"},
    @{n='/system/dict/type/list'; u="$base/system/dict/type/list"},
    @{n='/goods/category/list';   u="$base/goods/category/list"},
    @{n='/goodsCost/skuList';     u="$base/goodsCost/skuList"},
    @{n='/profit/order';          u="$base/profit/order"},
    @{n='/profit/link';           u="$base/profit/link"},
    @{n='/profit/shop';           u="$base/profit/shop"},
    @{n='/profit/completeness';   u="$base/profit/completeness"},
    @{n='/report/todayDaily';     u="$base/report/todayDaily"},
    @{n='/report/salesDaily';     u="$base/report/salesDaily"},
    @{n='/workOrder/list';        u="$base/workOrder/list"},
    @{n='/promotionFee/list';     u="$base/promotionFee/list"},
    @{n='/fixedFee/list';         u="$base/fixedFee/list"},
    @{n='/platformBill/list';     u="$base/platformBill/list"},
    @{n='/orderImport/template/list'; u="$base/orderImport/template/list"},
    @{n='/system/operlog/list';   u="$base/system/operlog/list"},
    @{n='/system/user/profile';   u="$base/system/user/profile"}
)) {
    try {
        $r = Invoke-RestMethod -Uri $t.u -Headers $hdr -TimeoutSec 20
        Check $t.n ($r.code -eq 200) ("code=" + $r.code)
    } catch { Check $t.n $false ($_.Exception.Message) }
}

Write-Output ''
Write-Output '=== 4. shop platform endpoints ==='
try {
    $r = Invoke-RestMethod -Uri "$base/shop/platformList" -Headers $hdr
    $cnt = @($r.rows).Count
    Check '/shop/platformList' ($r.code -eq 200 -and $cnt -ge 4) ("rows=" + $cnt)
    if ($cnt -gt 0) {
        $platId = $r.rows[0].id
        $r2 = Invoke-RestMethod -Uri "$base/shop/platform/$platId" -Headers $hdr
        Check "/shop/platform/$platId" ($r2.code -eq 200 -and $r2.data.code) ("code=" + $r2.data.code)
        $upd = @{ id=$platId; name=$r2.data.name; code=$r2.data.code; appKey='test-key'; appSecret='test-secret' } | ConvertTo-Json
        $r3 = Invoke-RestMethod -Uri "$base/shop/platform" -Method Put -Headers $hdr -Body $upd -ContentType 'application/json'
        Check '/shop/platform PUT' ($r3.code -eq 200) ("code=" + $r3.code)
    }
} catch { Check '/shop/platform*' $false ($_.Exception.Message) }

Write-Output ''
Write-Output '=== 5. export endpoints ==='
foreach ($t in @(
    @{n='/goods/category/export';  u="$base/goods/category/export"},
    @{n='/system/dict/type/export'; u="$base/system/dict/type/export"},
    @{n='/system/dict/data/export'; u="$base/system/dict/data/export"},
    @{n='/profit/export';           u="$base/profit/export"}
)) {
    try {
        $out = Join-Path $env:TEMP ("qh_" + [guid]::NewGuid().ToString('N') + ".xlsx")
        Invoke-WebRequest -Uri $t.u -Method Post -Headers $hdr -OutFile $out -TimeoutSec 30 | Out-Null
        $len = (Get-Item $out).Length
        $magic = [System.IO.File]::ReadAllBytes($out)[0..1] -join ','
        Check $t.n ($len -gt 1000 -and $magic -eq '80,75') ("size=" + $len + " magic=" + $magic)
        Remove-Item $out -Force
    } catch { Check $t.n $false ($_.Exception.Message) }
}

Write-Output ''
Write-Output '=== 6. admin not blocked by super-admin guard ==='
$cat = @{ id=''; name='SmokeCat'; number='V001'; parentId='0'; sort=99; remark='smoke' } | ConvertTo-Json
try {
    $r = Invoke-RestMethod -Uri "$base/goods/category" -Method Post -Headers $hdr -Body $cat -ContentType 'application/json'
    Check 'goods category add (id empty string)' ($r.code -eq 200) ("code=" + $r.code)
} catch { Check 'goods category add' $false ($_.Exception.Message) }
try {
    $r = Invoke-RestMethod -Uri "$base/order/999999" -Headers $hdr
    Check '/order/{id} not blocked' ($r.msg -notmatch 'super') ("msg=" + $r.msg)
} catch { Check '/order/{id}' $false ($_.Exception.Message) }

Write-Output ''
Write-Output '=== 7. resend work order deducts inventory ==='
$db = 'qihang_shop_verify'
Sql "DELETE FROM $db.erp_goods_inventory_operation;" | Out-Null
Sql "DELETE FROM $db.erp_goods_inventory;" | Out-Null
Sql "DELETE FROM $db.erp_goods_sku WHERE id=9001;" | Out-Null
Sql "INSERT INTO $db.erp_goods_sku (id,goods_id,goods_name,goods_num,spec_name,spec_num,unit_cost,retail_price,status,disable,create_time) VALUES (9001,9001,'SmokeGoods','G9001','Red','S9001',10.00,29.90,1,0,NOW());" | Out-Null
Sql "INSERT INTO $db.erp_goods_inventory (id,goods_id,goods_num,sku_id,sku_code,goods_name,sku_name,quantity,locked_qty,is_delete,create_time) VALUES (9001,9001,'G9001',9001,'S9001','SmokeGoods','Red',5,0,0,NOW());" | Out-Null
$before = Sql "SELECT quantity FROM $db.erp_goods_inventory WHERE id=9001;"
Check 'inventory seeded' ($before -eq '5') ("quantity=" + $before)
try {
    $wo = @{ type=3; amount=10; goodsId=9001; skuId=9001; quantity=1; shopId=1; orderNum='SMOKE001' } | ConvertTo-Json
    $r = Invoke-RestMethod -Uri "$base/workOrder/quick" -Method Post -Headers $hdr -Body $wo -ContentType 'application/json'
    Check 'work order quick (resend)' ($r.code -eq 200) ("code=" + $r.code + " id=" + $r.data)
} catch { Check 'work order quick' $false ($_.Exception.Message) }
$after = Sql "SELECT quantity FROM $db.erp_goods_inventory WHERE id=9001;"
Check 'inventory deducted 5 -> 4' ($after -eq '4') ("quantity=" + $after)
$ops = Sql "SELECT CONCAT(type,'/',biz_type,'/',quantity) FROM $db.erp_goods_inventory_operation WHERE sku_id=9001;"
Check 'inventory operation logged (type2/biz203)' ($ops -eq '2/203/1') ("row=" + $ops)
$so = Sql "SELECT COUNT(*) FROM $db.erp_stock_out WHERE source_num LIKE 'WO%';"
Check 'stock out entry generated' ([int]$so -ge 1) ("count=" + $so)
# stock insufficient path
$wo2 = @{ type=3; amount=10; goodsId=9001; skuId=9001; quantity=99; shopId=1; orderNum='SMOKE002' } | ConvertTo-Json
try { $r = Invoke-RestMethod -Uri "$base/workOrder/quick" -Method Post -Headers $hdr -Body $wo2 -ContentType 'application/json' } catch {}
$after2 = Sql "SELECT quantity FROM $db.erp_goods_inventory WHERE id=9001;"
Check 'inventory not negative when insufficient' ($after2 -eq '4') ("quantity=" + $after2)

Write-Output ''
Write-Output '=== 8. avatar upload (multipart via curl) ==='
try {
    $png = [Convert]::FromBase64String('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==')
    $tmp = Join-Path $env:TEMP 'qh_avatar.png'
    [System.IO.File]::WriteAllBytes($tmp, $png)
    $respText = curl.exe -s -X POST "$base/system/user/profile/avatar" -H "Authorization: Bearer $token" -F "avatarfile=@$tmp"
    $j = $respText | ConvertFrom-Json
    Check 'avatar upload' ($j.code -eq 200 -and $j.imgUrl) ("imgUrl=" + $j.imgUrl)
    if ($j.imgUrl) {
        $imgOut = Join-Path $env:TEMP 'qh_avatar_get.png'
        $code = curl.exe -s -o $imgOut -w '%{http_code}' "$base$($j.imgUrl)"
        $len = (Get-Item $imgOut).Length
        Check 'avatar file served anonymously' ($code -eq '200' -and $len -gt 10) ("http=" + $code + " len=" + $len)
        Remove-Item $imgOut -Force
        $db2 = Sql "SELECT avatar FROM qihang_shop_verify.sys_user WHERE user_name='admin';"
        Check 'avatar persisted to sys_user' ($db2 -like '*avatar*') ("avatar=" + $db2)
    }
    Remove-Item $tmp -Force
} catch { Check 'avatar upload' $false ($_.Exception.Message) }

Write-Output ''
Write-Output ("===== PASS " + $pass + " / FAIL " + $fail + " =====")
