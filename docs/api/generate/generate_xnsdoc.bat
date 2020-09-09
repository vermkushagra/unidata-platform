setlocal 

call setenv.bat
set OUTPUT_DIR=out_xnsdoc

rmdir /S /Q %OUTPUT_DIR%
mkdir %OUTPUT_DIR%

call xnsdoc.bat -t "Unidata-2.1 API" -o %OUTPUT_DIR% -verbose ../unidata-api-2.1.xsd

endlocal