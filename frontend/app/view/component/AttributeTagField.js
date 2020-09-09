/**
 * Tagfield для выбора атрибутов
 *
 * @author Sergey Shishigin
 * @dae 2017-08-17
 */
Ext.define('Unidata.view.component.AttributeTagField', {
    extend: 'Ext.form.field.Tag',

    xtype: 'un.attributetagfield',

    filterPickList: true,
    forceSelection: true,
    grow: true,
    queryMode: 'local',
    displayField: 'displayName',
    valueField: 'name',
    defaultValueText: '',
    cls: 'un-attribute-tag-field',

    listeners: {
        afterrender: function () {
            this.applyDefaultValueText();

        },
        change: function () {
            this.applyDefaultValueText();
        }
    },

    setDefaultValueText: function (text) {
        this.defaultValueText = text;
        this.applyDefaultValueText();
    },

    applyDefaultValueText: function () {
        var me = this,
            values = me.getValueRecords();

        if (!me.rendered) {
            return;
        }

        me.inputEl.set({'placeholder': values.length ? '' : this.defaultValueText});
        me.inputEl.set({'data-qtip': values.length ? '' : this.defaultValueText});
    },

    setValue: function () {
        this.callParent(arguments);
        this.applyDefaultValueText();
    },

    statics: {
        /**
         * Заполнить store атрибутами
         * @param store
         * @param metaRecord
         * @param cleanBefore Очистить перед заполнением
         */
        fillStore: function (store, metaRecord, cleanBefore) {
            var self = Unidata.view.component.AttributeTagField,
                attributes;

            cleanBefore = Ext.isBoolean(cleanBefore) ? cleanBefore : true;

            if (cleanBefore) {
                store.removeAll();
            }

            attributes = self.getAllFirstLevelSimpleAttributes(metaRecord);
            store.setRecords(attributes);
        },
        /**
         * Получить все простые атрибуты первого уровня (простые, кодовые, альт.кодовые)
         * @param metaRecord
         * @returns {Array}
         */
        getAllFirstLevelSimpleAttributes: function (metaRecord) {
            var attributes = [],
                simpleAttributes,
                aliasCodeAttributes,
                codeAttribute;

            if (metaRecord instanceof Unidata.model.entity.LookupEntity) {
                codeAttribute       = metaRecord.getCodeAttribute();
                aliasCodeAttributes = metaRecord.aliasCodeAttributes().getRange();

                if (codeAttribute) {
                    attributes = attributes.concat([codeAttribute]);
                }
                attributes = attributes.concat(aliasCodeAttributes);
            }

            simpleAttributes = metaRecord.simpleAttributes().getRange();

            attributes = attributes.concat(simpleAttributes);

            return attributes;
        }
    }
});
