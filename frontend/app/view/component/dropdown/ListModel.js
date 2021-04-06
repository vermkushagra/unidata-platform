Ext.define('Unidata.view.component.dropdown.ListModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.dropdownpickerfield.list',

    data: {
        entityType: null,
        entity: null
    },

    stores: {
        dropdownList: {
            model: 'Unidata.model.search.SearchHit',
            pageSize: 10,
            proxy: {
                type: 'data.searchproxysayt'
            }
        },
        emptyStore: {
            model: 'Unidata.model.search.SearchHit'
        }
    },

    formulas: {
        isLookupEntity: {
            bind: {
                bindTo: '{entityType}'
            },
            get: function (entityType) {
                var result = false;

                if (entityType === 'lookupentity') {
                    result = true;
                }

                return result;
            }
        },
        codeAttributeName: {
            bind: {
                entity: '{entity}',
                entityType: '{entityType}',
                deep: true
            },
            get: function (getter) {
                var codeName = null;

                if (getter.entityType === 'lookupentity' && getter.entity) {
                    codeName = getter.entity.getCodeAttribute().get('name');
                }

                return codeName;
            }
        }
    }
});
