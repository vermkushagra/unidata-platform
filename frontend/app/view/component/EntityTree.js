/**
 * Дерево реестров/справочников
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-19
 */
Ext.define('Unidata.view.component.EntityTree', {

    extend: 'Ext.tree.Panel',

    alias: 'widget.un.entitytree',

    requires: [
        'Unidata.view.component.grid.column.TreeColumn'
    ],

    config: {
        rootVisible: false,
        hideHeaders: true,
        catalogMode: false
    },

    constructor: function () {
        this.callParent(arguments);

        Ext.tip.QuickTipManager.init();
    },

    columns: [
        {
            xtype: 'un.treecolumn',
            dataIndex: 'displayName',
            flex: 1,
            hideable: false,
            sortable: false,
            renderer: function (value) {
                return '<span data-qtip="' + Ext.String.htmlEncodeMulti(value, 2) + '">' + Ext.String.htmlEncodeMulti(value, 1) + '</span>';
            }
        }
    ]
});
