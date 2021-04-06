Ext.define('Unidata.view.admin.entity.layout.LayoutController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.layout',

    control: {
        'admin\\.entity\\.resultset un\\.entitytree': {
            'itemclick': 'onItemResultClick'
        },
        'admin\\.entity\\.resultset': {
            'metasearchresultclick': 'onItemResultClick'
        }
    },

    onItemResultClick: function (component, record) {
        if (record instanceof Unidata.model.entity.AbstractEntity) {
            this.createRecordTabFromRecord(record);
        }
    },

    createRecordTabFromRecord: function (record) {
        var entityName, entityType, tabView;

        tabView = this.lookupReference('recordshowTabPanel');
        entityName = record.get('name');
        entityType = record.get('type', 'value');

        this.createRecordTab(tabView, entityName, entityType);
    },

    onShowCatalogEditor: function () {
        this.switchCatalogEditorVisibility(!this.catalogEditorVisible);
    },

    switchCatalogEditorVisibility: function (visible) {
        var recordShowTabPanel = this.lookupReference('recordshowTabPanel'),
            catalogEditor = this.lookupReference('catalogEditor');

        recordShowTabPanel[visible ? 'hide' : 'show']();
        catalogEditor[!visible ? 'hide' : 'show']();

        this.catalogEditorVisible = visible;
    },

    onAddRecord: function (view, params) {
        var tabView = this.lookupReference('recordshowTabPanel');

        this.createRecordTab(tabView, params[0], params[1]);
    },

    createRecordTab: function (tabView, entityName, entityType) {
        var view = this.getView(),
            viewModel,
            tabComponent,
            metaRecord,
            me = this;

        this.switchCatalogEditorVisibility(false);

        // FIXME: функция должна использовать парметры (не переменные определенные выше уровня функции)
        function findRecordTab () {
            var tabComponent = tabView.items.findBy(function (item) {
                var viewModel = item.getViewModel(),
                    metaRecord = viewModel.get('currentRecord');

                // можно создавать много новых вкладок
                if (metaRecord && metaRecord.phantom) {
                    return null;
                }

                return viewModel.get('entityKey') === entityName || (metaRecord && (metaRecord.get('name') === entityName));
            });

            return tabComponent;
        }

        if (Ext.isEmpty(entityName) || (tabComponent = findRecordTab()) === null) {
            tabComponent = Ext.create('Unidata.view.admin.entity.metarecord.MetaRecord',
                {
                    draftMode: view.getDraftMode(),
                    readOnly: view.getDraftMode() ? false : true,
                    viewModel: {
                        data: {
                            entityKey: entityName,
                            canSave: true
                        }
                    }
                }
            );

            tabComponent.on('opendraft', this.onMetaModelOpenDraft, this);

            metaRecord = Ext.create('Unidata.model.entity.' + entityType);

            if (entityName !== null) {
                metaRecord.setId(entityName);
                metaRecord.load({
                    params: {
                        'draft': view.getDraftMode()
                    },
                    success: function (r) {
                        var viewModel = tabComponent.getViewModel(),
                            canWrite  = Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'update');

                        if (viewModel) {
                            viewModel.set('currentRecord', r);
                            viewModel.set('canSave', canWrite);
                        }
                    },
                    failure: function () {
                        tabComponent.close();
                    }
                });

            } else {
                metaRecord.set('groupName', 'ROOT');
                tabComponent.getViewModel().set('currentRecord', metaRecord);
            }

            tabView.add(tabComponent);

        } else {
            tabView.setActiveTab(tabComponent);
        }
    },

    onMetaModelOpenDraft: function (entityName, entityType) {
        var view = this.getView(),
            me = this,
            recordShow = view.recordshowTabPanel,
            DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier,
            eventData,
            prompt;

        eventData = {
            entityName: entityName,
            entityType: entityType
        };

        if (recordShow.getOpenedTabCount() <= 1) {
            DraftModeNotifier.toggleDraftMode(eventData);

            return;
        }

        prompt = Ext.create('Ext.window.MessageBox', {
            header: true
        });

        prompt.show({
            title: Unidata.i18n.t('common:confirmation'),
            message: Unidata.i18n.t('admin.metamodel>draftModeOpenCurrentMetaRecordPromptText'),
            scope: this,
            defaultFocus: 3,
            buttons: Ext.MessageBox.YESNO,
            buttonText: {
                yes: Unidata.i18n.t('common:yes'),
                no: Unidata.i18n.t('common:no')
            },
            fn: function (btn) {
                var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier,
                    eventData;

                if (btn === 'yes') {
                    DraftModeNotifier.toggleDraftMode(eventData);
                } else if (btn === 'no') {
                    return;
                }
            }
        });
    },

    onNeedReloadCatalog: function () {
        this.lookupReference('resultsetPanel').reloadTree();
    },

    onModeluploadsuccess: function () {
        this.lookupReference('catalogEditor').reloadCatalogStore();
    },

    closeTabsSilent: function () {
        var view = this.getView(),
            tabPanel = view.recordshowTabPanel;

        if (tabPanel.hasOpenedTabs()) {
            tabPanel.closeTabsSilent();
        }
    },

    onApplyDraft: function (msgBox) {
        var me = this,
            prompt;

        prompt = Ext.create('Ext.window.MessageBox', {
            header: true
        });

        prompt.show({
            title: Unidata.i18n.t('common:confirmation'),
            message: Unidata.i18n.t('admin.metamodel>draftApplyPromptText'),
            scope: this,
            defaultFocus: 3,
            buttons: Ext.MessageBox.YESNO,
            buttonText: {
                yes: Unidata.i18n.t('common:yes'),
                no: Unidata.i18n.t('common:no')
            },
            fn: function (btn) {
                if (btn === 'yes') {
                    msgBox.close();

                    me.closeTabsSilent();
                    me.applyDraft();
                } else if (btn === 'no') {
                    return;
                }
            }
        });

    },

    applyDraft: function () {
        var me = this,
            view = this.getView(),
            notificationCountPoller = Unidata.module.poller.NotificationCountPoller.getInstance(),
            taskCountPoller = Unidata.module.poller.TaskCountPoller.getInstance(),
            promise,
            mask;

        mask = new Ext.LoadMask({
            msg: Unidata.i18n.t('admin.metamodel>draftApplyInProgress'),
            target: Unidata.getApplication().getActiveView(),
            style: {
                zIndex: 999999
            }
        });

        mask.show();

        // обновляем сессию, пока идёт обновление модели из черновика
        Unidata.util.Session.autoUpdateSessionStart();

        promise = Unidata.util.api.Draft.applyDraft();
        notificationCountPoller.stop();
        taskCountPoller.stop();

        promise
            .then(
                function () {
                    var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

                    me.showMessage(Unidata.i18n.t('admin.metamodel>draftSuccessApply'));

                    DraftModeNotifier.notify(DraftModeNotifier.types.APPLYDRAFT);

                    view.resultsetPanel.reloadTree();
                },
                function () {
                    return;
                }
            )
            .always(function () {
                mask.destroy();

                Unidata.util.Session.autoUpdateSessionEnd();
                notificationCountPoller.start();
                taskCountPoller.start();
            })
            .done();
    },

    onRemoveDraft: function (msgBox) {
        var me = this,
            prompt;

        prompt = Ext.create('Ext.window.MessageBox', {
            header: true
        });

        prompt.show({
            title: Unidata.i18n.t('common:confirmation'),
            message: Unidata.i18n.t('admin.metamodel>draftRemovePromptText'),
            scope: this,
            defaultFocus: 3,
            buttons: Ext.MessageBox.YESNO,
            buttonText: {
                yes: Unidata.i18n.t('common:yes'),
                no: Unidata.i18n.t('common:no')
            },
            fn: function (btn) {
                if (btn === 'yes') {
                    msgBox.close();

                    me.closeTabsSilent();
                    me.removeDraft();
                } else if (btn === 'no') {
                    return;
                }
            }
        });
    },

    removeDraft: function () {
        var me = this,
            view = this.getView(),
            promise,
            mask;

        mask = new Ext.LoadMask({
            msg: Unidata.i18n.t('admin.metamodel>draftRemoveInProgress'),
            target: Unidata.getApplication().getActiveView(),
            style: {
                zIndex: 999999
            }
        });
        mask.show();

        promise = Unidata.util.api.Draft.removeDraft();

        promise
            .then(function () {
                var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

                me.showMessage(Unidata.i18n.t('admin.metamodel>draftSuccessRemove'));

                DraftModeNotifier.notify(DraftModeNotifier.types.REMOVEDRAFT);

                view.resultsetPanel.reloadTree();
            })
            .always(function () {
                mask.destroy();
            }).done();
    },

    draftModeChangeHandler: function (draftMode, eventData) {
        var me = this,
            view = this.getView(),
            resultsetPanel = view.resultsetPanel,
            recordshowTabPanel = view.recordshowTabPanel;

        me.closeTabsSilent();

        view.setDraftMode(draftMode);
        resultsetPanel.setDraftMode(draftMode);

        // переоткрываем вкладки
        if (eventData && eventData.entityName && eventData.entityType) {
            this.createRecordTab(recordshowTabPanel, eventData.entityName, eventData.entityType);
        }
    },

    updateDraftMode: function (draftMode) {
        var view = this.getView();

        if (view.catalogEditor) {
            view.catalogEditor.setDraftMode(draftMode);
        }
    }
});
