/**
 * Панель реализующая настройка сортировки связей
 *
 * @author Sergey Shishigin
 * @date 2016-08-24
 */

Ext.define('Unidata.view.admin.entity.metarecord.presentation.relation.Relation', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.entity.metarecord.presentation.relation',

    cls: 'un-presentation-relation',

    controller: 'admin.entity.metarecord.presentation.relation',

    viewModel: {
        type: 'admin.entity.metarecord.presentation.relation'
    },

    mixins: {
        nodata: 'Unidata.mixin.NoDataDisplayable'
    },

    config: {
        readOnly: false
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateReadOnly'
        }
    ],

    layout: {
        type: 'vbox',
        align: 'left'
    },

    listeners: {
        render: 'onRender'
    },

    noDataText: Unidata.i18n.t('admin.metamodel>presentation.relation.noRelationGroups')
});
