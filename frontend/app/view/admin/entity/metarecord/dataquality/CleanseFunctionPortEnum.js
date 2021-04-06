/**
 * Константные перечисления для некоторых входных портов некоторых cleanse functions
 *
 * @author Sergey Shishigin
 * @date 2017-01-10
 */

Ext.define('Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionPortEnum', {
    singleton: true,

    CF_INNER_FETCH_FETCH_MODE: [
        {value: 0, name: Unidata.i18n.t('admin.metamodel>takeFirstValue')},
        {value: 1, name: Unidata.i18n.t('admin.metamodel>takeLastValue')}
    ],
    CF_INNER_FETCH_DATA_TYPE: [
        {value: 'String', name: Unidata.i18n.t('admin.metamodel>string')},
        {value: 'Integer', name: Unidata.i18n.t('admin.metamodel>integer')},
        {value: 'Number', name: Unidata.i18n.t('admin.metamodel>number')},
        {value: 'Boolean', name: Unidata.i18n.t('glossary:boolean')},
        {value: 'Date', name: Unidata.i18n.t('glossary:date')},
        {value: 'Timestamp', name: Unidata.i18n.t('glossary:dateTime')},
        {value: 'Time', name: Unidata.i18n.t('glossary:time')}
    ]
});
