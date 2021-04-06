package com.unidata.mdm.backend.service.job.export;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
/**
 * Empty class. Needed only because it's required by job infrastracture.
 */
public class ExportItemReader implements ItemReader<String> {

	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemReader#read()
	 */
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		return "";
	}

}
