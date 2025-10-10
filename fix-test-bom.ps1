# Fix BOM characters in test files only
$testFiles = Get-ChildItem -Path "src\test" -Filter "*.java" -Recurse
$count = 0

foreach ($file in $testFiles) {
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
    if ($content -and $content[0] -eq [char]0xFEFF) {
        $content = $content.Substring(1)
        [System.IO.File]::WriteAllText($file.FullName, $content, [System.Text.UTF8Encoding]::new($false))
        $count++
        Write-Host "Fixed BOM in: $($file.FullName)"
    }
}

Write-Host "Processed $count test files with BOM issues"