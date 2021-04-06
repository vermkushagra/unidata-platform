/**
 * Столбец treepanel, у которого иконки папок заменены на иконки font awesome
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-19
 */
Ext.define('Unidata.view.component.grid.column.TreeColumn', {

    extend: 'Ext.tree.Column',

    alias: 'widget.un.treecolumn',

    iconCls: 'un-tree-icon',

    cellTpl: [
        '<tpl for="lines">',
            '<img src="{parent.blankUrl}" class="{parent.childCls} {parent.elbowCls}-img ',
            '{parent.elbowCls}-<tpl if=".">line<tpl else>empty</tpl>" role="presentation"/>',
        '</tpl>',
        '<img src="{blankUrl}" class="{childCls} {elbowCls}-img {elbowCls}',
            '<tpl if="isLast">-end</tpl><tpl if="expandable">-plus {expanderCls}</tpl>" role="presentation"/>',
        '<tpl if="checked !== null">',
            '<input type="button" {ariaCellCheckboxAttr}',
                ' class="{childCls} {checkboxCls}<tpl if="checked"> {checkboxCls}-checked</tpl>"/>',
        '</tpl>',
        '<span role="presentation" class="fa {childCls} {baseIconCls} ',
            '<tpl if="iconCls">',
                '{iconCls}',
            '<tpl else>',
                '{baseIconCls}-<tpl if="leaf">leaf<tpl else>parent</tpl>',
            '</tpl>',
        '"></span>',
        '<tpl if="href">',
            '<a href="{href}" role="link" target="{hrefTarget}" class="{textCls} {childCls}">{value}</a>',
        '<tpl else>',
            '<span class="{textCls} {childCls}">{value}</span>',
        '</tpl>'
    ]

});
