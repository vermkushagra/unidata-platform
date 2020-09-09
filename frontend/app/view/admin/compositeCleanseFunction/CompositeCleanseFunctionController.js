Ext.define('Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunctionController', {
    extend: 'Unidata.view.admin.cleanseFunction.CleanseFunctionController',

    alias: 'controller.admin.compositeCleanseFunction',

    CFComponent: null,

    inputPorts: null,

    outputPorts: null,

    cleanseFunctionPath: null,

    inputPortSeqIdentifier: null,
    outputPortSeqIdentifier: null,

    portDialogSaveButton: null,

    edit: null,

    init: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            store = viewModel.getStore('cleanseGroupsStore'),
            draftMode = view.getDraftMode();

        this.callParent(arguments);

        this.CFComponent = Ext.create('Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionEditor', {
            draftMode: draftMode
        });
        this.CFComponent.setView(this.lookupReference('cfcontainer'));

        store.load({
            params: {
                draft: draftMode
            }
        });

        this.inputPortSeqIdentifier = Ext.create('Ext.data.identifier.Sequential');
        this.outputPortSeqIdentifier = Ext.create('Ext.data.identifier.Sequential');
    },

    onNodeDrop: function (nodeData, source, e, data) {
        var CompositeCleanseFunction = Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunction,
            me = this,
            view = this.getView(),
            mouseEvent = e.event,
            node = data.records[0],
            record,
            blockType,
            cleanseFunction,
            constant;

        if (node instanceof Ext.data.TreeModel) {
            record = node.get('record');

            if (record instanceof Unidata.model.cleansefunction.Group) {
                return;
            }
            cleanseFunction = node.getPath('name', '.').substring(2);
            Unidata.model.cleansefunction.CleanseFunction.load(cleanseFunction, {
                params: {
                    draft: view.getDraftMode()
                },
                success: function (record) {
                    me.CFComponent.addCleanseFunction(record, {x: mouseEvent.layerX, y: mouseEvent.layerY});
                },
                failure: function (record) {
                    Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>functionLoadError', {name: record.get('name')}));
                }
            });
        } else {
            blockType = node.get('type');
            switch (blockType) {
                case CompositeCleanseFunction.CCF_BLOCK_TYPE.CONSTANT:
                    // default empty constant block
                    constant = Ext.create('Unidata.model.data.SimpleAttribute', {
                        type: 'String'
                    });

                    me.CFComponent.addConstant(constant, {x: mouseEvent.layerX, y: mouseEvent.layerY});
                    break;
                case CompositeCleanseFunction.CCF_BLOCK_TYPE.IFTHENELSE:
                    me.CFComponent.editIfThenElsePorts({x: mouseEvent.layerX, y: mouseEvent.layerY});
                    break;
            }
        }
    },

    onPortDialogOpen: function (type, saveMethod, store) {
        var me = this,
            myForm;

        myForm = new Ext.form.Panel({
            referenceHolder: true,
            width: 500,
            height: 300,
            floating: true,
            closable: true,
            modal: true,
            title: Unidata.i18n.t('admin.cleanseFunction>editPorts'),
            layout: {
                type: 'fit',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'grid',
                    store: store,
                    scrollable: true,
                    columns: [
                        {
                            flex: 1,
                            header: Unidata.i18n.t('admin.cleanseFunction>portName'),
                            dataIndex: 'name',
                            editor: {
                                xtype: 'textfield',
                                allowBlank: false
                            }
                        },
                        {
                            flex: 1,
                            header: Unidata.i18n.t('admin.cleanseFunction>portType'),
                            dataIndex: 'dataType',
                            editor: new Ext.form.field.ComboBox({
                                typeAhead: false,
                                editable: false,
                                triggerAction: 'all',
                                store: [
                                    ['Any', Unidata.i18n.t('admin.dq>dataType.any')],
                                    ['String', Unidata.i18n.t('admin.dq>dataType.string')],
                                    ['Integer', Unidata.i18n.t('admin.dq>dataType.integer')],
                                    ['Number', Unidata.i18n.t('admin.dq>dataType.number')],
                                    ['Date', Unidata.i18n.t('admin.dq>dataType.date')],
                                    ['Timestamp', Unidata.i18n.t('admin.dq>dataType.timestamp')],
                                    ['Time', Unidata.i18n.t('admin.dq>dataType.time')],
                                    ['Boolean', Unidata.i18n.t('admin.dq>dataType.boolean')]
                                ]
                            })
                        },
                        {
                            flex: 1,
                            header: Unidata.i18n.t('glossary:description'),
                            dataIndex: 'description',
                            editor: {
                                allowBlank: false
                            }
                        },
                        {
                            flex: 1,
                            header: Unidata.i18n.t('admin.cleanseFunction>required'),
                            dataIndex: 'required',
                            xtype: 'checkcolumn'
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                icon: 'resources/delete.png',
                                handler: function (grid, rowIndex) {
                                    grid.getStore().removeAt(rowIndex);
                                }
                            }]
                        }
                    ],
                    plugins: {
                        ptype: 'cellediting',
                        clicksToEdit: 1
                    },
                    listeners: {
                        edit: this.onGridEdit.bind(this, store)
                    },
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'bottom',
                            layout: {
                                type: 'hbox',
                                pack: 'center',
                                align: 'middle'
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    ui: 'un-toolbar-admin',
                                    scale: 'small',
                                    iconCls: 'icon-plus',
                                    handler: function () {
                                        var CompositeCleanseFunction = Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunction,
                                            result,
                                            portSeqIdentifier,
                                            portDataType,
                                            portDescription;

                                        switch (type) {
                                            case CompositeCleanseFunction.CCF_BLOCK_TYPE.INPUT_PORTS:
                                                portSeqIdentifier = me.inputPortSeqIdentifier;
                                                break;
                                            case CompositeCleanseFunction.CCF_BLOCK_TYPE.OUTPUT_PORTS:
                                                portSeqIdentifier = me.outputPortSeqIdentifier;
                                                break;
                                        }

                                        result = me.generatePortName(portSeqIdentifier, store);
                                        portDescription = Ext.String.format('{0} {1}', Unidata.i18n.t('admin.cleanseFunction>port'), result.id);
                                        portDataType = 'String';

                                        store.add({
                                            name: result.portName,
                                            dataType: portDataType,
                                            description: portDescription
                                        });
                                    },
                                    tooltip: Unidata.i18n.t('common:add')
                                }
                            ]
                        }
                    ]
                }
            ],
            buttons: [
                {
                    text: Unidata.i18n.t('common:save'),
                    reference: 'saveButton',
                    handler: saveMethod
                }
            ]
        });

        myForm.show();

        this.portDialogSaveButton = myForm.lookupReference('saveButton');
        this.calcPortDialogSaveButtonDisabled(store);
    },

    onGridEdit: function (store, self, e) {
        var field = e.field;

        if (field === 'name') {
            this.calcPortDialogSaveButtonDisabled(store);
        }
    },

    /**
     *
     * @param store
     * @return {boolean}
     */
    checkPortNamesUnique: function (store) {
        var ports,
            names;

        ports = store.getRange();
        names = Ext.Array.map(ports, function (port) {
            return port.get('name');
        });

        return names.length === Ext.Array.unique(names).length;
    },

    calcPortDialogSaveButtonDisabled: function (store) {
        var unique;

        unique = this.checkPortNamesUnique(store);

        if (this.portDialogSaveButton) {
            this.portDialogSaveButton.setDisabled(!unique);
        }
    },

    generatePortName: function (seqIdentifier, store) {
        var id,
            portName,
            ports,
            isUnique = false,
            result;

        ports = store.getRange();

        while (!isUnique) {
            id = seqIdentifier.generate();
            portName = Ext.String.format('port{0}', id);
            isUnique = Ext.Array.every(ports, function (port) {
                return port.get('name') !== portName;
            });
        }

        result = {portName: portName, id: id};

        return result;
    },

    onEditInputPortClick: function () {
        var CompositeCleanseFunction = Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunction,
            me = this,
            store = me.CFComponent.getRecord().inputPorts(),
            type;

        type = CompositeCleanseFunction.CCF_BLOCK_TYPE.INPUT_PORTS;
        this.onPortDialogOpen(type, function () {
            me.CFComponent.drawInputPorts();
            me.inputPorts = store;
            // me.buildInputPorts();
            this.up('panel').close();
        }, store);
    },

    onEditOutputPortClick: function () {
        var CompositeCleanseFunction = Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunction,
            me = this,
            store = me.CFComponent.getRecord().outputPorts(),
            type;

        type = CompositeCleanseFunction.CCF_BLOCK_TYPE.OUTPUT_PORTS;
        this.onPortDialogOpen(type, function () {
            me.CFComponent.drawOutputPorts();
            this.up('panel').close();
        }, store);
    },

    initInstance: function () {
        var me = this,
            view = me.getView(),
            viewModel = me.getViewModel(),
            supportedExecutionContextsCheckboxGroup = view.supportedExecutionContextsCheckboxGroup,
            newCleanseFunction;

        this.CFComponent.initJsPlumb();

        if (this.edit) {
            me.cleanseFunctionPath = this.edit;
            Unidata.model.cleansefunction.CompositeCleanseFunction.load(this.edit, {
                params: {
                    draft: view.getDraftMode()
                },
                success: function (cleanseFunction) {
                    var supportedExecutionContexts;

                    me.CFComponent.setRecord(cleanseFunction);
                    viewModel.set('currentRecord', cleanseFunction);
                    me.inputPorts = cleanseFunction.inputPorts();
                    supportedExecutionContexts = cleanseFunction.get('supportedExecutionContexts');
                    supportedExecutionContextsCheckboxGroup.setValue({
                        supported_execution_contexts: supportedExecutionContexts
                    });
                    // me.buildInputPorts();
                }
                //TODO: add failure
            });
        } else {
            newCleanseFunction = new Unidata.model.cleansefunction.CompositeCleanseFunction();

            newCleanseFunction.setLogic(new Unidata.model.cleansefunction.Logic());
            this.CFComponent.setRecord(newCleanseFunction);
        }

    },

    onSaveClick: function () {
        var me = this,
            view = this.getView(),
            record = this.CFComponent.getRecord(),
            supportedExecutionContextsCheckboxGroup = view.supportedExecutionContextsCheckboxGroup,
            supportedExecutionContexts,
            modal;

        supportedExecutionContexts = supportedExecutionContextsCheckboxGroup.getValue()['supported_execution_contexts'];

        if (Ext.isString(supportedExecutionContexts)) {
            supportedExecutionContexts = [supportedExecutionContexts];
        }

        if (!Ext.isArray(supportedExecutionContexts) || supportedExecutionContexts < 1) {
            supportedExecutionContextsCheckboxGroup.validate();
            Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>requiredSupportedExecutionContexts'));

            return;
        }

        //@TODO: Create component save window;
        modal = Ext.create({
            xtype: 'window',
            title: Unidata.i18n.t('common:save', {context: 'process'}),
            height: 200,
            width: 400,
            layout: 'fit',
            modal: true,
            bodyPadding: 10,
            items: [
                {
                    xtype: 'form',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    border: false,
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel: Unidata.i18n.t('admin.metamodel>functionName'),
                            allowBlank: false,
                            listeners: {
                                change: function (field, value) {
                                    record.set('name', value);
                                }
                            },
                            value: record.get('name')
                        }, {
                            xtype: 'textareafield',
                            fieldLabel: Unidata.i18n.t('glossary:description'),
                            flex: 1,
                            allowBlank: true,
                            listeners: {
                                change: function (field, value) {
                                    record.set('description', value);
                                }
                            },
                            value: record.get('description')
                        }
                    ]
                }
            ],
            buttons: [{
                text: Unidata.i18n.t('common:save'),
                handler: function () {
                    var view = me.getView(),
                        viewModel = me.getViewModel(),
                        store = viewModel.getStore('cleanseGroupsStore'),
                        record = me.CFComponent.getRecord(),
                        associatedData = record.getAssociatedData(null, {serialize: true, persist: true}),
                        form = modal.down('form'),
                        checkValid,
                        //checkValid = me.CFComponent.checkValid(),
                        i;

                    // временно отключена проверка checkValid, т.к. этот метод имеет баги
                    // необходимо вернуть проверку в рамках задачи UN-5102
                    checkValid = {
                        valid: true
                    };

                    if (checkValid.valid === true) {
                        if (form.isValid()) {
                            for (i in associatedData) {
                                if (associatedData.hasOwnProperty(i)) {
                                    record.set(i, associatedData[i]);
                                }
                            }
                            record.setId(record.data.name);

                            if (record.modified.name) {
                                record.setId(record.modified.name);
                            }

                            record.set('type', 'COMPOSITE_FUNCTION');
                            record.set('supportedExecutionContexts', supportedExecutionContexts);
                            record.save({
                                success: function (response) {
                                    if (response.get('status') === 'SUCCESS') {
                                        modal.close();
                                        me.showMessage(Unidata.i18n.t('admin.common>dataSaveSuccess'));

                                        me.cleanseFunctionPath = record.getId();
                                        Unidata.util.api.CleanseFunction.resetCache();
                                        viewModel.set('currentRecord', record);

                                        store.load({
                                            params: {
                                                draft: view.getDraftMode()
                                            },
                                            callback: function (records) {
                                                me.lookupReference('functionsGrid').setData(records[0]);
                                            }
                                        });
                                    } else {
                                        Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>saveCompositeFunctionError'));
                                    }
                                }
                                //TODO: add failure
                            });
                        }
                    } else {
                        Unidata.showError(checkValid.errorText);

                        return;
                    }
                }
            }]
        });

        modal.show();
    },

    onResize: function () {
        this.CFComponent.refresh();
    },

    onContainerRender: function (component) {
        var me = this;

        component.dropZone = Ext.create('Ext.dd.DropZone', component.getEl(), {
            getTargetFromEvent: function () {
                return component;
            },
            onNodeDrop: function () {
                me.onNodeDrop.apply(me, arguments);
            },
            ddGroup: 'ccfDragDrop'
        });
    },

    onReturnBackClick: function () {
        var view = this.getView(),
            mainContainer = Ext.ComponentQuery.query('[reference=mainContainer]')[0],
            widget;

        if (mainContainer.items.getAt(0) !== view) {
            return;
        }

        widget = Ext.create({
            xtype: 'admin.cleanseFunction',
            initNodeSelection: view.getCcfNode(),
            draftMode: view.getDraftMode()
        });

        mainContainer.removeAll();
        mainContainer.add(widget);
        mainContainer.updateLayout();
    },

    createSlider: function (el) {
        var slider,
            sliderPanel,
            self = this,
            increment = 500,
            minValue = 1500,
            maxValue = 3000;

        slider = Ext.create('Ext.slider.Single', {
            cls: 'un-composite-cleanse-function-slider',
            labelWidth: 180,
            width: 320,
            labelSeparator: '',
            value: 1500,
            increment: increment,
            minValue: 1500,
            maxValue: 3000,
            renderTo: el,
            listeners: {
                changecomplete: this.onSliderChangeComplete.bind(this)
            }
        });
        Ext.create('Ext.Container', {
            width: 550,
            height: 100,
            bodyPadding: 10,
            items: [{
                xtype: 'fieldcontainer',
                fieldLabel: Unidata.i18n.t('admin.cleanseFunction>workingAreaSize'),
                labelWidth: 180,

                layout: 'hbox',
                items: [{
                    xtype: 'button',
                    cls: 'un-slider-increment-button un-slider-button-minus',
                    handler: function () {
                        var nextValue = slider.getValue() - increment;

                        if (nextValue  >= minValue) {
                            slider.setValue(nextValue);
                        } else {
                            slider.setValue(minValue);
                        }
                        self.onSliderChangeComplete(slider, slider.getValue());
                    }
                },
                    slider, {
                        xtype: 'button',
                        cls: 'un-slider-increment-button un-slider-button-plus',
                        handler: function () {
                            var nextValue = slider.getValue() + increment;

                            if (nextValue  <= maxValue) {
                                slider.setValue(nextValue);
                            } else {
                                slider.setValue(maxValue);
                            }
                            self.onSliderChangeComplete(slider, slider.getValue());
                        }
                    }
                ]
            }],
            renderTo: el
        });

        return sliderPanel;
    },

    onSliderChangeComplete: function (self, width) {
        var view = this.getView(),
            timeout = 1000,
            cfcontainer = view.cfcontainer,
            oldWidth;

        view.setLoading(true);
        self.setDisabled(true);

        oldWidth = cfcontainer.getWidth();
        cfcontainer.setWidth(width);

        if (width < oldWidth) {
            this.CFComponent.recalcBlockPositions(width, oldWidth);
        }

        Ext.defer(function () {
            view.setLoading(false);
            self.setDisabled(false);
        }, timeout);
    }
});
