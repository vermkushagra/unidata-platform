/**
 *
 * @author Ivan Marshalkin
 * @date 2018-01-29
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.dqnavigation.DqNavigationController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord.dq.dqnavigation',

    filterDqBySpecial: null,
    userFilter: null,

    init: function () {
        var view = this.getView();

        this.filterDqBySpecial = this.createFilterBySpecial();
        this.userFilter = this.createUserFilter();

        this.setReadOnlyComponentState(view.getReadOnly());
    },

    /**
     * Возвращает выбранное правило качества
     *
     * @returns {*}
     */
    getSelectedDqRule: function () {
        var view = this.getView(),
            grid = view.dqList,
            sm = grid.getSelectionModel(),
            dqRule = null;

        if (sm.getCount()) {
            dqRule = sm.getSelection()[0];
        }

        return dqRule;
    },

    /**
     * Возвращает выбранный атрибут в дереве атрибутов
     */
    getSelectedAttribute: function () {
        var view = this.getView(),
            sm = view.attributeTree.getSelectionModel(),
            node = null;

        if (sm.getCount()) {
            node = sm.getSelection()[0];
        }

        return node;
    },

    /**
     * Возвращает корневую ноду дерева атрибутов метамодели
     */
    getRootAttribute: function () {
        var view = this.getView(),
            store = view.attributeTree.getStore(),
            root = store.getRoot();

        return root;
    },

    /**
     * Выбирает корневую ноду дерева атрибутов метамодели
     */
    selectRootAttribute: function () {
        var view = this.getView(),
            root = this.getRootAttribute(),
            sm = view.attributeTree.getSelectionModel();

        sm.select([root]);
    },

    /**
     * Подсвечивает атрибутв дереве, которые используются в правиле качества
     *
     * @param dqRule
     */
    highlightDqRuleAttributes: function (dqRule) {
        var me = this,
            view = this.getView(),
            metaRecord = view.getMetaRecord(),
            root = this.getRootAttribute(),
            paths = [];

        paths = this.getDqRuleAttributePaths(dqRule);

        root.cascadeBy(function (node) {
            var record = node.get('record'),
                attributePath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, record);

            if (node.isRoot()) {
                return;
            }

            if (Ext.Array.contains(paths, attributePath)) {
                // раскрываем ноду т.к. иначе она не будет посвечена
                view.attributeTree.expandPath(node.getPath());
                me.highlightAttribute(node, true);
            } else {
                me.highlightAttribute(node, false);
            }
        });
    },

    /**
     * Возвращает массив путей атрибутов испоьзуемых в dq
     *
     * @param dqRule
     * @returns {*}
     */
    getDqRuleAttributePaths: function (dqRule) {
        var inputPaths = [],
            outputPaths = [],
            fieldName = 'canonicalPath',
            paths;

        if (dqRule) {
            dqRule.inputs().each(function (port) {
                var path = port.get(fieldName);

                if (path) {
                    inputPaths.push(path);
                }
            });

            dqRule.outputs().each(function (port) {
                var path = port.get(fieldName);

                if (path) {
                    outputPaths.push(path);
                }
            });
        }

        paths = Ext.Array.merge(inputPaths, outputPaths);
        paths = Ext.Array.unique(paths);

        return paths;
    },

    /**
     * Подсвечивает / снимает подсветку атрибутов
     *
     * @param node
     * @param highlight - признак необходимости подсветить
     */
    highlightAttribute: function (node, highlight) {
        var view = this.getView(),
            attributeTreeView = view.attributeTree.getView(),
            row = attributeTreeView.getRowByRecord(node),
            rowElement = Ext.get(row),
            highlightCls = 'un-dq-rule-useattr';

        if (!rowElement) {
            return;
        }

        if (highlight) {
            rowElement.addCls(highlightCls);
        } else {
            rowElement.removeCls(highlightCls);
        }
    },

    /**
     * Возвращает массив путей всех вложенных атрибутов включая путь переданного атрибута
     *
     * @param attributeNode
     * @returns {Array}
     */
    getNestedAttributePaths: function (attributeNode) {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            paths = [];

        if (!attributeNode) {
            return paths;
        }

        attributeNode.cascadeBy(function (node) {
            var record = node.get('record'),
                attributePath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, record);

            paths.push(attributePath);
        });

        paths = Ext.Array.unique(paths);

        return paths;
    },

    /**
     * Фильтрует список правил качества - оставляем те в которых используется указанный атрибут
     *
     * @param attributeNode
     */
    filterDqRulesByAttribute: function (attributeNode) {
        var me = this,
            viewModel = this.getViewModel(),
            store = viewModel.getStore('filteredDqRules'),
            filterByName,
            nestedAttributePaths;

        nestedAttributePaths = this.getNestedAttributePaths(attributeNode);

        store.getFilters().removeAll();

        store.addFilter(this.filterDqBySpecial);
        store.addFilter(this.userFilter);

        if (attributeNode.isRoot()) {
            return;
        }

        filterByName = new Ext.util.Filter({
            filterFn: function (record) {
                var dqAttributePaths = me.getDqRuleAttributePaths(record);

                if (Ext.Array.intersect(dqAttributePaths, nestedAttributePaths).length) {
                    return true;
                } else {
                    return false;
                }

                return attributeStore.findExact('name', record.get('name')) === -1;
            }
        });

        store.addFilter(filterByName);
    },

    /**
     * Сбрасывает выбранное правило качества
     */
    resetDqRuleSelection: function () {
        var view = this.getView(),
            grid = view.dqList,
            sm = grid.getSelectionModel();

        sm.deselectAll();
    },

    /**
     * Возвращает истину если указанное правило качества не отфильтровано
     *
     * @param dqRule
     * @returns {boolean}
     */
    isDqRuleFiltered: function (dqRule) {
        var viewModel = this.getViewModel(),
            store = viewModel.getStore('filteredDqRules'),
            filtered = true;

        if (store.contains(dqRule)) {
            filtered = false;
        }

        return filtered;
    },

    /**
     * Активирует / деактивирует возможноть drag and drop в списке правил качества
     *
     * @param draggable
     */
    setDqRuleDraggable: function (draggable) {
        var view = this.getView(),
            grid = view.dqList,
            gridView = grid.getView(),
            plugin = gridView.getPlugin('ddplugin');

        if (draggable) {
            plugin.dragZone.unlock();
            plugin.dropZone.unlock();
        } else {
            plugin.dragZone.lock();
            plugin.dropZone.lock();
        }
    },

    /**
     * Обновляет состояние навигатора по переданному правилу качества
     *
     * @param dqRule
     */
    refreshViewByDqRule: function (dqRule) {
        var view = this.getView(),
            dqAttributePaths,
            attributePaths;

        if (!dqRule) {
            this.selectRootAttribute();
        } else {
            attributePaths = this.getNestedAttributePaths(this.getSelectedAttribute());
            dqAttributePaths = this.getDqRuleAttributePaths(dqRule);

            // если в правило качества использует хотя бы один атрибут из выбранных -> перефильтровываем список dq
            // иначе выделяем корневой атрибут
            if (Ext.Array.intersect(attributePaths, dqAttributePaths).length) {
                this.filterDqRulesByAttribute(this.getSelectedAttribute());
            } else {
                view.attributeTree.suspendEvent('select');
                this.selectRootAttribute();
                view.attributeTree.resumeEvent('select');

                this.filterDqRulesByAttribute(this.getSelectedAttribute());
            }

            this.highlightDqRuleAttributes(dqRule);
        }
    },

    /**
     * Возвращает фильтр по типу фильтрации
     *
     * @returns {*}
     */
    createFilterBySpecial: function () {
        var view = this.getView(),
            specialFilterType = view.getSpecialFilterType(),
            filter;

        switch (specialFilterType) {
            case Unidata.view.admin.entity.metarecord.dq.dqnavigation.DqNavigation.filterType.NONSPECIAL:
                filter = new Ext.util.Filter({
                    property: 'special',
                    value: false
                });
                break;

            case Unidata.view.admin.entity.metarecord.dq.dqnavigation.DqNavigation.filterType.SPECIAL:
                filter = new Ext.util.Filter({
                    property: 'special',
                    value: true
                });
                break;

            case Unidata.view.admin.entity.metarecord.dq.dqnavigation.DqNavigation.filterType.NONE:
                filter = new Ext.util.Filter({
                    filterFn: function () {
                        return true;
                    }
                });
                break;
        }

        return filter;
    },

    /**
     * Возвращает фильтр по данным введеным пользователем
     */
    createUserFilter: function () {
        var filter;

        filter = new Ext.util.Filter({
            filterFn: function (record) {
                var viewModel = this.getViewModel(),
                    filterData = viewModel.get('filterData') || {},
                    keys = Ext.Object.getKeys(filterData),
                    result;

                if (keys.length === 0) {
                    return true;
                }

                result = Ext.Array.every(keys, function (key) {
                    if (String(record.get(key)).indexOf(filterData[key]) !== -1) {
                        return true;
                    }
                });

                return result;
            }.bind(this)
        });

        return filter;
    },

    /**
     * Обрабатывает изменение типа фильтрации
     */
    updateSpecialFilterType: function () {
        var viewModel = this.getViewModel(),
            store = viewModel.getStore('filteredDqRules');

        store.getFilters().remove(this.filterDqBySpecial);

        this.filterDqBySpecial = this.createFilterBySpecial();

        store.addFilter(this.filterDqBySpecial);
    },

    /**
     * Обработчик смены readOnly
     */
    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel();

        viewModel.set('readOnly', readOnly);

        this.setReadOnlyComponentState(readOnly);

        if (this.getSelectedAttribute()) {
            this.updateDqRuleDraggable();
        }
    },

    setReadOnlyComponentState: function () {
        var view = this.getView(),
            items = view.items;

        if (!items.isMixedCollection) {
            return;
        }
    },

    onDqListSelectionChange: function (cmp, selected) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            dqTestMode = view.getDqTestMode();

        if (!dqTestMode) {
            view.fireEvent('dqrulegridselectionchange', cmp, selected);

            this.highlightDqRuleAttributes(selected[0]);
        } else {
            view.fireEvent('dqrulegridselectionchange', cmp, []);
        }

        viewModel.set('selectedCount', selected.length);
    },

    onAttributeTreeSelectionChange: function (cmp, selected) {
        var viewModel = this.getViewModel(),
            node = null;

        if (selected.length) {
            node = selected[0];
        }

        viewModel.set('attributeTreeNode', node);
    },

    onAttributeTreeBeforeDeselect: function () {
        var viewModel = this.getViewModel();

        // находясь в режиме тестирования правил качества нельзя переключаться между атрибутами т.к. список правил качества фильтруется
        // по выбранному атрибуту
        if (viewModel.get('dqTestMode')) {
            return false;
        }
    },

    onAttributeTreeNodeSelect: function (sm, node) {
        var selectedDqRule = this.getSelectedDqRule();

        this.filterDqRulesByAttribute(node);

        if (!selectedDqRule || this.isDqRuleFiltered(selectedDqRule)) {
            this.resetDqRuleSelection();
        }

        this.updateDqRuleDraggable();
    },

    /**
     * Обработчик клика по кнопке создания нового правила качества
     */
    onCreateDqButtonClick: function () {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            store = metaRecord.dataQualityRules(),
            record,
            origins;

        record = Ext.create('Unidata.model.dataquality.DqRule', {
            name: this.getNewDqName(),
            special: false,
            active: true,
            order: this.getNewDqOrder()
        });

        origins = Ext.create('Unidata.model.dataquality.DqOrigins', {
            all: false
        });

        record.setOrigins(origins);

        this.selectRootAttribute();

        store.add(record);

        this.resetUserFilter();
        view.dqList.setSelection(record);

        // проматываем до выбраного рекорда
        view.dqList.ensureVisible(record, {
            highlight: true
        });
    },

    /**
     * Обработка клика по кнопке включения режима выбора правил качества для тестирования
     */
    onTestDqModeButtonClick: function () {
        var view = this.getView(),
            dqTestMode = view.getDqTestMode();

        view.setDqTestMode(!dqTestMode);
    },

    showDqTestWizard: function () {
        var view = this.getView(),
            dqRuleNames = [],
            wnd,
            selection,
            sm;

        sm = view.dqList.getSelectionModel();

        if (!sm.getCount()) {
            return;
        }

        selection = sm.getSelection();

        Ext.Array.each(selection, function (item) {
            dqRuleNames.push(item.get('name'));
        });

        wnd = Ext.create('Unidata.view.admin.entity.metarecord.dq.testwizard.DqTestWizardWnd', {
            metaRecord: view.getMetaRecord(),
            dqRules: dqRuleNames,
            width: 600,
            height: 300
        });

        wnd.show();
    },

    /**
     * Возвращает позицию для нового правила качества
     *
     * @returns {number}
     */
    getNewDqOrder: function () {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            store = metaRecord.dataQualityRules(),
            no = 1;

        store.each(function (record) {
            if (!record.get('special') && record.get('order') >= no) {
                no = record.get('order') + 1;
            }
        });

        return no;
    },

    /**
     * Возвращает имя нового правила
     *
     * @returns {*}
     */
    getNewDqName: function () {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            store = metaRecord.dataQualityRules(),
            count = 1,
            names = [],
            name;

        store.each(function (record) {
            names.push(record.get('name'));
        });

        do {
            name = 'DataQualityRule' + count;
            count++;
        } while (Ext.Array.contains(names, name));

        return name;
    },

    onDeleteDqButtonClick: function (view, rowIndex, colIndex, item, e, record) {
        this.showPrompt(
            Unidata.i18n.t('admin.duplicates>removingRule'),
            Unidata.i18n.t('admin.duplicates>confirmRemoveRule'),
            this.onDeleteDqConfirm.bind(this, record), this, null
        );
    },

    onDeleteDqConfirm: function (record) {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            store = metaRecord.dataQualityRules(),
            index = store.indexOf(record),
            recordsToUpdate = store.getRange(index + 1);

        store.remove(record);

        recordsToUpdate.forEach(function (r) {
            r.set('order', r.get('order') - 1);
        });
    },

    onDataQualityDrop: function () {
        var view = this.getView(),
            grid = view.dqList,
            store = grid.getStore();

        store.each(function (record) {
            var index = store.indexOf(record);

            record.set('order', index);
        });
    },

    /**
     * Обработчик события завершения построения дерева атрибутов
     */
    onAttributeTreeDataComplete: function () {
        var view = this.getView(),
            metaRecord = view.getMetaRecord();

        // дерево атрибутов отвечает datacomplete но из-за биндинга туда могли передать null
        if (!metaRecord) {
            return;
        }

        setTimeout(function () {
            this.selectRootAttribute();
        }.bind(this), 300);
    },

    onAttributeTreeBeforeRender: function () {
        var view = this.getView();

        view.attributeComboBox.setAttributeTree(view.attributeTree);
    },

    /**
     * Обрабатывает событие изменения локальных фильтров пользователя
     *
     * @param cmp
     * @param value
     */
    onDqListFilterChange: function (cmp, value) {
        var viewModel = this.getViewModel(),
            store = viewModel.getStore('filteredDqRules'),
            filterData = viewModel.get('filterData') || {},
            filter = {},
            storeFilters = store.getFilters(),
            dataIndex = cmp.ownerCt.dataIndex;

        if (Ext.isEmpty(value)) {
            delete filterData[dataIndex];
        } else {
            filter[dataIndex] = value;

            filterData = Ext.apply(filterData, filter);
        }

        viewModel.set('filterData', filterData);

        // переприменяем фильтр
        storeFilters.remove(this.userFilter);
        storeFilters.add(this.userFilter);

        this.updateDqRuleDraggable();
    },

    /**
     * Обновить возможность задавать порядок исполнения правил качества
     */
    updateDqRuleDraggable: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            readOnly = view.getReadOnly(),
            node = this.getSelectedAttribute(),
            filterData = viewModel.get('filterData') || {},
            keys = Ext.Object.getKeys(filterData),
            draggable = true;

        // режим только чтения
        if (readOnly) {
            draggable = false;
        }

        // выбрана не корневая нода
        if (node && !node.isRoot()) {
            draggable = false;
        }

        // заданы фильтры пользователя
        if (keys.length) {
            draggable = false;
        }

        this.setDqRuleDraggable(draggable);
    },

    /**
     * Сбрасывает пользовательские фильтры
     */
    resetUserFilter: function () {
        var view = this.getView(),
            columnMgr = view.dqList.getColumnManager(),
            columns = columnMgr.getColumns();

        Ext.Array.each(columns, function (column) {
            var plugin = column.getPlugin('headerSwitcherPlugin');

            if (!plugin) {
                return;
            }

            column.items.each(function (item) {
                item.reset();
            });

            plugin.hideItems();
        });
    },

    onShowDqTestWizardButtonClick: function () {
        this.showDqTestWizard();
    },

    updateDqTestMode: function (dqTestMode) {
        var viewModel = this.getViewModel();

        this.updateDqTestModeComponentState();

        viewModel.set('dqTestMode', dqTestMode);
    },

    /**
     * Обновляет состояние UI согласно настройкам dqTestMode
     */
    updateDqTestModeComponentState: function () {
        var view = this.getView(),
            dqTestMode = view.getDqTestMode(),
            headerCt,
            checkColumn,
            sm;

        if (view.dqList) {
            sm = view.dqList.getSelectionModel();

            sm.deselectAll();
            headerCt = view.dqList.headerCt;
            checkColumn = headerCt.down('[isCheckerHd]');

            checkColumn.setHidden(!dqTestMode);
        }
    },

    onDqNavigationRender: function () {
        this.updateDqTestModeComponentState();
    },

    refreshAttributeTree: function () {
        var view = this.getView();

        view.attributeTree.refreshMetaRecordTree();
    }
});
