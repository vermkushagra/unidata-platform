/**
 * @author Ivan Marshalkin
 * @date 2017-02-03
 */
Ext.define('Unidata.view.admin.security.user.UserAdditionalPropertySettingsModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.useradditionalpropertysettings',

    data: {},

    stores: {
        additionalProperties: {
            autoLoad: true,
            model: 'Unidata.model.user.UserProperty'
        }
    },

    formulas: {}
});
