package com.unidata.mdm.backend.service.job.stat;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

/**
 * Empty class. Needed only because it's required by job infrastracture.
 */
public class StatItemWriter implements ItemWriter<String> {

	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends String> items) throws Exception {
		// TODO Auto-generated method stub

	}

}
