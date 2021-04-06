package com.unidata.mdm.backend.service.search.util;

import com.unidata.mdm.backend.common.search.SearchField;

public interface SearchObjectConvert<T> extends SearchField {

    Object getIndexedElement(T indexedObject);

}
