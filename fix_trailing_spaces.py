import re

# Read the file
with open('src/test/java/com/documentor/cli/handlers/ProjectAnalysisCommandHandlerBranchTest.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Remove trailing spaces
lines = content.split('\n')
cleaned_lines = [line.rstrip() for line in lines]
cleaned_content = '\n'.join(cleaned_lines)

# Write back
with open('src/test/java/com/documentor/cli/handlers/ProjectAnalysisCommandHandlerBranchTest.java', 'w', encoding='utf-8') as f:
    f.write(cleaned_content)

print('Removed trailing spaces from ProjectAnalysisCommandHandlerBranchTest.java')