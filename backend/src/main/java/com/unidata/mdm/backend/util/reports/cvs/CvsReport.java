package com.unidata.mdm.backend.util.reports.cvs;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.util.reports.Report;

/**
 * Stateful class responsible for creating cvs report.
 */
public class CvsReport implements Report<String> {

    /**
     * New row
     */
    private static final String NEW_ROW = "\n";

    /**
     * Separator
     */
    private final char separator;

    /**
     * CharSet
     */
    @Nonnull
    private final Charset charSet;

    /**
     * Current state of cvs
     */
    private final StringBuffer result = new StringBuffer();

    /**
     * Constructor
     *
     * @param separator - cvs separator
     * @param charSet   - returned charSet
     */
    public CvsReport(char separator, @Nullable String charSet) {
        this.separator = separator;
        this.charSet = charSet == null ? Charset.forName("UTF-8") : Charset.forName(charSet);
    }

    /**
     * Constructor where will be default charset.
     *
     * @param separator - cvs separator
     */
    public CvsReport(char separator) {
        this.separator = separator;
        this.charSet = Charset.forName("UTF-8");
    }

    @Override
    public void newRow() {
        result.append(NEW_ROW);
    }

    @Override
    public void addElement(@Nonnull String element) {
        String finalElement = element.replace(separator, ' ').replace(NEW_ROW, "");
        result.append(finalElement);
        result.append(separator);
    }

    @Nonnull
    @Override
    public byte[] generate() {
        return result.toString().getBytes(charSet);
    }
}
