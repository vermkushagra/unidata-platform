Ext.define('Unidata.view.steward.dataentity.classifier.flat.FlatClassifierNodeTabletController', {
    extend: 'Unidata.view.steward.dataentity.classifier.abstraction.AbstractClassifierNodeTabletController',

    alias: 'controller.steward.dataentity.classifier.flatclassifiernodetablet',

    init: function () {
        var view = this.getView(),
            containers;

        this.callParent(arguments);

        containers = this.buildTablet();

        view.add(containers);

        this.refreshRecordsInfo();
    },

    refreshRecordsInfo: function () {
        var view = this.getView(),
            itemCount = view.items.getCount();

        if (itemCount === 0) {
            view.showNoData(view.noDataText);
        } else {
            view.hideNoData();
        }

        this.refreshTitle();
    },

    refreshTitle: function () {
        var view = this.getView(),
            classifier = view.getClassifier(),
            title;

        title = classifier.get('displayName');

        view.setTitle(title);
    }

    // TODO: implement readOnly if need

});
