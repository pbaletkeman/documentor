$checkstyleOutput = & ".\gradlew.bat" "checkstyleMain" 2>&1

$lineViolations = @{}

foreach ($line in $checkstyleOutput) {
    if ($line -match "Line is longer than 80 characters") {
        # Extract filename
        if ($line -match "\\([^\\]+\.java):") {
            $filename = $matches[1]
            if ($lineViolations.ContainsKey($filename)) {
                $lineViolations[$filename]++
            } else {
                $lineViolations[$filename] = 1
            }
        }
    }
}

# Sort by violation count (descending)
$sortedFiles = $lineViolations.GetEnumerator() | Sort-Object Value -Descending

# Create todo-long.md
$content = "# Long Line Violations (LineLength > 80 characters)`n`n"
$content += "Files sorted by number of violations (highest first):`n`n"

foreach ($file in $sortedFiles) {
    $content += "## $($file.Name) - $($file.Value) violations`n`n"
}

$content += "`nTotal files with long line violations: $($sortedFiles.Count)`n"
$totalViolations = ($sortedFiles | Measure-Object Value -Sum).Sum
$content += "Total long line violations: $totalViolations`n"

$content | Out-File -FilePath "todo-long.md" -Encoding utf8
Write-Host "Created todo-long.md with $($sortedFiles.Count) files and $totalViolations violations"
