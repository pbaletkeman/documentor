gradlew.bat clean checkstyletest
list all issues by file,
only list longline issues
save list to todo-long.md
list files with most long issues at the top

---

take todo-long as input.
for each file listed ONLY fix all the long line issues and no other issues

gradlew.bat clean checkstyletest

list all issues by file,
only list longline issues
save list to todo-long.md
list files with most long issues at the top
after each file has been fixed remove from todo-long.md

gradlew.bat clean checkstyletest
list all issues by file,
only list longline issues
save list to todo-long.md
list files with most long issues at the top

after each file has been fixes prompt to continue
after each file fixed list how many more issues are left
