$javaFiles = Get-ChildItem -Path ".\src" -Filter "*.java" -Recurse

foreach ($file in $javaFiles) {
    Write-Host "Processing file: $($file.FullName)"
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
    if ($content -match "^\xEF\xBB\xBF") {
        Write-Host "Removing BOM from $($file.FullName)"
        $content = $content -replace "^\xEF\xBB\xBF", ""
        [System.IO.File]::WriteAllText($file.FullName, $content, [System.Text.Encoding]::UTF8)
    }
}

Write-Host "BOM removal process completed"