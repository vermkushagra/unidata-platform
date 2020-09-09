/**
 *
 * @author Ivan Marshalkin
 * @date 2016-12-01
 */

Ext.define('Unidata.view.component.search.query.classifier.ClassifierFilterItemController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.query.classifieritem',

    init: function () {
        this.callParent(arguments);

        this.initListeners();
        this.initRelayEvents();
    },

    initListeners: function () {
        var view = this.getView(),
            picker = view.classifierNodePicker;

        picker.addComponentListener('nodeselect', this.onClassifierNodeSelect, this);
        picker.addComponentListener('classifierreset', this.onClassifierReset, this);
    },

    initRelayEvents: function () {
        var view = this.getView(),
            classifierFilterPanel = view.lookupReference('classifierFilterPanel');

        view.relayEvents(classifierFilterPanel, ['change']);
    },

    onClassifierNodeSelect: function (classifierNode) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            searchClassifierStore = viewModel.getStore('searchClassifierResultStore'),
            classifierFilterPanel = view.lookupReference('classifierFilterPanel'),
            nodeTerm = view.getNodeTerm(),
            classifierName;

        if (classifierNode) {
            classifierFilterPanel.setClassifierNode(classifierNode);

            classifierName = classifierNode.get('classifierName');

            nodeTerm.setName([classifierName, '$nodes.$node_id'].join('.'));
            nodeTerm.setValue(classifierNode.get('id'));

            searchClassifierStore.getProxy().setExtraParam('text', classifierName);

            view.setCurrentClassifierName(classifierName);

            searchClassifierStore.reload({
                scope: this,
                callback: function (records, store, success) {
                    var entityNames = [];

                    if (success) {
                        Ext.Array.each(records, function (record) {
                            entityNames.push(record.get('keyValue'));
                        });

                        view.setAllowedEntities(entityNames);

                        view.fireEvent('classifieritemnodeselect', view, entityNames);
                    }
                }
            });
        } else {
            view.setCurrentClassifierName(null);
            classifierFilterPanel.removeSearchParamsPanel();
        }
    },

    onClassifierReset: function () {
        var view = this.getView(),
            classifierFilterPanel = view.classifierFilterPanel;

        classifierFilterPanel.removeSearchParamsPanel();

        view.fireEvent('classifieritemnodereset', view);
    },

    getFilters: function () {
        var view = this.getView();

        return view.classifierFilterPanel.getFilters();
    },

    isEmptyFilter: function () {
        var view = this.getView();

        return view.classifierFilterPanel.isEmptyFilter();
    },

    setAllowedClassifiers: function (allowedClassifiers) {
        var view = this.getView();

        view.classifierNodePicker.setClassifiers(allowedClassifiers);
    },

    getClassifierNode: function () {
        var view = this.getView();

        return view.classifierNodePicker.getClassifierNode();
    },

    excludeField: function (attributePath) {
        var view = this.getView();

        return view.classifierFilterPanel.excludeField(attributePath);
    },

    setDisabled: function (value) {
        var view = this.getView();

        view.classifierFilterPanel.setDisabled(value);
        view.classifierNodePicker.setDisabled(value);
    }
});
