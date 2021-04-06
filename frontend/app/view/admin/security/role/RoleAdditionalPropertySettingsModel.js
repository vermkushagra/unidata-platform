/**
 * @author Ivan Marshalkin
 * @date 2017-02-03
 */
Ext.define('Unidata.view.admin.security.role.RoleAdditionalPropertySettingsModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.roleadditionalpropertysettings',

    data: {},

    stores: {
        additionalProperties: {
            autoLoad: true,
            model: 'Unidata.model.user.RoleProperty'
        }
    },

    formulas: {}
});
