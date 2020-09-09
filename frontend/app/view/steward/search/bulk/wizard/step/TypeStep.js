/**
 * @author Aleksandr Bavin
 * @date 20.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.step.TypeStep', {
    extend: 'Unidata.view.steward.search.bulk.wizard.WizardStep',

    requires: [
        'Unidata.view.steward.search.bulk.wizard.step.TypeStepController'
    ],

    alias: 'widget.steward.search.bulk.wizard.step.type',

    controller: 'type',

    referenceHolder: true,

    typesStore: null,
    allTypes: {}, // все типы операций key = тип, value = description

    items: [
        {
            xtype: 'radiogroup',
            reference: 'radiogroup',
            columns: 1,
            items: [],
            listeners: {
                change: 'onRadiogroupChange'
            }
        }
    ],

    initItems: function () {
        this.callParent(arguments);

        this.setLoading(true);

        this.initRadiogroup();
    },

    initButtonItems: function () {
        this.lookupReference('buttonItems').add(
            {
                xtype: 'container',
                flex: 1
            },
            {
                text: Unidata.i18n.t('search>wizard.next'),
                xtype: 'button',
                bind: {
                    disabled: '{!nextStepAllowed}'
                },
                listeners: {
                    click: 'onNextClick'
                }
            }
        );
    },

    /**
     * Инициализация элементов радиогруппы
     */
    initRadiogroup: function () {

        this.typesStore = Ext.create('Ext.data.Store', {
            autoLoad: true,
            fields: ['type', 'description'],
            proxy: {
                type: 'ajax',
                url: Unidata.Config.getMainUrl() + 'internal/data/bulk',
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            }
        });

        this.typesStore.on('load', function (store, records) {
            this.lookupReference('radiogroup').removeAll();
            Ext.Array.each(records, this.initRadiogroupItem, this);
            store.on('filterchange', this.onFilterchange, this);
            this.filterTypesStore();
        }, this);

        this.getWizard().on('metarecordchange', this.filterTypesStore, this);
    },

    /**
     * Фильтрует типы операций
     */
    filterTypesStore: function () {
        this.typesStore.filterBy(this.filterTypes, this);
    },

    /**
     * Фильтр для стора с типами операций
     */
    filterTypes: function (record) {
        var wizard = this.getWizard(),
            metaRecord = wizard.getMetarecord(),
            entityName;

        if (!metaRecord) {
            return false;
        }

        entityName = metaRecord.get('name');

        switch (record.get('type')) {
            case 'IMPORT_RECORDS_FROM_XLS':
            case 'REPUBLISH_RECORDS':
                return false;
                break;
            case 'REMOVE_RECORDS':
                return Unidata.Config.userHasRights(entityName, ['delete']);
                break;
            case 'MODIFY_RECORDS':
            case 'REMOVE_RELATION':
                return Unidata.Config.userHasRights(entityName, ['update']);
                break;
            case 'EXPORT_RECORDS_TO_XLS':
                return Unidata.Config.userHasAnyRights(entityName, ['read', 'create', 'delete']);
                break;
        }

        return true;
    },

    /**
     * При фильтрации - прячем/показываем нужные радиобаттоны
     */
    onFilterchange: function () {
        var radiogroup = this.lookupReference('radiogroup'),
            data = this.typesStore.getData();

        radiogroup.reset();

        radiogroup.items.each(function (item) {
            item.setHidden(!data.find('type', item.inputValue));
        });
    },

    /**
     * Инициализация элемента радиогруппы
     * @param record
     */
    initRadiogroupItem: function (record) {
        var radiogroup = this.lookupReference('radiogroup'),
            type = record.get('type'),
            description = record.get('description');

        this.allTypes[type] = description;

        radiogroup.add({
            boxLabel: description,
            name: 'selectedType',
            inputValue: type
        });
    }

});
