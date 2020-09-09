/**
 * Класс, реализующий редактирование аттрибута типа simple
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */

Ext.define('Unidata.view.steward.dataentity.attribute.SimpleAttribute', {

    requires: [
        'Unidata.view.steward.dataentity.ExternalAttributeResolverBase',
        'Unidata.view.steward.dataentity.attribute.BlobAttribute',
        'Unidata.view.steward.dataentity.attribute.BooleanAttribute',
        'Unidata.view.steward.dataentity.attribute.ClobAttribute',
        'Unidata.view.steward.dataentity.attribute.DateAttribute',
        'Unidata.view.steward.dataentity.attribute.IntegerAttribute',
        'Unidata.view.steward.dataentity.attribute.LookupAttribute',
        'Unidata.view.steward.dataentity.attribute.MeasurementAttribute',
        'Unidata.view.steward.dataentity.attribute.NumberAttribute',
        'Unidata.view.steward.dataentity.attribute.StringAttribute',
        'Unidata.view.steward.dataentity.attribute.TimeAttribute',
        'Unidata.view.steward.dataentity.attribute.TimestampAttribute',
        'Unidata.view.steward.dataentity.attribute.UnknownAttribute',
        'Unidata.view.steward.dataentity.attribute.WeblinkAttribute'
    ],

    cls: 'un-simple-attribute',

    constructor: function (config) {
        var metaRecord = config.metaRecord,
            dataRecord = config.dataRecord,
            metaAttribute = config.metaAttribute,
            dataAttribute = config.dataAttribute,
            simpleDataType = metaAttribute.get('simpleDataType') || metaAttribute.get('arrayDataType') ,
            WeblinkAttributeClass = Unidata.view.steward.dataentity.attribute.WeblinkAttribute,
            LookupAttributeClass = Unidata.view.steward.dataentity.attribute.LookupAttribute,
            EnumerationAttributeClass = Unidata.view.steward.dataentity.attribute.EnumerationAttribute,
            MeasurementAttributeClass = Unidata.view.steward.dataentity.attribute.MeasurementAttribute,
            UnknownAttributeClass = Unidata.view.steward.dataentity.attribute.UnknownAttribute,
            AttributeClass = UnknownAttributeClass,
            CustomerAttributeClass;

        // если простой тип то ищем класс
        if (simpleDataType) {
            CustomerAttributeClass = Unidata.view.steward.dataentity.ExternalAttributeResolverBase.resolveExternalAttributeClassName(metaRecord, dataRecord, metaAttribute, dataAttribute);

            if (CustomerAttributeClass) {
                AttributeClass = CustomerAttributeClass;
            } else {
                AttributeClass = this.findSimpleAttributeClass(simpleDataType);

                //наброски контрола measurement
                if (!(AttributeClass instanceof UnknownAttributeClass) && metaAttribute.get('valueId')) {
                    AttributeClass = MeasurementAttributeClass;
                }
            }
        } else {
            // ссылка на справочник
            if (metaAttribute.get('lookupEntityType')) {
                AttributeClass = LookupAttributeClass;
            }
            // enum
            else if (metaAttribute.get('enumDataType')) {
                AttributeClass = EnumerationAttributeClass;
            }
            // url
            else if (metaAttribute.get('linkDataType')) {
                AttributeClass = WeblinkAttributeClass;
            }
        }

        return new AttributeClass(config);
    },

    /**
     * Поиск класса атрибута для конкретного simpleDataType
     * @param simpleDataType
     * @returns {Unidata.view.steward.dataentity.attribute.AbstractAttribute}
     */
    findSimpleAttributeClass: function (simpleDataType) {
        var allClasses                = Ext.ClassManager.classes,
            AbstractAttributeClass    = Unidata.view.steward.dataentity.attribute.AbstractAttribute,
            AttributeClass            = Unidata.view.steward.dataentity.attribute.UnknownAttribute,
            classes;

        classes = Ext.Object.getValues(allClasses);

        AttributeClass = Ext.Array.findBy(classes, function (cls) {
            return cls && cls.TYPE === simpleDataType && cls.prototype instanceof AbstractAttributeClass;
        });

        if (!AttributeClass) {
            AttributeClass = Unidata.view.steward.dataentity.attribute.UnknownAttribute;
        }

        return AttributeClass;
    }

});
