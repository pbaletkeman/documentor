$xmlPath = 'build/reports/checkstyle/test.xml'
$xml = [xml](Get-Content $xmlPath)
$lineLengthViolations = @{}

foreach ($file in $xml.checkstyle.file) {
    $filePath = $file.name
    $longLineCount = ($file.error | Where-Object { $_.source -eq 'com.puppycrawl.tools.checkstyle.checks.sizes.LineLengthCheck' }).Count
    if ($longLineCount -gt 0) {
        $lineLengthViolations[$filePath] = $longLineCount
    }
}

$sortedFiles = $lineLengthViolations.GetEnumerator() | Sort-Object Value -Descending

$totalViolations = ($sortedFiles | Measure-Object -Property Value -Sum).Sum
$fileCount = $sortedFiles.Count

$content = @()
$content += '# Long Line Issues - Test Files'
$content += ''
$content += "Total LineLength violations: $totalViolations"
$content += "Files with violations: $fileCount"
$content += ''
$content += '## Files ordered by number of violations:'
$content += ''

foreach ($file in $sortedFiles) {
    # Skip the already fixed file
    if ($file.Key -notlike '*ElementDocumentationGeneratorEnhancedBranchCoverageTest.java') {
        $content += "- **$($file.Key)** - $($file.Value) violations"
    }
}

$content -join "`n" | Out-File -FilePath 'todo-long.md' -Encoding UTF8
