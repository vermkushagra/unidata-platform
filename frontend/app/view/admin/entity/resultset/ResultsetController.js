Ext.define('Unidata.view.admin.entity.resultset.ResultsetController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.resultset',

    init: function () {
        this.callParent(arguments);
    },

    onEditCatalogButtonClick: function () {
        this.fireViewEvent('showCatalogEditor');
    },

    /**
     * Открываем окно для поиска по мета данным
     */
    onSearchButtonClick: function () {
        var view = this.getView(),
            wnd;

        wnd = Ext.create('Unidata.view.admin.entity.metasearch.MetasearchWindow', {
            draftMode: view.getDraftMode()
        });

        wnd.on('metasearchresultclick', this.onMetasearchResultClick, this);

        wnd.show();
    },

    onMetasearchResultClick: function (component, metaRecordName) {
        var view = this.getView(),
            resultsetStore = this.getStore('resultsetStore'),
            index = resultsetStore.findExact('entityName', metaRecordName);

        if (index != -1) {
            view.fireEvent('metasearchresultclick', component, resultsetStore.getAt(index));
        }
    },

    onAddRecordButtonClick: function (btn) {
        var me = this,
            msgBox;

        msgBox = Ext.window.MessageBox.create({
            header: true
        });

        msgBox.show({
            title: Unidata.i18n.t('admin.metamodel>selectObjectType'),
            buttons: Ext.MessageBox.YESNO,
            buttonText: {
                yes: Unidata.i18n.t('glossary:entity'),
                no: Unidata.i18n.t('glossary:lookupEntity')
            },
            scope: this,
            cls: 'unidata-message-box-metamodel',
            animateTarget: btn,
            defaultFocus: 3,
            fn: function (btn) {
                if (btn === 'yes') {
                    this.fireViewEvent('addrecord', me.getView(), [null, 'Entity']);
                } else if (btn === 'no') {
                    this.fireViewEvent('addrecord', me.getView(), [null, 'LookupEntity']);
                }
            }
        });
    },

    /**
     * TODO: удалить, когда точно будет не нужен
     * Теперь используется новый экспорт
     * @deprecated
     */
    onDownloadReportButtonClick: function (btn) {
        var title = Unidata.i18n.t('admin.metamodel>exportMetamodel'),
            msg = Unidata.i18n.t('admin.metamodel>confirmExportFullMetamodel');

        function downloadMeta () {
            var url = Unidata.Config.getMainUrl() + 'internal/meta/model',
                downloadConfig;

            downloadConfig = {
                method: 'GET',
                url: url,
                params: {
                    token: Unidata.Config.getToken()
                }
            };

            Unidata.util.DownloadFile.downloadFile(downloadConfig);
        }

        this.showPrompt(title, msg, downloadMeta, this, btn);
    },

    /**
     * TODO: удалить, когда точно будет не нужен
     * Теперь используется новый импорт
     * @deprecated
     */
    onImportCreateChange: function (component) {
        this.onUploadFileFieldChange(component, true);
    },

    /**
     * TODO: удалить, когда точно будет не нужен
     * Теперь используется новый импорт
     * @deprecated
     */
    onImportUpdateChange: function (component) {
        this.onUploadFileFieldChange(component, false);
    },

    /**
     * TODO: удалить, когда точно будет не нужен
     * Теперь используется новый импорт
     * @deprecated
     */
    onUploadFileFieldChange: function (uploadFilefield, recreate) {
        var view = this.getView(),
            url,
            fileUploadDownload,
            FileUploadDownloadUtil = Unidata.util.FileUploadDownload,
            me = this;

        url = Unidata.Config.getMainUrl() + 'internal/meta/model';

        fileUploadDownload = FileUploadDownloadUtil.create({
            listeners: {
                uploadstart: function () {
                    view.setLoading(Unidata.i18n.t('common:fileLoadingEllipsis'));
                },
                uploaderror: function () {
                    Unidata.showError(Unidata.i18n.t('common:fileLoadError'));
                    view.setLoading(false);
                    uploadFilefield.reset();
                },
                uploadsuccess: function (successful, errors) {
                    if (!successful) {
                        Unidata.showError(errors);

                        view.setLoading(false);
                    } else {
                        me.showMessage(Unidata.i18n.t(
                            'admin.metamodel>metaModelSuccessfully',
                            {action: recreate ? Unidata.i18n.t('admin.metamodel>recreated') : Unidata.i18n.t('admin.metamodel>updated')}
                        ));
                        me.reloadData()
                            .then(function () {
                                view.setLoading(false);
                            }, function () {
                                me.showError(Unidata.i18n.t('admin.metamodel>updateDataError'));
                            })
                            .done();

                        me.getView().fireEvent('modeluploadsuccess');
                    }

                    uploadFilefield.reset();
                }
            }
        });
        fileUploadDownload.uploadFiles(uploadFilefield, url, {recreate: recreate});
    },

    // TODO: move to a better place when import/export will be extracted
    reloadResultsetStore: function () {
        var view = this.getView(),
            resultSetGrid = this.lookupReference('resultsetGrid'),
            store = resultSetGrid.getStore(),
            deferred;

        deferred = Ext.create('Ext.Deferred');

        store.getProxy().setDraftMode(view.getDraftMode());

        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    deferred.resolve(store);
                } else {
                    deferred.reject();
                }
            }
        });

        return deferred.promise;
    },

    reloadEnumerationStore: function () {
        var EnumerationApi = Unidata.util.api.Enumeration,
            promise;

        // первично загружаем enums
        promise = EnumerationApi.getStoreReloaded();

        return promise;
    },

    reloadData: function () {
        var resultSetPromise,
            enumerationPromise;

        resultSetPromise = this.reloadResultsetStore();
        enumerationPromise = this.reloadEnumerationStore();

        return Ext.Deferred.all([resultSetPromise, enumerationPromise]);
    },

    updateDraftMode: function (draftMode) {
        var view = this.getView(),
            viewModel = this.getViewModel();

        viewModel.set('draftMode', draftMode);

        if (view.isConfiguring) {
            return;
        }

        view.reloadTree();
    },

    onDraftModeAdminPanelButtonClick: function () {
        var view = this.getView(),
            msgBox;

        msgBox = Ext.window.MessageBox.create({
            header: true
        });

        msgBox = Ext.widget('window', {
            cls: 'unidata-message-box-metamodel',
            title: Unidata.i18n.t('admin.metamodel>selectDraftActionType'),
            autoShow: true,
            resizable: false,
            modal: true,
            width: 430,
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        {
                            xtype: 'button',
                            text: Unidata.i18n.t('admin.metamodel>applyDraftText'),
                            flex: 1,
                            handler: function () {
                                view.fireEvent('applydraft', msgBox);
                            }
                        },
                        {
                            xtype: 'button',
                            text: Unidata.i18n.t('admin.metamodel>removeDraftText'),
                            flex: 1,
                            handler: function () {
                                view.fireEvent('removedraft', msgBox);
                            }
                        }
                    ]
                }
            ],
            buttons: []
        });
    }
});
