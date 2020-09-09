Ext.define('Unidata.view.component.dropdown.List', {
    extend: 'Ext.Container',

    requires: [
        'Unidata.view.component.dropdown.ListController',
        'Unidata.view.component.dropdown.ListModel'
    ],

    alias: 'widget.dropdownpickerfield.list',

    viewModel: {
        type: 'dropdownpickerfield.list'
    },

    controller: 'dropdownpickerfield.list',

    config: {
        showListTooltip: true,
        asOf: null // дата на которую производится поиск записей
    },

    referenceHolder: true,

    cls: 'un-dropdown-list',

    displayAttributes: null, // атрибуты для отображения
    useAttributeNameForDisplay: false,

    items: [
        {
            xtype: 'grid',
            reference: 'resultGrid',
            hideHeaders: true,
            columns: [
                {
                    flex: 1,
                    text: 'Name',
                    dataIndex: 'displayValue',
                    renderer: 'nameCellRenderer'
                }
            ],
            listeners: {
                itemclick: 'onItemClick',
                viewready: 'onViewReady'
            },
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    reference: 'resultPaging',
                    dock: 'top',
                    displayInfo: false,
                    hideRefreshButton: true,
                    hideFirstButton: true,
                    hideLastButton: true
                }
            ]
        }
    ],

    setEmptyStore: function () {
        var controller = this.getController();

        controller.setEmptyStore.apply(controller, arguments);
    },

    setWorkStore: function () {
        var controller = this.getController();

        controller.setWorkStore.apply(controller, arguments);
    }
});
