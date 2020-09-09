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

package com.unidata.mdm.api.wsdl.v3;


import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.api.v3.CompareOperatorType;
import com.unidata.mdm.api.v3.SearchAndDef;
import com.unidata.mdm.api.v3.SearchAtomDef;
import com.unidata.mdm.api.v3.SearchBaseDef;
import com.unidata.mdm.api.v3.SearchConditionDef;
import com.unidata.mdm.api.v3.SearchOrDef;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AbstractSimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
* @author Mikhail Mikhailov
* Various constants and utilities.
*/
public class SoapSearchUtils {

    /**
     * Colon separator for various fields.
     */
    public static final String COLON = ":";
    /**
     * Raw, not analyzed, field value, indexed as field.$nan.
     */
    public static final String NAN = "$nan";
    /**
     * Separator for complex type (example: field.$nan)
     */
    public static final String DOT = ".";

   /**
    * No instance of this class allowed.
    */
   private SoapSearchUtils() {
       super();
   }

   /**
    * Builds a query string from SOAP API objects input.
    * @param condition input conditions
    * @param mms meta model service
    * @param entity entity name
    * @return query string
    */
   public static String buildQStringFromConditions(SearchConditionDef condition, MetaModelService mms, String entity) {

       SearchBaseDef root = condition.getExpression();
       if (root instanceof SearchAndDef) {
           return processAnd((SearchAndDef) root, entity, mms);
       } else if (root instanceof SearchOrDef) {
           return processOr((SearchOrDef) root, entity, mms);
       } else if (root instanceof SearchAtomDef) {
           return processAtom((SearchAtomDef) root, entity, mms);
       }

       return null;
   }

   /**
    * Processes an atom.
 * @param atom the atom to process
 * @param entity the entity
 * @param mms meta model service
 * @param b the buffer
    */
   private static String processAtom(SearchAtomDef atom, String entity, MetaModelService mms) {
       if (atom.getOperator() == null) {
           throw new SystemRuntimeException(
                   "Operator is not set for atom [" + atom.getAttributeName() + ", " + atom.getConstant() + "]",
                   ExceptionId.EX_SOAP_SEARCH_OPERATOR_NOT_SET,
                   atom.getAttributeName(),
                   atom.getConstant()
           );
       }

       StringBuilder b = new StringBuilder();
       AbstractAttributeDef aad = mms.getAttributeByPath(entity, atom.getAttributeName());
       boolean exactMatch = false;
       if (aad != null) {
           boolean isString = aad instanceof AbstractSimpleAttributeDef
                   && ((AbstractSimpleAttributeDef) aad).getSimpleDataType() == SimpleDataType.STRING;
           exactMatch = isString && atom.getOperator() == CompareOperatorType.EQUALS;
       }

       b.append(atom.getAttributeName());

       if (exactMatch) {
           b.append(DOT)
            .append(NAN);
       }

       b.append(COLON)
        .append(processValue(atom.getConstant(), atom.getOperator()));

       return b.toString();
   }

   /**
    * Processes AND.
 * @param and the AND element
 * @param entity the entity
 * @param mms meta model service
 * @param b parent builder
    */
   private static String processAnd(SearchAndDef and, String entity, MetaModelService mms) {

       List<String> tokens = processExpressions(and.getExpressions(), entity, mms);
       if (tokens.isEmpty()) {
           return null;
       }

       StringBuilder local = new StringBuilder();
       if (tokens.size() > 1) {
           local.append("(");
       }

       for (int i = 0; i < tokens.size(); i++) {

           if (i > 0) {
               local.append(" AND ");
           }
           local.append(tokens.get(i));
       }

       if (tokens.size() > 1) {
           local.append(")");
       }

       return local.toString();
   }

   /**
    * Process OR.
 * @param or this OR element
 * @param entity the entity
 * @param mms meta model service
 * @param b parent builder.
    */
   private static String processOr(SearchOrDef or, String entity, MetaModelService mms) {

       List<String> tokens = processExpressions(or.getExpressions(), entity, mms);
       if (tokens.isEmpty()) {
           return null;
       }

       StringBuilder local = new StringBuilder();
       if (tokens.size() > 1) {
           local.append("(");
       }

       for (int i = 0; i < tokens.size(); i++) {

           if (i > 0) {
               local.append(" OR ");
           }
           local.append(tokens.get(i));
       }

       if (tokens.size() > 1) {
           local.append(")");
       }

       return local.toString();
   }
   /**
    * Does expression list processing.
    * @param expressions the list of expressions
    * @param entity the entity name
    * @param mms MMS
    * @return list of tokens
    */
   private static List<String> processExpressions(List<SearchBaseDef> expressions, String entity, MetaModelService mms) {

       List<String> tokens = new ArrayList<>(expressions.size());
       for (SearchBaseDef base : expressions) {

           String token = null;
           if (base instanceof SearchAndDef) {
               token = processAnd((SearchAndDef) base, entity, mms);
           } else if (base instanceof SearchOrDef) {
               token = processOr((SearchOrDef) base, entity, mms);
           } else if (base instanceof SearchAtomDef) {
               token = processAtom((SearchAtomDef) base, entity, mms);
           }

           if (StringUtils.isNotBlank(token)) {
               tokens.add(token);
           }
       }

       return tokens;
   }

   /**
    * Processes value.
    * @param value the value
    * @param op operator
    * @return processed value
    */
   private static String processValue(String value, CompareOperatorType op) {

       boolean hasWhitespaces = StringUtils.containsWhitespace(value);
       String result = hasWhitespaces ? "\"" + value + "\"" : value;
       switch (op) {
           case FUZZY_EQUALS:
               result = result + "~";
               break;
           case GREATER:
               result = ">" + result;
               break;
           case GREATER_OR_EQUALS:
               result = ">=" + result;
               break;
           case LESS:
               result = "<" + result;
               break;
           case LESS_OR_EQUALS:
               result = "<=" + result;
               break;
           case LIKE:
               result = result + "~5";
               break;
           case NOT_EQUALS:
               result = "-" + result;
               break;
           case EQUALS:
           default:
               break;
       }
       return result;
   }
}

