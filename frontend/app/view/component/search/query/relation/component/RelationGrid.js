/**
 * Таблица связей
 *
 * @author Sergey Shishigin
 * @date 2017-02-01
 */
Ext.define('Unidata.view.component.search.query.relation.component.RelationGrid', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.component.search.query.relation.component.relationgrid',

    hideHeaders: true,
    header: false,
    focusOnToFront: false,

    config: {
        metaRecord: null,
        autoSelectExclusive: true
    },

    cls: 'un-relation-grid',

    store: {
        proxy: {
            type: 'memory'
        },
        model: 'Unidata.model.entity.Relation',
        groupField: 'relType',
        sorters: ['displayName']
    },

    columns: [
        {
            dataIndex: 'displayName',
            flex: 1
        }
    ],

    initComponent: function () {
        var metaRecord;

        this.features = this.buildFeatures();
        this.callParent(arguments);
        metaRecord = this.getMetaRecord();
        this.fillStore(metaRecord);

        this.on('hide', this.onGridHide, this);
    },

    onGridHide: function () {
        this.selModel.deselectAll();
    },

    buildFeatures: function () {
        var features,
            tpl;

        tpl = Ext.create('Ext.XTemplate',
            '<div>{name:this.groupValue}</div>',
            {
                groupValue: function (name) {
                    return Unidata.model.data.RelationTimeline.getRelationTypeDisplayName(name);
                }
            }
        );

        features = [{
            ftype: 'grouping',
            groupHeaderTpl: tpl,
            hideGroupedHeader: true,
            startCollapsed: false,
            id: 'relTypeGrouping'
        }];

        return features;
    },

    updateMetaRecord: function (metaRecord) {
        this.fillStore(metaRecord);
    },

    /**
     * Выбрать единственную связь (если таковая имеется)
     */
    selectExclusiveItem: function () {
        var autoSelectExclusive = this.getAutoSelectExclusive(),
            store               = this.getStore(),
            count               = store.count(),
            selectionModel      = this.getSelectionModel(),
            items               = store.getData().getRange();

        if (autoSelectExclusive && count === 1) {
            selectionModel.select(items[0]);
        }
    },

    /**
     * Заполнить store метаинформацией о связях
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity}
     */
    fillStore: function (metaRecord) {
        var MetaRecordUtil = Unidata.util.MetaRecord,
            store     = this.getStore(),
            proxy     = store.getProxy(),
            relations = [];

        if (metaRecord && MetaRecordUtil.isEntity(metaRecord)) {
            relations = metaRecord.getRelationsFilteredByPermission();
        }

        proxy.setData(relations);
        store.load({
            scope: this,
            callback: function () {
                // this.selectExclusiveItem();
            }
        });
    }
});
