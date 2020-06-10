package com.unidata.mdm.backend.common.search;

public interface SearchObjectConvert<T> extends SearchField {

    Object getIndexedElement(T indexedObject);

}
