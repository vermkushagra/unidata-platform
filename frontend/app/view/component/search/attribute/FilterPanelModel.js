/**
 * @author Ivan Marshalkin
 * 2015-09-01
 */

Ext.define('Unidata.view.component.search.attribute.FilterPanelModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.search.attribute.filterpanel',

    data: {
        addFilterTabletButtonDisabled: true
    },

    stores: {
        searchableAttributes: {
            model: 'Unidata.model.attribute.AbstractAttribute',
            sorters: [
                {
                    property: 'order',
                    direction: 'ASC'
                }
            ]
        }
    }

});
