package com.unidata.mdm.backend.util;

/**
 * @author Michael Yashin. Created on 04.04.2015.
 */
public class NumberUtils {

    public static boolean equals(final Number n1, final Number n2) {
        if (n1 == null && n2 == null) {
            return true;
        }
        if (n1 == null || n2 == null) {
            return false;
        }
        return n1.equals(n2);
    }
}
