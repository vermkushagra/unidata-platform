Ext.define('Unidata.view.main.Main', {
    extend: 'Ext.container.Viewport',

    alias: 'widget.main',

    requires: [
        'Unidata.view.steward.search.id.IdSearch',
        'Unidata.view.main.MainController',
        'Unidata.view.main.MainModel'
    ],

    controller: 'main',

    viewModel: {
        type: 'main'
    },

    referenceHolder: true,

    menuPanel: null,                    // ссылка на панель-меню

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    plugins: 'responsive',

    initComponent: function () {
        this.callParent(arguments);
        Unidata.module.MainViewManager.setMainView(this);
    },

    initComponentReference: function () {
        var me = this;

        me.menuPanel = me.lookupReference('menuPanel');
    },

    onDestroy: function () {
        var me = this;

        me.menuPanel = null;

        this.callParent(arguments);
    },

    initItems: function () {
        this.callParent(arguments);

        this.add([
            {
                xtype: 'mainmenu',
                reference: 'mainMenu',
                listeners: {
                    itemselected: 'onMenuItemSelected'
                }
            },
            {
                reference: 'main-layout',
                flex:      1,
                xtype:     'panel',
                region:    'center',
                layout:    {
                    type:  'vbox',
                    pack:  'start',
                    align: 'stretch'
                },
                cls: 'unidata-main-container',
                items: [
                    {
                        flex:      1,
                        xtype:     'container',
                        layout:    'fit',
                        reference: 'mainContainer',
                        items:     []
                    }
                ]
            }
        ]);

        this.initComponentReference();
    }
});
