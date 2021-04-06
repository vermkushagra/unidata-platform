Ext.define('Unidata.view.admin.cleanseFunction.CleanseFunctionController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.cleanseFunction',

    inputPorts: {},
    cleanseFunctionPath: null,

    init: function () {
        this.callParent(arguments);

        this.reloadCleanseFunctions();
    },

    /**
     * @param {Ext.view.View} component
     * @param {Ext.data.Model} node
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     */
    onSelectCleanseFunction: function (component, node) {
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
        this.cleanseFunctionPath = path;

        if (record instanceof  Unidata.model.cleansefunction.CleanseFunction) {
            Unidata.model.cleansefunction.CleanseFunction.load(path, {
                params: {
                    draft: view.getDraftMode()
                },
                success: function (record) {
                    var deletable;

                    deletable = node.parentNode.isRoot();
                    viewModel.set('currentRecord', record);
                    viewModel.set('currentRecordDeletable', deletable);
                    me.inputPorts = record.inputPorts();
                    me.setInputPorts();
                }
            });
        } else {
            viewModel.set('currentRecord', record);
            viewModel.set('currentRecordDeletable', false);
        }
    },

    setInputPorts: function () {
        var ref = this.lookupReference('inputPorts'),
            oRef = this.lookupReference('outputPorts'),
            portField = {},
            type;

        ref.removeAll();
        oRef.removeAll();
        this.inputPorts.each(function (port) {
            switch (port.get('dataType')) {
                case 'Number':
                case 'Integer':
                    type = 'numberfield';
                    break;
                case 'Boolean':
                    type = 'combo';
                    portField.displayField = 'name';
                    portField.valueField = 'value';
                    portField.store = {
                        fields: ['name', 'value'],
                        data: [
                            {name: 'TRUE', value: true},
                            {name: 'FALSE', value: false},
                            {name: Unidata.i18n.t('glossary:notSet'), value: ''}
                        ]
                    };
                    break;
                default:
                    type = 'textfield';
            }

            portField.decimalPrecision = 20;
            portField.fieldLabel = Ext.String.htmlEncode(port.get('description')); // не забываем предотвращение XSS
            portField.name = port.getId();
            portField.xtype = type;
            ref.add(portField);
        });
    },

    setOutputPorts: function (ports) {
        var outputPortFields = this.lookupReference('outputPorts');

        outputPortFields.removeAll();
        ports.forEach(this.setOutputPort, this);
    },

    setOutputPort: function (port) {
        var outputPortFields = this.lookupReference('outputPorts'),
            cleanseFunction = this.getSelectedCleanseFunction(),
            outputPort,
            description;

        outputPort = cleanseFunction.findOutputPortByName(port.name);

        if (!outputPort) {
            throw new Error(Unidata.i18n.t('admin.cleanseFunction>outputPortNotFound'), {portName: port.name});
        }

        description = outputPort.get('description');

        outputPortFields.add({
            fieldLabel: description,
            value: port.value
        });
    },

    executeFunction: function () {
        var me   = this,
            form = this.lookupReference('inputPorts');

        if (!this.cleanseFunctionPath) {
            Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>functionSaveError'));

            return;
        }

        if (form.isValid()) {
            var data,
                jsonData,
                value,
                values = form.getValues(),
                dataType;

            jsonData = {
                functionName: this.cleanseFunctionPath,
                simpleAttributes: []
            };

            for (var i in values) {
                if (values.hasOwnProperty(i)) {
                    data     = me.inputPorts.getById(i);
                    dataType = data.get('dataType');

                    switch (data.get('dataType')) {
                        case 'Number':
                            value = parseFloat(values[i].replace(Ext.util.Format.decimalSeparator, '.'));
                            break;
                        case 'Integer':
                            value = Number(values[i]);
                            break;
                        default:
                            value = values[i];
                            // добавлено по мотивам бага UN-6895
                            // если значение не указано, то отправляем null
                            if (Ext.isEmpty(value)) {
                                value = null;
                            }
                    }

                    // по задаче UN-2432 FE должен отправлять вместо Any всегда String
                    if (dataType === 'Any') {
                        dataType = 'String';
                    }

                    jsonData.simpleAttributes.push({
                        name: data.get('name'),
                        type: dataType,
                        value: value
                    });
                }
            }
            Ext.Ajax.request({
                url: Unidata.Config.getMainUrl() + 'internal/meta/cleanse-functions/execute',
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                jsonData: Ext.util.JSON.encode(jsonData),
                success: function (response) {
                    var jsonResp;

                    if (response.status === 200) {
                        jsonResp = Ext.util.JSON.decode(response.responseText);

                        if (jsonResp.resultCode === 'ok') {
                            me.setOutputPorts(jsonResp.simpleAttributes);
                        } else {
                            Unidata.showError(jsonResp.errorMessage);
                        }
                    }
                }
            });
        }
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
                            me.onSelectCleanseFunction(functionsGrid, lastNode);
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

            if (!!component.getViewModel().get('isSuccess')) {
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
                msg = Unidata.i18n.t('admin.cleanseFunction>thirdPartyFunction', {action: actionTxt, name: functionRecord.get('name')});
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
            selection = ref.getSelection()[0],
            widget;

        if (!selection) {
            return;
        }

        widget = Ext.create({
            xtype: 'admin.compositeCleanseFunction',
            draftMode: view.getDraftMode()
        });

        widget.controller.edit = selection.getPath('name', '.').substring(2);

        mainContainer.removeAll();
        mainContainer.add(widget);
        mainContainer.updateLayout();
    },

    onDeleteConfirmClick: function (btn) {
        var msgBox = Ext.window.MessageBox.create({});

        msgBox.show({
            title: Unidata.i18n.t('glossary:removeFunction'),
            message: Unidata.i18n.t('admin.cleanseFunction>confirmDeleteFunction'),
            buttons: Ext.MessageBox.YESNO,
            buttonText: {
                yes: Unidata.i18n.t('common:yes'),
                no: Unidata.i18n.t('common:no')
            },
            scope: this,
            animateTarget: btn,
            defaultFocus: 3,
            fn: function (btn) {
                if (btn === 'yes') {
                    this.deleteRecord();
                }
            }
        });
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
    }
});
