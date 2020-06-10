package com.unidata.mdm.api.wsdl.v5;

import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.WebServiceContext;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

import com.unidata.mdm.error_handling.v5.ApiFaultType;

public class TokenHandler extends AbstractSoapInterceptor {
	@Resource
	private WebServiceContext context;

	public TokenHandler() {
		super(Phase.PRE_PROTOCOL);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		List<Header> headers = message.getHeaders();
		JAXBContext jc = null;
		Unmarshaller unmarshaller = null;
		try {
			jc = JAXBContext.newInstance("org.example.hello_ws");
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
//			throw e;
		}

		List<Header> list = message.getHeaders();
		for (Header header : list) {
//			header.g

		}

	}

}
