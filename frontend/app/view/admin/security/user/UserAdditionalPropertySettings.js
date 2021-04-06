/**
 * Экран редактирования доп свойств пользователей
 *
 * @author Ivan Marshalkin
 * @date 2017-02-03
 */
Ext.define('Unidata.view.admin.security.user.UserAdditionalPropertySettings', {
    extend: 'Ext.panel.Panel',

    required: [
        'Unidata.view.admin.security.role.RoleAdditionalPropertySettingsController',
        'Unidata.view.admin.security.role.RoleAdditionalPropertySettingsModel',

        'Unidata.view.admin.security.AdditionalPropertySettings'
    ],

    alias: 'widget.admin.security.useradditionalpropertysettings',

    viewModel: {
        type: 'useradditionalpropertysettings'
    },

    controller: 'useradditionalpropertysettings',

    referenceHolder: true,

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateReadOnly'
        }
    ],

    title: Unidata.i18n.t('admin.security>additionalUserPropertySettingsEditorTabTitle'),

    layout: 'fit',

    config: {
        readOnly: false
    },

    items: [
        {
            xtype: 'admin.security.additionalpropertysettings',
            reference: 'propertyEditor',
            title: Unidata.i18n.t('admin.security>additionalUserPropertySettingsEditorTabTitle')
        }
    ]
});
