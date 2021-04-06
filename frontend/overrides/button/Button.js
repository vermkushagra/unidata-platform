/**
 * Возможность устанавливать цвет для кнопки.
 * Цвета для кнопки генерятся в виде классов, на основе scss переменной $un-button-colors
 * @see /frontend/packages/unidata/sass/var/Ext/button/Button.scss
 * @see /frontend/packages/unidata/sass/src/Ext/button/Button.scss
 *
 * inactive - признак того, что кнопка не активна
 * Предполагается что для inactive кнопки не установлен обработчик click.
 * Цвет inactive кнопки неизменный не зависимо от событий (over, focus, press, select)
 * Состояние inactive аналогично disabled за исключением того, что цвет кнопки обычный (не полупрозрачный)
 *
 * @author Aleksandr Bavin
 * @date 2017-06-05
 */
Ext.define('Unidata.overrides.button.Button', {
    override: 'Ext.button.Button',

    config: {
        color: null,    // название цвета
        inactive: false // признак того, что кнопка не активна
    },

    colorPrefix: 'un-button-color-',
    buttonInactiveCls: 'un-button-inactive',

    updateColor: function (newColor, oldColor) {
        if (oldColor) {
            this.removeCls(this.colorPrefix + oldColor);
        }

        if (newColor) {
            this.addCls(this.colorPrefix + newColor);
        }
    },

    updateInactive: function (inactive) {
        if (inactive) {
            this.addCls(this.buttonInactiveCls);
        } else {
            this.removeCls(this.buttonInactiveCls);
        }
    }
});
