package com.unidata.mdm.backend.util.comparator;

import java.util.Comparator;

/**
 * @author Michael Yashin. Created on 17.04.2015.
 */
public class StringComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null || o2 == null) {
            return -1;
        }

        return o1.compareTo(o2);
    }
}
