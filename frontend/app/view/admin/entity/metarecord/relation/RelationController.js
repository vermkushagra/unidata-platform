Ext.define('Unidata.view.admin.entity.metarecord.relation.RelationController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.admin.entity.metarecord.relation',

    init: function () {
        var relTypes = Unidata.Constants.getRelTypes(),
            view = this.getView(),
            grid = view.lookupReference('relationGrid');

        this.getView().lookupReference('relationItemColumn').tpl =
            new Ext.XTemplate('<b>{[this.getRelationDisplayName(values)]}</b> {[this.getRelationTypeDisplayName(values)]}', {
                getRelationTypeDisplayName: function (values) {
                    return Unidata.model.data.RelationTimeline.getRelationTypeDisplayName(values.relType);
                },
                getRelationDisplayName: function (values) {
                    // не забываем предотвращение XSS
                    return (values.displayName !== '') ?
                        Ext.String.htmlEncode(values.displayName) :
                        Unidata.i18n.t('admin.metamodel>notNamed');
                }
            });

        this.getView().lookupReference('relationTypeComboBox').setStore({
            fields: ['key', 'value'],
            data: relTypes
        });

        grid.getView().getRowClass = this.getRelationGridRowClass.bind(this);
    },

    onEndProcessResponse: function (proxy, options, operation) {
        if (options.request.options.action === 'read' && operation.getError()) {
            this.fireViewEvent('serverexception');
        }
    },

    getRelationGridRowClass: function (record) {
        if (record && this.isValidRelation(record) && this.isUniqueRelationName(record)) {
            return '';
        }

        return 'errorrow';
    },

    onDisplayFieldSelect: function (tree, node) {
        var ref = this.lookupReference('displayFieldPicker');
        //@TODO create check for only simple attribute
        ref.setValue(node.get('record').get('name'));
        ref.picker.setHidden(true);
    },

    onAddRelation: function () {
        var viewModel = this.getViewModel(),
            ref = this.lookupReference('relationGrid'),
            from = viewModel.getParent().get('currentRecord').get('name'),
            store = viewModel.get('relations'),
            record = store.insert(0, {
                name: '',
                displayName: '',
                fromEntity: from
            })[0];

        ref.selModel.doSelect(record);
        this.checkRecordDirty(record, this.getViewModel().getParent());
    },

    onDeleteRelation: function (btn) {
        this.showPrompt(Unidata.i18n.t('admin.metamodel>removeRelation'), Unidata.i18n.t('admin.metamodel>confirmRemoveRelation'), this.deleteRelation, this, btn);
    },

    deleteRelation: function () {
        var ref = this.lookupReference('relationGrid'),
            record = ref.getSelection(),
            store = ref.getStore(),
            relation = record[0];

        store.remove(record);
        this.removeRelationFromGroup(relation);
        ref.selModel.doSelect(0);

        this.checkRecordDirty(relation, this.getViewModel().getParent());
    },

    /**
     * Удалить упоминание связи из relationGroup
     * @param relation
     */
    removeRelationFromGroup: function (relation) {
        var viewModel      = this.getViewModel(),
            metaRecord     = viewModel.get('currentRecord'),
            relationGroups = metaRecord.relationGroups(),
            index,
            relationGroup,
            relations,
            relationName = relation.get('name'),
            relationType = relation.get('relType');

        index = relationGroups.findExact('relType', relationType);

        if (index > -1) {
            relationGroup = relationGroups.getAt(index);
            relations = relationGroup.get('relations');
            Ext.Array.remove(relations, relationName);
            relationGroup.set('relations', relations);
        }
    },

    onSelectEntityRelation: function (field, record) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            relation = viewModel.get('currentRelation'),
            displayAttributeList = view.displayAttributeList,
            searchAttributeList = view.searchAttributeList,
            displayFieldPicker = this.lookupReference('displayFieldPicker'),
            entityName;

        displayFieldPicker.setValue('');
        displayAttributeList.setValue(null);
        searchAttributeList.setValue(null);

        entityName = record.get('name');
        relation.set('toEntity', entityName);

        this.loadMetaRecordAndFillAttributes(relation);
    },

    loadMetaRecordAndFillAttributes: function (relation) {
        var StatusConstant = Unidata.StatusConstant,
            view = this.getView(),
            me = this,
            entityName;

        entityName  = relation.get('toEntity');
        view.setStatus(StatusConstant.LOADING);
        Unidata.util.api.MetaRecord.getMetaRecord({
            entityName: entityName,
            entityType: 'Entity',
            draft: view.draftMode
        }).then(function (record) {
                var MetaAttributeFormatter = Unidata.util.MetaAttributeFormatter,
                    displayableAttributeDisplayNames,
                    searchAttributeDisplayNames,
                    displayAttributeList,
                    searchAttributeList;

                displayableAttributeDisplayNames = MetaAttributeFormatter.getMainDisplayableAttributeDisplayNames(record);
                searchAttributeDisplayNames = MetaAttributeFormatter.getSearchAttributeDisplayNames(record);
                displayAttributeList = view.displayAttributeList;
                displayAttributeList.setDefaultValueText(displayableAttributeDisplayNames.join(', '));
                searchAttributeList = view.searchAttributeList;
                searchAttributeList.setDefaultValueText(searchAttributeDisplayNames.join(', '));

                me.fillAttributeStore(record);
                view.setStatus(StatusConstant.READY);
            },
            function () {
                view.setStatus(StatusConstant.NONE);
            })
        .done();
    },

    fillAttributeStore: function (record) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            displayAttributeList,
            searchAttributeList,
            relation,
            toEntityDefaultDisplayAttributes,
            toEntitySearchAttributes;

        relation = viewModel.get('currentRelation');
        toEntityDefaultDisplayAttributes = relation.get('toEntityDefaultDisplayAttributes');
        toEntitySearchAttributes = relation.get('toEntitySearchAttributes');
        displayAttributeList = view.displayAttributeList;
        searchAttributeList = view.searchAttributeList;

        Unidata.view.component.AttributeTagField.fillStore(displayAttributeList.getStore(), record);
        Unidata.view.component.AttributeTagField.fillStore(searchAttributeList.getStore(), record);

        displayAttributeList.setValue(toEntityDefaultDisplayAttributes);
        searchAttributeList.setValue(toEntitySearchAttributes);
    },

    validateRequiredFields: function () {
        var view = this.getView(),
            validateReferences = [
                'name',
                'displayName',
                'entityRelation',
                //'displayFieldPicker',
                'relationTypeComboBox'
            ],
            field;

        Ext.Array.each(validateReferences, function (item) {
            field = view.lookupReference(item);

            if (field) {
                field.validate();
            }
        });
    },

    checkRelation: function () {
        var result                       = true,
            view                         = this.getView(),
            attributeTreePanel           = view.lookupReference('attributeTreePanel'),
            attributeTreePanelController = attributeTreePanel.getController();

        if (!this.isValidRelations() ||
            !this.isUniqueAllRelationName() ||
            !attributeTreePanelController.isAttributeNamesUnique() ||
            attributeTreePanel.isErrors) {

            result = false;
        }

        return result;
    },

    /**
     * Возвращает true, если модель связи без ошибок иначе false
     *
     * @param relation - модель связи
     * @returns {boolean}
     */
    isValidRelation: function (relation) {
        return relation.isValid();
    },

    /**
     * Возвращает true, если ВСЕ модели связей без ошибок иначе false
     *
     * @returns {boolean}
     */
    isValidRelations: function () {
        var me = this,
            result = true,
            relations,
            viewModel;

        viewModel = this.getViewModel();
        relations = viewModel.get('currentRecord').relations();

        relations.each(function (relation) {
            if (!me.isValidRelation(relation)) {
                result = false;

                return false; //завершение итерации
            }
        });

        return result;
    },

    isUniqueRelationName: function (relation) {
        var result = true,
            relations,
            viewModel;

        viewModel = this.getViewModel();
        relations = viewModel.get('currentRecord').relations();

        relations.each(function (rel) {
            if (rel !== relation && rel.get('name') === relation.get('name')) {
                result = false;

                return false; //завершение итерации
            }
        });

        return result;
    },

    isUniqueAllRelationName: function () {
        var names = [],
            result = true,
            relations,
            viewModel;

        viewModel = this.getViewModel();
        relations = viewModel.get('currentRecord').relations();

        relations.each(function (relation) {
            if (Ext.Array.contains(names, relation.get('name'))) {
                result = false;

                return false; //завершение итерации
            }

            names.push(relation.get('name'));
        });

        return result;
    },

    onRelationGridSelect: function (rowModel, relation) {
        var viewModel = this.getViewModel();

        this.loadMetaRecordAndFillAttributes(relation);

        viewModel.set('phantom', relation.phantom);
        viewModel.set('hasData', relation.get('hasData'));
    },

    onRelationGridBeforeDeselect: function () {
        var attributeTreePanel,
            attributeTreePanelController;

        attributeTreePanel = this.getView().lookupReference('attributeTreePanel');
        attributeTreePanelController = attributeTreePanel.getController();

        if (!attributeTreePanelController.isAttributeNamesUnique()) {
            return false;
        }

        return true;
    },

    relationComboBoxPickerOnBeforeSelect: function (dataViewModel, record) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            relation = viewModel.get('currentRelation'),
            simpleAttributes = relation.simpleAttributes(),
            attributesCount = simpleAttributes.count(),
            relationTypeComboBox = view.relationTypeComboBox,
            result = true,
            key;

        key = record ? record.get('key') : null;

        // если у текущей связи имеются
        if (key === 'Contains' && attributesCount > 0) {
            Unidata.showPrompt(view.changeToContainsTitle, view.changeToContainsText, this.changeRelationType, this, relationTypeComboBox, [key]);
            result = false;
        }

        relationTypeComboBox.collapse();

        return result;
    },

    /**
     * Изменить тип связи
     *
     * @param relType Имя типа связи
     */
    changeRelationType: function (relType) {
        var view                 = this.getView(),
            viewModel            = this.getViewModel(),
            relation             = viewModel.get('currentRelation'),
            simpleAttributes     = relation.simpleAttributes(),
            relationTypeComboBox = view.relationTypeComboBox,
            attributeTreePanel = view.attributeTreePanel;

        attributeTreePanel.deleteAttribute();
        simpleAttributes.removeAll();
        relationTypeComboBox.select(relType);
    }
});
