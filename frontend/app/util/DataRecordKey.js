/**
 * Утилитный класс для работы с ключами датарекордов
 *
 * @author Sergey Shishigin
 * @date 2016-10-31
 */

Ext.define('Unidata.util.DataRecordKey', {
    singleton: true,

    findDataRecordKey: function (dataRecordKeys, searchFor) {
        var DataRecordKeyUtil = Unidata.util.DataRecordKey,
            found;

        if (!searchFor) {
            return null;
        }

        found = Ext.Array.findBy(dataRecordKeys, function (dataRecordKey) {
            return dataRecordKey && DataRecordKeyUtil.isEqual(dataRecordKey, searchFor);
        });

        return found;
    },

    /**
     * Проверка на равенство двух dataRecordKey
     *
     * @param dataRecordKey1
     * @param dataRecordKey2
     * @returns {boolean}
     */
    isEqual: function (dataRecordKey1, dataRecordKey2) {
        var isEqual;

        if (dataRecordKey1 === dataRecordKey2) {
            isEqual = true;
        } else if (dataRecordKey1 && !dataRecordKey2 || !dataRecordKey1 && dataRecordKey2) {
            isEqual = false;
        } else {
            isEqual = dataRecordKey1.get('etalonId') === dataRecordKey2.get('etalonId');
            // по дате пока не матчим, считаем что все происходит сегодня (в текущем периоде)
            /*&& Ext.Date.isEqual(dataRecordKey1.get('etalonDate'), dataRecordKey2.get('etalonDate'));*/
        }

        return isEqual;
    }
});
