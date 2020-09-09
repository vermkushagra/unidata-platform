Ext.define('Unidata.view.admin.entity.metarecord.property.PropertyController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord.property',

    propertyChange: function () {
        var viewModel = this.getViewModel().getParent(),
            record = viewModel.get('currentRecord');

        this.checkRecordDirty(record, this.getViewModel().getParent());
    },

    validateRequiredFields: function () {
        var view = this.getView(),
            validateReferences = ['name', 'displayName', 'groupName'],
            field;

        Ext.Array.each(validateReferences, function (item) {
            field = view.lookupReference(item);

            if (field) {
                field.validate();
            }
        });
    },

    onValidityPeriodCheckboxRender: function (component) {
        var tip;

        tip = Ext.create('Ext.tip.ToolTip', {
            target: component.getEl(),
            html: ''
        });

        component.tip = tip;

        component.getEl().on('mouseenter', function () {
            var html = Unidata.i18n.t('admin.metamodel>selectEntityTimeInterval');

            if (component.readOnly) {
                html = Unidata.i18n.t('admin.metamodel>changeTimeIntervalNotAvailable');
            }

            component.tip.setHtml(html);
        });
    },

    onNecessaryStoresLoad: function () {
        var view = this.getView();

        view.fireEvent('loadallstore');
    },

    onActivate: function () {
        var view = this.getView(),
            generationStrategyForm = view.generationStrategyForm,
            attributeFilters;

        if (!generationStrategyForm.getStrategyOwner()) {
            return;
        }

        // при активации таба перезаполняем store атрибутов, т.к. их состав мог измениться
        attributeFilters = this.buildGenerationStrategyAttributeFilters();
        generationStrategyForm.fillDisplayAttributeStore(attributeFilters);
    },

    buildGenerationStrategyAttributeFilters: function () {
        var attributeFilters;

        attributeFilters = [
            function (item) {
                var simpleDataType = item.get('simpleDataType'),
                    typeCategory = item.get('typeCategory'),
                    isSpecificSimpleDataType,
                    isLookupEntityType;

                isSpecificSimpleDataType = simpleDataType === 'String' || simpleDataType === 'Integer';
                isLookupEntityType = typeCategory === 'lookupEntityType';

                return isSpecificSimpleDataType || isLookupEntityType;
            }
        ];

        return attributeFilters;
    }
});
