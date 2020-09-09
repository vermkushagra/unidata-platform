/**
 * Плагин для форм филдов, реализующий взаимодействие с роутингом
 * @author Aleksandr Bavin
 * @date 2017-07-10
 */
Ext.define('Unidata.plugin.form.field.FieldRouter', {
    extend: 'Ext.plugin.Abstract',
    alias: 'plugin.form.field.router',

    routerTokenName: null,
    routerValueName: null,

    init: function (component) {
        this.setCmp(component);
    }

});
