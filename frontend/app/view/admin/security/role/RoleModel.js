/**
 * @author unidata team
 * @date 2015
 */
Ext.define('Unidata.view.admin.security.role.RoleModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.security.role',

    data: {},

    stores: {
        roles: {
            autoLoad: true,
            model: 'Unidata.model.user.Role',
            sorters: [{
                property: 'displayName',
                direction: 'ASC'
            }]
        }
    },

    formulas: {}
});
