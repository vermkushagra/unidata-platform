/**
 * Модель меню для управления каталогом реестров/справочников
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-15
 */
Ext.define('Unidata.view.admin.entity.catalog.menu.MenuModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.catalog.menu',

    data: {
        editAllowed:   false,
        deleteAllowed: false,
        createAllowed: false
    }
});
