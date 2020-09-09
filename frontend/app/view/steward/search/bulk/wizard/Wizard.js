/**
 * @author Aleksandr Bavin
 * @date 16.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.Wizard', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.search.bulk.wizard.WizardController',
        'Unidata.view.steward.search.bulk.wizard.WizardModel',

        'Unidata.view.steward.search.bulk.wizard.WizardStep',
        'Unidata.view.steward.search.bulk.wizard.step.ConfirmStep',
        'Unidata.view.steward.search.bulk.wizard.step.TypeStep',
        'Unidata.view.steward.search.bulk.wizard.step.SettingsStep'
    ],

    alias: 'widget.steward.search.bulk.wizard',

    viewModel: {
        type: 'wizard'
    },

    controller: 'wizard',

    layout: 'fit',

    cls: 'unidata-bulkwizard',

    methodMapper: [
        {
            method: 'getSelectionCount'
        },
        {
            method: 'getAllSteps'
        },
        {
            method: 'getNextStep'
        },
        {
            method: 'getPrevStep'
        },
        {
            method: 'activateStep'
        },
        {
            method: 'activateNextStep'
        },
        {
            method: 'activatePrevStep'
        }
    ],

    referenceHolder: true,

    eventBusHolder: true,
    bubbleBusEvents: [],

    config: {
        metarecord: null,
        useQueryCount: 0,          // если != 0, то используется поисковой запрос (всего результатов поиска)
        selectedIds: [],           // массив выбранных айдишников
        queryParams: null,         // поисковой запрос
        confirmText: null,         // дополнительный текст для окна подтверждения
        operationType: null,       // тип операции из TypeStep
        operationName: null,       // описание операции из TypeStep
        operationSettings: {}      // дополнительные опции из SettingsStep
    },

    margin: 16,

    // всякие разные внешние данные, например entityName,
    // те, которые необходимы на финальной стадии,
    // будут добавлены на этапе SettingsStep в operationSettings
    externalData: {},

    items: {
        xtype: 'tabpanel.wizard',
        reference: 'wizardTabpanel',

        ui: 'un-card',

        // tabPosition: 'top',
        // tabRotation: 0,

        bind: {
            title: '{selectionTitle}'
        },

        listeners: {
            add: 'onStepAdd',
            remove: 'onStepRemove',
            tabchange: 'onTabchange'
        }
    },

    listeners: {
        beforeactivate: 'onBeforeactivate'
    },

    onDestroy: function () {
        delete this.externalData;

        this.callParent(arguments);
    },

    initItems: function () {
        this.callParent(arguments);
        this.initWizardTabpanel();
    },

    initWizardTabpanel: function () {
        var tabItems;

        this.wizardTabpanel = this.lookupReference('wizardTabpanel');

        tabItems = [
            {
                title: Unidata.i18n.t('search>wizard.stepType'),
                xtype: 'steward.search.bulk.wizard.step.type',
                reference: 'typeStep',
                disabled: false,
                wizardTabPanel: this
            },
            {
                title: Unidata.i18n.t('search>wizard.stepSettings'),
                xtype: 'steward.search.bulk.wizard.step.settings',
                wizardTabPanel: this
            },
            {
                title: Unidata.i18n.t('search>wizard.stepApply'),
                xtype: 'steward.search.bulk.wizard.step.confirm',
                wizardTabPanel: this
            }
        ];

        this.wizardTabpanel.add(tabItems);

        this.wizardTabpanel.setActiveTab(0);
    },

    setExternalData: function (key, value) {
        this.externalData[key] = value;
    },

    getExternalData: function (key) {
        return this.externalData[key];
    },

    /**
     * Проверяет, все ли шаги разрешили шаг с подтверждением
     * @returns {boolean}
     */
    isConfirmStepAllowed: function () {
        var allSteps = this.getAllSteps(),
            allStepsAllowConfirm = true;

        allSteps.each(function (step) {
            if (!step.isConfirmStepAllowed()) {
                allStepsAllowConfirm = false;
            }
        });

        return allStepsAllowConfirm;
    },

    updateMetarecord: function (newMetarecord, oldMetarecord) {
        if (newMetarecord !== oldMetarecord) {
            this.fireEvent('metarecordchange', newMetarecord);
            this.wizardTabpanel.setActiveTab(0);
            this.fireComponentEvent('resetsettings');
        }
    },

    updateSelectedIds: function (selectedIds) {
        var useQueryCount = this.getUseQueryCount(),
            count = selectedIds.length;

        if (useQueryCount != 0) {
            count = useQueryCount;
        }

        this.getViewModel().set('selectedCount', count);
    }

});
