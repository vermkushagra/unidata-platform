Ext.define('Unidata.view.steward.dataentity.attribute.array.ArrayAttributeInputModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.arrayattributeinput',

    data: {
        pageSize: 1,
        count: 0,
        readOnly: false,
        disabled: false
    },

    stores: {
        values: {
            fields: ['value', 'displayValue'],
            proxy: {
                enablePaging: true,
                type: 'memory',
                reader: {
                    type: 'json'
                }
            }
        }
    },

    formulas: {
        pagingHidden: {
            bind: {
                count: '{count}'
            },

            get: function (data) {
                var hidden = true;

                if (data.count && data.count > 10) {
                    hidden = false;
                }

                return hidden;
            }
        }

    }

});
