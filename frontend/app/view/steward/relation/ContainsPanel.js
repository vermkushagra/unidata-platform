/**
 * Контейнер со связями
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-10
 */

Ext.define('Unidata.view.steward.relation.ContainsPanel', {

    extend: 'Ext.panel.Panel',

    alias: 'widget.relation.containspanel',

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.relation.contains.Contains',
        'Unidata.view.steward.relation.RelationFactory'
    ],

    referenceHolder: true,

    config: {
        drafts: false,
        operationId: null,
        metaRecord: null,
        dataRecord: null,
        readOnly: null,
        saveAtomic: true
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    title: Unidata.i18n.t('relation>includeType'),
    cls: 'un-relation-contains-panel',
    header: false,

    onRender: function () {
        this.callParent(arguments);
    },

    updateReadOnly: function (newReadOnly) {
        var items = this.items;

        if (!items) {
            return;
        }

        items.each(function (item) {
            item.setReadOnly(newReadOnly);
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
            dataRecord: dataRecord,
            operationId: operationId,
            drafts: drafts,
            readOnly: readOnly
        };

        panels = RelationFactory.buildRelationPanels(type, metaRecord, cfg);
        this.removeAll(true);
        this.add(panels);
    }

});
