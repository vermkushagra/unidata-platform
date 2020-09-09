/**
 * Окно с доп.информацией об эталонной записи
 *
 * @author Sergey Shishigin
 * @date 2016-02-02
 */

Ext.define('Unidata.view.steward.dataviewer.card.data.EtalonInfoWindow', {
    extend: 'Ext.window.Window',

    alias: 'widget.steward.dataviewer.card.data.EtalonInfoWindow',

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        etalonInfo: null
    },

    cls: 'un-etalon-info-window',
    title: Unidata.i18n.t('dataviewer>recordInfo'),
    scrollable: true,

    modal: true,
    resizable: false,
    width: 500,
    height: 500,

    closeOnOutsideClick: true,

    etalonInfoTextArea: null,

    items: [
        {
            xtype: 'grid',
            reference: 'etalonInfoGrid',
            readOnly: true,
            hideHeaders: true,
            scrollable: 'horizontal',
            height: 185,
            width: '100%',
            disableSelection: true,
            selModel: 'rowmodel',
            viewConfig: {
                trackOver: false,
                enableTextSelection: true
            },
            store: {
                proxy: {
                    type: 'memory'
                },
                fields: ['name', 'value']
            },
            columns: [
                {
                    dataIndex: 'name',
                    width: 160,
                    tdCls: 'un-td-name'
                },
                {
                    dataIndex: 'value',
                    flex: 1,
                    tdCls: 'un-td-value'
                }
            ]
        },
        {
            xtype: 'grid',
            reference: 'sourceSystemsGrid',
            cls: 'un-table-grid',
            readOnly: true,
            flex: 1,
            width: '100%',
            disableSelection: true,
            selModel: 'rowmodel',
            viewConfig: {
                trackOver: false
            },
            store: {
                proxy: {
                    type: 'memory'
                },
                fields: ['name', 'value']
            },
            columns: [
                {
                    dataIndex: 'name',
                    flex: 1,
                    text: Unidata.i18n.t('glossary:dataSources'),
                    menuDisabled: true,
                    titleInactive: true,
                    sortable: false
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();
        this.displayEtalonInfo();
    },

    initReferences: function () {
        this.etalonInfoGrid = this.lookupReference('etalonInfoGrid');
        this.sourceSystemsGrid = this.lookupReference('sourceSystemsGrid');
    },

    onDestroy: function () {
        this.etalonInfoGrid = null;
        this.sourceSystemsGrid = null;

        this.callParent(arguments);
    },

    displayEtalonInfo: function () {
        var etalonInfo = this.getEtalonInfo();

        this.fillEtalonInfoStore(etalonInfo);
        this.fillSourceSystemsStore(etalonInfo);
    },

    fillEtalonInfoStore: function (etalonInfo) {
        var EtalonSystemAttributeEntity = Unidata.util.EtalonSystemAttributeEntity,
            etalonInfoGrid = this.etalonInfoGrid,
            entityTypeDisplayName = etalonInfo.entityTypeDisplayName,
            store = etalonInfoGrid.getStore(),
            params = [],
            values = {};

        values.entity     = Ext.String.format('{0} ({1})', etalonInfo.entityDisplayName, entityTypeDisplayName);
        values.updateInfo = EtalonSystemAttributeEntity.buildDateValue(etalonInfo.updateDate, etalonInfo.updatedBy);
        values.createInfo = EtalonSystemAttributeEntity.buildDateValue(etalonInfo.createDate, etalonInfo.createdBy);
        values.etalonId   = etalonInfo.etalonId;
        values.gsn        = etalonInfo.gsn;

        params.push({
            name: Ext.String.capitalize(entityTypeDisplayName),
            value: values.entity
        });

        params.push({
            name: Unidata.i18n.t('dataviewer>recordId'),
            value: values.etalonId
        });

        params.push({
            name: 'GSN',
            value: values.gsn
        });

        params.push({
            name: Unidata.i18n.t('common:created'),
            value: values.createInfo
        });

        params.push({
            name: Unidata.i18n.t('common:updated'),
            value: values.updateInfo
        });

        store.add(params);
    },

    fillSourceSystemsStore: function (etalonInfo) {
        var sourceSystemsGrid = this.sourceSystemsGrid,
            store = sourceSystemsGrid.getStore(),
            sourceSystems = etalonInfo.sourceSystems,
            params;

        params = Ext.Array.map(sourceSystems, function (sourceSystem) {
            return {
                name: sourceSystem
            };
        });

        store.add(params);
    }
});
