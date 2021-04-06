Ext.define('Unidata.view.steward.dataentity.complex.flat.FlatAttributeTabletController', {
    extend: 'Unidata.view.steward.dataentity.complex.abstraction.AbstractAttributeTabletController',

    alias: 'controller.steward.dataentity.complex.flatattributetablet',

    init: function () {
        var view = this.getView(),
            containers;

        this.callParent(arguments);

        containers = this.buildTablet();

        view.add(containers);

        this.refreshRecordsInfo();
    },

    onDeleteComplexAttributeEnabledChange: function (value) {
        var containers = this.getComplexAttributeContainers();

        Ext.Array.each(containers, function (item) {
            item.setDeletable(value);
        });
    },

    onDeleteComplexAttributeVisibleChange: function (value) {
        var containers = this.getComplexAttributeContainers();

        Ext.Array.each(containers, function (item) {
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

        container = this.createNestedRecord();
        container.expand();

        view.add(container);
    },

    onRemoveComplexAttribute: function (panel) {
        var view = this.getView();

        this.callParent(arguments);

        view.remove(panel);
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

    /**
     *
     * @returns {Unidata.view.steward.dataentity.attribute.complex.ComplexAttribute[]}
     */
    getComplexAttributeContainers: function () {
        var containers = [],
            items      = this.getView().items;

        if (items && items.isMixedCollection) {
            containers = items.getRange();
            containers = Ext.Array.filter(containers, function (item) {
                return item instanceof Unidata.view.steward.dataentity.attribute.complex.ComplexAttribute;
            });
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
        var viewModel = this.getViewModel(),
            metaAttribute = this.getMetaAttribute(),
            displayName = metaAttribute.get('displayName'),
            complexAttributeCount = viewModel.get('complexAttributeCount'),
            title;

        if (complexAttributeCount > 0) {
            title = Ext.String.format('{0} <span class = "un-simple-title-text">(' + Unidata.i18n.t('dataentity>records') + ': {1})</span>', displayName, complexAttributeCount);
        } else {
            title = displayName;
        }

        this.setTitle(title);
    },

    onAdd: function () {
        this.refreshRecordsInfo();
    },

    onRemove: function () {
        this.refreshRecordsInfo();
    }
});
