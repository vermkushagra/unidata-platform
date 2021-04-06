Ext.define('Unidata.view.component.Diagramm', {

    extend: 'Unidata.view.component.diagramm.ZoomPan',

    alias: 'widget.diagrammcomponent',

    draftMode: null,                                  // режим работы с черновиком

    config: {
        /**
         * Параметр для отображения / скрытия nested-элементов
         */
        showNested: false
    },

    initComponent: function () {

        var me = this;

        me.layoutRelations = [];
        me.items = {};
        me.attrRelationPlaceIsDefault = false;

        me.callParent(arguments);

    },

    onRender: function () {

        var me = this;

        me.callParent(arguments);

        me.el.on({
            scope: me,
            load:  me.onLoad
        });

        // необходимо для того, что бы панель с настройками не перекрывала другие элементы
        me.widthExtender = Ext.ComponentManager.create({
            xtype: 'container',
            width: 1600,
            height: 1,
            jsPlumb: me.jsPlumbInstance,
            renderTo: this.jsPlumbCont
        }).show();

        me.initToolbar();
        me.setupJsPlumb();

    },

    /**
     * Тулбар с настройками
     */
    initToolbar: function () {

        var me = this,
            ComponentManager = Ext.ComponentManager;

        me.resetPositionsButton = ComponentManager.create({
            xtype: 'button',
            margin: '5 0 0 0',
            text: Unidata.i18n.t('admin.scheme>resetLocation'),
            renderTo: me.elToolbar,
            listeners: {
                click: me.resetPositions,
                scope: me
            }
        });

        this.nestedVisibilityCheckbox = ComponentManager.create({
            xtype: 'checkbox',
            boxLabel: Unidata.i18n.t('admin.scheme>displayNestedEntities'),
            style: {
                color: '#fff'
            },
            renderTo: me.elToolbar,
            value: me.getShowNested(),
            listeners: {
                change: me.onClickSwitchNestedVisibility,
                scope: me
            }
        });

        this.attrRelationPlaceCheckbox = ComponentManager.create({
            xtype: 'checkbox',
            boxLabel: Unidata.i18n.t('admin.scheme>expandedDataInfo'),
            style: {
                color: '#fff'
            },
            renderTo: me.elToolbar,
            value: !me.attrRelationPlaceIsDefault,
            listeners: {
                change: me.onClickSwitchAttrRelationPlace,
                scope: me
            }
        });

    },

    setupJsPlumb: function () {

        // регистрируем типы линий между итемами
        this.jsPlumbInstance.registerConnectionType('default', {
            paintStyle: {
                lineWidth: 2,
                strokeStyle: '#a3a3a3',
                joinstyle: 'round',
                outlineWidth: 2,
                dashstyle: '2 2'
            },
            hoverPaintStyle: {
                lineWidth: 4,
                strokeStyle: '#a3a3a3',
                outlineWidth: 2,
                dashstyle: '3 1'
            }
        });

        this.jsPlumbInstance.registerConnectionType('lookup', {
            paintStyle: {
                lineWidth: 2,
                strokeStyle: '#a3a3a3',
                joinstyle: 'round',
                outlineWidth: 2,
                dashstyle: '6 2'
            },
            hoverPaintStyle: {
                lineWidth: 4,
                strokeStyle: '#a3a3a3',
                outlineWidth: 2,
                dashstyle: '7 1'
            }
        });

    },

    clearConnections: function () {
        var i,
            connections = this.jsPlumbInstance.getAllConnections();

        for (i = connections.length - 1; i >= 0; i--) {
            this.jsPlumbInstance.detach(connections[i], {
                forceDetach: true
            });
        }
    },

    drawEntityRelations: function () {

        var i,
            relation,
            item,
            toItem,
            fromId,
            toId,
            me = this,
            items = me.items,
            relations = me.entityRelations;

        for (i in relations) {

            if (!relations.hasOwnProperty(i)) {
                continue;
            }

            relation = relations[i];

            fromId = relation.get('fromEntity');
            toId   = relation.get('toEntity');

            if (!items.hasOwnProperty(fromId) || !items.hasOwnProperty(toId)) {
                continue;
            }

            item = items[fromId];
            toItem = items[toId];

            me.drawConnection(
                item.getSourceEndpointUuid(),
                toItem.getTargetEndpointUuid(),
                fromId,
                toId
            );

        }

        me.updateLayout();

    },

    /**
     * Сеттер для связей. Вызывается после того, как уже всё отрисовалось,
     * потому в конце происходит обновление лэйаута
     *
     * @param relations
     */
    setRelations: function (relations) {
        this.entityRelations = relations;
        this.drawEntityRelations();
    },

    setData: function (entities) {
        this.entities = entities;
        this.refresh(true);
    },

    refresh: function (updateLayout) {

        var me = this,
            zoom = this.getZoom();

        this.setZoom(1);

        if (!me.rendered) {
            return;
        }

        me.clearConnections();
        me.clearEntities();
        me.layoutRelations = [];

        me.entities.forEach(function (entity) {
            me.drawEntity(entity);
        });

        me.entities.forEach(function (entity) {
            me.clearRelations(entity);
        });

        me.entities.forEach(function (entity) {
            me.drawRelation(entity);
        });

        me.drawEntityRelations();

        if (updateLayout) {
            this.updateLayout();
        }

        this.setZoom(zoom);
    },

    clearEntities: function () {

        var i,
            items = this.items;

        for (i in items) {

            if (!items.hasOwnProperty(i)) {
                continue;
            }

            items[i].hide();

        }
    },

    /**
     * Делает отрисовку справочников и реестров
     *
     * @param entity
     */
    drawEntity: function (entity) {

        var me = this,
            entityId;

        if (!entity) {
            return;
        }

        entityId = entity.elementId = entity.get('name');

        if (!me.items.hasOwnProperty(entityId)) {

            me.items[entityId] = Ext.ComponentManager.create({
                xtype: 'diagrammcomponent-item',
                entity: entity,
                jsPlumb: me.jsPlumbInstance,
                renderTo: this.jsPlumbCont
            });

        }

        me.items[entityId].show();

        // если разрешено отрисовывать nested, то отрисовываем
        if (me.getShowNested() && (typeof entity.complexAttributes === 'function')) {
            entity.complexAttributes().each(function (attribute) {
                me.drawEntity(attribute.getNestedEntity());
            });
        }

    },

    onDestroy: function () {
        this.callParent(arguments);

        if (this.items) {
            Ext.Object.each(this.items, function (key, item) {
                if (item.destroy) {
                    item.destroy();
                }
            });

            this.items = {};
        }
    },

    clearRelations: function (entity) {

        var me = this,
            items = me.items;

        if (!entity) {
            return;
        }

        items[entity.get('name')].clearEndpoints(); // очищаем все точки

        // если разрешено отрисовывать nested, то отрисовываем точки для связей
        if ((typeof entity.complexAttributes === 'function')) {

            entity.complexAttributes().each(function (attribute) {

                var nestedEntity = attribute.getNestedEntity();

                if (items.hasOwnProperty(nestedEntity.get('name'))) {
                    me.clearRelations(nestedEntity);
                }

            });

        }

    },

    /**
     * Рисует точки для связей и передаёт отрисовку связей в функцию drawConnection
     *
     * @param entity
     */
    drawRelation: function (entity) {

        var me = this,
            entityId,
            item,
            eachFn,
            items = me.items;

        if (!entity) {
            return;
        }

        entityId = entity.get('name');

        item = items[entityId];

        // если разрешено отрисовывать nested, то отрисовываем точки для связей
        if (me.getShowNested() && (typeof entity.complexAttributes === 'function')) {

            entity.complexAttributes().each(function (attribute) {

                var nestedEntity = attribute.getNestedEntity(),
                    nestedEntityId = nestedEntity.get('name');

                me.drawConnection(
                    items[entityId].getSourceEndpointUuid(),
                    items[nestedEntityId].getTargetEndpointUuid(),
                    entityId,
                    nestedEntityId
                );

                me.drawRelation(nestedEntity);

            });

        }

        eachFn = Ext.bind(this.drawLookupEntityTypePoints, this, [item, entityId], true);

        // отрисовываем точки для ссылок на справочники
        if (typeof entity.simpleAttributes === 'function') {
            entity.simpleAttributes().each(eachFn);
        }

        if (typeof entity.arrayAttributes === 'function') {
            entity.arrayAttributes().each(eachFn);
        }

    },

    drawLookupEntityTypePoints: function (attr, index, count, item, entityId) {

        var me = this,
            toEntity,
            toItem,
            fromUuid;

        if (attr.get('typeCategory') !== 'lookupEntityType') {
            return;
        }

        toEntity = attr.get('typeValue');
        toItem   = me.items[toEntity];

        if (!toItem) {
            return;
        }

        if (me.attrRelationPlaceIsDefault) {
            fromUuid = item.getSourceEndpointUuid();
        } else {
            fromUuid = item.addSourceEndpointForAttribute(attr.get('name'));
        }

        me.drawConnection(
            fromUuid,
            toItem.getTargetEndpointUuid(),
            entityId,
            toEntity,
            true
        );

    },

    /**
     * Рисует линию между точками
     *
     * @param fromUuid
     * @param toUuid
     * @param fromEntity
     * @param toEntity
     * @param isLinkToLookup
     */
    drawConnection: function (fromUuid, toUuid, fromEntity, toEntity, isLinkToLookup) {

        var config = {
                uuids: [
                    fromUuid,
                    toUuid
                ],
                type: isLinkToLookup ? 'lookup' : 'default',
                editable: false,
                detachable: false
            };

        if (fromEntity === toEntity) {
            // если линия сама к себе, то используем огибание по периметру
            config.connector = [
                'Flowchart',
                {
                    cornerRadius: 20
                }
            ];
        } else {
            // если связь с другой сущностью - используем кривую Безье
            config.connector = [
                'Bezier',
                {
                    curviness: 150
                }
            ];
        }

        this.jsPlumbInstance.connect(config);

        // тут сохраняем информацию о связях для лэйаута
        this.layoutRelations.push({
            from: fromEntity,
            to:   toEntity
        });

    },

    /**
     * Перерисовывает лэйаут на основании известной информации о связях
     */
    updateLayout: function () {

        var i, entity, entityName, cachedPosition, el, relation, nodeId,
            me = this,
            graph = new dagre.graphlib.Graph(),
            items = me.items,
            zoom = this.getZoom(),
            relations = me.layoutRelations,
            maxLeft = 0,
            left,
            top;

        graph.setGraph({
            rankdir: 'TB',
            align: 'DL',
            nodesep: 50,
            edgesep: 50,
            ranksep: 100,
            marginx: 50,
            marginy: 50
        });

        graph.setDefaultEdgeLabel(function () {
            return {};
        });

        for (i in items) {

            if (!items.hasOwnProperty(i)) {
                continue;
            }

            entity = items[i].getEntity();
            entityName = entity.get('name');

            el = items[i].getEl();

            cachedPosition = Unidata.LocalStorage.getCurrentUserValue(this.getLocalStorageNamespace(), entityName);

            if (!cachedPosition) {
                graph.setNode(el.id, {
                    width: el.getWidth() / zoom,
                    height: el.getHeight()
                });
            } else {
                if (maxLeft < cachedPosition['left']) {
                    maxLeft = cachedPosition['left'];
                }

                el.setStyle({
                    left: cachedPosition['left'] + 'px',
                    top: cachedPosition['top'] + 'px'
                });
            }
        }

        for (i in relations) {

            if (!relations.hasOwnProperty(i)) {
                continue;
            }

            relation = relations[i];

            if (!items.hasOwnProperty(relation.from) || !items.hasOwnProperty(relation.to)) {
                continue;
            }

            // устанавливаем связь между итемами
            graph.setEdge(items[relation.from].getId(), items[relation.to].getId(), {
                weight: 10,
                minlen: 2
            });

        }

        dagre.layout(graph);

        graph.nodes().forEach(function (nodeId) {

            if (!graph.node(nodeId)) {
                return;
            }

            el = Ext.getCmp(nodeId).getEl();

            left = graph.node(nodeId).x - Math.round(el.getWidth() / 2);
            top = graph.node(nodeId).y - Math.round(el.getHeight() / 2);

            if (maxLeft < left) {
                maxLeft = left;
            }

            el.setStyle({
                left: left + 'px',
                top: top + 'px'
            });

        });

        me.widthExtender.getEl().setStyle({
            left: maxLeft + 'px',
            top: '0px'
        });

        jsPlumb.repaintEverything();

        me.jsPlumbInstance.repaintEverything();
        me.jsPlumbInstance.draggable(jsPlumb.getSelector('.unidata-model-container'), {
            stop: Ext.bind(this.onItemDragStop, this)
        });
    },

    /**
     * При передвижении, сохраняем позицию в localStorage
     *
     * @param event
     * @param info
     */
    onItemDragStop: function (event, info) {
        var component = Ext.getCmp(info.helper.context.id),
            entity = component.getEntity(),
            entityName = entity.get('name'),
            left = info.position.left,
            top = info.position.top;

        Unidata.LocalStorage.setCurrentUserValue(
            this.getLocalStorageNamespace(),
            entityName,
            {
                left: left,
                top: top
            }
        );
    },

    resetPositions: function () {
        Unidata.LocalStorage.removeCurrentUserNamespace(this.getLocalStorageNamespace());
        this.refresh(true);
    },

    getShowNested: function () {
        return this.config.showNested;
    },

    setShowNested: function (value) {
        this.config.showNested = value;
        this.refresh(true);
    },

    onLoad: function () {
        this.fireEvent('load', this);
    },

    onClickSwitchNestedVisibility: function (checkbox, newValue) {
        this.setShowNested(newValue);
    },

    onClickSwitchAttrRelationPlace: function (checkbox, newValue) {
        this.attrRelationPlaceIsDefault = !newValue;
        this.refresh();
    }

});
