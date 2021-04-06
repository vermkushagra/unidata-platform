/**
 * Панель реализующая отображения связи многие-ко-многим
 *
 * @author Ivan Marshalkin
 * @date 2016-05-14
 */

Ext.define('Unidata.view.steward.relation.M2mPanel', {

    extend: 'Ext.panel.Panel',

    alias: 'widget.relation.m2mpanel',

    mixins: {
        // Поиск временно отключен
        //search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.relation.m2m.M2m',
        'Unidata.view.steward.relation.RelationFactory'
    ],

    referenceHolder: true,

    cls: 'un-relation-m2m-panel',

    config: {
        drafts: false,
        operationId: null,
        metaRecord: null,
        dataRecord: null,
        readOnly: null
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    title: Unidata.i18n.t('relation>manyToMany'),
    header: false,

    initComponent: function () {
        this.callParent(arguments);
    },

    /**
     * Установка флага "только для чтения"
     *
     * @param readOnly
     */
    updateReadOnly: function (readOnly) {
        var items = this.items;

        if (!items) {
            return;
        }

        items.each(function (item) {
            item.setReadOnly(readOnly);
        });
    },

    /**
     * Построение панелей для связей
     */
    displayRelations: function (type) {
        var RelationFactory = Unidata.view.steward.relation.RelationFactory,
            drafts     = this.getDrafts(),
            operationId = this.getOperationId(),
            metaRecord = this.getMetaRecord(),
            dataRecord = this.getDataRecord(),
            readOnly   = this.getReadOnly(),
            panels,
            cfg;

        cfg = {
            metaRecord: metaRecord,
            dataRecord: dataRecord,
            operationId: operationId,
            drafts: drafts,
            readOnly: readOnly
        };

        panels = RelationFactory.buildRelationPanels(type, cfg);
        this.removeAll(true);
        this.add(panels);
    }
});
