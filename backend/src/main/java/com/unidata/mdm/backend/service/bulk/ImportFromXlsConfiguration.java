/**
 *
 */
package com.unidata.mdm.backend.service.bulk;

import com.unidata.mdm.backend.common.bulk.BulkOperationConfiguration;
import com.unidata.mdm.backend.common.types.BulkOperationType;
import com.unidata.mdm.backend.exchange.ExchangeContext;

/**
 * The Class ImportFromXlsConfiguration.
 *
 * @author Mikhail Mikhailov Import from XLS configuration.
 */
public class ImportFromXlsConfiguration extends BulkOperationConfiguration {
	
	/** The exchange context. */
	private ExchangeContext exchangeContext;
    /**
     * Constructor.
     */
    public ImportFromXlsConfiguration() {
        super(BulkOperationType.IMPORT_RECORDS_FROM_XLS);
    }
	
	/**
	 * Gets the exchange context.
	 *
	 * @return the exchange context
	 */
	public ExchangeContext getExchangeContext() {
		return exchangeContext;
	}
	
	/**
	 * Sets the exchange context.
	 *
	 * @param exchangeContext
	 *            the new exchange context
	 */
	public void setExchangeContext(ExchangeContext exchangeContext) {
		this.exchangeContext = exchangeContext;
	}
	public static ExchangeContext constructExchangeContext(){
//		ExchangeContext context = new ExchangeContext();
		return null;
	}
    
}
