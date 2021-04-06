Ext.define('Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunctionController', {
    extend: 'Unidata.view.admin.cleanseFunction.CleanseFunctionController',

    alias: 'controller.admin.compositeCleanseFunction',

    CFComponent: null,

    inputPorts: null,

    outputPorts: null,

    cleanseFunctionPath: null,

    edit: null,

    init: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            store = viewModel.getStore('cleanseGroupsStore'),
            draftMode = view.getDraftMode();

        this.callParent(arguments);

        this.CFComponent = Ext.create('Unidata.view.component.CleanseFunction', {
            draftMode: draftMode
        });
        this.CFComponent.setView(this.lookupReference('cfcontainer'));

        store.load({
            params: {
                draft: draftMode
            }
        });
    },

    onNodeDrop: function (nodeData, source, e, data) {
        var me = this,
            view = this.getView(),
            mouseEvent = e.event,
            node = data.records[0],
            cleanseFunction = node.getPath('name', '.').substring(2);

        Unidata.model.cleansefunction.CleanseFunction.load(cleanseFunction, {
            params: {
                draft: view.getDraftMode()
            },
            success: function (record) {
                me.CFComponent.addCleanseFunction(record, {x: mouseEvent.layerX, y: mouseEvent.layerY});
            }
            //TODO: add failure
        });
    },

    onPortDialogOpen: function (saveMethod, store) {
        var myForm = new Ext.form.Panel({
            width: 400,
            height: 350,
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
                            dataIndex: 'name'
                        },
                        {
                            flex: 1,
                            header: Unidata.i18n.t('admin.cleanseFunction>portType'),
                            dataIndex: 'dataType',
                            editor: new Ext.form.field.ComboBox({
                                typeAhead: true,
                                triggerAction: 'all',
                                store: [
                                    ['String', 'String'],
                                    ['Number', 'Number'],
                                    ['Integer', 'Integer'],
                                    ['Boolean', 'Boolean']
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
                    }
                }
            ],
            buttons: [
                {
                    text: Unidata.i18n.t('common:add'),
                    handler: function () {
                        var panel = this.up('panel'),
                            store = panel.down('grid').getStore();

                        store.add({
                            name: 'port' + store.count(),
                            dataType: 'String',
                            description: Unidata.i18n.t('admin.cleanseFunction>port') + ' ' + store.count()
                        });
                    }
                },
                {
                    text: Unidata.i18n.t('common:save'),
                    handler: saveMethod
                }
            ]
        });

        myForm.show();
    },

    onEditInputPortClick: function () {
        var me = this,
            store = me.CFComponent.getRecord().inputPorts();

        this.onPortDialogOpen(function () {
            me.CFComponent.drawInputPorts();
            me.inputPorts = store;
            me.setInputPorts();
            this.up('panel').close();
        }, store);
    },

    onEditOutputPortClick: function () {
        var me = this,
            store = me.CFComponent.getRecord().outputPorts();

        this.onPortDialogOpen(function () {
            me.CFComponent.drawOutputPorts();
            this.up('panel').close();
        }, store);
    },

    initInstance: function () {
        var me = this,
            view = me.getView(),
            viewModel = me.getViewModel(),
            newRecord;

        this.CFComponent.setInstance();

        if (this.edit) {
            me.cleanseFunctionPath = this.edit;
            Unidata.model.cleansefunction.CompositeCleanseFunction.load(this.edit, {
                params: {
                    draft: view.getDraftMode()
                },
                success: function (record) {
                    me.CFComponent.setRecord(record);
                    viewModel.set('currentRecord', record);
                    me.inputPorts = record.inputPorts();
                    me.setInputPorts();
                }
                //TODO: add failure
            });
        } else {
            newRecord = new Unidata.model.cleansefunction.CompositeCleanseFunction();

            newRecord.setLogic(new Unidata.model.cleansefunction.Logic());
            this.CFComponent.setRecord(newRecord);
        }

    },

    onSaveClick: function () {
        var me = this,
            record = this.CFComponent.getRecord(),
            modal;

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
                        associatedData = record.getAssociatedData(),
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
                            record.save({
                                success: function (response) {
                                    if (response.get('status') === 'SUCCESS') {
                                        modal.close();
                                        me.showMessage(Unidata.i18n.t('admin.common>dataSaveSuccess'));

                                        me.cleanseFunctionPath = record.getId();
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
            draftMode: view.getDraftMode()
        });

        mainContainer.removeAll();
        mainContainer.add(widget);
        mainContainer.updateLayout();
    }

});
