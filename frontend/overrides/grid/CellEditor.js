/**
 * @author Aleksandr Bavin
 * @date 2018-02-02
 */
Ext.define('Unidata.overrides.grid.CellEditor', {

    override: 'Ext.grid.CellEditor',

    // выравнивание по левому краю, без попытки вписать в контейнер (было "l-l?")
    alignment: 'l-l'

});
