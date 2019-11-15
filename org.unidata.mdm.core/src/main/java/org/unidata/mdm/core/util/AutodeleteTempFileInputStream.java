/**
 *
 */
package org.unidata.mdm.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Mikhail Mikhailov
 *
 */
public class AutodeleteTempFileInputStream extends FileInputStream {

    /**
     * Temp file.
     */
    private File file;

    /**
     * Constructor.
     * @param name
     * @throws FileNotFoundException
     */
    public AutodeleteTempFileInputStream(String name) throws FileNotFoundException {
        this(new File(name));
    }

    /**
     * Constructor.
     * @param file
     * @throws FileNotFoundException
     */
    public AutodeleteTempFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            if (file != null) {
                file.delete();
                file = null;
            }
        }
    }
}
