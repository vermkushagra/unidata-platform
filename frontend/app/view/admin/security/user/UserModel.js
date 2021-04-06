Ext.define('Unidata.view.admin.security.user.UserModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.security.user',

    stores: {
        additionalProperties: {
            autoLoad: true,
            model: 'Unidata.model.user.UserProperty'
        },
        users: {
            autoLoad: true,
            model: 'Unidata.model.user.User',
            sorters: [
                {
                    property: 'active',
                    direction: 'DESC'
                }, {
                    property: 'login',
                    direction: 'ASC'
                }
            ]
        },
        authSources: {
            autoLoad: true,
            fields: [
                {
                    name: 'name',
                    type: 'string'
                },
                {
                    name: 'description',
                    type: 'string'
                }
            ],
            proxy: {
                type: 'ajax',
                url: Unidata.Config.getMainUrl() + 'internal/security/user/auth-sources/list',
                limitParam: '',
                startParam: '',
                pageParam: '',
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            }
        }
    }
});
