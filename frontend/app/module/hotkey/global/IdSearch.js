/**
 * Поиск по etalonId и не только
 *
 * @author Aleksandr Bavin
 * @date 2017-02-10
 */

Ext.define('Unidata.module.hotkey.global.IdSearch', {
    extend: 'Unidata.module.hotkey.KeyMapBase',

    requires: [
        'Unidata.view.steward.search.id.IdSearch'
    ],

    wnd: null,                     // ссылка на уже созданное окно
    hasWindowOpenWarningText: Unidata.i18n.t('other>closeAllModalsForIdSearch'),

    /**
     * Создаем кеймапу
     *
     * @returns {Ext.util.KeyMap}
     */
    createKeyMap: function () {
        var keymap;

        keymap = new Ext.util.KeyMap(document.body, {
            key: 'f',
            ctrl: true,
            shift: true,
            scope: this,
            fn: function (keycode, e) {
                e.stopEvent();

                // окно поиска должно отображаться только в пользовательском приложении или в режиме developer
                if (!Ext.Array.contains([Unidata.Config.APP_MODE.USER, Unidata.Config.APP_MODE.DEV], Unidata.Config.getAppMode())) {
                    return;
                }

                if (Ext.window.Window.hasAnyModalWindowOpen(this.wnd)) {
                    Unidata.showWarning(this.hasWindowOpenWarningText);

                    return;
                }

                if (!this.wnd || this.wnd.destroyed) {

                    this.wnd = Ext.widget({
                        xtype: 'form.window.idsearch',
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
