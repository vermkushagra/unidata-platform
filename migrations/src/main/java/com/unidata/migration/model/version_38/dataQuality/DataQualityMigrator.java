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

package com.unidata.migration.model.version_38.dataQuality;

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

public class DataQualityMigrator implements ChainMember {

    private final static String NS2_NAME_SPACE = "xmlns:ns2";
    private final static String NS3_NAME_SPACE = "xmlns:ns3";
    private final static String NS2 = "ns2:";
    private final static String EMPTY = "";
    private final static String TYPE = "type";
    private final static String INTEGER = "Integer";
    private final static String STRING = "String";
    private final static String NUMBER = "Number";
    private final static String BOOLEAN = "Boolean";
    private final static String BOOL_VALUE = "boolValue";
    private final static String STR_VALUE = "stringValue";
    private final static String INT_VALUE = "intValue";
    private final static String NUN_VALUE = "numberValue";

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
            System.out.println("----------------------------Read XML" + modelFilePath + " ----------");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document document = docBuilder.parse(new File(modelFilePath));
            document.getDocumentElement().normalize();
            System.out.println("----------------------------Process schema removing-------------------");
            NodeList model = document.getElementsByTagName("model");
            for (int temp = 0; temp < model.getLength(); temp++) {
                Node nNode = model.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    eElement.removeAttribute(NS2_NAME_SPACE);
                    eElement.removeAttribute(NS3_NAME_SPACE);
                }
            }
            System.out.println("----------------------------Process constants-------------------");
            NodeList constants = document.getElementsByTagName("attributeConstantValue");
            for (int temp = 0; temp < constants.getLength(); temp++) {
                Node nNode = constants.item(temp);
                if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element constant = (Element) nNode;
                    Node value = constant.getFirstChild();
                    if (value == null) {
                        continue;
                    }
                    while (!(value.getNodeType() == Node.ELEMENT_NODE || value.getNodeType() == Node.ATTRIBUTE_NODE)) {
                        value = value.getNextSibling();
                        if (value == null) {
                            break;
                        }
                    }
                    if (value == null) {
                        continue;
                    }
                    document.renameNode(value, null, value.getNodeName().replace(NS2, EMPTY));
                    for (int temp2 = 0; temp2 < constant.getAttributes().getLength(); temp2++) {
                        String attrName = constant.getAttributes().item(temp2).getNodeName();
                        constant.getAttributes().removeNamedItem(attrName);
                    }
                    if (value.getNodeName().contains(BOOL_VALUE)) {
                        constant.setAttribute(TYPE, BOOLEAN);
                    }
                    if (value.getNodeName().contains(NUN_VALUE)) {
                        constant.setAttribute(TYPE, NUMBER);
                    }
                    if (value.getNodeName().contains(STR_VALUE)) {
                        constant.setAttribute(TYPE, STRING);
                    }
                    if (value.getNodeName().contains(INT_VALUE)) {
                        constant.setAttribute(TYPE, INTEGER);
                    }
                }
            }
            System.out.println("----------------------------Save---------------------------------");
            Result output = new StreamResult(new File(modelFilePath));
            Source input = new DOMSource(document);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(input, output);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
