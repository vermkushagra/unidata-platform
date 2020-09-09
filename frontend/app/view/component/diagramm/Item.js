Ext.define('Unidata.view.component.diagramm.Item', {

    alias: 'widget.diagrammcomponent-item',

    extend: 'Ext.Component',

    requires: [
        'Unidata.util.MetaRecord'
    ],

    config: {
        autoRender: true,
        data: {
            name: 'name_example',
            displayName: 'display name example',
            entityType: 'default',
            simpleAttributes: [
                {
                    name: 'attr_name_example',
                    displayName: 'attribute display name example'
                }
            ],
            arrayAttributes: [
                {
                    name: 'attr_name_example',
                    displayName: 'attribute display name example'
                }
            ]
        },
        entity: null,
        jsPlumb: null
    },

    autoEl: {
        tag: 'div',
        cls: 'unidata-model-container'
    },

    tpl: [
        '<table>',
            '<col style="width: 200px">',
            '<col style="min-width: 100px">',
            '<tr class="unidata-model-table-title-row">',
                '<td class="unidata-model-table-title-name" colspan="2">{name:htmlEncode}</td>',
            '</tr>',
            '<tr class="unidata-model-table-title-row">',
                '<td class="unidata-model-table-title-displayName" colspan="2">{displayName:htmlEncode}</td>',
            '</tr>',
            '<tpl for="allAttributes">',
                '<tr class="unidata-model-row" id="unidata-record-{parent.id}__{name}">',
                    '<td class="unidata-model-row-title">{displayName:htmlEncode}:</td>',
                    '<td class="unidata-model-row-value">{name:htmlEncode}</td>',
                '</tr>',
            '</tpl>',
        '</table>'
    ],

    initComponent: function () {

        var me = this,
            entity = me.getEntity(),
            data = entity.getData(true);

        data.id = me.id;
        data.entityType = me.getEntityType(entity);
        data.simpleAttributes = me.filterAttributes(data.simpleAttributes);

        data.allAttributes = Ext.Array.merge(data.simpleAttributes, data.arrayAttributes);

        data.allAttributes = Ext.Array.sort(data.allAttributes, function (a, b) {
            return a.order - b.order;
        });

        me.data = data;
        me.endpoints = [];

        me.callParent();

    },

    getEntityType: function (entity) {

        var MetaRecordUtils = Unidata.util.MetaRecord;

        switch (MetaRecordUtils.getType(entity)) {
            case MetaRecordUtils.TYPE_ENTITY:
                return 'entity';
            case MetaRecordUtils.TYPE_LOOKUP:
                return 'lookup';
            case MetaRecordUtils.TYPE_NESTED:
                return 'nested';
            default:
                return 'default';
        }
    },

    filterAttributes: function (attributesSrc) {
        var i,
            attr,
            importantAttrCount = 0,
            attributes = [];

        for (i = 0; i < attributesSrc.length; i++) {
            attr = attributesSrc[i];

            if (attr.typeCategory === 'lookupEntityType') {
                importantAttrCount++;
                attributes.unshift(attr);
            } else {
                attributes.push(attr);
            }
        }

        if (attributes.length > 5) {
            if (importantAttrCount < 5) {
                attributes.length = 5;
            } else {
                attributes.length = importantAttrCount;
            }
        }

        return attributes;

    },

    render: function () {

        var me = this;

        me.callParent(arguments);

        me.elTable = me.el.selectNode('table', false);

        me.elTable.addCls([
            'unidata-model-table',
            'unidata-model-table__' + me.data.entityType,
            'animated',
            'fadeIn'
        ]);

    },

    clearEndpoints: function () {

        var i,
            me = this,
            jsPlumbInstance = me.getJsPlumb(),
            endpoints = me.endpoints;

        for (i = 0; i < endpoints.length; i++) {
            jsPlumbInstance.deleteEndpoint(jsPlumbInstance.getEndpoint(endpoints[i]));
        }

        me.endpoints = [];

        // удаляем кэширующие функции
        delete me.getSourceEndpointUuid;
        delete me.getTargetEndpointUuid;

        me.importantAttrCount = 1;

    },

    getSourceEndpointUuid: function () {

        var uuid = this.addSourceEndpoint();

        this.getSourceEndpointUuid = function () {
            return uuid;
        };

        return uuid;
    },

    addSourceEndpoint: function (elementId, uuid, anchor) {

        var me = this,
            id = me.id,
            sourceEndpointCfg = {
                connectionsDetachable: false,
                endpoint: 'Dot',
                paintStyle: {
                    strokeStyle: '#2D5B75',
                    radius: 4,
                    lineWidth: 3
                },
                maxConnections: -1,
                isSource: true,
                hoverPaintStyle: {
                    fillStyle: '#216477',
                    strokeStyle: '#216477'
                },
                dragOptions: {}
            };

        elementId = elementId || id;
        anchor    = anchor || 'BottomCenter';
        uuid      = uuid || 'BottomCenter-' + id;

        me.getJsPlumb().addEndpoint(elementId, sourceEndpointCfg, {
            anchor: anchor,
            uuid: uuid
        });

        me.endpoints.push(uuid);

        return uuid;

    },

    addSourceEndpointForAttribute: function (attributeName) {

        var fromId,
            fromUuid,
            anchor,
            me = this,
            id = me.id;

        if (!me.importantAttrCount) {
            me.importantAttrCount = 1;
        }

        fromId = 'unidata-record-' + id + '__' + attributeName;
        fromUuid = 'Right' + fromId;
        anchor = me.importantAttrCount++ % 2 === 1 ? 'Right' : 'Left';

        return me.addSourceEndpoint(fromId, fromUuid, anchor);

    },

    getTargetEndpointUuid: function () {

        var uuid = this.addTargetEndpoint();

        this.getTargetEndpointUuid = function () {
            return uuid;
        };

        return uuid;
    },

    addTargetEndpoint: function () {

        var me = this,
            id = me.id,
            uuid = 'TopCenter-' + id,
            targetEndpointCfg = {
                endpoint: 'Dot',
                paintStyle: {
                    fillStyle: '#2D5B75',
                    radius: 4
                },
                maxConnections: -1,
                dropOptions: {
                    hoverClass: 'hover',
                    activeClass: 'active'
                },
                isTarget: true
            };

        me.getJsPlumb().addEndpoint(id, targetEndpointCfg, {
            anchor: 'TopCenter',
            uuid: uuid
        });

        me.endpoints.push(uuid);

        return uuid;
    }

});
