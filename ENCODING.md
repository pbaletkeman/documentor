# Documentor Encoding Fix Guide

This guide addresses common encoding issues that can occur when using Unicode characters (like ✅, ⚠️, etc.) in the Documentor application on Windows environments.

## Common Encoding Issues

- Special characters appear as garbled text (e.g., `Γ£à` instead of `✅`)
- Error messages about invalid characters
- Misaligned or broken output in command prompt
- Configuration files with encoding issues

## Quick Fix

Run the master fix script to address all encoding issues at once:

```batch
fix_all_encoding.bat
```

This script will:

1. Configure Windows Command Prompt for UTF-8 support
2. Update all batch files to use UTF-8 encoding
3. Fix Documentor run scripts
4. Convert configuration files to UTF-8
5. Update Gradle settings for proper encoding

## Running the Application with UTF-8 Support

After applying the fixes, use one of these methods to run Documentor:

```batch
run-utf8.cmd
```

Or:

```batch
gradlew.bat runEnhancedApp -Dfile.encoding=UTF-8
```

## Testing Encoding Support

To verify that encoding is working correctly:

```batch
test-encoding.bat
```

This will run a test that displays various Unicode characters. If you see the symbols correctly, the fix was successful.

## Individual Fix Scripts

If you need to address specific issues:

- `fix_encoding_cmd.bat` - Fixes Windows Command Prompt settings
- `fix_batch_encoding.bat` - Updates batch files to use UTF-8
- `fix_documentor_encoding.bat` - Updates Documentor run scripts
- `fix_config_encoding.bat` - Fixes configuration files encoding
- `fix_gradle_encoding.bat` - Updates Gradle build files

## Manual Fixes

### For Command Prompt

Set UTF-8 code page manually:

```batch
chcp 65001
```

### For Java Applications

Run with explicit UTF-8 encoding:

```batch
java -Dfile.encoding=UTF-8 -jar documentor.jar
```

### For Gradle

Add to your gradle.properties:

```properties
org.gradle.jvmargs=-Dfile.encoding=UTF-8
systemProp.file.encoding=UTF-8
```

Add to your build.gradle:

```gradle
compileJava.options.encoding = 'UTF-8'
compileTestJava.options.encoding = 'UTF-8'
```

## Troubleshooting

If you still experience encoding issues:

1. Make sure you've restarted your command prompt after applying fixes
2. Verify your console font supports Unicode (Consolas is recommended)
3. Check file encoding in your editor (should be UTF-8 without BOM)
4. Try running the application from Windows Terminal instead of cmd.exe

## Unicode Test Characters

Here are some Unicode characters that should display correctly after fixing encoding:

✅ ❌ ⚠️ ℹ️ 🔍 📂 📄 🔧 ⚙️ 🔄 ▶️ ⏸️ ⏹️ ✨ 🔒 🔓 📊 📈 ⭐ ❤️ ✔️
