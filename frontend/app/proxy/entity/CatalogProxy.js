/**
 * Прокси для работы с каталогом
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-15
 */
Ext.define('Unidata.proxy.entity.CatalogProxy', {

    extend: 'Ext.data.proxy.Rest',

    alias: 'proxy.un.entity.catalog',

    config: {
        reader: null,
        showRoot: true,
        onlyCatalog: true,
        filterEmptyGroups: true,
        needEntities: true,
        needLookupEntities: true,
        draftMode: false
    },

    actionMethods: {
        create:  'PUT',
        read:    'GET',
        update:  'PUT',
        destroy: 'DELETE'
    },

    constructor: function () {

        var me = this,
            url = Unidata.Config.getMainUrl() + 'internal/meta/entitiesGroup';

        me.callParent(arguments);

        me.setReader({
            type: 'json',
            transform: me.transformIncData.bind(me),
            typeProperty: me.getItemModel.bind(me)
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
        var url,
            params;

        url = this.getUrl(request);

        if (params = request.getParams()) {
            // удалёем ненужный id - в ответ всегда приходит всё дерево
            delete params[this.getIdParam()];
        }

        if (!this.onlyCatalog) {
            request.setParam('filled', 'true');
        }

        request.setParam('draft', this.getDraftMode());

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
        var me = this,
            i,
            j,
            ln,
            srcNode,
            node,
            parentName,
            result = [],
            nodeMap = {},
            entities,
            lookupEntities,
            onlyCatalog = me.onlyCatalog,
            needEntities = me.needEntities && !onlyCatalog,
            needLookupEntities = me.needLookupEntities && !onlyCatalog,
            cloneRecords;

        cloneRecords = Ext.clone(records);

        if (cloneRecords && cloneRecords.content) {
            records = cloneRecords.content.groupNodes || [];
        } else {
            records = [];
        }

        // сортируем группы по алфавиту
        records = Ext.Array.sort(records, function (a, b) {
            var aTitle = a.title,
                bTitle = b.title;

            if (aTitle > bTitle) {
                return -1;
            } else if (aTitle < bTitle) {
                return 1;
            }

            return 0;
        });

        ln = records.length;

        for (i = 0; i < ln; i++) {

            srcNode = records[i];

            node = {
                groupName: srcNode.groupName,
                displayName: srcNode.title,
                children: []
            };

            nodeMap[node.groupName] = node;

            entities = srcNode.entities;

            if (needEntities && entities && entities.length) {
                for (j = 0; j < entities.length; j++) {
                    entities[j].leaf = true;
                    entities[j].type = 'Entity';
                    node.children.push(entities[j]);
                }
            }

            lookupEntities = srcNode.lookupEntities;

            if (needLookupEntities && lookupEntities && lookupEntities.length) {
                for (j = 0; j < lookupEntities.length; j++) {
                    lookupEntities[j].leaf = true;
                    lookupEntities[j].type = 'LookupEntity';
                    node.children.push(lookupEntities[j]);
                }
            }

            node.expanded = (node.children.length > 0);

            records[i] = node;

        }

        for (i = 0; i < ln; i++) {

            srcNode = records[i];

            parentName = srcNode.groupName.split('.');
            parentName.pop();
            parentName = parentName.join('.');

            if (!parentName || !nodeMap.hasOwnProperty(parentName)) {
                result.push(srcNode);
            } else {
                nodeMap[parentName].expanded = true;
                nodeMap[parentName].children.unshift(srcNode);
            }
        }

        if (this.filterEmptyGroups) {
            this.clearEmptyGroups(result[0]);
        }

        return this.showRoot ? result : result[0];
    },

    clearEmptyGroups: function (data) {
        var result = 0,
            children,
            i;

        // удивительно но факт: такое тоже бывает
        if (!data) {
            return result;
        }

        children = data.children;

        for (i = children.length - 1; i >= 0; i--) {
            if (!children[i].leaf && !this.clearEmptyGroups(children[i])) {
                children.splice(i, 1);
            }
        }

        result = children.length;

        return result;
    },

    getItemModel: function (data) {
        switch (data.type) {
            case 'Entity':
                return 'Unidata.model.entity.catalog.Entity';
            case 'LookupEntity':
                return 'Unidata.model.entity.catalog.LookupEntity';
            default:
                return 'Unidata.model.entity.Catalog';
        }
    }

});
