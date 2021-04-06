Ext.define('Unidata.view.admin.entity.metarecord.MetaRecord', {
    extend: 'Ext.panel.Panel',

    controller: 'admin.entity.metarecord',

    viewModel: {
        type: 'admin.entity.metarecord'
    },

    requires: [
        'Unidata.view.admin.entity.metarecord.MetaRecordController',
        'Unidata.view.admin.entity.metarecord.MetaRecordModel',

        'Unidata.view.admin.entity.metarecord.property.Property',
        'Unidata.view.admin.entity.metarecord.attribute.Attribute',
        'Unidata.view.admin.entity.metarecord.model.Model',
        'Unidata.view.admin.entity.metarecord.relation.Relation',
        'Unidata.view.admin.entity.metarecord.dataquality.DataQuality',
        'Unidata.view.admin.entity.metarecord.consolidation.Consolidation',
        'Unidata.view.admin.entity.metarecord.presentation.Presentation',

        'Unidata.plugin.tab.DirtyTabClosePrompt',

        'Unidata.view.admin.entity.catalog.Catalog'
    ],

    mixins: [
        'Unidata.mixin.StatusManageable'
    ],

    alias: 'widget.admin.entity.metarecord',

    referenceHolder: true,

    cls: 'animated fadeIn un-section-admin-metarecord',

    bind: {
        title: '{typeIcon} {changed} {tabName:htmlEncode}', // не забываем предотвращение XSS
        loading2: '{!isMetaRecordDone}'
    },

    setLoading2: function (value) {
        if (value) {
            value = Unidata.i18n.t('admin.metamodel>loadProcess');
        }

        this.setLoading(value);
    },

    config: {
        draftMode: null,                                  // режим работы с черновиком
        readOnly: false
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateDraftMode'
        },
        {
            method: 'updateReadOnly'
        }
    ],

    layout: {
        type: 'fit',
        align: 'stretch'
    },

    plugins: [
        {
            ptype: 'dirtytabcloseprompt',
            pluginId: 'dirtytabcloseprompt',
            closeUnsavedTabText: Unidata.i18n.t('admin.metamodel>confirmCloseUnsavedModel')
        }
    ],

    initComponent: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        this.setDraftMode(DraftModeNotifier.getDraftMode());

        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
        this.callParent(arguments);
    },

    initItems: function () {
        var draftMode = this.draftMode;

        this.callParent(arguments);

        this.add([
            {
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        cls: 'right-toolbar',
                        dock: 'right',
                        width: 45,
                        defaults: {
                            xtype: 'button',
                            ui: 'un-toolbar-admin',
                            scale: 'medium'
                        },
                        items: [
                            {
                                handler: 'onButtonSaveRecordClick',
                                tooltip: Unidata.i18n.t('common:save'),
                                hidden: true,
                                bind: {
                                    disabled: '{!saveButtonEnabled}',
                                    hidden: '{!saveButtonVisible}'
                                },
                                iconCls: 'icon-floppy-disk'
                            },
                            // временно убрано т.к. теоретически можно открывать модель которой нет в
                            // черновике на данный момент не красиво получается
                            // {
                            //     handler: 'onOpenDraftButtonClick',
                            //     tooltip: Unidata.i18n.t('admin.metamodel>openDraft'),
                            //     hidden: true,
                            //     bind: {
                            //         // hidden: '{!openDraftVisible}'
                            //     },
                            //     iconCls: 'icon-pencil5'
                            // },
                            // временно убрали т.к. функционал сырой и неиспользуемый
                            //{
                            //    handler: 'onJsonModeClick',
                            //    tooltip: 'JSON модель',о
                            //    iconCls: 'icon-code'
                            //},
                            '->',
                            {
                                tooltip: Unidata.i18n.t('admin.metamodel>removeEntityOrLookupEntity'),
                                margin: '0 0 80 0',
                                handler: 'onDeleteConfirmClick',
                                securedResource: 'ADMIN_DATA_MANAGEMENT',
                                securedEvent: 'delete',
                                iconCls: 'icon-trash2',
                                hidden: true,
                                bind: {
                                    disabled: '{!removeButtonEnabled}',
                                    hidden: '{!removeButtonVisible}'
                                }
                            }
                        ]
                    }
                ],
                xtype: 'tabpanel',
                reference: 'metaTabPanel',
                ui: 'un-underlined',
                defaults: {
                    overflow: 'auto'
                },
                items: [
                    {
                        xtype: 'admin.entity.metarecord.property',
                        reference: 'propertyPanel',
                        cls: 'un-metarecord-section',
                        title: Unidata.i18n.t('admin.metamodel>properties'),
                        glyph: 'xf013@FontAwesome',
                        draftMode: draftMode,
                        listeners: {
                            loadallstore: 'onLoadAllStoreProperty'
                        }
                    },
                    {
                        xtype: 'admin.entity.metarecord.attribute',
                        reference: 'attributePanel',
                        cls: 'un-metarecord-section',
                        title: Unidata.i18n.t('glossary:attributes'),
                        glyph: 'xf039@FontAwesome',
                        draftMode: draftMode,
                        listeners: {
                            serverexception: 'onServerException',
                            loadallstore: 'onLoadAllStoreAttributeTab'
                        }
                    },
                    {
                        xtype: 'admin.entity.metarecord.relation',
                        reference: 'referencePanel',
                        cls: 'un-metarecord-section',
                        title: Unidata.i18n.t('glossary:relations'),
                        glyph: 'xf0c1@FontAwesome',
                        draftMode: draftMode,
                        bind: {
                            disabled: {
                                bindTo: '{isLookupEntity}',
                                deep: true
                            }
                        },
                        listeners: {
                            serverexception: 'onServerException'
                        }
                    },
                    {
                        xtype: 'admin.entity.metarecord.presentation',
                        reference: 'attributeGroupPanel',
                        cls: 'un-metarecord-section un-presentation',
                        title: Unidata.i18n.t('admin.metamodel>recordDisplay'),
                        glyph: 'xf039@FontAwesome'
                    },
                    {
                        xtype: 'admin.entity.metarecord.model',
                        reference: 'metaRecordModel',
                        title: Unidata.i18n.t('admin.metamodel>model'),
                        glyph: 'xf0e8@FontAwesome',
                        draftMode: draftMode
                    },
                    {
                        xtype: 'admin.entity.metarecord.dataquality',
                        reference: 'dataQualityPanel',
                        cls: 'un-metarecord-section unidata-admin-data-quality',
                        title: Unidata.i18n.t('glossary:dataQuality'),
                        glyph: 'xf058@FontAwesome',
                        draftMode: draftMode,
                        listeners: {
                            serverexception: 'onServerException',
                            loadallstore: 'onLoadAllStoreDqTab'
                        }
                    },
                    {
                        xtype: 'admin.entity.metarecord.consolidation',
                        reference: 'consolidationView',
                        cls: 'un-metarecord-section',
                        title: Unidata.i18n.t('admin.metamodel>consolidation'),
                        glyph: 'xf122@FontAwesome',
                        draftMode: draftMode,
                        margin: '10',
                        listeners: {
                            activate: 'onActivateConsolidationTab',
                            loadallstore: 'onLoadAllStoreConsolidationTab'
                        }
                    }
                ]
            }
        ]);

    },

    getDirty: function () {
        var viewModel = this.getViewModel(),
            dirty = false;

        if (viewModel) {
            dirty = viewModel.get('dirty');
        }

        return dirty;
    },

    closeTabSilent: function () {
        var me = this,
            plugin = me.getPlugin('dirtytabcloseprompt');

        if (plugin) {
            plugin.disablePrompt();
        }

        me.close();
    },

    getMetaRecord: function () {
        var viewModel = this.getViewModel(),
            metaRecord = null;

        if (viewModel) {
            metaRecord = viewModel.get('currentRecord');
        }

        return metaRecord;
    }
});
