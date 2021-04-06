package com.unidata.mdm.backend.service.data.xlsximport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 * The Interface DataImportService.
 * @author ilya.bykov
 */
public interface DataImportService{
	
	/**
	 * Import data.
	 *
	 * @param data
	 *            the data
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	void importData(File data, String entityNam, String sourceSystem, String creationDate, boolean mergeWithPrevious);
	
	/**
	 * Creates template file.
	 *
	 * @param entityName
	 *            the entity name to import.
	 * @return template file as an byte array.
	 */
	ByteArrayOutputStream createTemplateFile(String entityName);
}
