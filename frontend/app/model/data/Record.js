Ext.define('Unidata.model.data.Record', {
    extend: 'Unidata.model.data.AbstractRecord',

    requires: [
        'Unidata.mixin.model.AttributeFilterable',
        'Unidata.model.data.Classifiers',
        'Unidata.model.data.AttributeDiff'
    ],

    mixins: {
        attributeFilterable: 'Unidata.mixin.model.AttributeFilterable'
    },

    fields: [
        {
            name: 'etalonId',
            type: 'string'
        },
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
        // см. Unidata.util.DataRecord.bindManyToOneAssociationListeners
        {
            name: 'isLocalVersionBinded',
            type: 'boolean',
            defaultValue: false,
            persist: false
        },
        {
            name: 'rights',
            reference: 'user.Right'
        }
    ],

    //TODO: check how to inherit associations
    hasMany: [
        {
            name: 'simpleAttributes',
            model: 'data.SimpleAttribute'
        },
        {
            name: 'complexAttributes',
            model: 'data.ComplexAttribute'
        },
        {
            name: 'dqErrors',
            model: 'data.DqError'
        },
        {
            name: 'workflowState',
            model: 'workflow.Task'
        },
        {
            name: 'classifiers',
            model: 'data.Classifiers'
        },
        {
            name: 'arrayAttributes',
            model: 'data.ArrayAttribute'
        },
        {
            name: 'codeAttributes',
            model: 'data.CodeAttribute'
        },
        {
            name: 'diffToDraft',
            model: 'data.AttributeDiff'
        }
    ],

    proxy: {
        type: 'data.recordproxy',
        url: Unidata.Config.getMainUrl() + 'internal/data/entities/',
        dateFormat: Unidata.Config.getDateTimeFormatProxy(),
        writer: {
            type: 'json',
            allDataOptions: {
                persist: true,
                associated: true
            }
        }
    },

    idProperty: 'etalonId',

    /**
     * Переопределяем метод сохранения т.к. при сохранении должна происходить некоторая магическая фильтрмация
     *
     * @param options
     * @returns {*|Object}
     */
    save: function (options) {
        var me       = this,
            options  = Ext.apply({}, options),
            success  = options.success,
            failure  = options.failure,
            scope    = options.scope || me,
            filters;

        filters = this.applyAttributesFilterCascade(this);

        options.success  = onSuccess;
        options.failure  = onFailure;

        // обработчик успешного сохранения
        function onSuccess () {
            // возвращаем обратно фильтрацию как она была
            me.revertFilters();

            // вызываем success обработчк, если он был передан
            Ext.callback(success, scope, arguments);
        }

        // обработчик неудачного сохранения
        function onFailure () {
            // возвращаем обратно фильтрацию как она была
            me.revertFilters();

            // вызываем failure обработчк, если он был передан
            Ext.callback(failure, scope, arguments);
        }

        // сохраняем и возвращаем значение (вызов переопределенного метода)
        return this.callParent([options]);
    },

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

        if (typeof nested.arrayAttributes === 'function') {
            nested.arrayAttributes().each(checkDirty);
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
        var isDirty;

        isDirty = !this.erased && this.isDirtyNested(this);

        return isDirty;
    },

    /**
     * Реализует получение данных (аналог метода getData) с честной фильтрацией атрибутов по value == null
     *
     * @returns {string|*|Object}
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
//TODO: SS rename record to etalon ?
