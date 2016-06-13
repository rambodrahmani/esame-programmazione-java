@echo off
@echo Script bat per la compilazione del progetto di Programmazione Java.
@echo.
@echo Rimozione di eventuali precedenti files in corso...
if exist "build" @RD /S /Q "build"
if exist "dist" @RD /S /Q "dist"
@echo.
@echo Creazione struttura cartelle.
mkdir "build"
mkdir "build\messaggio"
mkdir "build\server"
mkdir "build\client"
mkdir "dist"
mkdir "dist\messaggio"
mkdir "dist\server"
mkdir "dist\client"
mkdir "dist\messaggio\lib"
mkdir "dist\server\lib"
mkdir "dist\client\lib"
@echo.
@echo Avvio compilazione Progetto ServerClient.
C:\prg\jdk8\bin\javac.exe -d build\ src\messaggio\*
@echo.
@echo Creazione Messaggio.jar.
cd build
C:\prg\jdk8\bin\jar.exe cvf messaggio.jar messaggio\*
@echo.
@echo Copio file generati nelle cartelle di destinazione.
xcopy /s "messaggio.jar" "..\dist\messaggio"
if exist "..\libs\messaggio.jar" del "..\libs\messaggio.jar"
xcopy /s "messaggio.jar" "..\libs"
if exist "messaggio.jar" del "messaggio.jar"
cd ..
@echo.
@echo Avvio compilazione progetto Server.
C:\prg\jdk8\bin\javac.exe -cp "libs\*" -d build\ src\server\*
@echo.
@echo Creazione Server.jar.
xcopy "altro\server\server-manifest.txt" "build\server"
cd build
C:\prg\jdk8\bin\jar.exe cvfm Server.jar server\server-manifest.txt server\*
@echo.
@echo Copio file generati nelle cartelle di destinazione.
xcopy /s "Server.jar" "..\dist\server"
if exist "Server.jar" del "Server.jar"
cd ..
xcopy "altro\server\configurazioni.xml" "dist\server"
xcopy "altro\server\configurazioni.xsd" "dist\server"
xcopy "altro\server\validaLogs.xsd" "dist\server"
xcopy "libs\*" "dist\server\lib"
@echo.
@echo Avvio compilazione progetto Client.
C:\prg\jdk8\bin\javac.exe -cp "libs\*" -d build\ src\client\*
@echo.
@echo Creazione Client.jar.
xcopy "altro\client\client-manifest.txt" "build\client"
cd build
C:\prg\jdk8\bin\jar.exe cvfm Client.jar client\client-manifest.txt client\*
@echo.
@echo Copio file generati nelle cartelle di destinazione.
xcopy /s "Client.jar" "..\dist\client"
if exist "Client.jar" del "Client.jar"
cd ..
xcopy "altro\client\configurazioni.xml" "dist\client"
xcopy "altro\client\configurazioni.xsd" "dist\client"
xcopy "altro\client\client.png" "dist\client"
xcopy "libs\*" "dist\client\lib"
@echo.
cd "dist\server"
start cmd /k C:\prg\jdk8\bin\java.exe -jar Server.jar
cd ..
cd "client"
start cmd /k C:\prg\jdk8\bin\java.exe -jar Client.jar
@echo.
pause
