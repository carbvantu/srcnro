@echo off
title Anwin Version 3 Server (Run without NetBeans)
cd /d "%~dp0"

echo ===============================
echo     🚀 Starting Anwin Version 3 Server
echo ===============================

REM --- Build source (CHUẨN WINDOWS) ---
echo [BUILD] Compiling source...

if exist sources.txt del sources.txt
dir /s /b src\*.java > sources.txt

javac -encoding UTF-8 ^
 -cp "lib\*" ^
 -d out ^
 @sources.txt

if errorlevel 1 (
    echo ❌ Build thất bại! Kiểm tra lỗi compile.
    pause
    exit /b
)

echo ✅ Build thành công!

REM --- Run server ---
echo [RUN] Starting server...
java -Xms128m -Xmx3g -Xss256k ^
 -XX:CompressedClassSpaceSize=128m ^
 -XX:ReservedCodeCacheSize=128m ^
 -XX:CICompilerCount=2 ^
 -cp "out;lib\*" ^
 -Duser.dir="%cd%" ^
 nro.server.ServerManager

echo ===============================
echo     ✅ Server đã dừng
echo ===============================
pause
