/**
 * Ридер, для результатов поиска
 *
 * @author Aleksandr Bavin
 * @date 2018-03-05
 */
Ext.define('Unidata.proxy.data.reader.JsonSearch', {

    extend: 'Ext.data.reader.Json',

    alias: 'reader.json.search',

    rootProperty: 'hits',

    totalProperty: 'total_count',

    config: {
        /**
         * У BE есть ограничение количества записей в поисковой выдаче
         *
         * @param data
         */
        transform: function (data) {
            var totalCount = data['total_count'],
                totalCountLimit;

            if (totalCountLimit = data['total_count_limit']) {
                if (totalCount > totalCountLimit) {
                    data['total_count'] = totalCountLimit;
                }
            }

            data['total_count_real'] = totalCount;

            return data;
        }
    }

});
