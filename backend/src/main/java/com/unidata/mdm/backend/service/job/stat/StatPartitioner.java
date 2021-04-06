

package com.unidata.mdm.backend.service.job.stat;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.service.statistic.StatServiceExt;

/**
 * Statistic partitioner.
 */
public class StatPartitioner implements Partitioner {
	
	/** The stat service. */
	@Autowired
	private StatServiceExt statService;

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.partition.support.Partitioner#partition(int)
	 */
	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		//statService.persistStatistic();
		return new HashMap<String, ExecutionContext>();
	}
}
