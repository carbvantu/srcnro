@echo off
cd /d "c:\Users\vtson\Downloads\AWN_Version\AWN_Version"
if exist sources_test.txt del sources_test.txt
dir /s /b src\*.java > sources_test.txt
javac -encoding UTF-8 -cp "lib\*" -d out @sources_test.txt
echo COMPILE_RESULT=%ERRORLEVEL%
