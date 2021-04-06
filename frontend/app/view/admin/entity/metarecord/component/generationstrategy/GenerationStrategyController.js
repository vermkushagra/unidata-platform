Ext.define('Unidata.view.admin.entity.metarecord.component.generationstrategy.GenerationStrategyController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord.component.generationstrategy',

    initGenerationStrategy: function (metaRecord, strategyOwner, attributeFilters) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            generationStrategy;

        view.setMetaRecord(metaRecord);
        view.setStrategyOwner(strategyOwner);
        generationStrategy = this.buildGenerationStrategy();
        viewModel.set('generationStrategy', generationStrategy);
        this.fillGenerationStrategyType();
        this.fillDisplayAttributeStore(attributeFilters);
    },

    /**
     * Заполнить store объединяемых атрибутов
     */
    fillDisplayAttributeStore: function (attributeFilters) {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            generationStrategyAttributesCombo = view.lookupReference('generationStrategyAttributes'),
            AttributeTagField = Unidata.view.component.AttributeTagField,
            store,
            attributes = null,
            externalIdGenerationStrategy;

        externalIdGenerationStrategy = this.buildGenerationStrategy();

        if (externalIdGenerationStrategy) {
            attributes = externalIdGenerationStrategy.get('attributes');
        }

        store = generationStrategyAttributesCombo.getStore();

        if (attributeFilters) {
            store.setFilters(attributeFilters);
        }
        AttributeTagField.fillStore(store, metaRecord);
        generationStrategyAttributesCombo.setValue(attributes);
    },

    /**
     * Заполнить объект externalIdGenerationStrategy в составе объекта strategyOwner
     */
    fillGenerationStrategyType: function () {
        var GenerationStrategy = Unidata.model.entity.GenerationStrategy,
            view = this.getView(),
            generationStrategy = this.buildGenerationStrategy(),
            type = generationStrategy.get('@type'),
            generateStrategyRadioGroupName = view.getGenerateStrategyRadioGroupName(),
            value;

        type = type || GenerationStrategy.generationStrategyType.NONE.value;
        value = {};
        value[generateStrategyRadioGroupName] = type;

        view.generationStrategyType.setValue(value);
    },

    /**
     * Обработчик события изменения типа
     * @param component
     * @param value
     */
    onGenerationStrategyTypeChange: function (component, value) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            externalIdGenerationStrategy = this.buildGenerationStrategy(),
            generateStrategyRadioGroupName = view.getGenerateStrategyRadioGroupName(),
            generationStrategyType = value[generateStrategyRadioGroupName];

        viewModel.set('generationStrategyType', generationStrategyType);
        externalIdGenerationStrategy.set('@type', generationStrategyType);
    },

    /**
     * Получить объект ExternalIdGenerationStrategy у strategyOwner (или создать новый)
     * @param strategyOwner
     * @returns {*}
     */
    buildGenerationStrategy: function () {
        var view = this.getView(),
            externalIdGenerationStrategy,
            strategyOwner = view.getStrategyOwner();

        if (!strategyOwner) {
            return null;
        }

        externalIdGenerationStrategy = this.getGenerationStrategy();

        if (!externalIdGenerationStrategy) {
            externalIdGenerationStrategy = Ext.create('Unidata.model.entity.GenerationStrategy');
            strategyOwner.setExternalIdGenerationStrategy(externalIdGenerationStrategy);
        }

        return externalIdGenerationStrategy;
    },

    getGenerationStrategy: function () {
        var view          = this.getView(),
            strategyOwner = view.getStrategyOwner(),
            idGenerationStrategy;

        if (!strategyOwner) {
            return null;
        }

        idGenerationStrategy = strategyOwner.getExternalIdGenerationStrategy();

        return idGenerationStrategy;
    },

    validateAllFields: function () {
        var me = this,
            fields = ['attributes', 'separator'],
            isValid;

        isValid = Ext.Array.reduce(fields, function (previous, field) {
            var isValid;

            isValid = me.validateField(field) && previous;

            return isValid;
        }, this);

        return isValid;
    },

    validateField: function (name) {
        var reference,
            field,
            validationField,
            generationStrategy = this.buildGenerationStrategy();

        reference = this.buildFieldReference(name);
        field = this.lookupReference(reference);

        if (!field || !generationStrategy) {
            return true;
        }

        validationField = field.getValidationField();

        if (!validationField) {
            return true;
        }

        validationField.addPropertiesToValidators({
            record: generationStrategy
        });

        return field.validate();
    },

    buildFieldReference: function (name) {
        var prefix = 'generationStrategy';

        return prefix + Ext.String.capitalize(name);
    },

    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel();

        viewModel.set('readOnly', readOnly);
    },

    clearGenerationStrategy: function () {
        var GenerationStrategy = Unidata.model.entity.GenerationStrategy,
            view = this.getView(),
            type,
            generateStrategyRadioGroupName = view.getGenerateStrategyRadioGroupName(),
            value;

        type = GenerationStrategy.generationStrategyType.NONE.value;
        value = {};
        value[generateStrategyRadioGroupName] = type;

        view.generationStrategyType.setValue(value);
        view.generationStrategyAttributes.setValue(null);
        view.generationStrategySeparator.setValue(null);
    },

    onGenerationStrategyAttributesChange: function () {
        this.validateField('attributes');
    },

    onGenerationStrategySeparatorChange: function () {
        this.validateField('separator');
    },

    onGenerationStrategyAttributesBlur: function () {
        this.validateField('attributes');
    },

    onGenerationStrategySeparatorBlur: function () {
        this.validateField('separator');
    },

    /**
     * Получить массив имен выбранных атрибутов для стратегии "Объединение"
     * @returns {String[]}
     */
    getAttributes: function () {
        var view = this.getView(),
            generationStrategyAttributes = view.generationStrategyAttributes,
            attributes = generationStrategyAttributes.getValue();

        if (Ext.isArray(attributes)) {
            attributes = Ext.Array.map(attributes, function (attribute) {
                if (attribute instanceof Unidata.model.attribute.AbstractSimpleAttribute) {
                    return attribute.get('name');
                } else {
                    return attribute;
                }
            });
        }

        return attributes;
    },

    /**
     * Получить тип стратегии
     * @returns {*}
     */
    getType: function () {
        var view                   = this.getView(),
            generationStrategyType = view.generationStrategyType,
            value                  = generationStrategyType.getValue(),
            radioGroupName         = view.getGenerateStrategyRadioGroupName();

        return value[radioGroupName];
    },

    /**
     * Получить атрибуты из первого уровня метамодели
     * @returns {*}
     */
    getAvailableAttributes: function () {
        var self       = Unidata.view.component.AttributeTagField,
            view       = this.getView(),
            metaRecord = view.getMetaRecord(),
            availableAttributes;

        availableAttributes = self.getAllFirstLevelSimpleAttributes(metaRecord);
        availableAttributes = Ext.Array.map(availableAttributes, function (attribute) {
            return attribute.get('name');
        });

        return availableAttributes;
    },

    /**
     * Вычислить разницу между выбранными атрибутами и атрибутами существующими в метамодели
     *
     * Для определения того имееются ли выбранные атрибуты в метамодели
     * @returns {Array|*}
     */
    calcAttributesDiff: function () {
        var attributes = this.getAttributes(),
            availableAttributes = this.getAvailableAttributes(),
            diff;

        attributes = attributes || [];
        availableAttributes = availableAttributes || [];

        diff = Ext.Array.difference(attributes, availableAttributes);

        return diff;
    }
});
