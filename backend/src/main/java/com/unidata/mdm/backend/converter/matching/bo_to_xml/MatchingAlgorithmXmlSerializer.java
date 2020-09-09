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

package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.AbstractXmlSerializer;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.match.MatchingAlgorithmDef;

@ConverterQualifier
@Component
public class MatchingAlgorithmXmlSerializer extends AbstractXmlSerializer<MatchingAlgorithmDef> {

    /**
     * Matching algo {@link QName}.
     */
    private static final QName MATCH_ALGO_QNAME = new QName("http://match.mdm.unidata.com/", "MatchingAlgorithmDef", "matchingAlgorithm");

    @Override
    protected QName getQName() {
        return MATCH_ALGO_QNAME;
    }

    @Override
    protected JAXBContext getContext() {
        return JaxbUtils.getMatchContext();
    }

    @Override
    protected Class<MatchingAlgorithmDef> getConvertClass() {
        return MatchingAlgorithmDef.class;
    }
}
