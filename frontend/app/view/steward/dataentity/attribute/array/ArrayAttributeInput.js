/**
 * @author Aleksandr Bavin
 * @date 2017-01-20
 */
Ext.define('Unidata.view.steward.dataentity.attribute.array.ArrayAttributeInput', {

    extend: 'Ext.container.Container',

    alias: 'widget.arrayattributeinput',

    viewModel: {
        type: 'arrayattributeinput'
    },

    controller: 'arrayattributeinput',

    requires: [
        'Unidata.view.steward.dataentity.attribute.array.ArrayAttributeInputController',
        'Unidata.view.steward.dataentity.attribute.array.ArrayAttributeInputModel',
        'Unidata.view.steward.dataentity.attribute.SimpleAttribute'
    ],

    cls: 'un-array-attribute-input',

    referenceHolder: true,

    config: {
        pageSize: 10,
        value: null,
        metaRecord: null,
        dataRecord: null,
        metaAttribute: null,
        dataAttribute: null,
        attributePath: null,
        readOnly: null,
        disabled: null,
        preventMarkField: null
    },

    methodMapper: [
        {
            method: 'addArrayItem'
        }
    ],

    items: [
        {
            xtype: 'container',
            reference: 'arrayContainer',
            items: []
        },
        {
            xtype: 'container',
            reference: 'inputControls',
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            cls: 'un-array-attribute-input-controls',
            items: [
                {
                    xtype: 'container',
                    reference: 'inputControlsSwitch',
                    cls: 'un-array-attribute-input-controls-switch',
                    tpl: '<span>' +  Unidata.i18n.t('dataentity>records') + ': {count}</span>',
                    data: {
                        count: 0
                    },
                    bind: {
                        data: {
                            count: '{count}'
                        }
                    }
                },
                {
                    xtype: 'pagingtoolbar',
                    reference: 'pagingToolbar',
                    // displayMsg: 'Отображаются элементы: {0} - {1}, всего {2}',
                    displayMsg: '',
                    flex: 1,
                    displayInfo: false,
                    // hidden: true,
                    bind: {
                        //hidden: '{pagingHidden}',
                        store: '{values}'
                    },
                    hideFirstButton: true,
                    hideSeparator1: true,
                    hideBeforePageText: true,
                    hideSeparator2: true,
                    hideLastButton: true,
                    hideSeparator3: true,
                    hideRefreshButton: true
                }
            ]
        }
    ],

    setValue: function (value) {
        var viewModel = this.getViewModel(),
            store = this.getValuesStore();

        store.getProxy().setData(Ext.clone(value));
        store.reload();

        if (value) {
            viewModel.set('count', value.length);
        }

        return this.callParent(arguments);
    },

    getValue: function () {
        var data = Ext.clone(this.getValuesStore().getProxy().getData());

        Ext.Array.each(data, function (item) {
            if (this.value === '') {
                this.value = null;
            }
            delete item.id;
        });

        return data;
    },

    initItems: function () {
        this.callParent(arguments);
    },

    onDestroy: function () {
        this.callParent(arguments);
    },

    updatePageSize: function (value) {
        this.getViewModel().set('pageSize', value);
        this.getValuesStore().pageSize = value;
        this.fixPage();
    },

    fixPage: function () {
        var pagingToolbar = this.lookupReference('pagingToolbar'),
            pageData;

        if (pagingToolbar) {
            pageData = pagingToolbar.getPageData();

            if (pageData.currentPage > pageData.pageCount) {
                pagingToolbar.moveLast();
            } else {
                pagingToolbar.doRefresh();
            }
        }
    },

    setDisabled: function (value) {
        this.getViewModel().set('disabled', value);
    },

    updateReadOnly: function (value) {
        this.getViewModel().set('readOnly', value);
    },

    getValuesStore: function () {
        return this.getViewModel().getStore('values');
    }

});
