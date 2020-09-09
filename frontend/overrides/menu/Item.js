/**
 * В некоторых браузерах по клику на пункт выпадающего меню проиходит изменение hash url
 *
 * см подробности
 *
 * https://www.sencha.com/forum/showthread.php?291955
 * https://www.sencha.com/forum/showthread.php?294605-Ext-JS-5.0.1255-Ext.menu.Menu-not-working-in-IE-10
 * https://www.sencha.com/forum/showthread.php?291955
 *
 * судя по changelog проблема должна быть исправлена но она всеравно проявляется изредко и не у всех
 *
 * @author Ivan Marshalkin
 * @date 2017-07-26
 */

Ext.define('Ext.overrides.menu.Item', {
    override: 'Ext.menu.Item',

    onRender: function () {
        this.callParent(arguments);

        if (!this.itemEl) {
            return;
        }

        /**
         * Другой метод решения: переопределить renderTpl заменив href="{href}" -> <tpl if="hasHref">href="{href}"</tpl>
         * но от него отказались по причине необходимости копипастить большой кусок
         */
        this.itemEl.dom.addEventListener('click', function (e) {
            if (!this.renderData.hasHref) {
                e.preventDefault();
            }
        }.bind(this));
    }
});
