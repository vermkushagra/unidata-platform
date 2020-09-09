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
        'Unidata.view.admin.entity.metarecord.dq.DataQuality',
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
        readOnly: false,
        cleanseGroups: null,
        sourceSystems: null
    },

    // флаг позволяющие скрывать некоторые вкладки системы
    hidePropertyTab: false,
    hideAttributeTab: false,
    hideRelationTab: false,
    hidePresentationTab: false,
    hideModelTab: false,
    hideDqTab: false,
    hideConsolidationTab: false,

    // признак необходимости панель с вкладками
    hideTabBar: false,

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

    loadAllData: function () {
        var CleanseFunctionApi = Unidata.util.api.CleanseFunction,
            SourceSystemsApi = Unidata.util.api.SourceSystem,
            cleanseGroupsPromise,
            sourceSystemsPromise;

        cleanseGroupsPromise = CleanseFunctionApi.loadCleanseFunctionList();
        sourceSystemsPromise = SourceSystemsApi.loadSourceSystems();

        return Ext.Deferred.all([cleanseGroupsPromise, sourceSystemsPromise]);
    },

    initItems: function () {
        var draftMode = this.draftMode,
            cleanseFunctions,
            sourceSystems,
            viewModel = this.getViewModel(),
            controller = this.getController(),
            readOnly = this.getReadOnly(),
            me = this;

        this.callParent(arguments);

        this.loadAllData().then(function (data) {
            cleanseFunctions = data[0];
            sourceSystems = data[1];

            me.add([
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
                            hidden: me.hidePropertyTab,
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
                            hidden: me.hideAttributeTab,
                            cls: 'un-metarecord-section',
                            title: Unidata.i18n.t('glossary:attributes'),
                            glyph: 'xf039@FontAwesome',
                            draftMode: draftMode,
                            readOnly: readOnly,
                            listeners: {
                                serverexception: 'onServerException',
                                loadallstore: 'onLoadAllStoreAttributeTab'
                            }
                        },
                        {
                            xtype: 'admin.entity.metarecord.relation',
                            reference: 'referencePanel',
                            hidden: me.hideRelationTab,
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
                            hidden: me.hidePresentationTab,
                            cls: 'un-metarecord-section un-presentation',
                            title: Unidata.i18n.t('admin.metamodel>recordDisplay'),
                            glyph: 'xf039@FontAwesome',
                            listeners: {
                                activate: 'onActivatePresentationTab'
                            }
                        },
                        {
                            xtype: 'admin.entity.metarecord.model',
                            reference: 'metaRecordModel',
                            hidden: me.hideModelTab,
                            title: Unidata.i18n.t('admin.metamodel>model'),
                            glyph: 'xf0e8@FontAwesome',
                            draftMode: draftMode
                        },
                        {
                            xtype: 'admin.entity.metarecord.dq',
                            reference: 'dataQuality',
                            hidden: me.hideDqTab,
                            title: Unidata.i18n.t('glossary:dataQuality'),
                            glyph: 'xf058@FontAwesome',
                            draftMode: draftMode,
                            cleanseFunctions: cleanseFunctions,
                            bind: {
                                readOnly: '{metaRecordViewReadOnly}'
                            },
                            sourceSystems: sourceSystems
                        },
                        {
                            xtype: 'admin.entity.metarecord.consolidation',
                            reference: 'consolidationView',
                            hidden: me.hideConsolidationTab,
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

            me.afterConfigure();

            if (me.hideTabBar) {
                me.lookupReference('metaTabPanel').tabBar.hide();
            }

            viewModel.bind('{currentRecord}', controller.updateMetaRecordDataTabs, controller);
        }, function () {
            //TODO: implement me
            throw new Error('Method is not implemented');
        }).done();

    },

    /**
     * Функция для потребностей интеграции. Хук вызывается после добавления вкладок в панель
     */
    afterConfigure: Ext.emptyFn,

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
