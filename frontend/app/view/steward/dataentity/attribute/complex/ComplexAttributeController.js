Ext.define('Unidata.view.steward.dataentity.attribute.complex.ComplexAttributeController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.dataentity.attribute.complex.complexattribute',

    init: function () {
        var view       = this.getView(),
            metaNested = this.getMetaNested(),
            dataNested = this.getDataNested(),
            containers,
            title;

        this.callParent(arguments);

        if (!metaNested) {
            throw 'metaNested is empty';
        }

        if (!dataNested) {
            throw 'dataNested is empty';
        }

        title = Unidata.util.DataAttributeFormatter.buildEntityTitleFromDataRecord(metaNested, dataNested);
        title = title ? title : this.getTitle();

        this.setTitle(title);

        view.simpleContainers  = this.buildSimpleAttributeContainers();
        view.complexContainers = this.buildComplexAttributeContainers();

        containers = Ext.Array.merge(view.simpleContainers, view.complexContainers);

        view.add(containers);
    },

    getReadOnly: function () {
        var view = this.getView();

        return view.getReadOnly();
    },

    getMetaNested: function () {
        var view = this.getView();

        return view.getMetaNested();
    },

    getDataNested: function () {
        var view = this.getView();

        return view.getDataNested();
    },

    getMetaRecord: function () {
        var view = this.getView();

        return view.getMetaRecord();
    },

    getDataRecord: function () {
        var view = this.getView();

        return view.getDataRecord();
    },

    getDepth: function () {
        var view = this.getView();

        return view.getDepth();
    },

    getHiddenAttribute: function () {
        var view = this.getView();

        return view.getHiddenAttribute();
    },

    setTitle: function (title) {
        var view = this.getView();

        return view.setTitle(title);
    },

    getTitle: function () {
        var view = this.getView();

        return view.getTitle();
    },

    removeComplexAttribute: function () {
        var view       = this.getView(),
            metaRecord = this.getMetaRecord(),
            dataRecord = this.getDataRecord(),
            metaNested = this.getMetaNested(),
            dataNested = this.getDataNested();

        view.fireEvent('removecomplexattribute', view, metaRecord, dataRecord, metaNested, dataNested);
    },

    onRemoveButtonClick: function (btn, e) {
        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.preventDefault();
        e.stopPropagation();

        this.removeComplexAttribute();
    },

    buildSimpleAttributeContainers: function () {
        var view             = this.getView(),
            readOnly         = this.getReadOnly(),
            hiddenAttribute  = this.getHiddenAttribute(),
            preventMarkField = view.getPreventMarkField(),
            container;

        container = Ext.create('Unidata.view.steward.dataentity.simple.AttributeTablet', {
            metaRecord: this.getMetaRecord(),
            dataRecord: this.getDataRecord(),
            metaNested: this.getMetaNested(),
            dataNested: this.getDataNested(),
            readOnly: readOnly,
            hiddenAttribute: hiddenAttribute,
            preventMarkField: preventMarkField
        });

        return container;
    },

    buildComplexAttributeContainers: function () {
        var view             = this.getView(),
            metaRecord       = this.getMetaRecord(),
            dataRecord       = this.getDataRecord(),
            metaNested       = this.getMetaNested(),
            dataNested       = this.getDataNested(),
            containers       = [],
            depth            = this.getDepth(),
            readOnly         = this.getReadOnly(),
            hiddenAttribute  = this.getHiddenAttribute(),
            preventMarkField = view.getPreventMarkField(),
            metaComplexAttributes,
            dataComplexAttribute,
            nestedRecords,
            container;

        metaComplexAttributes = metaNested.complexAttributes();

        // сортируем complexAttributes по полю order в метамодели
        Unidata.util.MetaRecord.localSortComplexAttributeByOder(metaNested, 'ASC');

        metaComplexAttributes.each(function (metaComplexAttribute) {
            var metaAttributeName = metaComplexAttribute.get('name');

            // создаем комплексный атрибут, если не существует
            Unidata.view.steward.dataentity.util.ComplexAttribute.createDataComplexAttributeIfNotExist(
                dataNested,
                metaAttributeName);

            // находим комплексный атрибут по имени. 100% существует, т.к. его создали выше
            dataComplexAttribute = Unidata.view.steward.dataentity.util.ComplexAttribute.findDataComplexAttributeByName(
                dataNested,
                metaAttributeName);

            // ограничения согласно minCount метамодели для комплексного атрибута
            Unidata.view.steward.dataentity.util.ComplexAttribute.constrainDataComplexAttributeByMinCount(
                metaComplexAttribute,
                dataComplexAttribute);

            nestedRecords = dataComplexAttribute.nestedRecords();

            container = Ext.create('Unidata.view.steward.dataentity.complex.flat.FlatAttributeTablet', {
                metaRecord: metaRecord,
                dataRecord: dataRecord,
                metaNested: metaComplexAttribute.getNestedEntity(),
                dataNested: dataComplexAttribute.nestedRecords(),
                metaAttribute: metaComplexAttribute,
                dataAttribute: dataComplexAttribute,
                readOnly: readOnly,
                depth: depth + 1,
                hiddenAttribute: hiddenAttribute,
                preventMarkField: preventMarkField
            });

            containers.push(container);
        });

        return containers;
    },

    updateReadOnly: function (readOnly) {
        var items     = this.getContainers(),
            viewModel = this.getViewModel();

        viewModel.set('readOnly', readOnly);

        Ext.Array.each(items, function (item) {
            item.setReadOnly(readOnly);
        });
    },

    /**
     * Включает / выключает режим подсвечивания ошибок на ошибочных полях
     *
     * @param value
     */
    updatePreventMarkField: function (value) {
        var items = this.getContainers();

        Ext.Array.each(items, function (item) {
            item.setPreventMarkField(value);
        });
    },

    updateHiddenAttribute: function (hiddenAttribute) {
        var items = this.getContainers();

        Ext.Array.each(items, function (item) {
            item.setHiddenAttribute(hiddenAttribute);
        });
    },

    /**
     * Возвращает массив дочерних элементов
     *
     * @returns {Array}
     */
    getContainers: function () {
        var containers = [],
            view       = this.getView(),
            items      = view.items;

        if (items && items.isMixedCollection) {
            containers = items.getRange();
        }

        return containers;
    },

    getSimpleAttributeContainers: function () {
        var containers = [],
            view       = this.getView();

        Ext.Array.each(view.simpleContainers, function (container) {
            var childContainers;

            childContainers = container.getSimpleAttributeContainers();
            containers = Ext.Array.merge(containers, childContainers);
        });

        Ext.Array.each(view.complexContainers, function (container) {
            var childContainers;

            childContainers = container.getSimpleAttributeContainers();
            containers = Ext.Array.merge(containers, childContainers);
        });

        containers = Ext.Array.unique(containers);

        return containers;
    }
});
