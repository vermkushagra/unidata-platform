/**
 * Контроллер меню для управления каталогом реестров/справочников
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-15
 */
Ext.define('Unidata.view.admin.entity.catalog.menu.MenuController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.catalog.menu',

    onClickAdd: function () {
        this.fireViewEvent('clickAdd', this.record);
    },

    onClickEdit: function () {
        this.fireViewEvent('clickEdit', this.record, 1);
    },

    onClickRemove: function () {
        this.fireViewEvent('clickRemove', this.record);
    },

    setRecord: function (record) {
        this.record = record;
    }

});
