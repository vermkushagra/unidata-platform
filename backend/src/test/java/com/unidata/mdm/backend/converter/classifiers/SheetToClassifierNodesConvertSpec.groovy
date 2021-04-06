package com.unidata.mdm.backend.converter.classifiers

import com.unidata.mdm.backend.common.exception.DataProcessingException
import com.unidata.mdm.classifier.ClassifierValueType
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.mockito.Mockito
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class SheetToClassifierNodesConvertSpec extends Specification {

    @Shared converter = new SheetToClassifierNodesConvert(
            new ClassifierAttrDefConvert(
                    new ClassifierAttrConvert()
            )
    )
    def sheet = Mock(XSSFSheet)
    def columnsNames = row([
            cell("name", 1),
            cell("description", 1),
            cell("code", 1),
            cell("nodeId", 1),
            cell("parentId", 1),
            cell("nodeAttr.0.name", 1),
            cell("nodeAttr.0.displayName", 1),
            cell("nodeAttr.0.description", 1),
            cell("nodeAttr.0.dataType", 1),
            cell("nodeAttr.0.readOnly", 1),
            cell("nodeAttr.0.hidden", 1),
            cell("nodeAttr.0.nullable", 1),
            cell("nodeAttr.0.inherited", 1),
            cell("nodeAttr.0.unique", 1),
            cell("nodeAttr.0.searchable", 1),
            cell("nodeAttr.0.value", 1)
    ])

    def cell(value, type) {
        def cell = Mockito.mock(XSSFCell)
        Mockito.when(cell.getStringCellValue()).thenReturn(value)
        Mockito.when(cell.getCellType()).thenReturn(type)
        Mockito.when(cell.toString()).thenReturn("cell($value)".toString())
        cell
    }

    def row(cells) {
        def row = Mock(XSSFRow) {
            cellIterator() >> { cells.iterator() }
            getCell(*_) >> { cells.get(it[0]) }
            toString() >> cells.join('; ')
        }
        row
    }

    def "convert sheet to classifier nodes with attrs"() {
        given:
        def rows = [
                row([
                        cell("nodeName", 1),
                        cell("", 1),
                        cell("", 1),
                        cell("nodeId", 1),
                        cell(null, 1),
                        cell("nodeAttrName", 1),
                        cell("nodeAttrName", 1),
                        cell("description", 1),
                        cell("INTEGER", 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1)
                ])
        ]
        sheet.iterator() >> [columnsNames, *rows].iterator()

        when:
        def nodes = converter.convert(sheet)

        then:
        nodes.size() == 1
        nodes.indexed().get(0).name == "nodeName"
        nodes.indexed().get(0).id == "nodeId"
        nodes.indexed().get(0).attributes.size() == 1
        nodes.indexed().get(0).attributes.get(0).name == "nodeAttrName"
        nodes.indexed().get(0).attributes.get(0).displayName == "nodeAttrName"
        nodes.indexed().get(0).attributes.get(0).description == "description"
        nodes.indexed().get(0).attributes.get(0).valueType == ClassifierValueType.INTEGER
    }

    @Unroll
    def "throw exception when no required fields values in '#rows'"() {
        given:
        sheet.iterator() >> [columnsNames, *rows].iterator()

        when:
        converter.convert(sheet)

        then:
        thrown(DataProcessingException)

        where:
        rows << [
                [
                        row([
                                cell(null, 1),
                                cell("", 1),
                                cell("", 1),
                                cell("nodeId", 1),
                                cell(null, 1),
                                cell("nodeAttrName", 1),
                                cell("nodeAttrName", 1),
                                cell("description", 1),
                                cell("INTEGER", 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1)
                        ])
                ],
                [
                        row([
                                cell("nodeName", 1),
                                cell("", 1),
                                cell("", 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell("nodeAttrName", 1),
                                cell("nodeAttrName", 1),
                                cell("description", 1),
                                cell("INTEGER", 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1)
                        ])
                ],
                [
                        row([
                                cell("nodeName", 1),
                                cell("", 1),
                                cell("", 1),
                                cell("nodeId", 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell("nodeAttrName", 1),
                                cell("description", 1),
                                cell("INTEGER", 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1)
                        ])
                ],
                [
                        row([
                                cell("nodeName", 1),
                                cell("", 1),
                                cell("", 1),
                                cell("nodeId", 1),
                                cell(null, 1),
                                cell("nodeAttrName", 1),
                                cell(null, 1),
                                cell("description", 1),
                                cell("INTEGER", 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1)
                        ])
                ],
                [
                        row([
                                cell("nodeName", 1),
                                cell("", 1),
                                cell("", 1),
                                cell("nodeId", 1),
                                cell(null, 1),
                                cell("nodeAttrName", 1),
                                cell("nodeAttrName", 1),
                                cell("description", 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1),
                                cell(null, 1)
                        ])
                ]
        ]
    }

    def "skip blank lines"() {
        given:
        def rows = [
                row([
                        cell("nodeName1", 1),
                        cell("", 1),
                        cell("", 1),
                        cell("nodeId1", 1),
                        cell(null, 1),
                        cell("nodeAttrName1", 1),
                        cell("nodeAttrName1", 1),
                        cell("description1", 1),
                        cell("INTEGER", 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1)
                ]),
                row([
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1)
                ]),
                row([
                        cell("nodeName2", 1),
                        cell("", 1),
                        cell("", 1),
                        cell("nodeId2", 1),
                        cell(null, 1),
                        cell("nodeAttrName2", 1),
                        cell("nodeAttrName2", 1),
                        cell("description2", 1),
                        cell("STRING", 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1)
                ]),
                row([
                        cell(" ", 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(" ", 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell(null, 1),
                        cell("", 1),
                        cell("", 1),
                        cell("", 1),
                        cell("", 1),
                        cell("", 1),
                        cell("", 1),
                        cell(" ", 1),
                        cell(null, 1),
                        cell(null, 1)
                ]),
        ]
        sheet.iterator() >> [columnsNames, *rows].iterator()

        when:
        def nodes = converter.convert(sheet)

        then:
        nodes.size() == 2
        nodes.indexed().get(0).name == "nodeName1"
        nodes.indexed().get(0).id == "nodeId1"
        nodes.indexed().get(1).name == "nodeName2"
        nodes.indexed().get(1).id == "nodeId2"
    }
}
