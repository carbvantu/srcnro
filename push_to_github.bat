@echo off
title Push Code to GitHub
cd /d "%~dp0"
echo =======================================================
echo   Dang day ma nguon len https://github.com/carbvantu/srcnro
echo =======================================================
echo.
git push -u origin main
echo.
echo =======================================================
if %ERRORLEVEL% EQU 0 (
    echo [THANH CONG] Da day code len GitHub thanh cong!
) else (
    echo [LUU Y] Neu chua dang nhap, cua so dang nhap se hien len.
)
echo =======================================================
pause
