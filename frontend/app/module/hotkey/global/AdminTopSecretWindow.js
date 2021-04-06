/**
 * Глобальный хоткей для top secret окна администратора
 *
 * @author Ivan Marshalkin
 * @date 2017-02-10
 */

Ext.define('Unidata.module.hotkey.global.AdminTopSecretWindow', {
    extend: 'Unidata.module.hotkey.KeyMapBase',

    requires: [
        'Unidata.view.admin.topsecret.SystemToolsWindow'
    ],

    constructor: function () {
        this.callParent(arguments);
    },

    wnd: null,                     // ссылка на уже созданное окно

    /**
     * Создаем кеймапу
     *
     * @returns {Ext.util.KeyMap}
     */
    createKeyMap: function () {
        var keymap;

        keymap = new Ext.util.KeyMap(document.body, {
            key: 'a',
            ctrl: true,
            shift: true,
            scope: this,
            fn: function (keycode, e) {
                e.stopEvent();

                // только для администратора
                if (!Unidata.Config.isUserAdmin()) {
                    return;
                }

                // окно поиска должно отображаться только в административном приложении или в режиме developer
                if (!Ext.Array.contains([Unidata.Config.APP_MODE.ADMIN, Unidata.Config.APP_MODE.DEV], Unidata.Config.getAppMode())) {
                    return;
                }

                if (!this.wnd || this.wnd.destroyed) {
                    this.wnd = Ext.create('Unidata.view.admin.topsecret.SystemToolsWindow', {
                        modal: true,
                        width: 400,
                        height: 400,
                        autoDestroy: false,
                        closeAction: 'hide'
                    });
                }

                this.wnd.show();
                this.wnd.center();
            }
        });

        keymap.disable();

        return keymap;
    }
});
