/**
 * @author Ivan Marshalkin
 * @date 2017-02-03
 */
Ext.define('Unidata.view.admin.security.user.UserAdditionalPropertySettingsController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.useradditionalpropertysettings',

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
