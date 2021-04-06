/**
 * Переопределяем кофигурацию грида, т.к. там есть баг буферизованно вывода
 *
 * @see https://www.sencha.com/forum/showthread.php?302034-Buffered-Rendering-Scroll-Whitespace-issue
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-31
 */
Ext.define('Unidata.overrides.grid.Panel', {
    override: 'Ext.grid.Panel',

    requires: [
        'Unidata.plugin.grid.HiddenSortableMenuItem'
    ],

    config: {
        bufferedRenderer: false
    },

    initComponent: function () {
        var plugin;

        this.callParent(arguments);

        // подключаем плагин скрывающий пункты меню сортировки, если они недоступны
        plugin = Ext.create('Unidata.plugin.grid.HiddenSortableMenuItem');
        this.addPlugin(plugin);
    }
});
