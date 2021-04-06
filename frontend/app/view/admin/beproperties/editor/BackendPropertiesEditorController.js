/**
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.editor.BackendPropertiesEditorController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.beproperties.editor',

    init: function () {
        var view = this.getView();

        this.showPropertyGroups();
        this.setReadOnlyComponentState(view.getReadOnly());
    },

    /**
     * Отображает backend properties
     */
    showPropertyGroups: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            store = viewModel.getStore('backendPropertiesStore'),
            components = [],
            groups,
            groupPanel,

        groups = store.getGroups();

        groups.each(function (group) {
            groupPanel = Ext.create('Unidata.view.admin.beproperties.propertygroup.PropertyGroup', {
                title: group.getGroupKey(),
                properties: group.getRange(),
                readOnly: view.getReadOnly()
            });

            components.push(groupPanel);
        });

        view.add(components);
    },

    setBackendProperties: function (records) {
        var viewModel = this.getViewModel(),
            store = viewModel.getStore('backendPropertiesStore');

        store.removeAll();
        store.add(records);

        this.showPropertyGroups();
    },

    getBackendProperties: function () {
        var viewModel = this.getViewModel(),
            store = viewModel.getStore('backendPropertiesStore');

        return store.getRange();
    },

    updateReadOnly: function (readOnly) {
        this.setReadOnlyComponentState(readOnly);
    },

    setReadOnlyComponentState: function (readOnly) {
        var view = this.getView(),
            items = view.items;

        if (!items.isMixedCollection) {
            return;
        }

        items.each(function (item) {
            item.setReadOnly(readOnly);
        });
    }
});
