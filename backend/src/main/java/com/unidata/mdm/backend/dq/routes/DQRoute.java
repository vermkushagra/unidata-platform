package com.unidata.mdm.backend.dq.routes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataQualityStatus;
import com.unidata.mdm.backend.dao.DQErrorsDao;
import com.unidata.mdm.backend.po.DQErrorPO;


/**
 * The Class DQRoute.
 */
public class DQRoute extends RouteBuilder {
	@Autowired
	private DQErrorsDao dqErrorsDao;

	/* (non-Javadoc)
	 * @see org.apache.camel.builder.RouteBuilder#configure()
	 */
	@Override
	public void configure() throws Exception {
		from("vm:SAVE_ERRORS").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				Message in = exchange.getIn();
				@SuppressWarnings("unchecked")
				List<DataQualityError> from = (List<DataQualityError>) in.getBody(List.class);
				DQErrorPO to = new DQErrorPO();			
				to.setEntityName(exchange.getIn().getHeader("ENTITY_NAME", String.class));
				to.setRecordId(exchange.getIn().getHeader("RECORD_ID", String.class));
				to.setRequestId(exchange.getIn().getHeader("REQUEST_ID", String.class));
				to.setCreatedBy(exchange.getIn().getHeader("USER_NAME", String.class));
				to.setCreatedAt(new Date());
				to.setUpdatedAt(null);
				to.setUpdatedBy(null);
				for (int i = 0; i < from.size(); i++) {
					DataQualityError error = from.get(i);
					to.addCategory(error.getCategory());
					to.addRuleName(error.getRuleName());
			//		to.addMessage(error.getMessage());
					to.addSeverity(error.getSeverity().name());
					to.addStatus(error.getStatus().name());
				}
				in.setBody(to);
			}
		}).aggregate(constant(true), new ListAgregationStrategy())
		.completionSize(500)		
		.completionTimeout(5000l)
		.bean(dqErrorsDao,"saveErrors(${body})");
		
		from("direct-vm:GET_ERRORS").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				Message in = exchange.getIn();
				String id  = in.getBody(String.class);
				DQErrorPO from = dqErrorsDao.getErrorsByRecordID(id);
				List<DataQualityError> to = new ArrayList<>(from==null?0:from.getRuleName().size());
				if(from!=null){
					for (int i = 0; i < from.getRuleName().size(); i++) {
						DataQualityError ter 
							= DataQualityError.builder()
								.category(from.getCategory().get(i))
								.createDate(from.getCreatedAt())
								.errorId(from.getId()+"")
								.severity(from.getSeverity().get(i))
//								.message(from.getMessage())
								.ruleName(from.getRuleName().get(i))
								.status(DataQualityStatus.valueOf(from.getStatus().get(i)))
								.updateDate(from.getUpdatedAt())
								.build();
						to.add(ter);
					}
				}
				exchange.getOut().setBody(to);

			}
		});

	}

	/**
	 * Our strategy just group a list of integers.
	 */
	public final class ListAgregationStrategy extends AbstractListAggregationStrategy<DQErrorPO> {

		/* (non-Javadoc)
		 * @see org.apache.camel.processor.aggregate.AbstractListAggregationStrategy#getValue(org.apache.camel.Exchange)
		 */
		@Override
		public DQErrorPO getValue(Exchange exchange) {
			return exchange.getIn().getBody(DQErrorPO.class);
		}
	}
}