Ext.define('Unidata.view.steward.dataentity.complex.carousel.CarouselAttributeTabletController', {
    extend: 'Unidata.view.steward.dataentity.complex.abstraction.AbstractAttributeTabletController',

    alias: 'controller.steward.dataentity.complex.carouselattributetablet',

    init: function () {
        var view = this.getView(),
            complexAttributes,
            carouselItems,
            metaAttribute = this.getMetaAttribute(),
            title;

        this.callParent(arguments);

        title = metaAttribute.get('displayName');

        this.setTitle(title);

        complexAttributes = this.buildTablet();

        view.carouselPanel = Ext.create('Unidata.view.component.CarouselPanel');
        view.add(view.carouselPanel);
        view.carouselPanel.on('itemcountchanged', this.onCarouselPanelItemCountChanged, this);

        if (complexAttributes.length > 0) {
            carouselItems = this.buildCarouselItems(complexAttributes);
            view.carouselPanel.addCarouselItems(carouselItems);
        }
    },

    onCarouselPanelItemCountChanged: function (carouselPanel, count) {
        var view = this.getView();

        view.setCarouselItemCount(count);
    },

    getCarouselPanel: function () {
        return this.getView().carouselPanel;
    },

    getCarouselItemsContainer: function () {
        return this.getCarouselPanel().carouselContainer;
    },

    onDeleteComplexAttributeEnabledChange: function (value) {
        var carouselPanel = this.getCarouselPanel();

        if (!carouselPanel) {
            return;
        }

        this.getCarouselPanel().getCarouselItems().each(function (item) {
            item.setDeletable(value);
        });
    },

    onDeleteComplexAttributeVisibleChange: function (value) {
        var carouselPanel = this.getCarouselPanel();

        if (!carouselPanel) {
            return;
        }

        this.getCarouselPanel().getCarouselItems().each(function (item) {
            item.setDeletableHidden(!value);
        });
    },

    onAddComplexAttributeEnabledChange: function (value) {
        var addButton = this.lookupReference('addComplexAttributeButton');

        addButton.setDisabled(!value);
    },

    onAddComplexAttributeVisibleChange: function (value) {
        var addButton = this.lookupReference('addComplexAttributeButton');

        addButton.setHidden(!value);
    },

    onAddComplexAttributeClick: function (btn, e) {
        var view = this.getView(),
            container;

        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.stopEvent();

        view.expand();

        if (!view.carouselPanel) {
            view.carouselPanel = Ext.create('Unidata.view.component.CarouselPanel');
            view.add(view.carouselPanel);
        }

        container = this.createNestedRecord();
        view.carouselPanel.addCarouselItem(this.buildCarouselItem(container));
        view.carouselPanel.moveLast();
    },

    onRemoveComplexAttribute: function (panel) {
        this.callParent(arguments);

        this.getCarouselPanel().removeCarouselItem(panel);
    },

    buildCarouselItem: function (complexAttribute) {
        return this.getCarouselPanel().doConfigCarouselItem(complexAttribute);
    },

    buildCarouselItems: function (complexAttributes) {
        var carouselItems,
            me = this;

        carouselItems = complexAttributes.map(function (complexAttribute) {
            return me.buildCarouselItem(complexAttribute);
        });

        return carouselItems;
    },

    applyReadOnly: function (readOnly) {
        var view = this.getView(),
            metaAttribute = view.getMetaAttribute();

        if (metaAttribute && metaAttribute.get('readOnly')) {
            return true;
        }

        return readOnly;
    },

    updateReadOnly: function (readOnly) {
        var viewModel  = this.getViewModel(),
            containers = this.getComplexAttributeContainers();

        Ext.Array.each(containers, function (container) {
            container.setReadOnly(readOnly);
        });

        // флаг - необходим для определения достпности операции добавления / удаления инстанса комплексного атрибута
        viewModel.set('readOnly', readOnly);
    },

    updatePreventMarkField: function (value) {
        var containers = this.getComplexAttributeContainers();

        Ext.Array.each(containers, function (container) {
            container.setPreventMarkField(value);
        });
    },

    updateHiddenAttribute: function (hiddenAttribute) {
        var viewModel  = this.getViewModel(),
            containers = this.getComplexAttributeContainers();

        Ext.Array.each(containers, function (container) {
            container.setHiddenAttribute(hiddenAttribute);
        });

        // флаг - необходим для определения видимости комплексного атрибута если он отмечен в модели как скрытй
        viewModel.set('hiddenAttribute', hiddenAttribute);
    },

    getComplexAttributeContainers: function () {
        var containers    = [],
            carouselPanel = this.getCarouselPanel(),
            items;

        if (carouselPanel) {
            items = carouselPanel.getCarouselItems();
        }

        if (items && items.isMixedCollection) {
            containers = items.getRange();
        }

        return containers;
    },

    getSimpleAttributeContainers: function () {
        var containers                 = [],
            complexAttributeContainers = this.getComplexAttributeContainers();

        Ext.Array.each(complexAttributeContainers, function (container) {
            var childContainers;

            childContainers = container.getSimpleAttributeContainers();
            containers = Ext.Array.merge(containers, childContainers);
        });

        containers = Ext.Array.unique(containers);

        return containers;
    },

    refreshTitle: function () {
        var view = this.getView(),
            metaAttribute = this.getMetaAttribute(),
            displayName = metaAttribute.get('displayName'),
            carouselItemCount = view.getCarouselItemCount(),
            title,
            tpl;

        if (carouselItemCount > 0) {
            tpl = '<span class = "un-title-text">{0} </span><span class = "un-simple-title-text">(' + Unidata.i18n.t('dataentity>records') + ': {1}) </span>';
            title = Ext.String.format(tpl, displayName, carouselItemCount);
        } else {
            title = displayName;
        }

        this.setTitle(Ext.String.format('<span class = "un-title-text">{0}</span>', title));
    },

    updateCarouselItemCount: function () {
        // делаем не через viewModel, чтобы не было проблем из-за наследования от AbstractAttributeTable
        this.refreshTitle();
    }
});
