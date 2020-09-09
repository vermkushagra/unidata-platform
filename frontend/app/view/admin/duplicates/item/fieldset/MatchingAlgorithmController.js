/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */
Ext.define('Unidata.view.admin.duplicates.item.fieldset.MatchingAlgorithmController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.duplicates.ruleedit.matchingalgorithm',

    init: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            currentAlgorithm = view.getMatchingAlgorithm(),
            matchingAlgorithms,
            defaultAlgorithm;

        matchingAlgorithms = view.getMatchingAlgorithmStore().getRange();
        view.matchingAlgorithmField.getStore().add(matchingAlgorithms);

        defaultAlgorithm = Ext.Array.findBy(matchingAlgorithms, function (algorithm) {
            return algorithm.get('id') === 1;
        });

        if (!currentAlgorithm.get('name') && defaultAlgorithm) {
            currentAlgorithm.set('id', defaultAlgorithm.get('id'));
            currentAlgorithm.set('name', defaultAlgorithm.get('name'));

            this.updateAlgorithmMatchingFields(defaultAlgorithm);
        }

        this.createMatchingFieldsFromAlgorithm();

        // кастомный валидатор для matchingAlgorithmField
        view.matchingAlgorithmField.validator = this.matchingAlgorithmFieldValidator.bind(this);

        // следим за изменениями флага withPreprocessing
        viewModel.bind('{withPreprocessing}', this.onWithPreprocessingChange, this);
    },

    matchingAlgorithmFieldValidator: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            withPreprocessing = viewModel.get('withPreprocessing'),
            matchingAlgorithmField = view.matchingAlgorithmField,
            selectedAlgorithm = matchingAlgorithmField.getSelection();

        if (withPreprocessing && !selectedAlgorithm.get('preprocessingAvailable')) {
            return Unidata.i18n.t('admin.duplicates>error.wrongPreprocessingAlgorithm');
        }

        return true;
    },

    /**
     * Фильтруем алгоритмы комбобокса, в зависимости от флага
     *
     * @param {boolean} withPreprocessing
     */
    onWithPreprocessingChange: function (withPreprocessing) {
        var view = this.getView(),
            matchingAlgorithmField = view.matchingAlgorithmField,
            store = matchingAlgorithmField.getStore();

        if (withPreprocessing) {
            store.filterBy(this.filterPreprocessingAvailable, this);
        } else {
            store.clearFilter();
        }

        matchingAlgorithmField.isValid();
    },

    /**
     * Фильтр, который оставляет только те алгоритмы, где preprocessingAvailable == true
     *
     * @param matchingAlgorithmItem
     * @returns {boolean}
     */
    filterPreprocessingAvailable: function (matchingAlgorithmItem) {
        return Boolean(matchingAlgorithmItem.get('preprocessingAvailable'));
    },

    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel();

        this.setReadOnlyFields(readOnly);

        viewModel.set('readOnly', readOnly);
    },

    updateMatchingAlgorithm: function (matchingAlgorithm) {
        var viewModel = this.getViewModel();

        viewModel.set('matchingAlgorithm', matchingAlgorithm);
    },

    updateMatchingAlgorithmStore: function (store) {
        var viewModel = this.getViewModel();

        viewModel.getStore('matchingAlgorithmListStore').removeAll();
        viewModel.getStore('matchingAlgorithmListStore').add(store.getRange());
    },

    clearMatchingFieldsContainer: function () {
        var view = this.getView();

        view.fieldsContainer.removeAll();
    },

    onMatchingAlgorithmSelect: function (combobox, comboMatchingAlgorithmRecord) {
        var view              = this.getView(),
            matchingAlgorithm = view.getMatchingAlgorithm();

        matchingAlgorithm.set('id', comboMatchingAlgorithmRecord.get('id'));
        matchingAlgorithm.set('name', comboMatchingAlgorithmRecord.get('name'));
        matchingAlgorithm.set('description', comboMatchingAlgorithmRecord.get('description'));

        this.clearMatchingFieldsContainer();

        this.updateAlgorithmMatchingFields(comboMatchingAlgorithmRecord);

        this.createMatchingFieldsFromAlgorithm();
    },

    createMatchingFieldsFromAlgorithm: function () {
        var me                = this,
            view              = this.getView(),
            matchingAlgorithm = view.getMatchingAlgorithm(),
            matchingFields;

        matchingFields = matchingAlgorithm.matchingFields();

        matchingFields.each(function (matchingField) {
            var container,
                constantField;

            constantField = matchingField.get('constantField');
            container = me.createMatchingFieldContainer(matchingField, constantField);

            view.fieldsContainer.add(container);
        });
    },

    updateAlgorithmMatchingFields: function (comboMatchingAlgorithmRecord) {
        var view              = this.getView(),
            matchingAlgorithm = view.getMatchingAlgorithm();

        matchingAlgorithm.matchingFields().removeAll();
        matchingAlgorithm.set('id', comboMatchingAlgorithmRecord.get('id'));

        comboMatchingAlgorithmRecord.matchingFields().each(function (comboMatchingField) {
            var matchingField;

            matchingField = Ext.create('Unidata.model.matching.MatchingField', {
                id: comboMatchingField.get('id'),
                name: comboMatchingField.get('name'),
                description: comboMatchingField.get('description'),
                constantField: comboMatchingField.get('constantField')
            });

            matchingAlgorithm.matchingFields().add(matchingField);
        });
    },

    createMatchingFieldContainer: function (matchingField, constantField) {
        var me         = this,
            view       = this.getView(),
            metaRecord = view.getMetaRecord(),
            readOnly   = view.getReadOnly(),
            description = matchingField.get('description'),
            container,
            fieldXtype;

        constantField = Ext.isBoolean(constantField) ? constantField : false;

        fieldXtype = !constantField ? 'un.entityattributehtmlcombo' : 'textfield';

        container = Ext.create({
            xtype: 'container',
            referenceHolder: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: fieldXtype,
                    reference: 'attributeCombobox',
                    metaRecord: metaRecord,
                    matchingField: matchingField,
                    value: matchingField ? matchingField.get('name') : null,
                    readOnly: readOnly,
                    listeners: {
                        change: {
                            fn: me.onFieldChange,
                            scope: me
                        }
                    },
                    triggers: {
                        clear: {
                            hideOnReadOnly: true,
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.setValue(null);
                            }
                        }
                    }
                }
            ]
        });

        container.attributeCombo = container.lookupReference('attributeCombobox');

        if (description) {
            container.attributeCombo.on('render', function () {
                container.attributeCombo.tip = Ext.create('Ext.tip.ToolTip', {
                    target: container.attributeCombo.getEl(),
                    html: description
                });
            });
        }

        if (!constantField) {
            container.attributeCombo.on('afterRender', function (field) {
                new Ext.dd.DropTarget(this.bodyEl.dom, {
                    ddGroup: 'attributeDDGroup',
                    notifyDrop: function (dd, e, node) {
                        var path = node.records[0].getPath('name', '.').substring(2);

                        if (field.readOnly) {
                            return;
                        }

                        field.setValue(path);

                        return true;
                    }
                });
            });
        }

        return container;
    },

    onFieldChange: function (field) {
        field.matchingField.set('name', field.getValue());
    },

    onDeleteMatchingAlgorithmButtonClick: function () {
        var title = Unidata.i18n.t('common:confirmation'),
            msg   = Unidata.i18n.t('admin.duplicates>confirmRemoveMatchingAlgorithm');

        Unidata.showPrompt(title, msg, this.deleteMatchingAlgorithm, this, null, []);
    },

    deleteMatchingAlgorithm: function () {
        var view              = this.getView(),
            matchingAlgorithm = view.getMatchingAlgorithm();

        view.fireEvent('deletealgorithm', view, matchingAlgorithm);
    },

    /**
     * Возвращает истину если алгоритм сопоставления заполнен корректно
     */
    isValidMatchingAlgorithm: function () {
        var view              = this.getView(),
            matchingAlgorithm = view.getMatchingAlgorithm(),
            isValid           = true;

        if (!view.matchingAlgorithmField.isValid()) {
            return false;
        }

        if (!matchingAlgorithm.get('id')) {
            isValid = false;
        }

        matchingAlgorithm.matchingFields().each(function (matchingField) {
            if (!matchingField.get('name')) {
                isValid = false;
            }
        });

        this.clearIncorrectField();
        this.markIncorrectField();

        return isValid;
    },

    markIncorrectField: function () {
        var view       = this.getView(),
            containers = view.fieldsContainer.items;

        containers.each(function (container) {
            if (!container.attributeCombo.getValue()) {
                container.attributeCombo.markInvalid('');
            }
        });
    },

    setReadOnlyFields: function (readOnly) {
        var view = this.getView(),
            containers;

        if (!view.fieldsContainer) {
            return;
        }

        containers = view.fieldsContainer.items;

        containers.each(function (container) {
            container.attributeCombo.setReadOnly(readOnly);
        });
    },

    clearIncorrectField: function () {
        var view       = this.getView(),
            containers = view.fieldsContainer.items;

        containers.each(function (container) {
            container.attributeCombo.clearInvalid();
        });
    }
});
