package com.unidata.mdm.cleanse.postaladdress.addressmaster

import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Unroll
import com.unidata.mdm.cleanse.postaladdress.addressmaster.dto.AddressList
import spock.lang.Specification

import static com.unidata.mdm.cleanse.postaladdress.addressmaster.dto.Address.MatchStatus.*

/**
 * @author Pavel Alexeev.
 * @created 2016-01-20 17:27.
 */
@Ignore
class AddressmasterClientTest extends Specification {
    @Shared AddressmasterClient client = new AddressmasterClient();

    @Unroll
    def "addressMatch: #address"() {
        when:
            AddressList addressList = client.addressMatch(address);

        then:
            noExceptionThrown()
            addressList
            addressList.addressList.size() == 10
            addressList.addressList.take(3)*.MATCH_STATUS == top3Statuses
            addressList.addressList.take(3)*.DEBUG_INFO.matchScore == top3score
            addressList.addressList.take(3)*.ZIPCODE == top3ZipCodes
            addressList.addressList.take(3)*.fullAddressString == top3FullAddressStrings
            addressList.addressList.take(3)*.intermediateAddressString == top3IntermediateAddressString
            addressList.addressList.take(3)*.houseNumberString == top3HouseNumberString
            addressList.addressList.take(3)*.buildingNumberString == top3BuildingNumberString
            addressList.addressList.take(3)*.buildingStructureString == top3BuildingStructureString

        where:
            address                                                                   || top3Statuses                                   || top3score     || top3ZipCodes                   || top3FullAddressStrings                                                                                                                                                                                                                    || top3IntermediateAddressString                                                                                                                                                                                     || top3HouseNumberString         || top3BuildingNumberString || top3BuildingStructureString
            'Средний 88а Петербург'                                                   |  [GOOD, GOOD_ONLY_STREET_RECOGNIZED, REJECTED]  |  [46L, 35, 33] | ['199106', '197022', '197022']  | ['199106, город Санкт-Петербург, проспект Средний В.О., дом 88, литер А', '197022, город Санкт-Петербург, набережная Реки Средней Невки', '197022, город Санкт-Петербург, аллея Средняя']                                                  | ['город Санкт-Петербург, проспект Средний В.О.', 'город Санкт-Петербург, набережная Реки Средней Невки', 'город Санкт-Петербург, аллея Средняя']                                                                   | ['дом 88', '', '']             | ['', '', '']          |  ['литер А', '', '']
            'Средний 88а'                                                             |  [REJECTED, REJECTED, REJECTED]                 |  [-4, -6, -8]  | ['162501', '442142', '423516']  | ['162501, область Вологодская, район Кадуйский, деревня Середник', '442142, область Пензенская, район Нижнеломовский, поселок Средний, улица Средняя', '423516, республика Татарстан, район Заинский, село Средний Багряж, улица Средняя'] | ['область Вологодская, район Кадуйский, деревня Середник', 'область Пензенская, район Нижнеломовский, поселок Средний, улица Средняя', 'республика Татарстан, район Заинский, село Средний Багряж, улица Средняя'] | ['', '', '']                   | ['', '', '']          |  ['', '', '']
            'Средний В.О. 88а'                                                        |  [REJECTED, REJECTED, REJECTED]                 |  [3, -16, -16] | ['199106', '199178', '199406']  | ['199106, город Санкт-Петербург, проспект Средний В.О., дом 88, литер А', '199178, город Санкт-Петербург, линия 15-я В.О., дом 88, литер А', '199406, город Санкт-Петербург, проспект Малый В.О., дом 88, литер А']                        | ['город Санкт-Петербург, проспект Средний В.О.', 'город Санкт-Петербург, линия 15-я В.О.', 'город Санкт-Петербург, проспект Малый В.О.']                                                                           | ['дом 88', 'дом 88', 'дом 88'] | ['', '', '']          |  ['литер А', 'литер А', 'литер А']
            'СПб Средий проспект 88а'                                                 |  [GOOD, REJECTED, REJECTED]                     |  [35, 20, 20]  | ['199106', '197720', '197706']  | ['199106, город Санкт-Петербург, проспект Средний В.О., дом 88, литер А', '197720, город Санкт-Петербург, город Зеленогорск, проспект Средний', '197706, город Санкт-Петербург, город Сестрорецк, проспект Средний']                       | ['город Санкт-Петербург, проспект Средний В.О.', 'город Санкт-Петербург, город Зеленогорск, проспект Средний', 'город Санкт-Петербург, город Сестрорецк, проспект Средний']                                        | ['дом 88', '', '']             | ['', '', '']          |  ['литер А', '', '']
            // Normalized variant from previous r!            |
            '199106, город. Санкт-Петербург, проспект. Средний В.О., дом 88, литер А' |  [GOOD, GOOD, GOOD]                             |  [42, 33, 31]  | ['199106', '199406', '199178']  | ['199106, город Санкт-Петербург, проспект Средний В.О., дом 88, литер А', '199406, город Санкт-Петербург, проспект Малый В.О., дом 88, литер А', '199178, город Санкт-Петербург, линия 15-я В.О., дом 88, литер А']                        | ['город Санкт-Петербург, проспект Средний В.О.', 'город Санкт-Петербург, проспект Малый В.О.', 'город Санкт-Петербург, линия 15-я В.О.']                                                                           | ['дом 88', 'дом 88', 'дом 88'] | ['', '', '']          |  ['литер А', 'литер А', 'литер А']
    }

    def "addressMatch: possible several zip codes in field"(){
        when:
            AddressList addressList = client.addressMatch('Средний В.О. 88а');
        then:
            noExceptionThrown()
            addressList
            addressList.addressList*.ZIPCODE == ['199106', '199178', '199406', '199106', '199106', '199034 199178', '199106', '199034 199004', '199034 199178', '199106']
    }
}
