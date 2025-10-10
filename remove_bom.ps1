$files = Get-ChildItem -Path "src" -Recurse -Filter "*.java"

foreach ($file in $files) {
    Write-Host "Processing file: $($file.FullName)"
    
    # Read the content of the file
    $content = Get-Content -Path $file.FullName -Raw -Encoding utf8
    
    # Check if the file starts with BOM
    if ($content.StartsWith([char]0xFEFF)) {
        Write-Host "  Removing BOM from $($file.FullName)"
        
        # Remove BOM and write back to the file without BOM
        $contentWithoutBOM = $content.Substring(1)
        [System.IO.File]::WriteAllText($file.FullName, $contentWithoutBOM)
    }
}

Write-Host "All BOM characters have been removed from Java files."