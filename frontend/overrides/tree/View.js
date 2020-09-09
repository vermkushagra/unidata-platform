/**
 * Переопределяем кофигурацию грида, т.к. там есть баг буферизованно вывода
 *
 * @see Unidata.overrides.grid.Panel
 *
 * @author Ivan Marshalkin
 * @date 2016-06-02
 */

Ext.define('Unidata.overrides.tree.View', {
    override: 'Ext.tree.View',

    // по умолчанию не различает четные / нечетные строки см UN-3751
    stripeRows: true
});
