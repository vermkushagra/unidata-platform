/**
 * @author Aleksandr Bavin
 * @date 02.06.2016
 */
Ext.define('Unidata.view.admin.entity.metasearch.MetasearchWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Unidata.view.admin.entity.metasearch.Metasearch'
    ],

    config: {
        draftMode: false
    },

    referenceHolder: true,

    metaserch: null,                                          // ссылка на компонент отображающий результат поиска

    title: Unidata.i18n.t('admin.metamodel>modelSearch'),
    draggable: false,
    resizable: false,
    modal: true,
    layout: 'fit',
    fullSizeMargin: 100,
    monitorResize: true,

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.metaserch.setDraftMode(this.getDraftMode());
    },

    initComponentReference: function () {
        var me = this;

        me.metaserch = this.lookupReference('metaserch');
    },

    onDestroy: function () {
        var me = this;

        me.metaserch = null;

        me.callParent(arguments);
    },

    items: [
        {
            xtype: 'admin.entity.metasearch',
            reference: 'metaserch',
            draftMode: false
        }
    ]
});
