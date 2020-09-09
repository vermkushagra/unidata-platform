/**
 * Свитчер в фасетах
 *
 * @date 22-03-2018
 * @author Sergey Shishigin
 */

Ext.define('Unidata.view.component.search.query.facet.FacetToggleButton', {
    extend: 'Ext.button.Button',

    xtype: 'un.query.facettogglebutton',

    iconCls: '',
    text: '',
    pressed: true,
    enableToggle: true,
    focusable: false,
    ui: 'un-query-toggle-button',
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
        } else {
            this.setIconCls('icon-toggle-off');
        }
    },

    toggleHandler: function (self, pressed) {
        this.displayPressedState(pressed);
    }
});
