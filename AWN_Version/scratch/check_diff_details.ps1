$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"

$files = @(
    "src\jbcd\dao\EventDAO.java",
    "src\jbcd\dao\GiftDAO.java",
    "src\jbcd\data\GodGK.java",
    "src\models\Item\ItemService.java",
    "src\nro\giftcode\GiftCodeService.java",
    "src\nro\inventory\InventoryService.java",
    "src\nro\npc\NpcFactory.java",
    "src\nro\player\Player.java",
    "src\nro\server\Controller.java",
    "src\nro\server\DropItemPanel.java",
    "src\nro\server\Manager.java",
    "src\nro\server\ServerManager.java",
    "src\nro\services\Service.java",
    "src\nro\services\fun\Input.java",
    "src\nro\services\fun\UseItem.java"
)

foreach ($f in $files) {
    $origContent = & $exe e $rar "AWN_Version\$f" -so
    $localPath = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\$f"
    $localContent = Get-Content $localPath -Raw -Encoding UTF8
    if ($origContent -ne $localContent) {
        $origLines = ($origContent -split "`r?`n").Count
        $localLines = ($localContent -split "`r?`n").Count
        Write-Host "DIFF in $f : orig lines=$origLines, local lines=$localLines"
    } else {
        Write-Host "IDENTICAL: $f"
    }
}
