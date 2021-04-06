@setlocal
call ../init-env.bat
rem %SENCHA_CMD_HOME%\sencha app upgrade -p %SENCHA_SDK_HOME%
%SENCHA_CMD_HOME%\sencha app clean --environment development
%SENCHA_CMD_HOME%\sencha app build --environment development
@endlocal