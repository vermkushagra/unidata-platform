Ext.define('Unidata.view.steward.dataentity.classifier.carousel.CarouselClassifierNodeTabletController', {
    extend: 'Unidata.view.steward.dataentity.classifier.abstraction.AbstractClassifierNodeTabletController',

    alias: 'controller.steward.dataentity.classifier.carouselclassifiernodetablet',

    init: function () {
        var view = this.getView(),
            classifierAttributeTablets,
            carouselItems,
            classifier = view.getClassifier(), //?
            title;

        this.callParent(arguments);

        title = classifier.get('displayName');

        view.setTitle(title);

        classifierAttributeTablets = this.buildTablet();

        view.carouselPanel = Ext.create('Unidata.view.component.CarouselPanel', {
            noDataText: Unidata.i18n.t('dataentity>recordNotClassified')
        });
        view.add(view.carouselPanel);
        view.carouselPanel.on('itemcountchanged', this.onCarouselPanelItemCountChanged, this);

        if (classifierAttributeTablets.length > 0) {
            carouselItems = this.buildCarouselItems(classifierAttributeTablets);
            view.carouselPanel.addCarouselItems(carouselItems);
        }
    },

    onAddClassifierNodeButtonClick: function (btn, e) {
        var view = this.getView(),
            container;

        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.stopEvent();

        view.expand();

        container = this.createClassifierAttributeTablet();
        container.showClassifierNodeEditorWindow(this.onShowClassifierNodeEditorWindowOkButtonClick.bind(this, container));
    },

    onShowClassifierNodeEditorWindowOkButtonClick: function (container, wnd, selected) {
        var me = this,
            view = this.getView(),
            classifier = view.getClassifier();

        view.expand();

        if (!view.carouselPanel) {
            view.carouselPanel = Ext.create('Unidata.view.component.CarouselPanel');
            view.add(view.carouselPanel);
        }

        container.useSelectedClassifierNode(selected);
        view.fireComponentEvent('datarecordclassifiernodechange', classifier, selected[0]);

        view.carouselPanel.addCarouselItem(me.buildCarouselItem(container));
        view.carouselPanel.moveLast();
    },

    buildCarouselItem: function (classifierAttributeTablet) {
        return this.getView().carouselPanel.doConfigCarouselItem(classifierAttributeTablet);
    },

    buildCarouselItems: function (classifierAttributeTablets) {
        var carouselItems,
            me = this;

        carouselItems = classifierAttributeTablets.map(function (classifierAttribute) {
            return me.buildCarouselItem(classifierAttribute);
        });

        return carouselItems;
    },

    getSimpleAttributeContainers: function () {
        //TODO: implement me
        throw new Error('Method is not implemented');
    },

    onRemoveClassifierNode: function (panel) {
        var view = this.getView(),
            carouselPanel = view.carouselPanel;

        this.callParent(arguments);

        carouselPanel.removeCarouselItem(panel);
    },

    onCarouselPanelItemCountChanged: function (carouselPanel, count) {
        var view = this.getView();

        view.setCarouselItemCount(count);
    },

    refreshTitle: function () {
        var view = this.getView(),
            classifier = view.getClassifier(),
            displayName = classifier.get('displayName'),
            carouselItemCount = view.getCarouselItemCount(),
            title,
            tpl;

        if (carouselItemCount > 0) {
            tpl = '<span class = "un-title-text">{0} </span><span class = "un-simple-title-text">(' + Unidata.i18n.t('dataentity>classifierNodes') + ': {1}) </span>';
            title = Ext.String.format(tpl, displayName, carouselItemCount);
        } else {
            title = displayName;
        }

        view.setTitle(Ext.String.format('<span class = "un-title-text">{0}</span>', title));
    },

    updateCarouselItemCount: function () {
        // делаем не через viewModel, чтобы не было проблем из-за наследования от AbstractAttributeTable
        this.refreshTitle();
    }
});
