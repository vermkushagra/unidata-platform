Ext.define('Unidata.model.entity.EntityDependency', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'sourceType',
            type: 'string'
        },
        {
            name: 'targetType',
            type: 'string'
        },
        {
            name: 'sourceKey',
            type: 'auto'
        },
        {
            name: 'targetKey',
            type: 'auto'
        }
    ],

    validators: {
        sourceType: [
            {
                type: 'inclusion',
                list: Unidata.util.EntityDependency.getTypesList(),
                message: Unidata.i18n.t('model>invalidSourceType')
            }
        ],
        targetType: [
            {
                type: 'inclusion',
                list: Unidata.util.EntityDependency.getTypesList(),
                message: Unidata.i18n.t('model>invalidTargetType')
            }
        ]
    }
});
