Ext.define('Unidata.model.data.RelationReference', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.mixin.model.AttributeFilterable'
    ],

    mixins: {
        attributeFilterable: 'Unidata.mixin.model.AttributeFilterable'
    },

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        // Локальное свойство, которое позволяет отслеживать изменения в модели
        // на самых глубинных уровнях. Для срабатывания биндинга необходимо вручную
        // инкрементировать свойство
        // Для этой цели можно использовать helper Unidata.util.DataRecord.bindManyToOneAssociationListeners
        // Вызывать данный метод нужно сразу после создания dataRecord. В этом случае на associations stores
        // будут повешены обработчики событий datachanged, update. При возникновении этих событий значение localVersion
        // инкрементируется
        {
            name: 'localVersion',
            type: 'integer',
            persist: false
        },
        {
            name: 'validFrom',
            type: 'datetimeintervalfrom',
            allowNull: true
        },
        {
            name: 'validTo',
            type: 'datetimeintervalto',
            allowNull: true
        },
        {
            name: 'etalonId',
            type: 'string'
        },
        {
            name: 'etalonIdTo',
            type: 'string'
        },
        {
            name: 'etalonDisplayNameTo',
            type: 'string'
        },
        {
            name: 'relName',
            type: 'string'
        }
    ],

    hasMany: [
        {
            name: 'simpleAttributes',
            model: 'data.SimpleAttribute'
        },
        // complexAttributes и dqErrors в принципе сейчас не нужны, но в данных с сервера приходят
        // и при отрисовке связей в dataentityviewer нужно проходить по этим полям + Миша сказал, что эти поля могут
        // появиться в будущем, потому я посчитал разумным их тут описать, чтобы не городить костыли в dataentity
        {
            name: 'complexAttributes',
            model: 'data.ComplexAttribute'
        },
        {
            name: 'dqErrors',
            model: 'data.DqError',
            persist: true
        }
    ],

    proxy: {
        type: 'data.relationproxy',

        url: Unidata.Config.getMainUrl() + 'internal/data/relations/relation',
        reader: {
            rootProperty: 'content'
        }
    },

    /**
     * Взято из
     * @see Unidata.model.data.Record.isDirtyNested
     * @param nested
     * @returns {boolean}
     */
    isDirtyNested: function (nested) {
        var me    = this,
            dirty = false;

        if (nested.dirty) {
            dirty = true;
        }

        function checkDirty (attribute) {
            if (attribute.dirty) {
                dirty = true;
            }
        }

        if (typeof nested.classifiers === 'function') {
            if (nested.classifiers().dirty) {
                dirty = true;
            }

            nested.classifiers().each(function (classifierItem) {
                if (classifierItem.dirty) {
                    dirty = true;
                }

                if (typeof classifierItem.simpleAttributes === 'function') {
                    classifierItem.simpleAttributes().each(checkDirty);
                }
            });
        }

        if (typeof nested.simpleAttributes === 'function') {
            nested.simpleAttributes().each(checkDirty);
        }

        if (typeof nested.codeAttributes === 'function') {
            nested.codeAttributes().each(checkDirty);
        }

        if (typeof nested.complexAttributes === 'function') {
            nested.complexAttributes().each(function (complexAttribute) {
                var nested = complexAttribute.nestedRecords();

                if (nested.dirty) {
                    dirty = true;
                }

                nested.each(function (nestedRecord) {
                    if (me.isDirtyNested(nestedRecord)) {
                        dirty = true;
                    }
                });
            });
        }

        return dirty;
    },

    checkDirty: function () {
        return this.isDirtyNested(this);
    },

    /**
     *
     * @param options Options for getData method
     * @return {*}
     */
    getFilteredData: function () {
        var filters,
            data;

        filters = this.applyAttributesFilterCascade(this);

        data = this.getData.apply(this, arguments);

        this.revertFilters(filters);

        return data;
    }
});
