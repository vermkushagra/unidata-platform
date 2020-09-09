Ext.define('Unidata.view.admin.cleanseFunction.CleanseFunctionController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.cleanseFunction',

    inputPorts: {},

    init: function () {
        this.callParent(arguments);

        this.reloadCleanseFunctions();
        this.executionPanel = this.lookupReference('executionPanel');
    },

    /**
     * @param {Ext.view.View} component
     * @param {Ext.data.Model} node
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     */
    onAttributeTreeSelect: function (component, node) {
        this.useCleanseFunction(node);
    },

    onAttributeTreeDataComplete: function () {
        var view = this.getView(),
            initNodeSelection = view.getInitNodeSelection(),
            functionsGrid = this.lookupReference('functionsGrid'),
            store = functionsGrid.getStore(),
            cfNameToFind,
            path,
            index;

        if (initNodeSelection) {
            cfNameToFind = initNodeSelection.get('record').get('name');
            index = this.findCleanseFunctionByName(cfNameToFind);

            if (index > -1) {
                path = store.getAt(index).getPath('name', '.').substring(2);
                functionsGrid.selectAttributeByPath(path);
                view.setInitNodeSelection(null);
            }
        }
    },

    findCleanseFunctionByName: function (name) {
        var functionsGrid = this.lookupReference('functionsGrid'),
            store = functionsGrid.getStore();

        return store.findBy(function (item) {
           var cleanseFunction;

           cleanseFunction = item.get('record');

           if (cleanseFunction) {
               return cleanseFunction.get('name') === name;
           }

           return false;
       });
    },

    useCleanseFunction: function (node) {
        var me = this,
            view = this.getView(),
            viewModel = this.getViewModel(),
            path,
            type,
            record;

        path = node.getPath('name', '.').substring(2);
        record = node.get('record');

        type = node.get('record').get('type');
        viewModel.set('currentRecordPath', path);
        viewModel.set('currentRecordIcon', Unidata.model.cleansefunction.CleanseFunction.createTypeIcon(type));

        if (record instanceof Unidata.model.cleansefunction.CleanseFunction) {
            Unidata.util.api.CleanseFunction.loadCleanseFunction(path, view.getDraftMode())
                .then(function (record) {
                    var deletable;

                    deletable = node.parentNode.isRoot();
                    viewModel.set('currentRecord', record);
                    viewModel.set('currentRecordDeletable', deletable);

                    me.executionPanel.setCleanseFunction(record);
                    me.executionPanel.setCleanseFunctionPath(path);

                    me.inputPorts = record.inputPorts();
                    me.outputPorts = record.outputPorts();
                }, function () {
                    Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>errorFunctionLoading'));
                }).done();
        } else {
            viewModel.set('currentRecord', record);
            viewModel.set('currentRecordDeletable', false);
        }
    },

    buildSubmitDataType: function (port) {
        var dataType = port.get('dataType');

        if (dataType === 'Any') {
            return 'String';
        }

        return dataType;
    },

    reloadCleanseFunctions: function (highlightRecordName) {
        var me = this,
            view = this.getView(),
            store,
            currentRecordPath;

        view.setLoading(true);
        store = this.getViewModel().getStore('cleanseGroupsStore');
        currentRecordPath = this.getCurrentGroupPath();
        store.load({
            params: {
                draft: view.getDraftMode()
            },
            callback: function (records) {
                var index, node, path, functionsGrid;

                functionsGrid = me.lookupReference('functionsGrid');

                //@TODO Must fix hack
                functionsGrid.setData(records[0]);

                if (highlightRecordName !== null && highlightRecordName !== undefined) {
                    index = functionsGrid.getStore().findBy(function (item) {
                        return me.calcNodePath(item) === currentRecordPath &&
                            item.get('record').get('name') === highlightRecordName;
                    });

                    if (index > -1) {
                        node = functionsGrid.getStore().getAt(index);
                        path = node.getPath('name', '.').substring(2);
                        functionsGrid.selectPath(path, 'name', '.', function (success, lastNode) {
                            me.onAttributeTreeSelect(functionsGrid, lastNode);
                        });
                    }
                }

                view.setLoading(false);
                //TODO: add load failure handle
            }
        });
    },

    calcNodePath: function (node) {
        var path;

        path = node.getPath('name', '.').substring(2);

        if (node.get('record').get('type') !== undefined) {
            path = path.substring(0, path.lastIndexOf('.'));
        }

        return path;
    },

    getCurrentGroupPath: function () {
        var functionsGrid,
            path;

        functionsGrid = this.lookupReference('functionsGrid');

        if (functionsGrid.getSelection() !== null &&
            functionsGrid.getSelection().length === 1) {
            path = this.calcNodePath(functionsGrid.getSelection()[0]);
        }

        return path;
    },

    startUploadCustomCleanseFunction: function (name) {
        var me = this,
            view = this.getView(),
            fileUploadWindow;

        name = name || null;

        fileUploadWindow = Unidata.view.admin.cleanseFunction.window.FileUploadWindow.create({
            draftMode: view.getDraftMode(),
            viewModel: {
                data: {
                    cleanseFunctionName: name
                }
            }
        });

        fileUploadWindow.show();
        fileUploadWindow.on('beforeclose', function (component) {
            var actionTxt = Unidata.i18n.t('admin.cleanseFunction>unknownActionFor'),
                functionRecord,
                msg;

            if (Boolean(component.getViewModel().get('isSuccess'))) {
                functionRecord = component.getViewModel().get('loadedCleanseFunction');
                me.reloadCleanseFunctions(functionRecord.get('name'));
                switch (functionRecord.get('state')) {
                    case 'NEW':
                        actionTxt = Unidata.i18n.t('admin.cleanseFunction>uploaded');
                        break;
                    case 'OVERWRITE':
                        actionTxt = Unidata.i18n.t('common:updated');
                        break;
                }
                msg = Unidata.i18n.t('admin.cleanseFunction>thirdPartyFunction', {
                    action: actionTxt,
                    name: functionRecord.get('name')
                });
                me.showMessage(msg);
            }
        });
    },

    showCleanseFunctionUploadWindow: function (btn) {
        var view = this.getView(),
            msgBox;

        msgBox = Ext.window.MessageBox.create({
            header: true
        });

        msgBox.show({
            title: Unidata.i18n.t('admin.cleanseFunction>newFunction'),
            buttons: Ext.MessageBox.YESNO,
            buttonText: {
                yes: Unidata.i18n.t('admin.cleanseFunction>composite'),
                no: Unidata.i18n.t('admin.cleanseFunction>thirdParty')
            },
            scope: this,
            cls: 'unidata-message-box-metamodel',
            animateTarget: btn,
            defaultFocus: 3,
            fn: function (btn) {
                var mainContainer;

                if (btn === 'yes') {
                    mainContainer = Ext.ComponentQuery.query('[reference=mainContainer]')[0];

                    mainContainer.removeAll();
                    mainContainer.add({
                        xtype: 'admin.compositeCleanseFunction',
                        draftMode: view.getDraftMode()
                    });
                    mainContainer.updateLayout();
                } else if (btn === 'no') {
                    this.startUploadCustomCleanseFunction();
                }
            }
        });
    },

    onAddCleanseFunctionButtonClick: function () {
        this.showCleanseFunctionUploadWindow();
    },

    onReloadCustomFunction: function () {
        var name = this.getView().lookupReference('functionsGrid').getSelection()[0].get('name');

        this.startUploadCustomCleanseFunction(name);
    },

    onEditCleanseFunctionButtonClick: function () {
        var view = this.getView(),
            mainContainer = Ext.ComponentQuery.query('[reference=mainContainer]')[0],
            ref = this.lookupReference('functionsGrid'),
            node = ref.getSelection()[0],
            widget;

        if (!node) {
            return;
        }

        widget = Ext.create({
            xtype: 'admin.compositeCleanseFunction',
            ccfNode: node,
            draftMode: view.getDraftMode()
        });

        widget.controller.edit = node.getPath('name', '.').substring(2);

        mainContainer.removeAll();
        mainContainer.add(widget);
        mainContainer.updateLayout();
    },

    onDeleteConfirmClick: function (btn) {
        var userDialog = Unidata.util.UserDialog,
            yesHandler,
            dialogBody,
            title,
            viewConfig,
            deleteConfirmationText;

        title = Unidata.i18n.t('glossary:removeFunction');
        yesHandler = this.deleteRecord;
        deleteConfirmationText = this.buildDeleteConfirmationHtml();
        dialogBody = userDialog.buildDialogBodyHtml({
            iconHtml: Unidata.util.Icon.getLinearIcon('warning'),
            textHtml: deleteConfirmationText
        });

        viewConfig = {
            yesText: Unidata.i18n.t('common:delete'),
            noText: Unidata.i18n.t('common:cancel_noun'),
            html: dialogBody
        };

        userDialog.showPrompt(title, null, yesHandler, this, btn, null, null, null, viewConfig);
    },

    deleteRecord: function () {
        var viewModel = this.getViewModel(),
            cleanseFunction = viewModel.get('currentRecord');

        cleanseFunction.safeErase({
            success: function () {
                viewModel.set('currentRecord', null);
                viewModel.set('currentRecordIcon', null);
                viewModel.set('currentRecordPath', null);
                this.reloadCleanseFunctions();
            }, failure: function () {
                Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>deleteFailure'));
            },
            scope: this
        });
    },

    getSelectedCleanseFunction: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('currentRecord');
    },

    updateDraftMode: function (draftMode) {
        var view = this.getView(),
            viewModel = this.getViewModel();

        viewModel.set('draftMode', draftMode);

        if (view.isConfiguring) {
            return;
        }

        this.reloadCleanseFunctions();
    },

    /**
     * Возвращает html верстку для диалогового окна
     *
     * @returns {*}
     */
    buildDeleteConfirmationHtml: function () {
        var tpl = new Ext.XTemplate('<div>{title} {afterTitle} {paragraph}</div>'),
            html;

        html = tpl.apply({
            title: Unidata.util.UserDialog.buildInlineBoldHtml(Unidata.i18n.t('admin.cleanseFunction>attention')),
            afterTitle: Unidata.i18n.t('admin.cleanseFunction>functionRemovalWarning'),
            paragraph: Unidata.util.UserDialog.buildBlockHtml(Unidata.i18n.t('admin.cleanseFunction>confirmDeleteFunction'))
        });

        return html;
    }
});
