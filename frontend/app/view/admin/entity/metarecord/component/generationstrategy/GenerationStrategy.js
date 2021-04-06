/**
 * Форма настройки правил генерации значений атрибута
 * @author Sergey Shishigin
 * @date 27-10-2017
 */
Ext.define('Unidata.view.admin.entity.metarecord.component.generationstrategy.GenerationStrategy', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.entity.metarecord.component.generationstrategy.GenerationStrategyController',
        'Unidata.view.admin.entity.metarecord.component.generationstrategy.GenerationStrategyModel'
    ],

    alias: 'widget.admin.entity.metarecord.component.generationstrategy',

    controller: 'admin.entity.metarecord.component.generationstrategy',
    viewModel: {
        type: 'admin.entity.metarecord.component.generationstrategy'
    },

    generationStrategyType: null,           // Комбобокс "Тип стратегии"
    generationStrategyAttributes: null,     // Текстовое поле "Список атрибутов"
    generationStrategySeparator: null,      // Текстовое поле "Разделитель"

    radioGroupNamePrefix: 'gen-ext-strategy-type-', // префикс для генерации name для radioGroup
    radioGroupName: null,                           // сгенерированное имя radioGroup

    config: {
        metaRecord: null,
        strategyOwner: null,
        readOnly: false
    },

    methodMapper: [
        {
            method: 'initGenerationStrategy'
        },
        {
            method: 'clearGenerationStrategy'
        },
        {
            method: 'fillGenerationStrategyType'
        },
        {
            method: 'fillDisplayAttributeStore'
        },
        {
            method: 'validateAllFields'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'getGenerationStrategy'
        },
        {
            method: 'getType'
        },
        {
            method: 'getAttributes'
        },
        {
            method: 'getAvailableAttributes'
        },
        {
            method: 'calcAttributesDiff'
        }
    ],

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    initItems: function () {
        var GenerationStrategy = Unidata.model.entity.GenerationStrategy,
            items,
            generateStrategyRadioGroupName;

        this.callParent(arguments);
        // генерируем уникальное имя radio group
        // необходимость вызвана багом ExtJS: если имеется несколько radioGroup с одинаковым name у элементов, то установка значений в одной из radioGroup
        // приводит к сбросу значений других
        generateStrategyRadioGroupName = this.getGenerateStrategyRadioGroupName();

        items = [{
            xtype: 'radiogroup',
            columns: 1,
            vertical: true,
            reference: 'generationStrategyType',
            listeners: {
                change: 'onGenerationStrategyTypeChange'
            },
            bind: {
                readOnly: '{readOnly}'
            },
            defaults: {
                listeners: {
                    render: function (cmp) {
                        if (!cmp.qtip) {
                            return;
                        }

                        Ext.QuickTips.register({
                            target: cmp.getEl(),
                            text: cmp.qtip
                        });
                    }
                }
            },
            items: [
                {
                    boxLabel: GenerationStrategy.generationStrategyType.NONE.displayName,
                    inputValue: GenerationStrategy.generationStrategyType.NONE.value,
                    name: generateStrategyRadioGroupName,
                    qtip: Unidata.i18n.t('admin.metamodel>generationStrategy.typeNone')
                },
                {
                    boxLabel: GenerationStrategy.generationStrategyType.RANDOM.displayName,
                    inputValue: GenerationStrategy.generationStrategyType.RANDOM.value,
                    name: generateStrategyRadioGroupName,
                    qtip: Unidata.i18n.t('admin.metamodel>generationStrategy.typeRandom')
                },
                // Временно закомментировано, т.к. не используется
                //{
                //    boxLabel: this.self.generationStrategyType.SEQUENCE.displayName,
                //    inputValue: this.self.generationStrategyType.SEQUENCE.value,
                //    name: generateStrategyRadioGroupName,
                //    qtip: Unidata.i18n.t('admin.metamodel>generationStrategy.typeSequence')
                //},
                {
                    boxLabel: GenerationStrategy.generationStrategyType.CONCAT.displayName,
                    inputValue: GenerationStrategy.generationStrategyType.CONCAT.value,
                    name: generateStrategyRadioGroupName,
                    qtip: Unidata.i18n.t('admin.metamodel>generationStrategy.typeConcat')
                }
            ]
        },
            {
                xtype: 'un.attributetagfield',
                reference: 'generationStrategyAttributes',
                fieldLabel: Unidata.i18n.t('admin.metamodel>showAttributes'),
                modelValidation: true,
                validateOnBlur: false,
                validateOnChange: false,
                msgTarget: 'under',
                labelWidth: 180,
                bind: {
                    disabled: '{!isGenerationStrategyTypeConcat}',
                    value: '{generationStrategy.attributes}',
                    readOnly: '{readOnly}'
                },
                store: {
                    model: 'Unidata.model.attribute.AbstractAttribute',
                    sorters: [{
                        property: 'displayName',
                        direction: 'ASC'
                    }]
                },
                triggers: {
                    clear: {
                        cls: 'x-form-clear-trigger',
                        handler: function () {
                            this.clearValue();
                        }
                    }
                },
                listeners: {
                    change: 'onGenerationStrategyAttributesChange',
                    blur: 'onGenerationStrategyAttributesBlur'
                }
            },
            {
                xtype: 'textfield',
                fieldLabel: Unidata.i18n.t('admin.metamodel>generationStrategy.separator'),
                labelWidth: 180,
                modelValidation: true,
                validateOnBlur: false,
                validateOnChange: false,
                msgTarget: 'under',
                bind: {
                    disabled: '{!isGenerationStrategyTypeConcat}',
                    value: '{generationStrategy.separator}',
                    readOnly: '{readOnly}'
                },
                reference: 'generationStrategySeparator',
                listeners: {
                    change: 'onGenerationStrategySeparatorChange',
                    blur: 'onGenerationStrategySeparatorBlur'
                }
            }
        ];

        this.add(items);
    },

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
    },

    initReferences: function () {
        this.generationStrategyType = this.lookupReference('generationStrategyType');
        this.generationStrategyAttributes = this.lookupReference('generationStrategyAttributes');
        this.generationStrategySeparator = this.lookupReference('generationStrategySeparator');
    },

    getGenerateStrategyRadioGroupName: function () {
        var prefix = this.radioGroupNamePrefix;

        if (this.radioGroupName) {
            return this.radioGroupName;
        }

        this.radioGroupName = prefix + this.getId();

        return this.radioGroupName;
    }
});
