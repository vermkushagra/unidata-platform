/**
 * Выбор по группам закомментирован, что бы можно было оперативно вернуть
 *
 * @author Ivan Marshalkin
 * @date 2016-10-26
 */

Ext.define('Unidata.view.steward.cluster.filter.ClusterFilterController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.cluster.filterview',

    onSearchByChange: function (radiogroup, newValue) {
        var view = this.getView();

        switch (newValue.searchBy) {
            case 'record':
                view.dataRecordCombo.show();
                // view.duplicateGroupCombo.hide();
                view.duplicateRuleCombo.hide();
                break;
            case 'rule':
                view.dataRecordCombo.hide();
                // view.duplicateGroupCombo.show();
                view.duplicateRuleCombo.show();
                break;
        }
    },

    onEntityChange: function (combobox, value) {
        var view = this.getView();

        if (view.entityCombo.getDataLoading()) {
            view.entityCombo.on('load', function () {
                this.loadDataRecordCombo(value);
            }, this, {single: true});
        } else {
            this.loadDataRecordCombo(value);
        }

        view.duplicateRuleCombo.setValue(null);
        // view.duplicateGroupCombo.setValue(null);

        view.duplicateRuleCombo.setEntityName(value);
        // view.duplicateGroupCombo.setEntityName(value);

        view.dataRecordCombo.reset();
    },

    loadDataRecordCombo: function (entityName) {
        var view = this.getView(),
            metaRecord = view.entityCombo.getRecord(),
            entityType = metaRecord.get('type');

        view.dataRecordCombo.loadMetaAndInitPicker(entityType, entityName);
    },

    /**
     * Заполняем entityCombo, если он пустой, а у нас уже есть dataRecordCombo
     *
     * @param {Unidata.view.component.DropdownPickerField} dataRecordCombo
     * @param value
     */
    onDataRecordComboChange: function (dataRecordCombo, value) {
        var view = this.getView(),
            entityCombo = view.entityCombo;

        if (value && !entityCombo.getValue()) {
            entityCombo.setValue(dataRecordCombo.getEntityName());
        }
    },

    /**
     * @param cfg {Object}
     * cfg.openIfOnlyOne - признак необходимости открытия кластера если он единственный
     * forceResultExpand - принудительно разворачивать свернутые результаты, если их больше 1
     */
    doSearch: function (cfg) {
        var view              = this.getView(),
            entityName        = view.entityCombo.getValue(),
            entityComboRecord = view.entityCombo.getRecordByValue(entityName),
            entityType        = entityComboRecord.get('type'),
            ruleId            = null,
            matchingRules     = view.duplicateRuleCombo.getMatchingRules(),
            etalonId          = null,
            openIfOnlyOne,
            forceResultExpand,
            preprocessingCheckboxValue = view.preprocessingCheckbox.getValue(),
            cfgParams;

        cfg = cfg || {};
        openIfOnlyOne     = cfg.openIfOnlyOne;
        forceResultExpand = cfg.forceResultExpand;

        // признак необходимости открытия кластера, если он единственный в выдачи
        openIfOnlyOne = openIfOnlyOne !== undefined ? openIfOnlyOne : false;
        forceResultExpand = forceResultExpand !== undefined ? forceResultExpand : false;

        switch (view.searchBy.getValue().searchBy) {
            case 'record':
                etalonId = view.dataRecordCombo.getEtalonId();
                break;
            case 'rule':
                ruleId  = view.duplicateRuleCombo.getValue();
                break;
        }

        cfgParams = {
            preprocessing: preprocessingCheckboxValue,
            entityName: entityName,
            entityType: entityType,
            ruleId: ruleId,
            etalonId: etalonId,
            matchingRules: matchingRules,
            openIfOnlyOne: openIfOnlyOne,
            forceResultExpand: forceResultExpand
        };

        view.fireComponentEvent('clustersearch', cfgParams);
    },

    onSearchButtonClick: function () {
        var cfg = {
            forceResultExpand: true
        };

        this.doSearch(cfg);
    },

    getCurrentEntityName: function () {
        var view       = this.getView(),
            record     = view.entityCombo.getSelection(),
            entityName = null;

        if (record) {
            entityName = record.get('name');
        }

        return entityName;
    },

    getCurrentEntityType: function () {
        var view       = this.getView(),
            record     = view.entityCombo.getSelection(),
            entityType = null;

        if (record) {
            entityType = record.get('type');
        }

        return entityType;
    },

    setRouteToClusterData: function (entityName, entityType, etalonId) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            searchBinding,
            cfg;

        if (!entityName && !etalonId) {
            return;
        }

        if (etalonId) {
            viewModel.set('dataRecordLoading', true);
            view.dataRecordCombo.loadRecordByEtalonId(etalonId).always(function () {
                viewModel.set('dataRecordLoading', false);
            });

            view.searchBy.setValue({searchBy: 'record'});
        }

        view.entityCombo.setValue(entityName);
        view.duplicateRuleCombo.setValue(null);
        // view.duplicateGroupCombo.setValue(null);

        searchBinding = viewModel.bind('{canSearch}', function (canSearch) {
            if (canSearch) {
                cfg = {
                    openIfOnlyOne: true,
                    forceResultExpand: true
                };
                this.doSearch(cfg);
                // после первого удачного поиска больше автоматически не ищем
                searchBinding.destroy();
            }
        }, this);

        if (etalonId) {
            viewModel.set('dataRecordLoading', true);
            view.dataRecordCombo.loadRecordByEtalonId(etalonId).always(function () {
                viewModel.set('dataRecordLoading', false);
            });

            view.searchBy.setValue({searchBy: 'record'});
        }

        view.entityCombo.setValue(entityName);
        view.duplicateRuleCombo.setValue(null);
        // view.duplicateGroupCombo.setValue(null);
    }

});
