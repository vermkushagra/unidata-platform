/**
 * Модель классификатора
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.model.classifier.Classifier', {
    extend: 'Ext.data.Model',

    idProperty: 'name',

    fields: [
        {
            name: 'name',
            type: 'string',
            allowBlank: false,
            unique: true
        },
        {
            name: 'displayName',
            type: 'string',
            allowBlank: false
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'children',
            persist: false
        },
        {
            name: 'codePattern',
            type: 'string'
        },
        {
            name: 'validateCodeByLevel',
            type: 'boolean',
            defaultValue: true
        },
        // см. Unidata.util.DataRecord.bindManyToOneAssociationListeners
        {
            name: 'localVersion',
            type: 'integer',
            persist: false
        },
        // см. Unidata.util.DataRecord.bindManyToOneAssociationListeners
        {
            name: 'isLocalVersionBinded',
            type: 'boolean',
            defaultValue: false,
            persist: false
        }
    ],

    // Если делаем association у которого name children, то перестают работать деревья с узлами ClassifierNode
    // TODO: SS, разобраться
    //hasMany: [
    //    {
    //        name: 'children',
    //        model: 'classifier.ClassifierNode'
    //    }
    //],

    proxy: {
        type: 'un.classifier'
    },

    validators: {
        name: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required')
            },
            {
                type: 'latinalphanumber'
            }
        ],
        displayName: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required')
            }
        ]
    }
});
