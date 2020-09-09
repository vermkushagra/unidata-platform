/**
 * Прокси для работы с каталогом групп / правил поиска дубликатов
 *
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.proxy.entity.GroupProxy', {

    extend: 'Ext.data.proxy.Rest',

    alias: 'proxy.un.duplicate.grouplist',

    config: {
        reader: null
    },

    actionMethods: {
        create:  'PUT',
        read:    'GET',
        update:  'PUT',
        destroy: 'DELETE'
    },

    constructor: function () {

        var me = this,
            url = Unidata.Config.getMainUrl() + 'internal/matching/group';

        me.callParent(arguments);

        me.setReader({
            type: 'json',
            transform: me.transformIncData.bind(me)
        });

        me.api = {
            read: url,
            update: url
        };

    },

    /**
     * Переопределяем, т.к. стандартные url от REST API нам тут не нужны
     *
     * @param request
     *
     * @returns {String}
     */
    buildUrl: function (request) {
        var rootUrlTpl = Unidata.Config.getMainUrl() + 'internal/matching/group',
            leafUrlTpl = Unidata.Config.getMainUrl() + 'internal/matching/group/{nodeId:htmlEncode}/settings',
            tpl,
            url;

        if (request.getParam('node') !== 'root') {
            tpl = new Ext.Template(leafUrlTpl);

            url = tpl.apply({nodeId: request.getParam('nodeId')});

        } else {
            url = rootUrlTpl;
        }

        request.setUrl(url);

        return Ext.data.proxy.Server.prototype.buildUrl.call(this, request);
    },

    /**
     * Данные с сервера приходят в плоском виде. Нам надо их привести к виду дерева (стандартный
     * способ от сенчи жутко глючный - пришлось написать свой ридер)
     *
     * @param records
     * @returns {Array}
     */
    transformIncData: function (records) {
        var result = [];

        Ext.Array.each(records.content, function (item) {
            var recortItem;

            recortItem = {
                record: Ext.create('Unidata.model.matching.Group', item),
                groupId: item.id,
                text: item.name,
                children: []
            };

            result.push(recortItem);
        });

        return result;
    }
});
