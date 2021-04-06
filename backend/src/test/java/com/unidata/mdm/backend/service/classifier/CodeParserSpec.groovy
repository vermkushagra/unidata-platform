package com.unidata.mdm.backend.service.classifier

import org.apache.commons.lang3.tuple.Pair
import spock.lang.Specification
import spock.lang.Unroll

class CodeParserSpec extends Specification {

    @Unroll
    def "Code pattern '#codePattern' invalid symbols in '#invalidSymbols'"() {
        expect:
        CodeParser.validateCodePatternContent(codePattern) == invalidSymbols

        where:
        codePattern     | invalidSymbols
        'xXx'           | [] as Set
        'xx.xx'         | [] as Set
        'xx+xx'         | [] as Set
        'xx-xx+xx+xx'   | [] as Set
        'xx.xx-xx.xx'   | [] as Set
        'xx/xx/xxxx'    | [] as Set
        'xx_xx_xx'      | [] as Set
        '-xx-xx-'       | [] as Set
        'x xx x'        | [] as Set
        'xx xx'         | [] as Set
        'xx0 xx'        | [] as Set
        '0xx xx'        | [wrongZeroFillerPosition(1)] as Set
        'x0x xx'        | [wrongZeroFillerPosition(2)] as Set
        'xx 0xx'        | [wrongZeroFillerPosition(4)] as Set
        'xx x0x'        | [wrongZeroFillerPosition(5)] as Set
        'xx x0x0'       | [wrongZeroFillerPosition(5), wrongZeroFillerPosition(7)] as Set
        'xx00 x0x'      | [wrongZeroFillerPosition(4), wrongZeroFillerPosition(7)] as Set
        'ZZZ'           | [unknownSymbol(1), unknownSymbol(2), unknownSymbol(3)] as Set
        'xx*xx*xx'      | [unknownSymbol(3), unknownSymbol(6)] as Set
        'aa/bb'         | [unknownSymbol(1), unknownSymbol(2), unknownSymbol(4), unknownSymbol(5)] as Set
        'aa+bb+cc'      | [unknownSymbol(1), unknownSymbol(2), unknownSymbol(4), unknownSymbol(5), unknownSymbol(7), unknownSymbol(8)] as Set
        'abcd'          | [unknownSymbol(1), unknownSymbol(2), unknownSymbol(3), unknownSymbol(4)] as Set
        'xx--xx'        | [mustBePlaceholder(4)] as Set
        'xx+-xx'        | [mustBePlaceholder(4)] as Set
        'xx//xx'        | [mustBePlaceholder(4)] as Set
        'x*x//xx'       | [unknownSymbol(2), mustBePlaceholder(5)] as Set
    }

    @Unroll
    def "Regexp code patter for code pattern '#codePattern' should be '#regexpCodePattern'"() {
        expect:
        CodeParser.regexpCodePatterBuilder(codePattern).pattern() == regexpCodePattern

        where:
        codePattern          | regexpCodePattern
        'xxx'                | '^(\\d{3})$'
        'xx.xxx'             | '^(\\d{2})(?>\\.(\\d{3}))?$'
        'xx.xxx.xxxx'        | '^(\\d{2})(?>\\.(\\d{3})(?>\\.(\\d{4}))?)?$'
        'xx+xx+xx'           | '^(\\d{2})(?>(\\d{2})(?>(\\d{2}))?)?$'
        'xx-xx-xx'           | '^(\\d{2})(?>\\-(\\d{2})(?>\\-(\\d{2}))?)?$'
        'xx-xx+xx+xx'        | '^(\\d{2})(?>\\-(\\d{2})(?>(\\d{2})(?>(\\d{2}))?)?)?$'
        '-xxx.xxx'           | '^\\-(\\d{3})(?>\\.(\\d{3}))?$'
        'xxx xxx'            | '^(\\d{3})(?>\\s(\\d{3}))?$'
        'xx0.xx.xx'          | '^(\\d{2})(?>(?>\\.00)|(?>\\.(\\d{2})(?>\\.(\\d{2}))?))$'
        'xx0 xx'             | '^(\\d{2})(?>(?>\\s00)|(?>\\s(\\d{2})))$'
        'xx0 xx0 xx'         | '^(\\d{2})(?>(?>\\s00\\s00)|(?>\\s(\\d{2})(?>(?>\\s00)|(?>\\s(\\d{2})))))$'
        'xx.x+x.x+x.xx0+x'   | '^(\\d{2})(?>\\.(\\d{1})(?>(\\d{1})(?>\\.(\\d{1})(?>(\\d{1})(?>\\.(\\d{2})(?>(?>0)|(?>(\\d{1}))))?)?)?)?)?$'
    }

    @Unroll
    def "Node id for code '#code' should be '#nodeId'"() {
        expect:
        CodeParser.toNodeId(code) == nodeId

        where:
        code        | nodeId
        '05/01.00'  | '050100'
        '05.01.00'  | '050100'
        '050100'    | '050100'
    }

    @Unroll
    def "All parent nodes ids for code '#code' should be '#parentNodesIds'"() {
        expect:
        CodeParser.extractParentIds(code, codePattern) == parentNodesIds

        where:
        code            | codePattern        | parentNodesIds
        '05/01.00'      | 'xx0/xx0.xx'       | ['050000']
        '05.01.01'      | 'xx.xx.xx'         | ['0501', '05']
        '050100'        | 'xx0+xx+xx'        | ['0501', '0500']
        '0501'          | 'xx0+xx'           | ['0500']
        '050101'        | 'xx0+xx0+xx'       | ['050100', '050000']
        '01.11.11.121'  | 'xx.x+x.x+x.xx0+x' | ['011111120', '011111', '01111', '0111', '011', '01']
    }

    @Unroll
    def "Parent node id for code '#code' should be '#parentNodeId'"() {
        expect:
        CodeParser.extractParentId(code, codePattern) == parentNodeId

        where:
        code            | codePattern        | parentNodeId
        '05/01.00'      | 'xx0/xx0.xx'       | '050000'
        '05.01.01'      | 'xx.xx.xx'         | '0501'
        '050100'        | 'xx0+xx+xx'        | '0501'
        '0501'          | 'xx0+xx'           | '0500'
        '050101'        | 'xx0+xx0+xx'       | '050100'
        '01.11.11.121'  | 'xx.x+x.x+x.xx0+x' | '011111120'
    }


    def unknownSymbol(position) {
        Pair.of(position, InvalidSymbolReason.UNKNOWN_SYMBOL)
    }

    def mustBePlaceholder(position) {
        Pair.of(position, InvalidSymbolReason.MUST_BE_PLACEHOLDER)
    }

    def wrongZeroFillerPosition(position) {
        Pair.of(position, InvalidSymbolReason.WRONG_ZERO_FILLER_POSITION)
    }
}
