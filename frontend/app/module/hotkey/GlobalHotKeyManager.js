/**
 * Менеджер глобальных hot key
 *
 * @author Ivan Marshalkin
 * @date 2017-02-10
 */

Ext.define('Unidata.module.hotkey.GlobalHotKeyManager', {

    requires: [
        'Unidata.module.hotkey.global.AdminTopSecretWindow',
        'Unidata.module.hotkey.global.IdSearch'
    ],

    singleton: true,

    autoManaged: null,                  // массив хоткеев включаемых / выключаемых платформой самостоятельно
    manualManaged: null,                // массив хоткеев включаемых / выключаемых платформой самостоятельно

    constructor: function () {
        this.callParent(arguments);

        this.autoManaged = this.createAutoManagedHotKeys();
        this.manualManaged = this.createManualManagedHotKeys();
    },

    /**
     * Возвращает массив глобальных хоткеев, которые будут активны / неактивны в зависимости от авторизации пользователя
     *
     * @returns {Array}
     */
    createAutoManagedHotKeys: function () {
        var keymaps = [];

        keymaps.push(Ext.create('Unidata.module.hotkey.global.AdminTopSecretWindow'));
        keymaps.push(Ext.create('Unidata.module.hotkey.global.IdSearch'));

        return keymaps;
    },

    /**
     * Здесь можно будет создавать именованые хоткеи которыми не участвуют в управлении в зависимости от авторизации пользователя
     * Условия активации их для каждого случая свои. Для этого необходимо будет использовать не массив а коллекшен
     *
     * @returns {Array}
     */
    createManualManagedHotKeys: function () {
        var keymaps = [];

        return keymaps;
    },

    /**
     * Активирует все глобальные хоткеи
     */
    enableAutoManagedHotKeys: function () {
        Ext.Array.each(this.autoManaged, function (keymap) {
            keymap.enableKeyMap();
        });
    },

    /**
     * Деактивирует все глобальные хоткеи
     */
    disableAutoManagedHotKeys: function () {
        Ext.Array.each(this.autoManaged, function (keymap) {
            keymap.disableKeyMap();
        });
    }
});
