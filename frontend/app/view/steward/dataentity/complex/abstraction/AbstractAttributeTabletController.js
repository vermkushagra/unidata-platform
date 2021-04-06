Ext.define('Unidata.view.steward.dataentity.complex.abstraction.AbstractAttributeTabletController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.dataentity.complex.abstractattributetablet',

    init: function () {
        var me            = this,
            view          = me.getView(),
            viewModel     = me.getViewModel(),
            metaNested    = me.getMetaNested(),
            dataNested    = me.getDataNested(),
            metaAttribute = me.getMetaAttribute(),
            dataAttribute = me.getDataAttribute();

        me.callParent(arguments);

        if (!metaNested) {
            throw 'metaNested is empty';
        }

        if (!dataNested) {
            throw 'dataNested is empty';
        }

        if (!metaAttribute) {
            throw 'metaAttribute is empty';
        }

        if (!dataAttribute) {
            throw 'dataAttribute is empty';
        }

        viewModel.set('minComplexAttributeCount', me.getMetaAttributeMinCount());
        viewModel.set('maxComplexAttributeCount', me.getMetaAttributeMaxCount());
        viewModel.set('hiddenComplexAttribute', me.getMetaAttributeHidden());
        me.updateComplexAttributeCount();

        view.on('render', this.onComponentRender, this, {single: true});
    },

    onComponentRender: function () {
        // инициализация перенесена в beforerender т.к. tools создаются после рендеринга компонента
        this.initBindChange();
    },

    initBindChange: function () {
        var viewModel = this.getViewModel(),
            options;

        options = {
            deep: true
        };

        viewModel.bind('{deleteComplexAttributeEnabled}', this.onDeleteComplexAttributeEnabledChange, this, options);
        viewModel.bind('{deleteComplexAttributeVisible}', this.onDeleteComplexAttributeVisibleChange, this, options);

        viewModel.bind('{addComplexAttributeEnabled}', this.onAddComplexAttributeEnabledChange, this, options);
        viewModel.bind('{addComplexAttributeVisible}', this.onAddComplexAttributeVisibleChange, this, options);
    },

    onDeleteComplexAttributeEnabledChange: function () {
        throw 'onDeleteComplexAttributeEnabledChange is not overriden';
    },

    onDeleteComplexAttributeVisibleChange: function () {
        throw 'onDeleteComplexAttributeVisibleChange is not overriden';
    },

    onAddComplexAttributeEnabledChange: function () {
        throw 'onAddComplexAttributeEnabledChange is not overriden';
    },

    onAddComplexAttributeVisibleChange: function () {
        throw 'onAddComplexAttributeVisibleChange is not overriden';
    },

    onAddComplexAttributeClick: function () {
        throw 'onAddComplexAttributeClick is not overriden';
    },

    updateComplexAttributeCount: function () {
        var viewModel     = this.getViewModel(),
            dataAttribute = this.getDataAttribute(),
            nestedRecords;

        nestedRecords = dataAttribute.nestedRecords();

        viewModel.set('complexAttributeCount', nestedRecords.getCount());
    },

    getTitle: function () {
        var view = this.getView();

        return view.getTitle();
    },

    setTitle: function (title) {
        var view = this.getView();

        return view.setTitle(title);
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

    getDataAttribute: function () {
        var view = this.getView();

        return view.getDataAttribute();
    },

    getMetaAttribute: function () {
        var view = this.getView();

        return view.getMetaAttribute();
    },

    getMetaAttributeMinCount: function () {
        var metaAttribute = this.getMetaAttribute();

        return metaAttribute.get('minCount');
    },

    getMetaAttributeMaxCount: function () {
        var metaAttribute = this.getMetaAttribute();

        return metaAttribute.get('maxCount');
    },

    getMetaAttributeHidden: function () {
        var metaAttribute = this.getMetaAttribute();

        return metaAttribute.get('hidden');
    },

    getDepth: function () {
        var view = this.getView();

        return view.getDepth();
    },

    getReadOnly: function () {
        var view = this.getView();

        return view.getReadOnly();
    },

    getHiddenAttribute: function () {
        var view = this.getView();

        return view.getHiddenAttribute();
    },

    createNestedRecord: function () {
        var dataAttribute = this.getDataAttribute(),
            metaAttribute = this.getMetaAttribute(),
            readOnly      = this.getReadOnly(),
            nestedRecords,
            newNestedRecord,
            container;

        newNestedRecord = Ext.create('Unidata.model.data.NestedRecord');

        nestedRecords = dataAttribute.nestedRecords();
        nestedRecords.add(newNestedRecord);

        nestedRecords.dirty = true;

        container = this.createComplexAttribute(metaAttribute.getNestedEntity(), newNestedRecord);

        this.updateComplexAttributeCount();

        return container;
    },

    buildTablet: function () {
        var me            = this,
            containers    = [],
            dataAttribute = this.getDataAttribute(),
            metaAttribute = this.getMetaAttribute(),
            nestedRecords;

        // ограничения согласно minCount метамодели для комплексного атрибута
        Unidata.view.steward.dataentity.util.ComplexAttribute.constrainDataComplexAttributeByMinCount(
            metaAttribute,
            dataAttribute);

        nestedRecords = dataAttribute.nestedRecords();

        nestedRecords.each(function (nestedRecord) {
            var container;

            container = me.createComplexAttribute(metaAttribute.getNestedEntity(), nestedRecord);

            containers.push(container);
        });

        // обновляем количество комплексных атрибутов
        // т.к. они согласно метамодели могли быть созданы при построении таблеток
        this.updateComplexAttributeCount();

        return containers;
    },

    createComplexAttribute: function (metaNested, dataNested) {
        var view             = this.getView(),
            preventMarkField = view.getPreventMarkField(),
            metaRecord       = this.getMetaRecord(),
            dataRecord       = this.getDataRecord(),
            readOnly         = this.getReadOnly(),
            depth            = this.getDepth(),
            hiddenAttribute  = this.getHiddenAttribute(),
            viewModel        = this.getViewModel(),
            deletable        = viewModel.get('deleteComplexAttributeEnabled'),
            deletableHidden  = !viewModel.get('deleteComplexAttributeVisible'),
            container;

        container = Ext.create('Unidata.view.steward.dataentity.attribute.complex.ComplexAttribute', {
            metaRecord: metaRecord,
            dataRecord: dataRecord,
            metaNested: metaNested,
            dataNested: dataNested,
            readOnly: readOnly,
            deletable: deletable,
            deletableHidden: deletableHidden,
            depth: depth,
            hiddenAttribute: hiddenAttribute,
            preventMarkField: preventMarkField
        });

        container.on('removecomplexattribute', this.onRemoveComplexAttribute, this);

        return container;
    },

    onRemoveComplexAttribute: function (panel, metaRecord, dataRecord, metaNested, dataNested) {
        var dataAttribute = this.getDataAttribute(),
            newRecords,
            nestedRecords;

        nestedRecords = dataAttribute.nestedRecords();

        nestedRecords.remove(dataNested);

        if (!dataNested.phantom) {
            // признак того, что был удален хотя бы одна существующая запись
            nestedRecords.isRemovedNonPhantom = true;
        }

        newRecords = nestedRecords.getNewRecords();

        if (nestedRecords.isRemovedNonPhantom) {
            // если удалили существующую вложенную запись, то эталонная запись остается грязной "навсегда"
            nestedRecords.dirty = true;
        } else {
            // а иначе эталонная запись грязная, пока есть новые вложенные записи
            nestedRecords.dirty = newRecords.length > 0;
        }

        this.updateComplexAttributeCount();
    },

    getComplexAttributeContainers: function () {
        throw 'getComplexAttributeContainers is not overriden';
    },

    getSimpleAttributeContainers: function () {
        var containers        = [],
            containersComplex = this.getComplexAttributeContainers();

        Ext.Array.each(containersComplex, function (containerComplex) {
            containers = Ext.Array.merge(containers, containerComplex.getSimpleAttributeContainers());
        });

        return containers;
    }
});
