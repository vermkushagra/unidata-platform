/**
 * Окно редактирования набора записей
 *
 * @author Sergey Shishigin
 * @date 2016-02-02
 */

Ext.define('Unidata.view.component.search.query.etaloncluster.EtalonClusterWindow', {
    extend: 'Ext.window.Window',

    alias: 'widget.component.search.query.etaloncluster.etalonclusterwindow',

    requires: [
        'Unidata.view.component.search.query.etaloncluster.EtalonClusterPanel'
    ],

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    modal: true,
    width: 500,
    height: 700,

    items: [
        {
            xtype: 'component.search.query.etaloncluster.etalonclusterpanel'
        }
    ]
});
