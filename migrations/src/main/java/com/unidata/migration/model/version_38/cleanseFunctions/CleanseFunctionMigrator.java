/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.migration.model.version_38.cleanseFunctions;


import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.beust.jcommander.JCommander;
import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.chain.ChainMember;
import com.unidata.migration.model.ModelMigrationParams;

public class CleanseFunctionMigrator implements ChainMember {

	private static final String FUNCTION_NAME = "functionName";
	private static final String JAVA_CLASS = "javaClass";
	private static final String CLEANSE_FUNCTION_CONSISTENCY_CLASS = "com.unidata.mdm.cleanse.misc.CFDataConsistency";
	private static final String CLEANSE_FUNCTION_IS_EXISTS_CLASS = "com.unidata.mdm.cleanse.misc.CFIsExists";
	private static final String CLEANSE_FUNCTION_MASK_CLASS = "com.unidata.mdm.cleanse.string.CFCheckMask";
	private static final String CLEANSE_FUNCTION_CONC_CLASS = "com.unidata.mdm.cleanse.string.CFConcatenate";
	private static final String CLEANSE_FUNCTION_CHECK_LINK_CLASS = "com.unidata.mdm.cleanse.misc.CFCheckLink";
	private static final String CLEANSE_FUNCTION_INNER_FETCH_CLASS = "com.unidata.mdm.cleanse.misc.CFInnerFetch";
	private static final String CLEANSE_FUNCTION_OUTER_FETCH_CLASS = "com.unidata.mdm.cleanse.misc.CFOuterFetch";
	private static final String GROUP_NAME = "groupName";
	private static final String CLEANSE_FUNCTION = "cleanseFunction";
	private static final String CLEANSE_FUNCTION_CONSISTENCY_NAME = "ПроверкаПересеченияИнтервалов";
	private static final String CLEANSE_FUNCTION_MASK_NAME = "ПроверкаПоМаске";
	private static final String CLEANSE_FUNCTION_CONC_NAME_OLD = "Соеденить";
	private static final String CLEANSE_FUNCTION_CONC_NAME_NEW = "Соединить";
	private static final String CLEANSE_FUNCTION_IS_EXISTS_NAME = "ПроверкаСуществованияАтрибута";
	private static final String CLEANSE_FUNCTION_CHECK_LINK_NAME = "ПроверкаСсылки";
	private static final String CLEANSE_FUNCTION_INNER_FETCH_NAME = "ПолучениеДанныхИзВнутреннихИсточников";
	private static final String CLEANSE_FUNCTION_OUTER_FETCH_NAME = "ПолучениеДанныхИзВнешнихИсточников";
	private static final String OTHER = "Разное";
	private static final String STRING = "Строковые";

	@Override

	public boolean execute(ExchangeContext ctx, ExchangeContext.Action currentAction) {
		System.out.println("----------------------------Start---------------------------------");
		ModelMigrationParams groupMigrationParams = new ModelMigrationParams();
		JCommander jCommander = new JCommander();
		jCommander.setAcceptUnknownOptions(true);
		jCommander.addObject(groupMigrationParams);
		jCommander.parse(ctx.getInitialArgs());
		String modelFilePath = groupMigrationParams.getCurrentModelPath();
		try {
			System.out.println("----------------------------Read XML[" + modelFilePath + "]----------");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			Document document = docBuilder.parse(new File(modelFilePath));
			document.getDocumentElement().normalize();

			System.out
					.println("----------------------------Process Cleanse Functions---------------------------------");
			NodeList cleanseFunctions = document.getElementsByTagName("cleanseFunctions");
			for (int temp = 0; temp < cleanseFunctions.getLength(); temp++) {
				Node nNode = cleanseFunctions.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element functions = (Element) nNode;
					NodeList groups = functions.getElementsByTagName("group");
					for (int groupNumber = 0; groupNumber < groups.getLength(); groupNumber++) {
						Node nodeGroup = groups.item(groupNumber);
						if (nodeGroup.getNodeType() == Node.ELEMENT_NODE) {
							Element entities = (Element) nodeGroup;
							// add check consistency function
							if (OTHER.startsWith(entities.getAttribute(GROUP_NAME))
									&& findNode(CLEANSE_FUNCTION_CONSISTENCY_NAME, entities.getChildNodes()) == null) {
								Element element =createCleanseFunction(document,CLEANSE_FUNCTION_CONSISTENCY_NAME,CLEANSE_FUNCTION_CONSISTENCY_CLASS);
								entities.appendChild(element);
							}
							// add inner fetch function
							if (OTHER.startsWith(entities.getAttribute(GROUP_NAME))
									&& findNode(CLEANSE_FUNCTION_INNER_FETCH_NAME, entities.getChildNodes()) == null) {
								Element element =createCleanseFunction(document,CLEANSE_FUNCTION_INNER_FETCH_NAME,CLEANSE_FUNCTION_INNER_FETCH_CLASS);
								entities.appendChild(element);
							}
							// add outer fetch function
							if (OTHER.startsWith(entities.getAttribute(GROUP_NAME))
									&& findNode(CLEANSE_FUNCTION_OUTER_FETCH_NAME, entities.getChildNodes()) == null) {
								Element element =createCleanseFunction(document,CLEANSE_FUNCTION_OUTER_FETCH_NAME,CLEANSE_FUNCTION_OUTER_FETCH_CLASS);
								entities.appendChild(element);
							}
							// add or replace check links function
							if (OTHER.startsWith(entities.getAttribute(GROUP_NAME))) {
								Node node = findNode(CLEANSE_FUNCTION_CHECK_LINK_NAME, entities.getChildNodes());
								Element element =createCleanseFunction(document,CLEANSE_FUNCTION_CHECK_LINK_NAME,CLEANSE_FUNCTION_CHECK_LINK_CLASS);
								if (node == null) {
									entities.appendChild(element);
								} else {
									entities.replaceChild(element, node);
								}
							}
							// add function for attribute existence validation
							if (OTHER.startsWith(entities.getAttribute(GROUP_NAME))
									&& findNode(CLEANSE_FUNCTION_IS_EXISTS_NAME, entities.getChildNodes()) == null) {
								Element element =createCleanseFunction(document,CLEANSE_FUNCTION_IS_EXISTS_NAME,CLEANSE_FUNCTION_IS_EXISTS_CLASS);
								entities.appendChild(element);
							}
							// add check by mask function
							if (STRING.startsWith(entities.getAttribute(GROUP_NAME))
									&& findNode(CLEANSE_FUNCTION_MASK_NAME, entities.getChildNodes()) == null) {
								Element element =createCleanseFunction(document,CLEANSE_FUNCTION_MASK_NAME,CLEANSE_FUNCTION_MASK_CLASS);
								entities.appendChild(element);
							}
							// Rename misspelled cleanse function
							if (STRING.startsWith(entities.getAttribute(GROUP_NAME))
									&& findNode(CLEANSE_FUNCTION_CONC_NAME_NEW, entities.getChildNodes()) == null
									&& findNode(CLEANSE_FUNCTION_CONC_NAME_OLD, entities.getChildNodes()) != null) {
								Element element =createCleanseFunction(document,CLEANSE_FUNCTION_CONC_NAME_NEW,CLEANSE_FUNCTION_CONC_CLASS);
								entities.replaceChild(element,
										findNode(CLEANSE_FUNCTION_CONC_NAME_OLD, entities.getChildNodes()));
							}
						}
					}
				}
			}

			System.out.println("----------------------------Save---------------------------------");
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(new File(modelFilePath));
			Source input = new DOMSource(document);

			transformer.transform(input, output);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static Element createCleanseFunction(Document document, String functionName, String functionClass) {
		Element element = document.createElement(CLEANSE_FUNCTION);
		element.setAttribute(FUNCTION_NAME, functionName);
		element.setAttribute(JAVA_CLASS, functionClass);
		return element;
	}

	private static Node findNode(String funcName, NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode!=null && childNode.getAttributes()!=null&&childNode.getAttributes().item(0).getNodeValue().equals(funcName)) {
				return childNode;
			}
		}
		return null;
	}
}
