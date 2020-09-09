Ext.define('Unidata.view.component.dropdown.DetailModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.dropdownpickerfield.detail',

    data: {},

    stores: {
        details: {
            model: 'Unidata.model.search.SearchHit',
            proxy: {
                type: 'data.searchproxyform'
            },
            sorters: [{
                property: '$from',
                direction: 'ASC'
            }]
        }
    },

    formulas: {}
});
