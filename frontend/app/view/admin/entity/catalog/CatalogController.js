/**
 * Контроллер компонента для управления каталогом реестров/справочников
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-15
 */
Ext.define('Unidata.view.admin.entity.catalog.CatalogController', {

    extend: 'Ext.app.ViewController',

    requires: [
        'Unidata.view.admin.entity.catalog.menu.Menu'
    ],

    alias: 'controller.admin.entity.catalog',

    config: {
        maxDepth: 3
    },

    privates: {

        /**
         * Хедеры, необходимые для корректной работы REST API
         */
        AJAX_HEADERS: {
            'Accept':       'application/json',
            'Content-Type': 'application/json'
        }
    },

    /**
     * Панелька с деревом каталога
     *
     * @type {Ext.tree.Panel}
     */
    catalogTree: null,

    /**
     * Хранилище дерева каталога
     *
     * @type {Ext.data.TreeStore}
     */
    catalogTreeStore: null,

    /**
     * Меню по правой кнопке
     *
     * @type {Unidata.view.admin.entity.catalog.menu.Menu}
     */
    menu: null,

    init: function () {

        var me = this,
            viewModel = me.getViewModel(),
            view,
            rights,
            menu;

        me.callParent(arguments);

        view = me.getView();

        rights = {
            createAllowed: me.userHasRight('create'),
            editAllowed:   me.userHasRight('update'),
            deleteAllowed: me.userHasRight('delete')
        };

        me.catalogTree      = me.lookupReference('catalogTree');
        me.catalogTreeStore = me.getStore('catalogStore');

        me.menu = menu = Ext.widget({
            xtype: 'admin.entity.catalog.menu',
            listeners: {
                clickAdd:    this.createItem,
                clickEdit:   this.editItem,
                clickRemove: this.removeItem,
                scope:       this
            }
        });

        menu.autoFocus = true;
        menu.updateRights(rights);

        viewModel.set(rights);

        view.on('beforedestroy', me.viewBeforeDestroy, me);
    },

    viewBeforeDestroy: function () {
        if (this.menu) {
            this.menu.destroy();
        }
    },

    /**
     * проверяем на наличие прав у пользователя
     *
     * @param {String} action
     *
     * @returns {boolean}
     */
    userHasRight: function (action) {
        return Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', action);
    },

    /**
     * проверяем на наличие нескольких прав у пользователя
     *
     * @param {String} action
     *
     * @returns {boolean}
     */
    userHasAnyRights: function (actions) {
        return Unidata.Config.userHasAnyRights('ADMIN_DATA_MANAGEMENT', actions);
    },

    /**
     * Создание нового элемента каталога
     *
     * @param {Unidata.model.entity.Catalog} parentRecord
     */
    createItem: function (parentRecord) {

        var me = this,
            newItem = new Unidata.model.entity.Catalog({
                children: [],
                expandable: false
            });

        parentRecord.appendChild(newItem);
        parentRecord.set('expandable', true);
        parentRecord.expand();

        // me.editItem(newItem, 0); // есть бага с последней строчкой

        me.markAsDirtyCatalog(true);

    },

    /**
     * Редактирование элемента каталога
     *
     * @param {Unidata.model.entity.Catalog} record
     * @param {Number} index
     */
    editItem: function (record, index) {

        var tree = this.catalogTree;

        tree.getPlugin('cellediting').startEdit(record, tree.columns[index === undefined ? 1 : index]);
    },

    /**
     * Удаление элемента каталога
     *
     * @param {Unidata.model.entity.Catalog} record
     */
    removeItem: function (record) {

        var i,
            childNodes = record.childNodes;

        for (i = 0; i < childNodes.length; i++) {
            this.removeItem(childNodes[i]);
        }

        if (record.parentNode) {
            record.parentNode.removeChild(record);
        }

        this.markAsDirtyCatalog(true);
    },

    /**
     * Получение выбранной в текущий момент записи
     *
     * @returns {Unidata.model.entity.Catalog}
     */
    getSelectedNode: function () {

        var selection = this.catalogTree.getSelection();

        if (selection && selection.length) {
            return selection[0];
        }

        return false;

    },

    /**
     * Проверяет, является ли переданный элемент каталога корневым (серверным)/скрытым корневым (у дерева)
     *
     * @param record
     * @returns {Boolean}
     */
    isRoot: function (record) {
        return record.isRoot() || record.parentNode.isRoot();
    },

    /**
     * Проверяет, можно ли удалять переданный элемент каталога
     *
     * @param {Unidata.model.entity.Catalog} record
     * @returns {Boolean}
     */
    deleteRecordAllowed: function (record) {
        var view = this.getView(),
            draftMode = view.getDraftMode();

        // редактировать можно только в режиме черновика
        if (!draftMode) {
            return false;
        }

        // корневой элемент, нельзя удалять
        if (this.isRoot(record)) {
            return false;
        }

        return this.getViewModel().get('deleteAllowed');
    },

    /**
     * Коллбэк, вызывается при рендеринге дерева столбца экшенов
     *
     * @param view
     * @param rowIdx
     * @param colIdx
     * @param item
     * @param {Unidata.model.entity.Catalog} record
     * @returns {Boolean}
     */
    deleteActionIsDisabled: function (view, rowIdx, colIdx, item, record) {
        return !this.deleteRecordAllowed(record);
    },

    /**
     * Помечает модель, что были изменения в дереве
     *
     * @param {Boolean} dirty
     */
    markAsDirtyCatalog: function (dirty) {
        this.getViewModel().set('dirtyCatalog', dirty);
    },

    markAsDirtyMetaModelName: function (dirty) {
        this.getViewModel().set('dirtyMetaModelName', dirty);
    },

    isCatalogDirty: function () {
        return Boolean(this.getViewModel().get('dirtyCatalog'));
    },

    isMetaModelNameDirty: function () {
        return Boolean(this.getViewModel().get('dirtyMetaModelName'));
    },

    /**
     * Обработчик события клика по кнопке добавления элемента каталога
     */
    onButtonAddClick: function () {
        var record = this.getSelectedNode();

        if (record) {
            this.createItem(record);
        }
    },

    /**
     * Обработчик события клика по кнопке сохранения
     */
    onButtonSaveClick: function () {
        var me = this,
            view = this.getView(),
            promise,
            promises;

        promises = [
            this.saveMetaModelName(),
            this.saveCatalog()
        ];

        promise = Ext.Deferred.all(promises);

        view.setLoading(true);

        promise
            .then(
                function () {
                    view.setLoading(false);

                    me.markAsDirtyCatalog(false);
                    me.markAsDirtyMetaModelName(false);
                },
                function () {
                    view.setLoading(false);
                }
            )
            .done();
    },

    /**
     * Обработчик события клика по кнопке удаления элемента каталога
     *
     * @param {Ext.tree.Panel} panel
     * @param {Number} rowIndex
     */
    onButtonDeleteClick: function (panel, rowIndex) {
        var view = this.getView(),
            catalogTree = view.lookupReference('catalogTree'),
            record = panel.getStore().getAt(rowIndex),
            editorPlugin = catalogTree.getPlugin('cellediting');

        if (record) {
            editorPlugin.cancelEdit();

            this.removeItem(record);
        }
    },

    /**
     * Вызывается при клике правой кнопкой по элементу каталога
     *
     * @param {Ext.tree.Panel} panel
     * @param {Unidata.model.entity.Catalog} record
     * @param {Node} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     */
    onItemContextMenu: function (panel, record, item, index, e) {
        var menu = this.menu,
            view = this.getView(),
            draftMode = view.getDraftMode();

        e.preventDefault();

        // меню доступно только в режиме черновика
        if (!draftMode) {
            return;
        }

        menu.setRecord(record);

        menu.updateRights({
            deleteAllowed: this.deleteRecordAllowed(record),
            createAllowed: this.getViewModel().get('creationAllowedInSelection')
        });

        menu.showAt([
            e.getX() + 5,
            e.getY() + 5
        ]);

    },

    /**
     * Вызывается при валидации записи после её редактирования
     *
     * @param {Ext.form.field.Text} editor
     * @param {Object} context
     * @returns {Boolean}
     */
    onCellValidate: function () {
        return true;
    },

    /**
     * Вызывается перед редактированием ячейки в гриде
     *
     * @param {Ext.form.field.Text} editor
     * @param {Object} context
     * @returns {Boolean}
     */
    onCellBeforeEdit: function (editor, context) {
        var view = this.getView(),
            draftMode = view.getDraftMode(),
            field = context.field,
            record = context.record;

        // редактировать можно только в режиме черновика
        if (!draftMode) {
            return false;
        }

        if (record.phantom && !this.getViewModel().get('createAllowed')) {
            return false;
        }

        if (!record.phantom && !this.getViewModel().get('editAllowed')) {
            return false;
        }

        if (this.isRoot(record) && field === 'name') {
            return false;
        }

        return true;

    },

    /**
     * Проверка корректности каталога и вывод ошибок валидации
     */
    updateErrors: function () {

        var view = this.catalogTree.getView(),
            errorCls = 'un-catalog-tree-cell__error',
            groupNamesMap = {},
            hasErrors = false;

        this.catalogTreeStore.each(function (record) {
            if (!groupNamesMap.hasOwnProperty(record.get('groupName'))) {
                groupNamesMap[record.get('groupName')] = [];
            }
            groupNamesMap[record.get('groupName')].push(record);
        });

        this.catalogTreeStore.each(function (record) {

            var clone = record.clone(),
                row = Ext.get(view.getRow(record)),
                nameCell = row.query('.x-grid-cell:nth-child(1)', false)[0],
                titleCell = row.query('.x-grid-cell:nth-child(2)', false)[0],
                errors,
                nameErrors = [],
                titleErrors = [],
                recordsWithGroupName = groupNamesMap[record.get('groupName')];

            function pushErrors (target, errorsArray) {
                var i;

                for (i = 0; i < errorsArray.length; i++) {
                    target.push(errorsArray[i].getMessage());
                }
            }

            nameCell.removeCls(errorCls);
            titleCell.removeCls(errorCls);

            nameCell.set({
                'data-errorqtip': ''
            });

            titleCell.set({
                'data-errorqtip': ''
            });

            clone.parentNode = record.parentNode;

            errors = clone.validate();

            if (errors.isValid() && recordsWithGroupName.length === 1) {
                return;
            }

            hasErrors = true;

            pushErrors(nameErrors, errors.getByField('name'));
            pushErrors(nameErrors, errors.getByField('groupName'));
            pushErrors(titleErrors, errors.getByField('displayName'));

            if (recordsWithGroupName.length > 1) {
                nameErrors.push(Unidata.i18n.t('admin.metamodel>groupNameShouldUnique'));
            }

            if (nameErrors.length) {
                nameCell.addCls(errorCls);
                nameCell.set({
                    'data-errorqtip': nameErrors.join('\n')
                });
            }

            titleErrors = errors.getByField('displayName');

            if (titleErrors.length) {
                titleCell.addCls(errorCls);
                titleCell.set({
                    'data-errorqtip': Ext.Array.pluck(titleErrors, 'message').join('\n')
                });
            }

        });

        this.getViewModel().set('hasErrors', hasErrors);

    },

    /**
     * Вызывается после редактирования ячейки в гриде
     */
    onCellEdit: function (editor) {
        var store = editor.grid.getStore(),

            dirty;

        if (this.isCatalogDirty()) {
            dirty = true;
        } else {
            // check if some model.entity.Catalog records is dirty
            dirty = Ext.Array.some(store.getRange(), function (record) {
                return record.dirty;
            });
        }

        this.updateErrors();
        this.markAsDirtyCatalog(dirty);
    },

    /**
     * Вызывается после выбора строки в гриде
     */
    onItemSelect: function (treepanel, record) {
        this.getViewModel().set('selection', record);
    },

    /**
     * Вызывается после снятия выделения со строки в гриде
     */
    onItemDeselect: function () {
        this.getViewModel().set('itemSelected', false);
    },

    reloadCatalogStore: function () {
        var me = this;

        if (this.catalogTreeStore) {
            this.catalogTreeStore.reload();

            me.markAsDirtyCatalog(false);
        }
    },

    loadMetaModelData: function () {
        var me = this,
            view = this.getView(),
            metaModelNameField = this.lookupReference('metaModelName'),
            metaModelVersionField = this.lookupReference('metaModelVersion'),
            promise;

        promise = Unidata.util.api.MetaModel.getMetaModelData({
            draft: view.getDraftMode()
        });

        view.setLoading(true);

        promise
            .then(function (data) {
                metaModelNameField.setValue(data.name);
                metaModelVersionField.setValue(data.version);

                me.markAsDirtyMetaModelName(false);
            })
            .otherwise(function () {
            })
            .always(function () {
                view.setLoading(false);
            })
            .done();
    },

    saveMetaModelName: function () {
        var view = this.getView(),
            metaModelNameField = this.lookupReference('metaModelName'),
            promise,
            name;

        // резолвим промис если сохранение не требуется
        if (!this.isMetaModelNameDirty()) {
            return true;
        }

        name = metaModelNameField.getValue();

        promise = Unidata.util.api.MetaModel.saveMetaModelName(name, {
            draft: view.getDraftMode()
        });

        return promise;
    },

    saveCatalog: function () {
        var me = this,
            data = [],
            store = me.catalogTreeStore,
            proxy = store.getProxy(),
            url = proxy.getApi().update,
            root = store.getRootNode(),
            deferred;

        // резолвим промис если сохранение не требуется
        if (!this.isCatalogDirty()) {
            return true;
        }

        deferred = new Ext.Deferred();

        // собираем все данные по дереву
        // cascade используется вместо store.each т.к. если нода свернута тогда рекорда не будет в store
        root.cascadeBy(function (record) {
            if (record.isRoot()) {
                return;
            }

            data.push({
                groupName: record.getGroupName(),
                title: record.get('displayName')
            });
        });

        data = {
            groupNodes: data
        };

        // сохраняем
        Ext.Ajax.unidataRequest({
            url: url,
            method: 'PUT',
            jsonData: data,
            headers: me.AJAX_HEADERS,
            success: function () {
                me.fireViewEvent('needReloadCatalog');

                store.reload();

                me.getViewModel().set('selection', false);

                Unidata.showMessage(Unidata.i18n.t('admin.metamodel>catalogSavedSuccess'));

                deferred.resolve();
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;
    },

    onShowCatalog: function () {
        this.loadMetaModelData();
    },

    updateDraftMode: function (draftMode) {
        var viewModel = this.getViewModel();

        viewModel.set('draftMode', draftMode);

        this.refreshCatalogData();
    },

    refreshCatalogData: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            store = viewModel.getStore('catalogStore'),
            draftMode = view.getDraftMode(),
            proxy;

        // обновляем дерево
        if (store) {
            proxy = store.getProxy();
            proxy.setDraftMode(draftMode);

            this.reloadCatalogStore();
        }

        // обновляем инфорамцию по версии / наименовании метамодели
        if (view.rendered) {
            this.loadMetaModelData();
        }
    },

    onMetaModelNameChange: function () {
        this.markAsDirtyMetaModelName(true);
    }

});
