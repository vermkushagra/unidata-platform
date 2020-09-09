/**
 * Лейаут просто содержащий в себе контейнеры без их модификации
 *
 * @author Ivan Marshalkin
 * @date 2016-05-25
 */

Ext.define('Unidata.view.steward.dataentity.layout.NoneLayout', {
    extend: 'Unidata.view.steward.dataentity.layout.AbstractLayout',

    tablets: null,

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    initComponent: function () {
        var me = this;

        this.callParent(arguments);

        me.add(this.tablets);
    }
});
