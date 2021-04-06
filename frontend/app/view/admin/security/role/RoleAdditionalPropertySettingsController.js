/**
 * @author Ivan Marshalkin
 * @date 2017-02-03
 */
Ext.define('Unidata.view.admin.security.role.RoleAdditionalPropertySettingsController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.roleadditionalpropertysettings',

    init: function () {
        this.callParent(arguments);

        this.updateChildComponentReadOnly();
    },

    updateReadOnly: function () {
        this.updateChildComponentReadOnly();
    },

    updateChildComponentReadOnly: function () {
        var view = this.getView(),
            readOnly = view.getReadOnly(),
            propertyEditor = view.lookupReference('propertyEditor');

        if (propertyEditor) {
            propertyEditor.setReadOnly(readOnly);
        }
    }
});
