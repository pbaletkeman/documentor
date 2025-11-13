#!/usr/bin/env python3
import re
from collections import defaultdict


def extract_linelength_violations(filename):
    """Extract only LineLength violations from checkstyle output file"""
    try:
        with open(filename, "r", encoding="utf-8") as f:
            output = f.read()
    except Exception as e:
        print(f"Error reading file: {e}")
        return {}

    linelength_pattern = r"\[WARN\] (.+):(\d+): Line is longer than 80 characters \(found (\d+)\)\. \[LineLength\]"
    violations = defaultdict(list)

    for match in re.finditer(linelength_pattern, output):
        file_path = match.group(1)
        line_num = int(match.group(2))
        char_count = int(match.group(3))

        # Extract just the filename from the full path
        filename = (
            file_path.split("\\")[-1] if "\\" in file_path else file_path.split("/")[-1]
        )

        violations[filename].append({"line": line_num, "chars": char_count})

    return violations


def create_todo_long_md(violations):
    """Create todo-long.md with files sorted by violation count"""
    # Sort files by number of violations (descending)
    sorted_files = sorted(violations.items(), key=lambda x: len(x[1]), reverse=True)

    content = "# Line Length Issues in Test Files\n\n"

    for filename, file_violations in sorted_files:
        count = len(file_violations)
        content += f"{filename} - {count} violations\n"

    content += "\n## Completed Files\n\n"
    content += (
        "- ElementDocumentationGeneratorEnhancedTest.java - Fixed (83→0 violations)\n"
    )
    content += "- BeanUtilsComprehensiveTest.java - Fixed (78→0 violations)\n"
    content += "- ServiceMetricsUtilsTest.java - Fixed (43→0 violations)\n"
    content += "- MermaidDiagramServiceTest.java - Fixed (37→0 violations)\n"
    content += "- ServicePerformanceUtilsTest.java - Fixed (27→0 violations)\n"
    content += "- PythonCodeAnalyzerTest.java - Fixed (11→0 violations)\n"
    content += "\n"

    return content


def main():
    print("Extracting LineLength violations from violations_output.txt...")
    violations = extract_linelength_violations("violations_output.txt")

    print("Creating todo-long.md...")
    content = create_todo_long_md(violations)

    with open("todo-long.md", "w", encoding="utf-8") as f:
        f.write(content)

    total_violations = sum(len(v) for v in violations.values())
    print(
        f"Found {total_violations} LineLength violations across {len(violations)} files"
    )
    print("Saved to todo-long.md")

    # Print the top 5 files for verification
    sorted_files = sorted(violations.items(), key=lambda x: len(x[1]), reverse=True)
    print("\nTop files with most violations:")
    for i, (filename, file_violations) in enumerate(sorted_files[:5]):
        print(f"{i+1}. {filename} - {len(file_violations)} violations")


if __name__ == "__main__":
    main()
