@echo off
cd /d "%~dp0\.."
java -cp "scratch;out;lib\*" CheckAccount
