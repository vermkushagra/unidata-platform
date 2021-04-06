/**
 * Модель статистики по сущностям (спр, реестр), привязанным к классификаторам
 *
 * @author Sergey Shishigin
 * @date 2016-10-07
 */
Ext.define('Unidata.model.classifier.ClassifierEntityStat', {
    extend: 'Ext.data.Model',

    fields: [
        {
            name: 'name',
            type: 'string',
            allowBlank: false,
            unique: true
        },
        {
            name: 'type',
            type: 'string',
            allowBlank: false
        },
        {
            name: 'count',
            type: 'integer'
        }
    ]

});
