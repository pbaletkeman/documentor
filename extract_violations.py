import re
from collections import defaultdict

# Sample checkstyle output with LineLength violations
output = """
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:46: Line is longer than 80 characters (found 110). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:50: Line is longer than 80 characters (found 82). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:51: Line is longer than 80 characters (found 113). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:54: Line is longer than 80 characters (found 88). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:66: Line is longer than 80 characters (found 104). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:70: Line is longer than 80 characters (found 87). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:71: Line is longer than 80 characters (found 115). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:74: Line is longer than 80 characters (found 111). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:75: Line is longer than 80 characters (found 99). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:87: Line is longer than 80 characters (found 101). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:96: Line is longer than 80 characters (found 95). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonCodeAnalyzerTest.java:97: Line is longer than 80 characters (found 99). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:65: Line is longer than 80 characters (found 81). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:74: Line is longer than 80 characters (found 99). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:76: Line is longer than 80 characters (found 84). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:81: Line is longer than 80 characters (found 88). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:87: Line is longer than 80 characters (found 92). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:88: Line is longer than 80 characters (found 98). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:89: Line is longer than 80 characters (found 98). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:92: Line is longer than 80 characters (found 94). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:122: Line is longer than 80 characters (found 97). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:156: Line is longer than 80 characters (found 97). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:165: Line is longer than 80 characters (found 82). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:187: Line is longer than 80 characters (found 97). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:213: Line is longer than 80 characters (found 84). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:243: Line is longer than 80 characters (found 83). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:254: Line is longer than 80 characters (found 88). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:255: Line is longer than 80 characters (found 94). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:256: Line is longer than 80 characters (found 94). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonRegexAnalyzerTest.java:257: Line is longer than 80 characters (found 90). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:79: Line is longer than 80 characters (found 88). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:139: Line is longer than 80 characters (found 85). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:140: Line is longer than 80 characters (found 86). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:141: Line is longer than 80 characters (found 91). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:142: Line is longer than 80 characters (found 90). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:162: Line is longer than 80 characters (found 81). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:164: Line is longer than 80 characters (found 91). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:171: Line is longer than 80 characters (found 110). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:181: Line is longer than 80 characters (found 91). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:182: Line is longer than 80 characters (found 118). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:192: Line is longer than 80 characters (found 88). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:194: Line is longer than 80 characters (found 94). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:197: Line is longer than 80 characters (found 99). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:200: Line is longer than 80 characters (found 96). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:210: Line is longer than 80 characters (found 84). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:212: Line is longer than 80 characters (found 85). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\python\\PythonASTProcessorTest.java:214: Line is longer than 80 characters (found 84). [LineLength]
[ant:checkstyle] [WARN] C:\\Users\\Pete\\Desktop\\documentor\\src\\test\\java\\com\\documentor\\service\\ServiceUtilsTest.java:56: Line is longer than 80 characters (found 81). [LineLength]
"""

# Extract filename and count violations
files = defaultdict(int)
pattern = r"\\([\\w\\.]+\\.java):"
for line in output.split("\n"):
    if "[LineLength]" in line:
        match = re.search(pattern, line)
        if match:
            filename = match.group(1)
            files[filename] += 1

# Sort by violation count (descending)
sorted_files = sorted(files.items(), key=lambda x: x[1], reverse=True)

print("LineLength violations by file:")
for filename, count in sorted_files:
    print(f"{filename} - {count} violations")
