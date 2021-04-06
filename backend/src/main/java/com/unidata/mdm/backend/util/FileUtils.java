package com.unidata.mdm.backend.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;

/**
 * @author Michael Yashin. Created on 21.05.2015.
 */
public class FileUtils {

    public static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * The Constant TO_IMPORT.
     */
    private static final String TO_IMPORT = "to_import";

    /**
     * The Constant TEMP.
     */
    private static final String TEMP = "temp";

    /**
     * The Constant CATALINA_BASE.
     */
    private static final String CATALINA_BASE = "catalina.base";

    /**
     * The Constant DELIMETER.
     */
    private static final String DELIMETER = "_";

    /**
     * The Constant FILENAME.
     */
    private static final String FILENAME = "filename";

    /**
     * The Constant SDF.
     */
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-M-yyyy_hh_mm_ss");

    public static String readFile(String path, Charset encoding) throws IOException {
        return readFile(Paths.get(path), encoding);
    }

    public static String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }

    public static String encodePath(String path) {
        try {
            return URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SystemRuntimeException("File encoding not recognized", ExceptionId.EX_DATA_INCORRECT_ENCODING);
        }
    }

    /**
     * Save file temp folder.
     *
     * @param attachment the attachment
     * @return the java.nio.file. path
     */
    public static java.nio.file.Path saveFileTempFolder(Attachment attachment) {
        String fileName = String.join(DELIMETER, SDF.format(new Date()),
                attachment.getContentDisposition().getParameter(FILENAME));
        String tempDirectory = String.join(File.separator, System.getProperty(CATALINA_BASE), TEMP, TO_IMPORT);
        try {
            Files.createDirectories(Paths.get(tempDirectory));
            java.nio.file.Path path = Paths.get(String.join(File.separator, tempDirectory, fileName));
            Files.deleteIfExists(path);
            InputStream in = attachment.getObject(InputStream.class);
            Files.copy(in, path);
            return path;
        } catch (IOException ex) {
            throw new SystemRuntimeException("Exception occurs while saving file.",
                    ExceptionId.EX_DATA_XLSX_IMPORT_SAVE_FILE, ex);
        }
    }
}
