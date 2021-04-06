/**
 * @author Aleksandr Bavin
 * @date 2017-06-27
 */
Ext.define('Unidata.view.component.dashboard.entity.items.DqerrorsAggregation', {

    extend: 'Ext.panel.Panel',

    mixins: [
        'Unidata.mixin.Savable'
    ],

    alias: 'widget.component.dashboard.entity.dqerrors.aggregation',

    layout: 'fit',

    referenceHolder: true,

    config: {
        entityName: null
    },

    cls: [
        'un-dashboard-entity-inner',
        'un-dqerrors-aggregation'
    ],

    title: Unidata.i18n.t('dashboard>categoryErrors'),

    items: [],

    updateEntityName: function (entityName) {
        var view = this,
            table = Ext.create('Unidata.model.table.dataquality.AggregationTable');

        table.load({
            params: {
                entityName: entityName
            },
            callback: this.onTableLoaded,
            scope: this
        });

        view.fireEvent('loadingstart', view);
    },

    onTableLoaded: function (model, operation, success) {
        var view = this,
            columns = [];

        view.fireEvent('loadingend', view);

        if (!success) {
            return;
        }

        columns.push({
            dataIndex: 'rowDisplayName',
            text: model.get('rowDisplayName') + ' / ' + model.get('columnDisplayName'),
            flex: 3
        });

        model.columns().each(function (column) {
            columns.push({
                dataIndex: column.get('name'),
                text: column.get('displayName')
            });
        });

        this.suspendLayouts();
        this.removeAll();
        this.add({
            xtype: 'grid',
            layout: 'fit',
            viewConfig: {
                listeners: {
                    cellclick: function (grid, td, cellIndex, record, tr, rowIndex, e) {
                        var rowModel = model.rows().getAt(rowIndex),
                            columnModel = model.columns().getAt(cellIndex - 1),
                            searchData = {},
                            runRedirect = false,
                            rowModelName,
                            columnModelName;

                        searchData['entityName'] = model.get('entityName');

                        if (rowModel) {
                            rowModelName = rowModel.get('name');

                            if (rowModelName !== 'Total') {
                                searchData[model.get('rowSearchName')] = rowModelName;
                                runRedirect = true;
                            }
                        }

                        if (columnModel) {
                            columnModelName = columnModel.get('name');

                            // если нет данных - выходим
                            if (!record.get(columnModelName)) {
                                return false;
                            }

                            if (columnModelName !== 'Total') {
                                searchData[model.get('columnSearchName')] = columnModelName;
                                runRedirect = true;
                            }
                        }

                        if (!runRedirect) {
                            return false;
                        }

                        Unidata.util.Router
                            .setToken('main', {section: 'data', reset: true})
                            .setToken('dataSearch', searchData);
                    }
                }
            },
            disableSelection: true,
            enableColumnHide: false,
            enableColumnResize: false,
            columns: {
                defaults: {
                    renderer: function (value) {
                        if (value === null || value === undefined) {
                            return '<span style="display: none;">&nbsp;</span>';
                        }

                        return value;
                    },
                    sortable: false,
                    hidable: false,
                    flex: 1
                },
                items: columns
            },
            store: model.getGridStore()
        });
        this.resumeLayouts(true);
    }

});
