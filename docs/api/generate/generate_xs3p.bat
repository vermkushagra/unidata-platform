setlocal

call setenv.bat
set OUTPUT_DIR=out_xs3p

rmdir /S /Q %OUTPUT_DIR%
mkdir %OUTPUT_DIR%

%JAVA_HOME%\bin\java.exe -jar %SAXON_HOME%\saxon9he.jar ../unidata-api-2.1.xsd xs3p/xs3p_tuned.xsl -o:%OUTPUT_DIR%/unidata-api-2.1.html

endlocal