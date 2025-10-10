# Fix BOM characters in Java files
Get-ChildItem -Path ".\src" -Filter "*.java" -Recurse | ForEach-Object {
    Write-Host "Processing $($_.FullName)"
    $content = Get-Content $_.FullName -Raw
    if ($content -match "\uFEFF") {
        Write-Host "BOM found in $($_.FullName)"
        $content = $content -replace "\uFEFF", ""
        [System.IO.File]::WriteAllText($_.FullName, $content)
        Write-Host "Removed BOM from $($_.FullName)"
    }
}
Write-Host "Done processing files"