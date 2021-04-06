/**
 * Переопределяем кофигурацию грида, т.к. там есть баг буферизованно вывода
 *
 * @see Unidata.overrides.grid.Panel
 *
 * @author Ivan Marshalkin
 * @date 2016-06-02
 */
Ext.define('Unidata.overrides.tree.Panel', {
    override: 'Ext.tree.Panel',

    config: {
        bufferedRenderer: false
    },

    // глючит анимация. приводит к неработоспособному интерфейсу
    animate: false
});
