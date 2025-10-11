#!/usr/bin/env python3
"""Remove trailing spaces from files"""

import sys
import os


def remove_trailing_spaces(file_path):
    """Remove trailing spaces from a file"""
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        return False

    try:
        with open(file_path, "r", encoding="utf-8") as f:
            lines = f.readlines()

        # Remove trailing spaces from each line
        cleaned_lines = [
            line.rstrip() + "\n" if line.endswith("\n") else line.rstrip()
            for line in lines
        ]

        with open(file_path, "w", encoding="utf-8") as f:
            f.writelines(cleaned_lines)

        print(f"Removed trailing spaces from {os.path.basename(file_path)}")
        return True
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return False


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python remove_trailing_spaces.py <file_path>")
        sys.exit(1)

    file_path = sys.argv[1]
    remove_trailing_spaces(file_path)
