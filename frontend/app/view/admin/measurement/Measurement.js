/**
 * Экран администрирования единиц измерения
 *
 * @author Ivan Marshalkin
 * @date 2016-11-08
 */

Ext.define('Unidata.view.admin.measurement.Measurement', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.measurement',

    requires: [
        'Unidata.view.admin.measurement.MeasurementController',
        'Unidata.view.admin.measurement.MeasurementModel'
    ],

    controller: 'admin.measurement',
    viewModel: {
        type: 'admin.measurement'
    },

    importMeasurementButton: null,          // ссылка на кнопку импорта
    exportMeasurementButton: null,          // ссылка на кнопку экспорта
    measurementTree: null,                  // ссылка на дерево величин единиц измерения
    toggleAllCheckBox: null,                // переключатель выбрать все / снять выделения со всего

    referenceHolder: true,

    cls: 'un-measurement',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        draftMode: null
    },

    methodMapper: [
        {
            method: 'updateDraftMode'
        }
    ],

    initComponent: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier,
            globalDraftMode;

        this.callParent(arguments);

        this.initComponentReference();

        globalDraftMode = DraftModeNotifier.getDraftMode();
        this.setDraftMode(globalDraftMode);

        DraftModeNotifier.subscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);
    },

    initComponentReference: function () {
        var me = this;

        me.importMeasurementButton = me.lookupReference('importMeasurementButton');
        me.exportMeasurementButton = me.lookupReference('exportMeasurementButton');
        me.measurementTree         = me.lookupReference('measurementTree');
        me.toggleAllCheckBox       = me.lookupReference('toggleAllCheckBox');
    },

    onDestroy: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier,
            me = this;

        me.importMeasurementButton = null;
        me.exportMeasurementButton = null;
        me.measurementTree         = null;
        me.toggleAllCheckBox       = null;

        DraftModeNotifier.unsubscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);

        me.callParent(arguments);
    },

    onGlobalDraftModeChange: function (draftMode) {
        if (this.isDestroyed || this.destroying) {
            return;
        }

        this.setDraftMode(draftMode);
    },

    items: [
        {
            xtype: 'treepanel',
            reference: 'measurementTree',
            title: Unidata.i18n.t('glossary:units'),
            flex: 1,
            bind: {
                store: '{measurementTreeStore}'
            },
            dockedItems: [{
                xtype: 'toolbar',
                reference: 'toolbar',
                dock: 'top',
                items: [
                    {
                        xtype: 'button',
                        ui: 'un-main-button',
                        scale: 'small',
                        iconCls: 'icon-exit-up',
                        reference: 'exportMeasurementButton',
                        handler: 'onExportMeasurementValueButtonClick',
                        tooltip: Unidata.i18n.t('admin.enumeration>export'),
                        bind: {
                            hidden: '{!exportButtonVisible}',
                            disabled: '{!exportButtonEnabled}'
                        }
                    },
                    {
                        xtype: 'filefield',
                        reference: 'importMeasurementButton',
                        cls: 'file-upload-field',
                        ui: 'un-toolbar-admin',
                        width: '32px',
                        buttonConfig: {
                            scale: 'small',
                            iconCls: 'icon-enter-down',
                            tooltip: Unidata.i18n.t('admin.enumeration>import'),
                            text: ''
                        },
                        disabled: true,
                        buttonOnly: true,
                        msgTarget: 'title',
                        listeners: {
                            change: 'onImportMeasurementButtonChange'
                        },
                        bind: {
                            disabled: '{!importButtonEnabled}',
                            hidden: '{!importButtonVisible}'
                        }
                    },
                    {
                        xtype: 'checkbox',
                        boxLabel: Unidata.i18n.t('admin.common>selectAll'),
                        reference: 'toggleAllCheckBox',
                        tooltip: Unidata.i18n.t('admin.enumeration>selectAll'),
                        labelAlign: 'right',
                        boxLabelAlign: 'after',
                        listeners: {
                            change: 'onToggleAllCheckBoxChange'
                        },
                        bind: {
                            disabled: '{!toggleAllCheckBoxEnabled}'
                        }
                    },
                    {
                        xtype: 'button',
                        ui: 'un-toolbar-admin',
                        scale: 'small',
                        iconCls: 'icon-trash2',
                        tooltip: Unidata.i18n.t('admin.enumeration>remove'),
                        handler: 'onDeleteMeasurementValueButtonClick',
                        disabled: true,
                        bind: {
                            disabled: '{!deleteButtonEnabled}',
                            hidden: '{!deleteButtonVisible}'
                        }
                    }
                ]
            }],
            columns: {
                defaults: {
                    sortable: false,
                    resizable: false,
                    menuDisabled: true
                },
                items: [
                    {
                        xtype: 'treecolumn',
                        text: Unidata.i18n.t('glossary:naming'),
                        resizable: true,
                        width: 300,
                        renderer: function (value, metaData, record) {
                            var nestedRecord = record.get('record'),
                                result = '';

                            if (nestedRecord) {
                                result = nestedRecord.get('name');
                            }

                            return result;
                        }
                    },
                    {
                        text: Unidata.i18n.t('admin.common>identifier'),
                        resizable: false,
                        width: 110,
                        renderer: function (value, metaData, record) {
                            var nestedRecord = record.get('record'),
                                result = '';

                            if (nestedRecord) {
                                result = nestedRecord.get('id');
                            }

                            return result;
                        }
                    },
                    {
                        text: Unidata.i18n.t('admin.measurement>notation'),
                        width: 200,
                        renderer: function (value, metaData, record) {
                            var nestedRecord = record.get('record'),
                                result = '';

                            if (nestedRecord) {
                                result = nestedRecord.get('shortName');
                            }

                            return result;
                        }
                    },
                    {
                        text: Unidata.i18n.t('admin.measurement>transformation'),
                        width: 200,
                        renderer: function (value, metaData, record) {
                            if (record.get('nodeType') === 'MEASUREMENTUNIT_NODE') {
                                return record.get('record').get('convectionFunction');
                            }

                            return '';
                        }
                    },
                    {
                        xtype: 'checkcolumn',
                        header: Unidata.i18n.t('admin.measurement>basic'),
                        width: 100,
                        renderer: function (value, metaData, record) {
                            if (record.get('nodeType') !== 'MEASUREMENTUNIT_NODE') {
                                return '';
                            }

                            return this.defaultRenderer(record.get('record').get('base'), metaData);
                        }
                    }
                ]
            },
            listeners: {
                checkchange: 'onMeasurementNodeCheckChange'
            }
        }
    ]
});
