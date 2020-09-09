/**
 *
 * Кнопка для отображения меню на экране мержа (символ "три точки, вертикально расположенные")
 *
 * @author Sergey Shishigin
 * @date 2017-04-14
 */
Ext.define('Unidata.view.steward.dataviewer.card.MergeRecordDottedMenuButton', {
    extend: 'Unidata.view.component.button.DottedMenuButton',

    alias: 'widget.steward.cluster.merge.dottedmenubtn',

    config: {

    },

    scale: 'small',

    menu: {
        xtype: 'un.dottedmenu',
        margin: 20,
        referenceHolder: true,
        items: [
            {
                'text': Unidata.i18n.t('glossary:excludeRecord'),
                reference: 'excludeMenuItem'
            },
            {
                'text': Unidata.i18n.t('common:deleteSomething', {
                    name: Unidata.i18n.t('glossary:record').toLowerCase()
                }),
                reference: 'deleteMenuItem'
            },
            {
                'text': Unidata.i18n.t('cluster>openRecord'),
                reference: 'openMenuItem'
            }
        ]
    },

    initComponent: function () {
        this.setMenuItems({
            exclude: null,
            delete: null,
            open: null
        });
        this.callParent(arguments);
        this.initReferences();
    },

    statics: {
        MENU_ITEM_EXCLUDE: 'exclude',
        MENU_ITEM_DELETE: 'delete',
        MENU_ITEM_OPEN: 'open'
    }
});
