/**
 * Экран дубликатов
 *
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.Duplicates', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.duplicates',

    requires: [
        'Unidata.view.admin.duplicates.DuplicatesController',
        'Unidata.view.admin.duplicates.DuplicatesModel',

        'Unidata.view.admin.duplicates.editor.EntityDuplicateEditor'
    ],

    controller: 'admin.duplicates',
    viewModel: {
        type: 'admin.duplicates'
    },

    referenceHolder: true,

    entityTree: null,                         // компонент дерево реестров / справочников
    entityDuplicateEditor: null,              // компонент редактор правил для реестра / справочника

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.entityTree            = me.lookupReference('entityTree');
        me.entityDuplicateEditor = me.lookupReference('entityDuplicateEditor');
    },

    onDestroy: function () {
        var me = this;

        me.entityTree            = null;
        me.entityDuplicateEditor = null;

        me.callParent(arguments);
    },

    items: [
        {
            xtype: 'panel',
            reference: 'entityListPanel',
            title: Unidata.i18n.t('glossary:entitiesOrLookupEntities'),
            collapsible: true,
            collapseDirection: 'left',
            animCollapse: false,
            titleCollapse: true,
            scrollable: 'vertical',
            ui: 'un-search',
            layout: {
                type: 'fit'
            },
            width: 300,
            bodyPadding: 0,
            items: [
                {
                    xtype: 'un.entitytree',
                    reference: 'entityTree',
                    hideHeaders: true,
                    focusable: false,
                    deferEmptyText: false,
                    emptyText: Unidata.i18n.t('search>resultset.empty'),
                    catalogMode: false,
                    listeners: {
                        selectionchange: 'oneEntitySelectionChange',
                        beforeselect: 'onBeforeSelectEntity'
                    },
                    bind: {
                        store: '{resultsetStore}'
                    },
                    ui: 'dark'
                }
            ]
        },
        {
            xtype: 'admin.duplicates.entityduplicateeditor',
            reference: 'entityDuplicateEditor',
            hidden: true,
            flex: 1
        }
    ],

    isDirty: function () {
        return this.getController().isDuplicateEditorDirty();
    }
});
