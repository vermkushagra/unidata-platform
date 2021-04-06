/**
 * Экран администрирования перечислений
 *
 * @author Ivan Marshalkin
 * @date 2016-12-07
 */

Ext.define('Unidata.view.admin.enumeration.Enumeration', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.enumeration',

    requires: [
        'Unidata.view.admin.enumeration.EnumerationController',
        'Unidata.view.admin.enumeration.EnumerationModel'
    ],

    controller: 'admin.enumeration',
    viewModel: {
        type: 'admin.enumeration'
    },

    config: {
        draftMode: null
    },

    methodMapper: [
        {
            method: 'updateDraftMode'
        }
    ],

    importEnumerationButton: null,          // ссылка на кнопку импорта
    exportEnumerationButton: null,          // ссылка на кнопку экспорта
    enumerationTree: null,                  // ссылка на дерево перечислений
    toggleAllCheckBox: null,                // переключатель выбрать все / снять выделения со всего

    referenceHolder: true,

    cls: 'un-measurement',

    layout: 'fit',

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

        me.importEnumerationButton = me.lookupReference('importEnumerationButton');
        me.exportEnumerationButton = me.lookupReference('exportEnumerationButton');
        me.enumerationTree         = me.lookupReference('enumerationTree');
        me.toggleAllCheckBox       = me.lookupReference('toggleAllCheckBox');
    },

    onDestroy: function () {
        var me = this,
            DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        me.importEnumerationButton = null;
        me.exportEnumerationButton = null;
        me.enumerationTree         = null;
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
            reference: 'enumerationTree',
            title: Unidata.i18n.t('glossary:enums'),
            scrollable: true,
            bind: {
                store: '{enumerationTreeStore}'
            },
            dockedItems: [{
                xtype: 'toolbar',
                reference: 'toolbar',
                dock: 'top',
                hidden: true,
                items: [
                    {
                        xtype: 'filefield',
                        reference: 'importEnumerationButton',
                        cls: 'file-upload-field',
                        width: '24px',
                        buttonConfig: {
                            glyph: 'xf093@FontAwesome',
                            tooltip: Unidata.i18n.t('admin.enumeration>import'),
                            text: ''
                        },
                        buttonOnly: true,
                        msgTarget: 'title',
                        listeners: {
                            change: 'onImportEnumerationButtonChange'
                        },
                        bind: {
                            //hidden: '{!importButtonVisible}'
                        }
                    },
                    {
                        xtype: 'button',
                        ui: 'un-toolbar-admin',
                        scale: 'small',
                        iconCls: 'icon-floppy-disk',
                        reference: 'exportEnumerationButton',
                        handler: 'onExportEnumerationButtonClick',
                        disabled: true,
                        tooltip: Unidata.i18n.t('admin.enumeration>export'),
                        bind: {
                            // disabled: '{!exportButtonEnabled}',
                            // hidden: '{!exportButtonVisible}'
                        }
                    },
                    {
                        xtype: 'button',
                        ui: 'un-toolbar-admin',
                        scale: 'small',
                        iconCls: 'icon-trash2',
                        tooltip: Unidata.i18n.t('admin.enumeration>remove'),
                        handler: 'onDeleteEnumerationButtonClick',
                        disabled: true,
                        bind: {
                            // disabled: '{!deleteButtonEnabled}',
                            // hidden: '{!deleteButtonVisible}'
                        },
                        margin: '0 10 0 0'
                    },
                    {
                        xtype: 'checkbox',
                        fieldLabel: Unidata.i18n.t('admin.common>selectAll'),
                        reference: 'toggleAllCheckBox',
                        tooltip: Unidata.i18n.t('admin.enumeration>selectAll'),
                        labelAlign: 'right',
                        listeners: {
                            change: 'onToggleAllCheckBoxChange'
                        },
                        bind: {
                            //disabled: '{!toggleAllCheckBoxEnabled}'
                        }
                    }
                ]
            }],
            columns: {
                defaults: {
                    resizable: false
                },
                items: [
                    {
                        xtype: 'treecolumn',
                        text: Unidata.i18n.t('glossary:naming'),
                        dataIndex: 'displayName',
                        resizable: true,
                        width: 300
                    },
                    {
                        text: Unidata.i18n.t('admin.common>identifier'),
                        dataIndex: 'name',
                        resizable: false,
                        flex: 1
                    }
                ]
            },
            listeners: {
                checkchange: 'onEnumerationNodeCheckChange'
            }
        }
    ]
});
