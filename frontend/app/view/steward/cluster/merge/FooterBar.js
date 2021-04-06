/**
 *
 * Нижняя панель экрана записи (подвал)
 *
 * @author Sergey Shishigin
 * @date 2017-04-17
 */

Ext.define('Unidata.view.steward.cluster.merge.FooterBar', {
    extend: 'Unidata.view.component.toolbar.Toolbar',

    alias: 'widget.steward.cluster.merge.footer',

    cls: 'x-docked-bottom',

    layout: {
        type: 'hbox',
        align: 'middle'
    },

    config: {
        buttons: null
    },

    referenceHolder: true,

    autoHide: true,

    items: [
        {
            xtype: 'button',
            scale: 'large',
            color: 'transparent',
            text: Unidata.i18n.t('common:cancel'),
            reference: 'cancelButton'
        },
        {
            xtype: 'container',
            flex: 1
        },
        {
            xtype: 'button',
            scale: 'large',
            text: Unidata.i18n.t('common:merge'),
            reference: 'mergeButton'
        }
    ],

    initComponent: function () {
        var FooterBar = Unidata.view.steward.cluster.merge.FooterBar;

        this.callParent(arguments);

        this.setButtons({
            cancel: this.lookupReference(FooterBar.CANCEL_BUTTON + FooterBar.BUTTON_REFERENCE_POSTFIX),
            merge: this.lookupReference(FooterBar.MERGE_BUTTON + FooterBar.BUTTON_REFERENCE_POSTFIX)
        });
    },

    getButton: function (name) {
        var buttons = this.getButtons();

        return buttons[name];
    },

    statics: {
        BUTTON_REFERENCE_POSTFIX: 'Button',
        CANCEL_BUTTON: 'cancel',
        MERGE_BUTTON: 'merge'
    }
});
