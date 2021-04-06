/**
 * @author Aleksandr Bavin
 * @date 2017-03-28
 */
Ext.define('Unidata.view.component.search.query.dataquality.DataQualitySearchController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.query.dataquality.dataqualitysearch',

    init: function () {
        this.initRouter();
    },

    initRouter: function () {
        Unidata.util.Router.on('main', this.onMainTokenChange, this);
    },

    onMainTokenChange: function (tokenValues) {
        if (tokenValues.section === 'data' && tokenValues.reset) {
            this.resetFields();
        }
    },

    updateMetaRecord: function (metaRecord, oldMetaRecord) {
        var view = this.getView(),
            dqCategoryCombo = this.lookupReference('category'),
            dqRuleNameCombo = this.lookupReference('ruleName'),
            dqNamesStore = dqRuleNameCombo.getStore(),
            dqCategoriesStore = dqCategoryCombo.getStore(),
            entityName = metaRecord.get('name');

        dqNamesStore.getProxy().setExtraParam('entityName', entityName);
        dqNamesStore.load();

        dqCategoriesStore.getProxy().setExtraParam('entityName', entityName);
        dqCategoriesStore.load();

        view.setDisabled(false);
    },

    resetFields: function (resetCheckbox) {
        this.lookupReference('ruleName').setValue(null);
        this.lookupReference('severity').setValue(null);
        this.lookupReference('category').setValue(null);

        if (resetCheckbox) {
            this.lookupReference('errorsOnlyCheckbox').setValue(false);
        }
    },

    onInputChange: function () {
        this.updateCheckbox();
        this.fireChangeEvent();
    },

    fireChangeEvent: function () {
        this.getView().fireEvent('change');
    },

    updateCheckbox: function () {
        var view = this.getView(),
            ruleName = this.lookupReference('ruleName').getValue(),
            severity = this.lookupReference('severity').getValue(),
            category = this.lookupReference('category').getValue();

        if (!Ext.isEmpty(ruleName) || !Ext.isEmpty(severity) || !Ext.isEmpty(category)) {
            this.lookupReference('errorsOnlyCheckbox').setValue(true);
            view.expand();
        }
    },

    /**
     * @returns {{}}
     */
    getFilter: function () {
        var searchItemsContainer = this.lookupReference('searchItemsContainer'),
            data = [];

        searchItemsContainer.items.each(function (item) {
            var name = item.getName(),
                value = item.getValue();

            if (Ext.isEmpty(value) || item.isDisabled()) {
                return;
            }

            data.push({
                name: name,
                value: value,
                type: 'String',
                inverted: false
            });
        });

        return data;
    },

    /**
     * @returns {boolean}
     */
    isEmptyFilter: function () {
        var searchItemsContainer = this.lookupReference('searchItemsContainer'),
            result = true;

        searchItemsContainer.items.each(function (item) {
            if (!Ext.isEmpty(item.getValue()) && !item.isDisabled()) {
                result = false;

                return false;
            }
        });

        return result;
    }

});
