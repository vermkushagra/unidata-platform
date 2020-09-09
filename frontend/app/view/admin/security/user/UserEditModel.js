Ext.define('Unidata.view.admin.security.user.UserEditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.security.useredit',

    data: {
        readOnly: true,
        activeReadOnly: false,
        securityLabelsPanelIsVisible: true,
        propertiesIsVisible: true
    },

    stores: {
        securityLabels: {
            model: 'Unidata.model.user.SecurityLabelRole',
            autoLoad: true,
            proxy: {
                type: 'rest',
                url: Unidata.Config.getMainUrl() + 'internal/security/role/get-all-security-labels',
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            }
        }
    }

});
