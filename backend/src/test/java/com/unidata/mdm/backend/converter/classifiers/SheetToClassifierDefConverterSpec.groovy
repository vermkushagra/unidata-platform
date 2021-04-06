package com.unidata.mdm.backend.converter.classifiers

import com.unidata.mdm.backend.common.exception.DataProcessingException
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class SheetToClassifierDefConverterSpec extends Specification {

    @Shared converter = new SheetToClassifierDefConverter()
    def sheet = Mock(XSSFSheet)
    def metaRow = Mock(XSSFRow)
    def dataRow = Mock(XSSFRow)
    def columnsNames = [cell("name"), cell("description"), cell("displayName"), cell("codePattern")]

    def cell(value) {
        def cell = Mock(Cell)
        cell.getStringCellValue() >> value
        cell.toString() >> value
        cell
    }

    def setup() {
        sheet.getRow(0) >> metaRow
        sheet.getRow(1) >> dataRow
        metaRow.cellIterator() >> columnsNames.iterator()
    }

    def "convert sheet to classifier"() {
        given:
        def columnsValues = [cell("classifier"), cell("classifier description"), cell("Классификатор"), cell("XX XX")]
        dataRow.cellIterator() >> columnsValues.iterator()

        when:
        def classifier = converter.convert(sheet)

        then:
        classifier.name == "classifier"
        classifier.displayName == "Классификатор"
        classifier.description == "classifier description"
        classifier.codePattern == "XX XX"

    }

    @Unroll
    def "throw exception when no required fields values in '#columnsValues'"() {
        given:
        dataRow.cellIterator() >> columnsValues.iterator()

        when:
        converter.convert(sheet)

        then:
        thrown(DataProcessingException)

        where:
        columnsValues << [
                [cell(null), cell("classifier description"), cell("Классификатор"), cell("XX XX")],
                [cell("classifier"), cell("classifier description"), cell(null), cell("XX XX")],
                [cell(null), cell("classifier description"), cell(null), cell("XX XX")]
        ]
    }
}
