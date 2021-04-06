setlocal
call init-env.bat
call %GRADLE_HOME%\bin\gradle.bat %*
endlocal