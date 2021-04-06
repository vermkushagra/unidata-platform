package com.unidata.migration.model.version_38.groups;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.UnsupportedEncodingException;

import com.beust.jcommander.JCommander;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.chain.ChainMember;

public class EntityGroupMigrator implements ChainMember {

    static final String ROOT_GROUP_NAME = "ROOT";
    private static final String GROUP_NAME = "groupName";
    private static final String TITLE = "title";
    private static final String VERSION = "version";
    private static final int INITIAL_VERSION = 1;
    public static final String ROOT_GROUP_TITLE = "Корневой каталог";

    @Override
    public boolean execute(ExchangeContext ctx, ExchangeContext.Action currentAction) {
        System.out.println("----------------------------Start---------------------------------");
        GroupMigrationParams groupMigrationParams = new GroupMigrationParams();
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

            NodeList entitiesGroup = document.getElementsByTagName("entitiesGroup");
            NodeList lookupEntitiesList = document.getElementsByTagName("lookupEntities");
            for (int temp = 0; temp < lookupEntitiesList.getLength(); temp++) {
                Node nNode = lookupEntitiesList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    NodeList lookupEntities = eElement.getElementsByTagName("lookupEntity");
                    for (int i = 0; i < lookupEntities.getLength(); i++) {
                        Node lookupEntity = lookupEntities.item(i);        
                      NodeList childs =  lookupEntity.getChildNodes();
                      for (int j = 0; j < childs.getLength(); j++) {
                    	 Node child = childs.item(j);
                    	if(child.getNodeName()!=null&&child.getNodeName().equals("codeAttribute")){
                    		NamedNodeMap attribs = child.getAttributes();
                    		Node node = attribs.getNamedItem("unique");
                    		node.setNodeValue("true");
                    		
                    	}
                      }
                      }
                    }
                }
            if (entitiesGroup.getLength() == 0) {
              

            System.out.println("----------------------------Add Root Group---------------------------------");
            Element element = document.createElement("entitiesGroup");
            element.setAttribute(GROUP_NAME, groupMigrationParams.getRootGroupName());
            element.setAttribute(TITLE, groupMigrationParams.getRootGroupTitle());
            element.setAttribute(VERSION, String.valueOf(INITIAL_VERSION));

            document.getDocumentElement().appendChild(element);

            System.out.println("----------------------------Process entities---------------------------------");

            NodeList entitiesList = document.getElementsByTagName("entities");
            for (int temp = 0; temp < entitiesList.getLength(); temp++) {
                Node nNode = entitiesList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element entities = (Element) nNode;
                    NodeList entityList = entities.getElementsByTagName("entity");
                    for (int i = 0; i < entityList.getLength(); i++) {
                        Node entity = entityList.item(i);
                        if (entity.getNodeType() == Node.ELEMENT_NODE) {
                            ((Element) entity).setAttribute(GROUP_NAME, groupMigrationParams.getRootGroupName());
                        }
                    }
                }
            }

            System.out.println("----------------------------Process lookup entities---------------------------------");
            for (int temp = 0; temp < lookupEntitiesList.getLength(); temp++) {
                Node nNode = lookupEntitiesList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    NodeList lookupEntities = eElement.getElementsByTagName("lookupEntity");
                    for (int i = 0; i < lookupEntities.getLength(); i++) {
                        Node lookupEntity = lookupEntities.item(i);
                        if (lookupEntity.getNodeType() == Node.ELEMENT_NODE) {
                            ((Element) lookupEntity).setAttribute(GROUP_NAME, groupMigrationParams.getRootGroupName());
                        }
                      NodeList childs =  lookupEntity.getChildNodes();
                      for (int j = 0; j < childs.getLength(); j++) {
                    	 Node child = childs.item(j);
                    	 System.out.println(child.getNodeName());
                    	if(child.getNodeName()!=null&&child.getNodeName().equals("codeAttribute")){
                    		NamedNodeMap attribs = child.getAttributes();
                    		Node node = attribs.getNamedItem("unique");
                    		System.out.println(node.getNodeValue());
                    		node.setNodeValue("true");
                    	}
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
}
