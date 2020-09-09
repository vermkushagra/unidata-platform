/**
 * Базовый класс для глобальных hot key
 *
 * @author Ivan Marshalkin
 * @date 2017-02-10
 */

Ext.define('Unidata.module.hotkey.KeyMapBase', {

    constructor: function () {
        this.callParent(arguments);

        this.keymap = this.createKeyMap();
    },

    createKeyMap: Ext.emptyFn,

    enableKeyMap: function () {
        this.keymap.enable();
    },

    disableKeyMap: function () {
        this.keymap.disable();
    }
});
