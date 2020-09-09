/**
 * Список правил сопоставления (отдельный компонент) необходимо его интегрировать
 * в 'Unidata.view.admin.duplicates.list.RuleList' а старую реализацию выпилить
 *
 * @author Ivan Marshalkin
 * @date 2016-10-27
 */

Ext.define('Unidata.view.admin.duplicates.list.RuleGrid', {
    extend: 'Ext.grid.Panel',

    config: {
        entityName: null                // кодовое имя реестра / справочника для которого отображаем правила поиска дубликатов
    },

    sortableColumns: false,
    hideHeaders: true,

    initComponent: function () {
        var entityName = this.getEntityName();

        this.callParent(arguments);

        this.getStore().on('load', this.onStoreLoad, this);

        if (entityName) {
            this.reloadRuleList();
        }
    },

    store: {
        model: 'Unidata.model.matching.Rule',
        autoLoad: false,
        proxy: {
            type: 'ajax',
            url: Unidata.Config.getMainUrl() + 'internal/matching/rules',
            reader: {
                type: 'json',
                rootProperty: 'content'
            }
        }
    },
    columns: [
        {
            dataIndex: 'name',
            flex: 1
        }
    ],

    /**
     * Обновление списка правил
     */
    reloadRuleList: function () {
        var entityName = this.getEntityName();

        this.getStore().reload({
            params: {
                entityName: entityName
            }
        });
    },

    /**
     * Обработка события загрузки стора
     */
    onStoreLoad: function (store, records, successful, operation) {
        this.fireEvent('roleload', records, successful, operation);
    },

    /**
     * Возвращает массив записей правил
     *
     * @returns {*|Ext.data.Model[]}
     */
    getMatchingRules: function () {
        return this.getStore().getRange();
    }
});
