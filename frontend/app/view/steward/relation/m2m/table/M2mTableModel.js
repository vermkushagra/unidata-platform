/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2017-05-02
 */

Ext.define('Unidata.view.steward.relation.m2m.table.M2mTableModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.relation.m2mtable',

    data: {
        readOnly: null
    },

    formulas: {},

    stores: {
        m2mrecords: {
            type: 'array',
            model: 'data.RelationReference',
            autoLoad: false
        },
        filteredm2mrecords: {
            source: 'ext-empty-store',
            pageSize: 20, // конфигурируется в контроллере через точку расширения Unidata.uiuserexit.overridable.relation.M2m
            currentPage: 1,
            totalCount: 0,
            // pagingtoolbar не умеет работать c chain store т.к. у него нет метода getTotalCount
            getTotalCount: function () {
                return this.totalCount;
            }
        }
    }
});
