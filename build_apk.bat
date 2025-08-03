@echo off
echo Iniciando build de la app Android...
cd /d %~dp0

REM Verificar si existe gradlew
if exist gradlew (
    echo Ejecutando gradlew assembleDebug...
    gradlew assembleDebug
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo ===== BUILD EXITOSO =====
        echo El APK ha sido generado en: app\build\outputs\apk\debug\
        echo.
        if exist "app\build\outputs\apk\debug\app-debug.apk" (
            echo Archivo APK encontrado: app-debug.apk
            echo Tamaño del archivo: 
            for %%A in ("app\build\outputs\apk\debug\app-debug.apk") do echo %%~zA bytes
            echo.
            echo === COMO INSTALAR EN BLUESTACKS ===
            echo 1. Abre BlueStacks
            echo 2. Arrastra el archivo APK a la ventana de BlueStacks
            echo 3. O usa: Configuracion ^> Avanzado ^> Instalar APK
            echo 4. Archivo ubicado en: %CD%\app\build\outputs\apk\debug\app-debug.apk
            echo.
            echo === COMO INSTALAR CON ADB ===
            echo adb install app\build\outputs\apk\debug\app-debug.apk
        )
    ) else (
        echo.
        echo ===== ERROR EN BUILD =====
        echo Revisa los errores anteriores
    )
) else (
    echo No se encontró gradlew. Asegúrate de ejecutar este script en la raíz del proyecto Android.
    exit /b 1
)
pause
