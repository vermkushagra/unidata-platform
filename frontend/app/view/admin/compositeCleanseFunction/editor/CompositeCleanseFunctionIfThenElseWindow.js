/**
 * Окно настройки блока if-then-else
 *
 * @author Denis Makarov
 * @date 2018-05-30
 */
Ext.define('Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionIfThenElseWindow', {
    extend: 'Ext.form.Panel',

    requires: ['Unidata.model.cleansefunction.IfThenElsePort'],

    config: {
        minPortCount: 1,
        maxPortCount: 7
    },

    referenceHolder: true,
    width: 500,
    height: 300,
    floating: true,
    closable: true,
    modal: true,
    cls: 'un-cleanse-function-ifthenelse-window',
    title: Unidata.i18n.t('admin.cleanseFunction>createPorts'),
    layout: {
        type: 'fit',
        align: 'stretch'
    },
    store: null,

    initItems: function () {
        var portsCfg;

        this.callParent(arguments);

        this.store = Ext.create('Ext.data.Store', {
            autoDestroy: true,
            model: 'Unidata.model.cleansefunction.IfThenElsePort',
            proxy: {
                type: 'memory'
            },
            data: []
        });

        portsCfg = this.buildPortsCfg(this.store);

        this.add(portsCfg);

        this.disableUiElements();
    },

    disableUiElements: function () {
        var saveButton = this.lookupReference('saveButton'),
            addPortButton = this.lookupReference('addPortButton'),
            store = this.store,
            storeSize;

        if (!this.store) {
            return;
        }

        storeSize = store.getData().length;

        if (storeSize < this.getMinPortCount()) {
            saveButton.setDisabled(true);
        } else {
            saveButton.setDisabled(false);
        }

        if (storeSize == this.getMaxPortCount()) {
            addPortButton.setDisabled(true);
        } else {
            addPortButton.setDisabled(false);
        }

    },

    buildPortsCfg: function (store) {
        var me = this,
            portsCfg = {
                xtype: 'grid',
                store: store,
                scrollable: true,
                columns: [
                    {
                        flex: 1,
                        header: Unidata.i18n.t('admin.cleanseFunction>portNumber'),
                        dataIndex: 'name',
                        renderer: function (value, columnMeta, record, index) {
                            return index + 1;
                        }
                    },
                    {
                        flex: 1,
                        header: Unidata.i18n.t('admin.cleanseFunction>portType'),
                        dataIndex: 'dataType',
                        editor: new Ext.form.field.ComboBox({
                            typeAhead: true,
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
                        xtype: 'un.actioncolumn',
                        width: 30,
                        sortable: false,
                        menuDisabled: true,
                        items: [{
                            faIcon: 'trash-o',
                            handler: function (grid, rowIndex) {
                                grid.getStore().removeAt(rowIndex);
                                me.disableUiElements();
                            }
                        }]
                    }
                ],
                plugins: {
                    ptype: 'cellediting',
                    clicksToEdit: 1
                },
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'bottom',
                        padding: 8,
                        layout: {
                            type: 'vbox',
                            align: 'right'
                        },
                        items: [
                            {
                                xtype: 'button',
                                scale: 'small',
                                cls: 'un-save-ifthenelse-button',
                                text: Unidata.i18n.t('common:save'),
                                reference: 'saveButton',
                                handler: me.onSaveButtonClick.bind(me)
                            }
                        ]
                    },
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
                                reference: 'addPortButton',
                                iconCls: 'icon-plus',
                                handler: function () {
                                    var portDataType = 'String';

                                    store.add({
                                        dataType: portDataType
                                    });

                                    me.disableUiElements();
                                },
                                tooltip: Unidata.i18n.t('common:add')
                            }
                        ]
                    }

                ]

            };

        return portsCfg;
    },

    onSaveButtonClick: function () {
        var ports = this.store;

        this.fireEvent('okbtnclick', this, ports.getData());
        this.close();
    }
});
