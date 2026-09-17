@echo off
title Auto Load API MBBank
echo ===============================
echo    AUTO LOAD API MBBANK
echo    Chay moi 30 giay
echo ===============================
echo.

:loop
echo [%date% %time%] Dang load API...
cd /d C:\xampp\htdocs
C:\xampp\php\php.exe auto-load-api.php

echo [%date% %time%] Hoan thanh. Cho 30 giay...
echo.
timeout /t 30 /nobreak >nul
goto loop
