/**
 * @author Ivan Marshalkin
 * @date 2016-12-07
 */

Ext.define('Unidata.view.admin.enumeration.EnumerationController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.enumeration',

    init: function () {
        this.callParent(arguments);

        this.loadEnumerationStore();
    },

    loadEnumerationStore: function () {
        var promise;

        promise = this.getEnumerationPromiseStoreLoad();
        this.handleEnumerationPromiseStoreLoad(promise);
    },

    getEnumerationPromiseStoreLoad: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            store = viewModel.getStore('enumerationStore'),
            draftMode = view.getDraftMode(),
            storeLoadCfg,
            promise;

        storeLoadCfg = {
            params: {
                draft: draftMode
            }
        };
        promise = Unidata.util.api.Enumeration.loadStore(store, true, storeLoadCfg);

        return promise;
    },

    handleEnumerationPromiseStoreLoad: function (promise) {
        promise.then(
            this.onEnumerationSuccessLoad.bind(this),
            this.onEnumerationFailureLoad.bind(this)
        ).done();
    },

    onEnumerationSuccessLoad: function (enumerationStore) {
        var viewModel = this.getViewModel(),
            enumerationTreeStore = viewModel.getStore('enumerationTreeStore'),
            rootNode = enumerationTreeStore.getRoot(),
            rootChildNodes,
            rootNodesCount;

        rootNode.removeAll();

        enumerationStore.each(function (enumeration) {
            var values = enumeration.values(),
                node;

            node = rootNode.appendChild({
                leaf: false,
                nodeType: 'ENUMERATION_NODE',
                record: enumeration,
                displayName: enumeration.get('displayName'),
                name: enumeration.get('name')
                //checked: false // раскоментировать для показа чекбокса у ноды
            });

            values.each(function (value) {
                node.appendChild({
                    leaf: true,
                    nodeType: 'ENUMERATIONVALUE_NODE',
                    record: value,
                    displayName: value.get('displayName'),
                    name: value.get('name')
                });
            });
        });

        enumerationTreeStore.setSorters([{
            property: 'displayName',
            direction: 'ASC'
        }]);

        if (rootNode.hasChildNodes()) {
            rootNode.expand();
        }

        rootChildNodes = this.getRootChildNodes();
        rootNodesCount = rootChildNodes.length;

        viewModel.set('rootNodesCount', rootNodesCount);
    },

    getRootChildNodes: function () {
        var view = this.getView(),
            tree = view.enumerationTree,
            nodes,
            rootNode;

        rootNode = tree.getRootNode();
        nodes = rootNode.childNodes;

        return nodes;
    },

    onEnumerationFailureLoad: function () {
    },

    onImportEnumerationButtonChange: function () {
    },

    onExportEnumerationButtonClick: function () {
    },

    onDeleteEnumerationButtonClick: function () {
    },

    onToggleAllCheckBoxChange: function () {
    },

    onEnumerationNodeCheckChange: function () {
    },

    updateDraftMode: function (draftMode) {
        var viewModel = this.getViewModel();

        viewModel.set('draftMode', draftMode);

        this.loadEnumerationStore();
    }
});
