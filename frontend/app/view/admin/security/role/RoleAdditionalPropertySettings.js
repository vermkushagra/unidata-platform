/**
 * Экран редактирования доп свойств ролей
 *
 * @author Ivan Marshalkin
 * @date 2017-02-03
 */
Ext.define('Unidata.view.admin.security.role.RoleAdditionalPropertySettings', {
    extend: 'Ext.panel.Panel',

    required: [
        'Unidata.view.admin.security.role.RoleAdditionalPropertySettingsController',
        'Unidata.view.admin.security.role.RoleAdditionalPropertySettingsModel',

        'Unidata.view.admin.security.AdditionalPropertySettings'
    ],

    alias: 'widget.admin.security.roleadditionalpropertysettings',

    viewModel: {
        type: 'roleadditionalpropertysettings'
    },

    controller: 'roleadditionalpropertysettings',

    referenceHolder: true,

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateReadOnly'
        }
    ],

    title: Unidata.i18n.t('admin.security>additionalRolePropertySettingsEditorTabTitle'),

    layout: 'fit',

    config: {
        readOnly: false
    },

    items: [
        {
            xtype: 'admin.security.additionalpropertysettings',
            reference: 'propertyEditor',
            title: Unidata.i18n.t('admin.security>additionalRolePropertySettingsEditorTabTitle')
        }
    ]
});
