/**
 * Кнопка активации правила качества
 *
 * @date 22-03-2018
 * @author Sergey Shishigin
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.ToggleButton', {
    extend: 'Ext.button.Button',

    xtype: 'un.togglebutton',

    iconCls: '',
    text: '',
    pressed: true,
    enableToggle: true,
    focusable: false,
    ui: 'un-no-border-button',
    textAlign: 'left',
    scale: 'medium',
    width: 175,

    initComponent: function () {
        this.callParent(arguments);
        this.displayPressedState(this.pressed);
    },

    /**
     * Отобразить состояние кнопки, соответствующее состоянию нажатия
     * @param pressed
     */
    displayPressedState: function (pressed) {
        if (pressed) {
            this.setIconCls('icon-toggle-on');
            this.setText(Unidata.i18n.t('admin.dq>dqRuleActive'));
        } else {
            this.setIconCls('icon-toggle-off');
            this.setText(Unidata.i18n.t('admin.dq>dqRuleInactive'));
        }
    },

    toggleHandler: function (self, pressed) {
        this.displayPressedState(pressed);
    }
});
