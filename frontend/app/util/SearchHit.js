/**
 * Утилитный класс для работы с SearchHits
 *
 * @author Sergey Shishigin
 * @date 2016-03-16
 */

Ext.define('Unidata.util.SearchHit', {
    singleton: true,

    /**
     * Получить массив etalonId
     *
     * @param searchHits {Unidata.model.search.SearchHit[]}
     * @returns {String[]}
     */
    pluckEtalonIds: function (searchHits) {
        var etalonIds;

        etalonIds = Ext.Array.map(searchHits, function (searchHit) {
            return searchHit.get('etalonId');
        });

        return etalonIds;
    }
});
