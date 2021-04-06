/**
 * Выпадающий список с списками записей
 *
 * @author Sergey Shishigin
 * @date 2017-02-01
 */
Ext.define('Unidata.view.component.search.query.relation.component.EtalonClusterCombobox', {
    extend: 'Ext.form.field.ComboBox',

    alias: 'widget.component.search.query.relation.component.etalonclustercombobox',

    mixins: [
        'Unidata.view.component.search.query.relation.mixin.EtalonClusterMixin'
    ],

    config: {
        entityName: null
    },

    displayField: 'name',
    valueField: 'name',

    autoSelect: false,
    editable: false,
    emptyText: '- ' + Unidata.i18n.t('search>query.recordSets') + ' -',
    queryMode: 'local',

    triggers: {
        clear: {
            cls: 'x-form-clear-trigger',
            handler: function () {
                this.setValue(null);
            }
        }
    },

    store: {
        model: 'Unidata.model.etaloncluster.EtalonCluster',
        proxy: {
            type: 'memory'
        }
    },

    initComponent: function () {
        this.callParent(arguments);
        this.initStore();
    },

    updateEntityName: function (entityName) {
        this.filterEtalonClusters(entityName);

        if (!entityName) {
            this.setValue(null);
        }
    }
});
