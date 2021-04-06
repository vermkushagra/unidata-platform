/**
 *
 * Нижняя панель экрана записи (подвал)
 *
 * @author Sergey Shishigin
 * @date 2017-04-17
 */

Ext.define('Unidata.view.steward.dataviewer.footer.FooterBar', {
    extend: 'Unidata.view.component.toolbar.Toolbar',

    alias: 'widget.steward.dataviewer.footer',

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
            text: Unidata.i18n.t('common:delete'),
            reference: 'deleteButton'
        },
        {
            xtype: 'container',
            flex: 1
        },
        {
            xtype: 'button',
            scale: 'large',
            text: Unidata.i18n.t('common:save'),
            reference: 'saveButton'
        },
        {
            xtype: 'button',
            scale: 'large',
            text: Unidata.i18n.t('glossary:restore'),
            hidden: true,
            reference: 'restoreButton'
        }
    ],

    initComponent: function () {
        var FooterBar = Unidata.view.steward.dataviewer.footer.FooterBar;

        this.callParent(arguments);

        this.setButtons({
            delete: this.lookupReference(FooterBar.DELETE_BUTTON + FooterBar.BUTTON_REFERENCE_POSTFIX),
            save: this.lookupReference(FooterBar.SAVE_BUTTON + FooterBar.BUTTON_REFERENCE_POSTFIX),
            restore: this.lookupReference(FooterBar.RESTORE_BUTTON + FooterBar.BUTTON_REFERENCE_POSTFIX)
        });
    },

    getButton: function (name) {
        var buttons = this.getButtons();

        return buttons[name];
    },

    statics: {
        BUTTON_REFERENCE_POSTFIX: 'Button',
        DELETE_BUTTON: 'delete',
        SAVE_BUTTON: 'save',
        RESTORE_BUTTON: 'restore'
    }
});
