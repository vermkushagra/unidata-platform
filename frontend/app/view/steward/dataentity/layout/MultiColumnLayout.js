/**
 * Лейаут отображения в произвольное количество колонок
 *
 * @author Ivan Marshalkin
 * @date 2016-05-25
 */

Ext.define('Unidata.view.steward.dataentity.layout.MultiColumnLayout', {
    extend: 'Unidata.view.steward.dataentity.layout.AbstractLayout',

    /**
     * {Unidata.view.steward.dataentity.simple.AttributeTablet[]}
     */
    tablets: null,

    referenceHolder: true,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    items: [],

    initComponent: function () {
        this.callParent(arguments);

        // создаем контейнеры
        this.createColumnsContainer();

        // размещаем по контейнерам таблетки
        Ext.Array.each(this.tablets, function (tablet) {
            var isAdmin      = Unidata.Config.isUserAdmin(),
                GroupPanel = Unidata.view.steward.dataentity.group.GroupPanel,
                group = tablet.attributeGroup,
                title = group.title,
                headerTooltip = group.headerTooltip,
                columnContainer,
                panel,
                stateComponentKey,
                stateComponentType,
                cfg,
                metaRecord,
                entityName,
                stateableCfg,
                groupHidden = false;

            metaRecord = tablet.getMetaRecord();
            entityName = metaRecord.get('name');

            if (!isAdmin) {
                groupHidden = tablet.isAllContainersInTabletHidden();
            }

            // конфигурация панели
            cfg = {
                title: Ext.String.htmlEncode(title), // не забываем предотвращение XSS
                headerTooltip: headerTooltip,
                collapsed: title ? true : false,
                entityName: entityName,
                itemsLazyRender: tablet,
                hidden: groupHidden
            };

            // определяем типа компонента с точки зрения хранения состояний
            stateComponentType = GroupPanel.resolveStateComponentType(tablet);
            // формируем ключ получения состояния панели
            stateComponentKey = GroupPanel.buildStateComponentKey(tablet, stateComponentType);
            // формируем конфиг для stateable панели (name + состояние)
            stateableCfg = Unidata.mixin.PanelStateable.getStateableCfg(stateComponentKey);
            // применяем
            Ext.apply(cfg, stateableCfg);

            panel = Ext.create('Unidata.view.steward.dataentity.group.GroupPanel', cfg);
            panel.enableStateable();
            // обновляем лейаутинг группы с задержкой после измения изображения в атрибуте blob
            tablet.on('layoutchanged', function () {
                Ext.defer(panel.updateLayout, 100, panel);
            });

            columnContainer = this.getColumnContainerAt(group.column);
            columnContainer.add(panel);
        }, this);

        // удаляем контейнеры
        this.removeEmptyColumnsContainer();
    },

    /**
     * Возвращает колонку-контейнер по индексу. Если запрошена не сущетсвующая колонка вернет последнюю
     *
     * @param {number|*} index
     */
    getColumnContainerAt: function (index) {
        var collection = this.items,
            column = collection.getAt(index);

        if (!column && collection.getCount() > 0) {
            column = collection.getAt(collection.getCount() - 1);
        }

        return column;
    },

    /**
     * Создает контейнеры в которые мы будем помещать группы
     */
    createColumnsContainer: function () {
        var columnIndexes = [],
            maxColumnIndex,
            i;

        Ext.Array.each(this.tablets, function (tablet) {
            var group = tablet.attributeGroup;

            if (group && Ext.isNumeric(group.column)) {
                Ext.Array.include(columnIndexes, group.column);
            }
        }, this);

        maxColumnIndex = Ext.Array.max(columnIndexes);

        for (i = 0; i <= maxColumnIndex; i++) {
            this.add(this.createColumnContainer());
        }
    },

    /**
     * Удаляет пустые колонки в которые не вставлены группы
     */
    removeEmptyColumnsContainer: function () {
        var removeItems = [];

        if (!this.items) {
            return;
        }

        this.items.each(function (item) {
            // если контейнер пустой то он нам не нужен
            if (!item.items || !item.items.getCount()) {
                Ext.Array.include(removeItems, item);
            }
        });

        this.items.remove(removeItems);
    },

    /**
     * Создает пустой контейнер
     *
     * @returns {*}
     */
    createColumnContainer: function () {
        var container;

        container = Ext.create({
            xtype: 'container',
            cls: 'un-ded-layout-column',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1
        });

        return container;
    }

});
