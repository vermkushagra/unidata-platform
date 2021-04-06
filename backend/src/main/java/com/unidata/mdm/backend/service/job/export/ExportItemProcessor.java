package com.unidata.mdm.backend.service.job.export;

import org.springframework.batch.item.ItemProcessor;
/**
 * Empty class. Needed only because it's required by job infrastracture.
 */
public class ExportItemProcessor implements ItemProcessor<String, String> {

	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public String process(String item) throws Exception {
		return "";
	}

}
