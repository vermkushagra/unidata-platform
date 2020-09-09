Ext.define('Unidata.view.admin.entity.metarecord.presentation.attributegroup.GroupController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord.presentation.attributegroup',

    initAttributes: function () {
        var view = this.getView(),
            viewModel     = this.getViewModel(),
            attributeGrid = view.getAttributeGrid(),
            store         = attributeGrid.getStore(),
            currentRecord = viewModel.get('currentRecord'),
            data          = [],
            codeAttribute;

        // первым помещаем кодовый атрибут
        if (Unidata.util.MetaRecord.isLookup(currentRecord)) {
            codeAttribute = currentRecord.getCodeAttribute();

            if (codeAttribute) {
                data.push({
                    name: codeAttribute.get('name'),
                    displayName: codeAttribute.get('displayName')
                });
            }

            // за кодовым атрибутом размещаем альтернативные кодовые
            currentRecord.aliasCodeAttributes().each(function (aliasCodeAttribute) {
                data.push({
                    name: aliasCodeAttribute.get('name'),
                    displayName: aliasCodeAttribute.get('displayName')
                });
            });
        }

        // после кодового перечисляем простые атрибуты
        currentRecord.simpleAttributes().each(function (attribute) {
            data.push({
                name: attribute.get('name'),
                displayName: attribute.get('displayName')
            });
        });

        // перечисляем атрибуты-массивы первого уровня
        currentRecord.arrayAttributes().each(function (attribute) {
            data.push({
                name: attribute.get('name'),
                displayName: attribute.get('displayName')
            });
        });

        store.getProxy().setData(data);
        store.load();

        // восстанавливаем группы
        this.restoreAttributeGroup();
        // фильтруем используемые атрибуты
        this.filterUsedAttribute();
    },

    filterUsedAttribute: function () {
        var me        = this,
            view = this.getView(),
            attributeGrid = view.getAttributeGrid(),
            store         = attributeGrid.getStore(),
            remove    = [];

        store.each(function (record) {
            if (me.isAttributeUsed(record.get('name'))) {
                remove.push(record);
            }
        });

        store.remove(remove);
    },

    onBeforeRemoveGroup: function () {
        // здесь можно отменить удаление группы если необходимо
    },

    onRemoveGroup: function (groupTablet, records) {
        var view           = this.getView(),
            columnsContainer = this.lookupReference('columnsContainer'),
            attributeStore = view.getAttributeGrid().getStore();

        attributeStore.add(records);

        columnsContainer.items.each(function (column) {
            column.removeTablet(groupTablet);
        });

        this.updateAttributeGroupInModel();
    },

    onMoveGroup: function (tablet, direction) {
        var columnsContainer = this.lookupReference('columnsContainer'),
            tabletIndex,
            tabletOwner,
            tabletOwnerIndex,
            targetTablet;

        columnsContainer.items.each(function (column, index) {
            if (column.contains(tablet, true)) {
                tabletOwner = column;
                tabletOwnerIndex = index;

                return false;
            }
        });

        if (!tabletOwner) {
            return;
        }

        if (direction === 'up') {
            tabletIndex = tabletOwner.getTabletIndex(tablet);

            if (tabletIndex > 0) {
                tabletOwner.cutTablet(tablet);
                tabletOwner.insertAttributeGroup(tablet, tabletIndex - 1);
            }
        } else if (direction === 'down') {
            tabletIndex = tabletOwner.getTabletIndex(tablet);
            tabletOwner.cutTablet(tablet);
            tabletOwner.insertAttributeGroup(tablet, tabletIndex + 1);
        } else if (direction === 'right') {
            targetTablet = this.getColumn(tabletOwnerIndex + 1);
        } else if (direction === 'left') {
            targetTablet = this.getColumn(tabletOwnerIndex - 1);
        }

        if (targetTablet) {
            tabletOwner.cutTablet(tablet);
            targetTablet.appendAttributeGroup(tablet);
        }
    },

    /**
     * Возвращает колонку по индексу,
     * создаёт её, если это необходимо
     * @param {number} index
     * @returns {Unidata.view.admin.entity.metarecord.presentation.attributegroup.GroupColumn}
     */
    getColumn: function (index) {
        var columnsContainer,
            column;

        if (index < 0) {
            index = 0;
        }

        columnsContainer = this.lookupReference('columnsContainer');
        column = columnsContainer.items.getAt(index);

        if (!column) {
            column = columnsContainer.insert(index, this.createColumn());
        }

        return column;
    },

    createColumn: function () {
        var view = this.getView(),
            widget;

        widget = Ext.widget({
            xtype: 'admin.entity.metarecord.presentation.attributegroup.column',
            referenceHolder: true,
            maxWidth: 350,
            readOnly: view.getReadOnly(),
            flex: 1,
            listeners: {
                tabletcountchange: 'onTabletCountChange',
                beforeremovegroup: 'onBeforeRemoveGroup',
                removegroup: 'onRemoveGroup',
                changegroup: 'onChangeGroup',
                movegroup: 'onMoveGroup'
            }
        });

        return widget;
    },

    onAddColumnClick: function () {
        this.lookupReference('columnsContainer').add(this.createColumn());
    },

    onRemoveColumnClick: function () {
        var columnsContainer = this.lookupReference('columnsContainer');

        columnsContainer.items.each(function (column) {
            if (!column.getTabletsCount()) {
                columnsContainer.remove(column);
            }
        });
    },

    getTabletsInfo: function () {
        var columnsContainer = this.lookupReference('columnsContainer'),
            counter = 0,
            info = [];

        columnsContainer.items.each(function (column) {
            if (column.getTabletsCount()) {
                info = Ext.Array.merge(info, column.getColumnTabletsInfo(counter));
                counter++;
            }
        });

        return info;
    },

    isAttributeUsed: function (attributeName) {
        var info = this.getTabletsInfo(),
            used = false;

        Ext.Array.each(info, function (infoItem) {
            if (Ext.Array.contains(infoItem.attributes, attributeName)) {
                used = true;

                return false; // прекращение итерации Ext.Array.each
            }
        });

        return used;
    },

    getAttributeFromStoreByName: function (attributeName) {
        var view = this.getView(),
            attributeGrid = view.getAttributeGrid(),
            store         = attributeGrid.getStore(),
            record    = null,
            index;

        index = store.findExact('name', attributeName);

        if (index !== -1) {
            record = store.getAt(index);
        }

        return record;
    },

    updateAttributeGroupInModel: function () {
        var viewModel       = this.getViewModel(),
            currentRecord   = viewModel.get('currentRecord'),
            info            = this.getTabletsInfo(),
            attributeGroups = currentRecord.attributeGroups();

        attributeGroups.removeAll();

        Ext.Array.each(info, function (item) {
            attributeGroups.add({
                row: item.row,
                column: item.column,
                title:  item.title,
                attributes: item.attributes
            });
        });
    },

    restoreAttributeGroup: function () {
        var viewModel = this.getViewModel(),
            currentRecord = viewModel.get('currentRecord'),
            attributeGroups = currentRecord.attributeGroups();

        attributeGroups.each(function (record) {
            var columnIndex = record.get('column');

            this.restoreColumnAttributeGroup(this.getColumn(columnIndex), columnIndex);
        }, this);
    },

    restoreColumnAttributeGroup: function (groupColumn, columnIndex) {
        var me              = this,
            viewModel       = this.getViewModel(),
            currentRecord   = viewModel.get('currentRecord'),
            attributeGroups = currentRecord.attributeGroups(),
            find            = [];

        groupColumn.removeAllTablet();

        attributeGroups.each(function (record) {
            if (record.get('column') === columnIndex) {
                find.push(record);
            }
        });

        find = Ext.Array.sort(find, function (a, b) {
            var result = 0;

            if (a.get('row') < b.get('row')) {
                result = -1;
            } else if (a.get('row') > b.get('row')) {
                result = 1;
            }

            return result;
        });

        Ext.Array.each(find, function (item) {
            var attributes = [],
                tablet,
                title;

            Ext.Array.each(item.get('attributes'), function (attributeName) {
                var attributeRecord;

                attributeRecord = me.getAttributeFromStoreByName(attributeName);

                if (attributeRecord) {
                    attributes.push(attributeRecord);
                }
            });

            title = item.get('title');

            tablet = groupColumn.createAttributeGroup(title, attributes);
            groupColumn.appendAttributeGroup(tablet);
        });
    },

    onDropNode: function () {
        this.updateAttributeGroupInModel();
    },

    onChangeGroup: function () {
        this.updateAttributeGroupInModel();
    },

    updateReadOnly: function (readOnly) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            columnsContainer = this.lookupReference('columnsContainer');

        view.attributeGrid.setReadOnly(readOnly);
        viewModel.set('readOnly', readOnly);

        if (view.isConfiguring) {
            return;
        }

        columnsContainer.items.each(function (column) {
            column.setReadOnly(readOnly);
        });
    }
});
