/**
 * @author Ivan Marshalkin
 * @date 2017-02-03
 *
 * Добавлен функционал подсветки несогласованных прав
 * @author Sergey Shishigin
 * @date 2017-03-06
 */
Ext.define('Unidata.view.admin.security.role.RoleEditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.admin.security.roleedit',

    /**
     * @typedef {Object} RightWarning Предупреждение о несогласованных правах
     * @param securedResourceName {String} Имя ресурса безопасности
     * @param rights {String[]} Права на которые распространяется предупреждение (create, read, update, delete)
     * @param relatedSecuredResourceName {String} Имя ресурса безопасности в связи с которым возникает несогласованность
     * @param message {String} Предупреждающее сообщение
     */

    specialNode: null,
    groupedNode: null,
    unGroupedNode: null,
    /**
     * Граф мета-зависимостей
     * @params {Unidata.model.entity.metadependency.MetaDependencyGraph}
     */
    metaDependencyGraph: null,
    /**
     * @param rightWarningMap {Object}
     * Ассоциативный массив, где ключами являются имена ресурсов,
     * а значениями ассоциативные массив ресурсов, которые влияют на данные ресурс и вызывают несогласованность
     *
     * Example:
     * {
     * "ContactType": {
     *   "Person": {
     *     "securedResource": "ContactType",
     *       "rights": "read",
     *       "relatedSecuredResource": "Person",
     *       "message": "Права не согласованы с реестром Person. Имеется ссылка."
     *    }
     * }
     */
    rightWarningMap: {},

    init: function () {
        var view = this.getView(),
            treePanelView;

        this.callParent(arguments);

        // configure row class getter
        treePanelView = view.treePanel.getView();
        treePanelView.getRowClass = this.getTreePanelGridRowClass.bind(this);
    },

    initRolePropertyEditor: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            currentRole = viewModel.get('currentRole'),
            rolePropertyPanel = view.lookupReference('rolePropertyPanel');

        currentRole.properties().each(function (property) {
            rolePropertyPanel.addPropertyItem(property.get('name'), property.get('displayName'), property.get('value'));
        });

        rolePropertyPanel.on('propertychange', this.onRolePropertyChange, this);

        viewModel.set('propertiesIsVisible', currentRole.properties().count());
    },

    /**
     * Обработчик загрузки стора с каталогом
     */
    onCatalogStoreLoad: function (store, records, successful) {
        var viewModel = this.getViewModel();

        if (successful) {
            viewModel.set('catalogStoreLoaded', true);
        }

        this.buildGroupedTreeStore();
    },

    /**
     * Обработчик загрузки стора со список всех ресусов безопасности заведенных в системе
     */
    onSecuredResourcesStoreLoad: function (store, records, successful) {
        var viewModel = this.getViewModel();

        if (successful) {
            viewModel.set('securedResourceStoreLoaded', true);
        }

        this.buildGroupedTreeStore();
    },

    /**
     * Обработка загрузки стора со списокм всех меток безопасности в системе
     */
    onSecurityLabelsStoreLoad: function (store, securityLabels, successful) {
        if (successful) {
            this.initSecurityLabels(securityLabels);
        }
    },

    /**
     *
     * @param securityLabels
     */
    initSecurityLabels: function (securityLabels) {
        var viewModel = this.getViewModel(),
            currentRole = viewModel.get('currentRole'),
            currentRoleSecurityLabelsStore = currentRole.securityLabels(),
            slAttributesContainer = this.lookupReference('securityLabelAttributes'),
            securityAttributeEditor;

        currentRoleSecurityLabelsStore.each(function (sl) {
            sl.set('checked', true);
        });

        // вызываем метод, что бы дополнить метками без атрибутов, если это необходимо
        this.prepareCurrentRoleSecurityLabels(currentRole);

        viewModel.set('securityLabelsStoreLoaded', true);

        currentRoleSecurityLabelsStore.remoteFilter = false;

        // перебираем список всех меток безопасности
        Ext.Array.each(securityLabels, function (securityLabel) {
            var securityLabelName = securityLabel.get('name'),
                securityLabelsGroup = [];

            // группируем метки по имени
            currentRoleSecurityLabelsStore.each(function (securityLabel) {
                if (securityLabel.get('name') === securityLabelName && securityLabel.attributes().getCount()) {
                    securityLabelsGroup.push(securityLabel);
                }
            });

            // создаём компонент для редактирования
            if (!securityLabelsGroup.length) {
                 securityAttributeEditor = Ext.create('Unidata.view.component.role.SecurityAttribute', {
                    securityLabelSample: securityLabel,
                    securityLabelsGroup: [],
                    securityLabels: currentRoleSecurityLabelsStore
                });
            } else {
                securityAttributeEditor = Ext.create('Unidata.view.component.role.SecurityAttribute', {
                    securityLabelsGroup: securityLabelsGroup,
                    securityLabels: currentRoleSecurityLabelsStore
                });
            }

            slAttributesContainer.add(securityAttributeEditor);
        });
    },

    /**
     * Возвращает массив имен всех ресурсов безопасности
     *
     * @returns {*}
     */
    getAllSecuredResourceNames: function () {
        return Ext.Array.unique(Ext.Object.getKeys(this.getAllSecuredResources()));
    },

    /**
     * Возвращает хеш всех ресурсов безопасности
     *
     * key => value
     *
     * @returns {{}}
     */
    getAllSecuredResources: function () {
        var viewModel            = this.getViewModel(),
            catalogStore         = viewModel.getStore('catalogStore'),
            securedResourceStore = viewModel.getStore('securedResourceStore'),
            resources            = {};

        securedResourceStore.getRootNode().cascadeBy(function (node) {
            if (!node.isRoot()) {
                resources[node.get('name')] = node;
            }
        });

        return resources;
    },

    /**
     * Возвращает ресурс безопасности по его кодовому имени
     *
     * @param name
     * @returns {*}
     */
    getSecuredResourceByName: function (name) {
        var allSecuredResources = this.getAllSecuredResources(),
            result              = null;

        if (allSecuredResources[name]) {
            result = allSecuredResources[name];
        }

        return result;
    },

    /**
     * Заполняет правами роль по всем существующим в системе ресурсам безопасности
     *
     * @param role
     */
    prepareCurrentRoleRights: function (role) {
        var me                   = this,
            viewModel            = this.getViewModel(),
            securedResourceStore = viewModel.getStore('securedResourceStore'),
            securedResourceNames = this.getAllSecuredResourceNames(),
            securedResources     = this.getAllSecuredResources(),
            securedResource;

        Ext.Array.each(securedResourceNames, function (securedResourceName) {
            var right = me.findRight(role.rights(), securedResourceName);

            if (!right) {
                right = Ext.create('Unidata.model.user.Right');
                securedResource = securedResources[securedResourceName];

                // TODO: сделать более аккуратное копирование
                right.setSecuredResource(Ext.create('Unidata.model.user.SecuredResource', securedResource.getData({persist: true})));
                right.commit();

                role.rights().add(right);
            }
        });
    },

    /**
     * Строим стор для отображения пользователю
     */
    buildGroupedTreeStore: function () {
        var view = this.getView(),
            viewModel            = this.getViewModel(),
            catalogStore         = viewModel.getStore('catalogStore'),
            securedResourceStore = viewModel.getStore('securedResourceStore'),
            treeGroupedStore     = viewModel.getStore('treeGroupedStore'),
            rootNode             = this.getGroupedTreeStoreRootNode();

        if (!catalogStore.isLoaded() || !securedResourceStore.isLoaded()) {
            return;
        }

        view.setStatus(Unidata.StatusConstant.LOADING);

        // заполняем группу со специальными правами
        this.createSpecialGroupNode();
        this.fillSpecialGroupNode();

        // реестры / справочники
        this.createGroupedGroupNode();
        this.fillGroupedGroupNode();

        // остальные ресурсы
        this.createUnGroupedGroupNode();
        this.fillUnGroupedGroupNode();

        treeGroupedStore.commitChanges();

        this.addSpecialGroupListener();

        rootNode.expand();

        this.sortSecuredResourcesTreeStore();

        treeGroupedStore.on('update', this.onTreeGroupedStoreUpdate, this);
        this.loadMetaDependencyGraphAndHighlightRightWarnings();
    },

    /**
     * Заполняет метками безопасности роль по всем существующим в системе меткам безопасности
     *
     * @param currentRole
     */
    prepareCurrentRoleSecurityLabels: function (role) {
        var me                = this,
            viewModel         = this.getViewModel(),
            allSecurityLabels = viewModel.getStore('securityLabels');

        allSecurityLabels.each(function (securityLabel) {
            var roleSecurityLabel = me.findSecurityLabelWithoutAttributes(role.securityLabels(), securityLabel.get('name'));

            if (!roleSecurityLabel) {
                roleSecurityLabel = Ext.create('Unidata.model.user.SecurityLabelRole', securityLabel.getData());

                roleSecurityLabel.set('checked', false);
                roleSecurityLabel.commit(false, ['checked']);

                role.securityLabels().add(roleSecurityLabel);
            }
        });
    },

    /**
     * Проставляем галочку что метка безопасности выбрана
     *
     * @param role
     */
    fillRoleSecurityLabels: function (role) {
        role.securityLabels().each(function (securityLabel) {
            if (securityLabel.get('checked') === undefined) {
                securityLabel.set('checked', true);
                securityLabel.commit(false, ['checked']);
            }
        });
    },

    /**
     * Возвращает корневую ноду сгруппированного стора
     *
     * @returns {*}
     */
    getGroupedTreeStoreRootNode: function () {
        var viewModel        = this.getViewModel(),
            treeGroupedStore = viewModel.getStore('treeGroupedStore');

        return treeGroupedStore.getRootNode();
    },

    /**
     * Саздает ноду 'Специальные права'
     */
    createSpecialGroupNode: function () {
        var rootNode = this.getGroupedTreeStoreRootNode(),
            node;

        node = rootNode.appendChild({
            leaf: false,
            nodeType: 'GROUP_NODE',
            displayName: Unidata.i18n.t('admin.security>specialGroup')
        });

        this.specialNode = node;
    },

    /**
     * Саздает ноду 'Группированные права'
     */
    createGroupedGroupNode: function () {
        var rootNode = this.getGroupedTreeStoreRootNode(),
            node;

        node = rootNode.appendChild({
            leaf: false,
            nodeType: 'GROUP_NODE',
            displayName: Unidata.i18n.t('admin.security>groupedGroup')
        });

        this.groupedNode = node;
    },

    /**
     * Саздает ноду 'Не группированные права'
     */
    createUnGroupedGroupNode: function () {
        var rootNode = this.getGroupedTreeStoreRootNode(),
            node;

        node = rootNode.appendChild({
            leaf: false,
            nodeType: 'GROUP_NODE',
            displayName: Unidata.i18n.t('admin.security>ungroupedGroup')
        });

        this.unGroupedNode = node;
    },

    /**
     * Подписываемся для спец групп
     */
    addSpecialGroupListener: function () {
        this.addSpecialGroupBeforeexpandListener(this.specialNode);
        this.addSpecialGroupBeforeexpandListener(this.groupedNode);
        this.addSpecialGroupBeforeexpandListener(this.unGroupedNode);
    },

    /**
     * Добавляет подписку на beforeexpand для спец групп
     *
     * @param node
     */
    addSpecialGroupBeforeexpandListener: function (node) {
        node.on('beforeexpand', function (node) {
            // если дочерних элементов нет - ноду не раскрываем
            // иначе будет не понятный запрос на сервер
            if (!node.childNodes.length) {
                return false;
            }
        } , this);
    },

    /**
     * Заполняет ноду 'Специальные права'
     */
    fillSpecialGroupNode: function () {
        var me                   = this,
            viewModel            = this.getViewModel(),
            catalogStore         = viewModel.getStore('catalogStore'),
            securedResourceStore = viewModel.getStore('securedResourceStore'),
            resources            = securedResourceStore.getRange();

        Ext.Array.each(resources, function (securedResource) {
            if (securedResource.get('isSystemResource')) {
                me.specialNode.appendChild({
                    leaf: true,
                    nodeType: 'SECURED_RESOURCE_NODE',
                    displayName: securedResource.get('displayName'),
                    record: securedResource
                });
            }
        });
    },

    /**
     * Заполняет ноду 'Группированные права'
     */
    fillGroupedGroupNode: function () {
        var me           = this,
            viewModel    = this.getViewModel(),
            catalogStore = viewModel.getStore('catalogStore');

        // создает поддерево с реестра / справочника с вложенными атрибутами любой вложенности
        function createEntitySubTree (treeNode, securedResource) {
            var newNode,
                nodeType,
                iconCls,
                isEntity;

            nodeType = treeNode.get('nodeType');

            if (securedResource) {
                if (nodeType === 'CATALOG_NODE' || nodeType === 'GROUP_NODE') {
                    isEntity = true;
                    iconCls = 'x-hidden';
                } else {
                    isEntity = false;
                    iconCls = '';
                }

                newNode = {
                    leaf: !securedResource.hasChildNodes(),
                    nodeType: 'SECURED_RESOURCE_NODE',
                    isEntity: isEntity,
                    iconCls: iconCls,
                    displayName: securedResource.get('displayName'),
                    record: securedResource
                };

                newNode = me.appendChildToNode(treeNode, newNode);

                // обрабатываем атрибуты реестров / справочников
                if (securedResource.hasChildNodes()) {
                    Ext.Array.each(securedResource.childNodes, function (securedResourceChild) {
                        createEntitySubTree(newNode, securedResourceChild);
                    });
                }
            }
        }

        // создает дерево для каталогов
        function createCatalogTree (treeNode, catalogNodes) {
            Ext.Array.each(catalogNodes, function (catalogNode) {
                var newNode,
                    securedResource;

                // процессим группы
                if (!Ext.Array.contains(['Entity', 'LookupEntity'], catalogNode.get('type'))) {
                    newNode = {
                        leaf: false,
                        nodeType: 'CATALOG_NODE',
                        displayName: catalogNode.get('displayName')
                    };

                    newNode = me.appendChildToNode(treeNode, newNode);

                    if (catalogNode.hasChildNodes()) {
                        createCatalogTree(newNode, catalogNode.childNodes);
                    }
                    // обрабатываем реестры / справочники
                } else {
                    securedResource = me.getSecuredResourceByName(catalogNode.get('name'));

                    createEntitySubTree(treeNode, securedResource);
                }
            });
        }

        createCatalogTree(me.groupedNode, catalogStore.getRootNode().childNodes[0].childNodes);
    },

    /**
     * Заполняет ноду 'Не группированные права'
     */
    fillUnGroupedGroupNode: function () {
        var me                          = this,
            usedSecurityResourceNames   = [],
            allSecurityResources        = this.getAllSecuredResources(),
            allSecurityResourceNames    = this.getAllSecuredResourceNames(),
            unUsedSecurityResourceNames;

        this.specialNode.cascadeBy(function (node) {
            if (node.get('nodeType') === 'SECURED_RESOURCE_NODE') {
                usedSecurityResourceNames.push(node.get('record').get('name'));
            }
        });

        this.groupedNode.cascadeBy(function (node) {
            if (node.get('nodeType') === 'SECURED_RESOURCE_NODE') {
                usedSecurityResourceNames.push(node.get('record').get('name'));
            }
        });

        unUsedSecurityResourceNames = Ext.Array.difference(allSecurityResourceNames, usedSecurityResourceNames);

        if (!unUsedSecurityResourceNames.length) {
            me.unGroupedNode.set('visible', false);
        }

        Ext.Array.each(unUsedSecurityResourceNames, function (securedResourceName) {
            var securedResource = allSecurityResources[securedResourceName],
                isClassifier = securedResource.get('category') == 'CLASSIFIER';

            me.unGroupedNode.appendChild({
                leaf: true,
                nodeType: 'SECURED_RESOURCE_NODE',
                isEntity: isClassifier ? false : true,
                iconCls: isClassifier ? '' : 'x-hidden',
                displayName: securedResource.get('displayName'),
                record: securedResource
            });
        });
    },

    /**
     * Аппендит дочернюю ноду к ноде
     *
     * @param node
     * @param childNode
     * @returns {Node|XML}
     */
    appendChildToNode: function (node, childNode) {
        return node.appendChild(childNode);
    },

    /**
     * Формируем данные для securedResourcesTreeStore
     */
    prepareTreeGroupedStore: function (currentRole) {
        var me               = this,
            viewModel        = this.getViewModel(),
            treeGroupedStore = viewModel.getStore('treeGroupedStore'),
            root             = this.getGroupedTreeStoreRootNode();

        treeGroupedStore.suspendEvent('update');

        // сбрасываем на всякий случай права
        root.cascadeBy(function (node) {
            node.set('create', false);
            node.set('read', false);
            node.set('update', false);
            node.set('delete', false);
            node.set('full', false);
        });

        // в данный момент в дереве и в редактируемой роли должны быть полные перечни прав пользователя
        root.cascadeBy(function (node) {
            var right;

            if (node.get('nodeType') === 'SECURED_RESOURCE_NODE') {
                right = me.findRight(currentRole.rights(), node.get('record').get('name'));

                node.set('create', right.get('create'));
                node.set('read', right.get('read'));
                node.set('update', right.get('update'));
                node.set('delete', right.get('delete'));
                node.set('full', right.get('full'));
            }

        }, this);

        treeGroupedStore.resumeEvent('update');

        treeGroupedStore.commitChanges();
    },

    updateByUserRight: function (currentRole) {
        var canCreate = Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'create'),
            canWrite  = Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'update'),
            viewModel;

        viewModel = this.getViewModel();

        if (canCreate && currentRole.phantom) {
            canWrite = true;
        }

        viewModel.set('readOnly', !canWrite);
        viewModel.set('nameEditable', currentRole.phantom);
    },

    getCurrentRole: function () {
        var viewModel = this.getViewModel(),
            role;

        role = viewModel.get('currentRole');

        return role;
    },

    rejectRoleChanges: function (role) {
        role.rights().rejectChanges();
        role.securityLabels().rejectChanges();
    },

    isCurrentRoleDirty: function () {
        var currentRole = this.getCurrentRole(),
            isDirty = false,
            updatedRights,
            updatedSecurityLabels,
            updatedProperties;

        if (currentRole) {
            if (currentRole.phantom || currentRole.dirty) {
                isDirty = true;
            }

            updatedRights = currentRole.rights().getUpdatedRecords();
            updatedSecurityLabels = currentRole.securityLabels().getUpdatedRecords();
            updatedProperties = currentRole.properties().getUpdatedRecords();

            if (updatedRights.length || updatedSecurityLabels.length || updatedProperties.length) {
                isDirty = true;
            }
        }

        return isDirty;
    },

    onBeforeSelectRole: function (selectionModel, record) {
        var me    = this,
            title = Unidata.i18n.t('common:confirmation'),
            msg   = Unidata.i18n.t('admin.security>confirmLeaveUnsavedRole');

        function acceptRejectChanges () {
            me.rejectRoleChanges(me.getCurrentRole());
            selectionModel.select([record]);
        }

        // если текущая роль с изменениями запрашиваем разрешение на смену редактируемой роли
        if (this.isCurrentRoleDirty()) {

            Unidata.showPrompt(title, msg, acceptRejectChanges);

            return false;
        }
    },

    onSuccessRoleLoad: function (currentRole) {
        var panel     = this.lookupReference('rolePanel'),
            viewModel = this.getViewModel();

        panel.setHidden(false);

        this.fillRoleSecurityLabels(currentRole);

        viewModel.set('currentRole', currentRole);

        this.updateByUserRight(currentRole);

        // создаем в редактируемой роли права по всем существующим ресурсам
        this.prepareCurrentRoleRights(currentRole);
        // создаем в редактируемой роли метки безопасности по всем существующим меткам
        this.prepareCurrentRoleSecurityLabels(currentRole);
        // копируем права для отображения
        this.prepareTreeGroupedStore(currentRole);

        this.initRolePropertyEditor();

        this.lookupReference('treepanel').getView().refresh();
    },

    /**
     * Поиск прав в по resourceName
     */
    findRight: function (rights, resourceName) {
        var right = null,
            index;

        index = rights.findBy(function (right) {
            return right.getSecuredResource().get('name') === resourceName;
        });

        if (index !== -1) {
            right = rights.getAt(index);
        }

        return right;
    },

    /**
     * Поиск securedResource по resourceName
     *
     * @param store {Ext.data.TreeStore}
     * @param resourceName {String}
     * @return {Ext.data.TreeModel}
     */
    findSecuredResourceNode: function (store, resourceName) {
        var securedResource = null,
            index;

        index = store.findBy(function (treeItem) {
            var record = treeItem.get('record');

            if (!record) {
                return false;
            }

            return record.get('name') === resourceName;
        });

        if (index !== -1) {
            securedResource = store.getAt(index);
        }

        return securedResource;
    },

    /**
     * Поиск метки безопасности в по securityLabelName
     */
    findSecurityLabel: function (securityLabels, securityLabelName) {
        var securityLabel = null,
            index;

        index = securityLabels.findBy(function (securityLabel) {
            return securityLabel.get('name') === securityLabelName;
        });

        if (index !== -1) {
            securityLabel = securityLabels.getAt(index);
        }

        return securityLabel;
    },

    /**
     * Поиск метки безопасности без атрибутов в по securityLabelName
     */
    findSecurityLabelWithoutAttributes: function (securityLabels, securityLabelName) {
        var securityLabel = null,
            index;

        index = securityLabels.findBy(function (securityLabel) {
            return (securityLabel.get('name') === securityLabelName && !securityLabel.attributes().getCount());
        });

        if (index !== -1) {
            securityLabel = securityLabels.getAt(index);
        }

        return securityLabel;
    },

    /**
     * костыль для скрывания/показа кнопки-виджета в гриде
     * @param record
     */
    updateDeactivateButton: function (record) {
        var right = record.right,
            deactivateButton = record.deactivateButton;

        if (right && deactivateButton && record.get('depth') != 1) {
            deactivateButton.setHidden(
                !(right.get('create') || right.get('read') || right.get('update') || right.get('delete'))
            );
        }
    },

    /**
     * При обновлении данных в дереве - синхронизируем данные с оригинальной моделью прав
     */
    onTreeGroupedStoreUpdate: function (store, record)  {
        var me = this,
            viewModel = this.getViewModel(),
            currentRole = viewModel.get('currentRole'),
            copyFieldNames,
            securedResourceName,
            right;

        if (!currentRole) {
            return;
        }

        copyFieldNames = [
            'create',
            'read',
            'update',
            'delete',
            'full'
        ];

        if (record.get('nodeType') === 'SECURED_RESOURCE_NODE') {
            securedResourceName = record.get('record').get('name');

            right = me.findRight(currentRole.rights(), securedResourceName);

            right.beginEdit();

            Ext.Array.each(copyFieldNames, function (fieldName) {
                right.set(fieldName, record.get(fieldName));
            });

            right.endEdit();
        }
    },

    /**
     * Производит сортировку ресурсов в удобном для пользователя виде
     */
    sortSecuredResourcesTreeStore: function () {
        var viewModel        = this.getViewModel(),
            treeGroupedStore = viewModel.get('treeGroupedStore');

        treeGroupedStore.sorters.clear();

        treeGroupedStore.sort([
            // первый уровень сортировать не нужно
            {
                sorterFn: function (res1) {
                    if (res1.get('depth') === 1) {
                        return -1;
                    }

                    return 0;
                },
                direction: 'ASC'
            },
            // вверху располагаем группы
            {
                sorterFn: function (res1, res2) {
                    if (res1.get('nodeType') === 'CATALOG_NODE' && res2 === 'CATALOG_NODE') {
                        return 0;
                    }

                    if (res1.get('nodeType') === 'CATALOG_NODE') {
                        return -1;
                    }

                    if (res2.get('nodeType') === 'CATALOG_NODE') {
                        return 1;
                    }

                    return 0;
                },
                direction: 'ASC'
            },
            // остальное сортируем по алфавиту
            {
                property: 'displayName',
                direction: 'ASC'
            }
        ]);
    },

    onSaveClick: function () {
        var title = Unidata.i18n.t('admin.security>saveRole'),
            msg = Unidata.i18n.t('admin.security>confirmSaveRoleWithUnapprovedGroups'),
            rightWarningCount;

        rightWarningCount = this.getRightWarningCount();

        if (rightWarningCount > 0) {
            Unidata.showPrompt(title, msg, this.saveRole, this);
        } else {
            this.saveRole();
        }
    },

    saveRole: function () {
        var me = this,
            view = this.getView(),
            viewModel = this.getViewModel(),
            form = this.lookupReference('roleForm'),
            currentRole = viewModel.get('currentRole'),
            rolePropertyPanel = view.lookupReference('rolePropertyPanel'),
            associatedData,
            securityLabels,
            selectedSecurityLabels;

        if (form.isValid()) {
            associatedData = currentRole.getAssociatedData();

            for (var i in associatedData) {
                if (associatedData.hasOwnProperty(i)) {
                    currentRole.set(i, associatedData[i]);
                }
            }

            securityLabels = currentRole.get('securityLabels');
            selectedSecurityLabels = [];

            Ext.Array.each(securityLabels, function (item) {
                if (item.checked === true) {
                    selectedSecurityLabels.push(item);
                }
            });

            currentRole.set('securityLabels', selectedSecurityLabels);

            currentRole.setId(currentRole.get('name'));

            if (currentRole.isModified('name')) {
                currentRole.setId(currentRole.getModified('name'));
            }

            currentRole.set('type', 'USER_DEFINED');

            // фильтруем права - отправляются только те, которые мы изменили
            currentRole.getProxy().setWriter(Ext.create('Ext.data.writer.Json', {
                writeAllFields: true,
                writeRecordId: false,
                transform: function (data) {
                    var dirtyRights = [];

                    Ext.Array.each(data.rights, function (right) {
                        var securedResourceName = right.securedResource.name,
                            rightModel = me.findRight(currentRole.rights(), securedResourceName);

                        if (rightModel && rightModel['dirty']) {
                            dirtyRights.push(right);
                        }
                    });

                    data.rights = dirtyRights;

                    return data;
                }
            }));

            currentRole.save({
                success: function () {
                    me.showMessage(Unidata.i18n.t('admin.common>dataSaveSuccess'));

                    currentRole.securityLabels().each(function (record) {
                        record.commit();
                    });

                    currentRole.rights().each(function (record) {
                        record.commit();
                    });

                    viewModel.getStore('treeGroupedStore').commitChanges();

                    me.updateByUserRight(currentRole);

                    view.fireEvent('save', view, currentRole);
                },
                scope: this
            });
        }
    },

    onDeleteClick: function () {
        var currentRole = this.getViewModel().get('currentRole'),
            panel = this.lookupReference('rolePanel'),
            view = this.getView();

        currentRole.setId(currentRole.get('name'));

        if (currentRole.isModified('name')) {
            currentRole.setId(currentRole.getModified('name'));
        }

        currentRole.erase({
            success: function () {
                panel.setHidden(true);

                view.fireEvent('delete', view);

                this.showMessage(Unidata.i18n.t('admin.security>roleRemoveSuccess'));
            },
            scope: this
        });
    },

    onBeforeCheckChangeColumn: function (column, rowIndex) {
        var header = column.ownerCt,
            grid   = header.grid,
            store  = grid.getStore(),
            record = store.getAt(rowIndex),
            view   = grid.getView(),
            cell   = view.getCell(record, column),
            currentRole = this.getViewModel().get('currentRole'),
            recordName,
            adminDataManagementRight;

        // если не содержит чекбокс ячейка тогда ничего не меняем
        if (!cell.down('.x-grid-checkcolumn')) {
            return false;
        }

        // Нельзя выбирать ADMIN_MATCHING_MANAGEMENT если нет прав read на ADMIN_DATA_MANAGEMENT
        if (record.get('nodeType') === 'SECURED_RESOURCE_NODE') {
            recordName = record.get('record').get('name');

            if (recordName === 'ADMIN_MATCHING_MANAGEMENT') {
                adminDataManagementRight = this.findRight(currentRole.rights(), 'ADMIN_DATA_MANAGEMENT');

                if (!adminDataManagementRight.get('read')) {
                    return false;
                }
            }
        }

        return true;
    },

    onCheckChangeColumn: function (column, rowIndex, checked) {
        var header = column.ownerCt,
            grid   = header.grid,
            store  = grid.getStore(),
            record = store.getAt(rowIndex),
            depth  = record.get('depth'),
            category = record.get('record').get('category'),
            securedResourceName,
            adminMatchingManagementRight;

        // меняем только для реестров / справочников / спец прав
        if (record.get('nodeType') === 'SECURED_RESOURCE_NODE' &&
            record.parentNode &&
            record.parentNode.get('nodeType') !== 'SECURED_RESOURCE_NODE') {
            // чтение минимальное право
            if (checked) {
                record.set('read', true);
            }

            if (column.dataIndex === 'read') {
                if (record.get('create') || record.get('read') || record.get('update') || record.get('delete')) {
                    record.set('read', true);
                }
            }

        } else {
            // права CUD выставляются согласовано ддля ресурсов безопасности второго и последующих уровней
            if (column.dataIndex === 'create' || column.dataIndex === 'update' || column.dataIndex === 'delete') {
                record.set('create', checked);
                record.set('delete', checked);
                record.set('update', checked);
            }
        }

        // если изменяем флажок full то сбрасываем / устанавливаем все права CRUD
        if (column.dataIndex === 'full') {
            record.set('create', checked);
            record.set('delete', checked);
            record.set('read', checked);
            record.set('update', checked);

            record.set('full', record.get('create') && record.get('read') && record.get('update') && record.get('delete'));
        }

        // если права есть все права CRUD, то флажог full должен быть проставлен
        if (record.get('create') && record.get('read') && record.get('update') && record.get('delete')) {
            record.set('full', true);
        } else {
            record.set('full', false);
        }

        // убираем чекбоксы с ADMIN_MATCHING_MANAGEMENT если нет прав read на ADMIN_DATA_MANAGEMENT
        if (record.get('nodeType') === 'SECURED_RESOURCE_NODE') {
            securedResourceName = record.get('record').get('name');

            if (securedResourceName === 'ADMIN_DATA_MANAGEMENT') {
                adminMatchingManagementRight = this.findSecuredResourceNode(store, 'ADMIN_MATCHING_MANAGEMENT');

                if (!record.get('read')) {
                    adminMatchingManagementRight.beginEdit();

                    adminMatchingManagementRight.set('create', false);
                    adminMatchingManagementRight.set('read', false);
                    adminMatchingManagementRight.set('update', false);
                    adminMatchingManagementRight.set('delete', false);
                    adminMatchingManagementRight.set('full', false);

                    adminMatchingManagementRight.endEdit();
                } else {
                    adminMatchingManagementRight.reject();
                }
            }
        }

        if (record.get('nodeType') === 'SECURED_RESOURCE_NODE') {
            this.calcAndHighlightRightWarnings(record.get('record'));

            this.updateWarningCountWidget();
        }
    },

    onExpandAllRightNode: function () {
        var tree = this.lookupReference('treepanel');

        tree.expandAll();
    },

    onCollapseAllRightNode: function () {
        var tree = this.lookupReference('treepanel');

        tree.collapseAll();
    },

    displayRoleEditor: function () {
        var me = this,
            view = this.getView(),
            viewModel = this.getViewModel(),
            role = view.getRole(),
            binding;

        if (viewModel.get('allDataLoaded')) {
            me.onSuccessRoleLoad(role);
        } else {
            binding = viewModel.bind('{allDataLoaded}', function (loaded) {
                if (loaded) {
                    me.onSuccessRoleLoad(role);

                    binding.destroy();
                }
            });
        }
    },

    onRolePropertyChange: function (fieldName, fieldDisplayName, fieldValue) {
        var viewModel = this.getViewModel(),
            currentRole = viewModel.get('currentRole');

        currentRole.properties().each(function (property) {
            if (property.get('name') === fieldName) {
                property.set('value', fieldValue);
            }
        });
    },

    /**
     * Загрузить граф мета-зависимостей и подсветить несогласованные права
     */
    loadMetaDependencyGraphAndHighlightRightWarnings: function () {
        var MetaDependencyGraphApi = Unidata.util.api.MetaDependencyGraph,
            view = this.getView(),
            forTypes = ['LOOKUP', 'ENTITY', 'RELATION', 'NESTED_ENTITY'],
            me = this,
            skipTypes = ['NESTED_ENTITY'];

        MetaDependencyGraphApi.getMetaDependencyGraph(forTypes, skipTypes).then(function (metaDependencyGraph) {
                me.metaDependencyGraph = metaDependencyGraph;
                me.calcAndHighlightAllRightWarnings();
                view.setStatus(Unidata.StatusConstant.READY);
            },
            function () {
                Unidata.showError(Unidata.i18n.t('admin.common>loadDependencyGraphError'));
                view.setStatus(Unidata.StatusConstant.NONE);
            })
            .done();
    },

    /**
     * Вычислить и подсветить все несогласованные права
     */
    calcAndHighlightAllRightWarnings: function () {
        var securedResources;

        securedResources = Ext.Object.getValues(this.getAllSecuredResources());

        Ext.Array.each(securedResources, this.calcAndHighlightRightWarnings, this);

        this.updateWarningCountWidget();
    },

    /**
     * Вычислить и подсветить несогласованные права для определенного ресурса
     *
     * @param securedResource {Unidata.model.user.SecuredResource}
     */
    calcAndHighlightRightWarnings: function (securedResource) {
        var rightWarnings,
            securedResourceName = securedResource.get('name'),
            securedResourceCategory = securedResource.get('category');

        if (securedResourceCategory !== 'META_MODEL') {
            return;
        }

        rightWarnings = this.findRightWarningsByEntityName(securedResourceName);
        this.updateRightWarningMap(rightWarnings);
        Ext.Array.each(rightWarnings, function (rightWarning) {
            this.refreshTreeRow(rightWarning.securedResource);
        }, this);
    },

    /**
     * Пересчитать rightWarningMap в соответствии с новым предупреждениями о несогласованности
     *
     * @param rightWarnings {RightWarning[]}
     */
    updateRightWarningMap: function (rightWarnings) {
        var view = this.getView();

        Ext.Array.each(rightWarnings, function (rightWarning) {
            var rightWarningMap = this.rightWarningMap,
                rights,
                securedResourceName,
                relatedSecuredResourceName;

            if (!rightWarning) {
                return;
            }

            securedResourceName = rightWarning.securedResource;
            relatedSecuredResourceName = rightWarning.relatedSecuredResource;
            rights = rightWarning.rights;

            if (!rightWarningMap[securedResourceName]) {
                rightWarningMap[securedResourceName] = {};
            }

            if (rights) {
                rightWarningMap[securedResourceName][relatedSecuredResourceName] = rightWarning;
            } else {
                delete rightWarningMap[securedResourceName][relatedSecuredResourceName];
            }
        }, this);
    },

    /**
     * Обновляет виджет с количеством несогласованных прав
     */
    updateWarningCountWidget: function () {
        var view = this.getView(),
            aggregatedCounts = view.aggregatedCounts,
            warningCount;

        warningCount = this.getRightWarningCount();

        aggregatedCounts.update({
            warningCount: warningCount
        });
    },

    /**
     * Вычислить общее количество предупреждений о несогласованности прав
     *
     * @returns {Integer}
     */
    getRightWarningCount: function () {
        var rightWarningMap = this.rightWarningMap,
            rightWarningMapValues,
            rightWarningsKeys = [],
            count;

        rightWarningMapValues = Ext.Object.getValues(rightWarningMap);
        Ext.Array.each(rightWarningMapValues, function (rightWarningMapValue) {
            var rightWarnings;

            rightWarnings = Ext.Object.getValues(rightWarningMapValue);
            rightWarnings = Ext.Array.filter(rightWarnings, function (rightWarning) {
                return rightWarning.rights !== null;
            });
            rightWarningsKeys = rightWarningsKeys.concat(Ext.Array.pluck(rightWarnings, 'securedResource'));
        });

        rightWarningsKeys = Ext.Array.unique(rightWarningsKeys);

        count = rightWarningsKeys.length;

        return count;
    },

    /**
     * Найти предупреждения о несогласовнности прав по имени реестра/справочника
     *
     * @param entityName
     */
    findRightWarningsByEntityName: function (entityName) {
        var EdgeDirection = Unidata.view.admin.security.role.RoleEdit.EDGE_DIRECTION,
            VertexType = Unidata.model.entity.metadependency.Vertex.type,
            /**
             * @param RightWarning[]
             */
            rightWarnings = [],
            vertex,
            vertexType,
            inboundVertexes,
            outboundVertexes;

        vertex = this.findMetaModelVertex(entityName);

        if (!vertex) {
            return rightWarnings;
        }

        vertexType = vertex.get('type');

        inboundVertexes = {
            relation: null,
            lookup: null
        };

        outboundVertexes = {
            relation: null,
            lookup: null
        };

        if (vertexType === VertexType.ENTITY) {
            inboundVertexes.relation = this.findInboundVertexesByRelation(entityName);
            outboundVertexes.relation = this.findOutboundVertexesByRelation(entityName);
            outboundVertexes.lookup = this.findOutboundVertexesByLookup(entityName);
            rightWarnings = rightWarnings.concat(
                Ext.Array.map(inboundVertexes.relation,
                    this.calcRightWarningsByRelation.bind(this, EdgeDirection.INBOUND, entityName))
            );
            rightWarnings = rightWarnings.concat(
                Ext.Array.map(outboundVertexes.relation,
                    this.calcRightWarningsByRelation.bind(this, EdgeDirection.OUTBOUND, entityName))
            );
            rightWarnings = rightWarnings.concat(
                Ext.Array.map(outboundVertexes.lookup,
                    this.calcRightWarningsByLookup.bind(this, EdgeDirection.OUTBOUND, entityName))
            );
        } else if (vertexType === VertexType.LOOKUP) {
            inboundVertexes.lookup = this.findInboundVertexesByLookup(entityName);
            outboundVertexes.lookup = this.findOutboundVertexesByLookup(entityName);
            rightWarnings = rightWarnings.concat(
                Ext.Array.map(inboundVertexes.lookup,
                    this.calcRightWarningsByLookup.bind(this, EdgeDirection.INBOUND, entityName))
            );
            rightWarnings = rightWarnings.concat(
                Ext.Array.map(outboundVertexes.lookup,
                    this.calcRightWarningsByLookup.bind(this, EdgeDirection.OUTBOUND, entityName))
            );
        }

        rightWarnings = Ext.Array.filter(rightWarnings, function (rightWarning) {
            return Ext.isObject(rightWarning);
        });

        return rightWarnings;
    },

    /**
     * Вычислить несогласованность прав для соответствующей связи (заданной relationVertex)
     *
     * @param direction {String} Направление относительно реестра/справочника (INBOUND|OUTBOUND)
     * @param entityName {String} Имя реестра/справочника
     * @param relationVertex {Unidata.model.entity.metadependency.Vertex} Vertex с информацией о связи
     * @returns {RightWarning} Предупреждение о несогласованности прав
     */
    calcRightWarningsByRelation: function (direction, entityName, relationVertex) {
        var EdgeDirection = Unidata.view.admin.security.role.RoleEdit.EDGE_DIRECTION,
            rightWarning,
            entityNamePair,
            propName;

        entityNamePair = {
            from: null,
            to: null
        };

        if (direction !== EdgeDirection.INBOUND && direction !== EdgeDirection.OUTBOUND) {
            throw new Error('Edge direction is not defined');
        }

        if (direction === EdgeDirection.INBOUND) {
            propName = 'FROM';
            entityNamePair.from = relationVertex.getCustomPropValue(propName);
            entityNamePair.to = entityName;
        } else {
            propName = 'TO';
            entityNamePair.from = entityName;
            entityNamePair.to = relationVertex.getCustomPropValue(propName);
        }

        rightWarning = this.compareRelationRights(entityNamePair.from, entityNamePair.to, relationVertex);

        return rightWarning;
    },

    /**
     * Вычислить несогласованность прав для соответствующей ссылка (заданной lookupVertex)
     *
     * @param direction {String} Направление относительно реестра/справочника (INBOUND|OUTBOUND)
     * @param entityName {String} Имя реестра/справочника
     * @param lookupVertex {Unidata.model.entity.metadependency.Vertex} Vertex с информацией о справочнике-ссылке
     * @returns {RightWarning} Предупреждение о несогласованности прав
     */
    calcRightWarningsByLookup: function (direction, entityName, lookupVertex) {
        var EdgeDirection = Unidata.view.admin.security.role.RoleEdit.EDGE_DIRECTION,
            lookupVertexId = lookupVertex.get('id'),
            rightWarning,
            entityNamePair;

        entityNamePair = {
            from: null,
            to: null
        };

        if (direction !== EdgeDirection.INBOUND && direction !== EdgeDirection.OUTBOUND) {
            throw new Error('Edge direction is not defined');
        }

        if (direction === EdgeDirection.INBOUND) {
            entityNamePair.from = lookupVertexId;
            entityNamePair.to = entityName;
        } else {
            entityNamePair.from = entityName;
            entityNamePair.to = lookupVertexId;
        }

        rightWarning = this.compareLookupRights(entityNamePair.from, entityNamePair.to);

        return rightWarning;
    },

    /**
     * Сравнить права связанных справочников
     *
     * @param fromEntityName {String} Имя реестра/справочника на левом конце связи
     * @param toEntityName {String} Имя реестра/справочника на правом конце связи
     * @param relationVertex {Unidata.model.entity.metadependency.Vertex} Vertex с информацией о связи
     * @returns {RightWarning} Предупреждение о несогласованности прав
     */
    compareRelationRights: function (fromEntityName, toEntityName, relationVertex) {
        var fromRights,
            toRights,
            viewModel = this.getViewModel(),
            currentRole = viewModel.get('currentRole'),
            relationName = relationVertex.get('displayName'),
            relationType = relationVertex.getCustomPropValue('REL_TYPE'),
            relationTypeDisplayName,
            rightListSubstraction = [],
            fromEntityVertex,
            fromEntityDisplayName,
            rightWarning;

        fromRights = this.findRight(currentRole.rights(), fromEntityName);
        toRights = this.findRight(currentRole.rights(), toEntityName);
        relationTypeDisplayName = Unidata.model.data.Relation.getRelationTypeDisplayName(relationType);

        rightWarning = {
            securedResource: toEntityName,
            rights: null,
            relatedSecuredResource: fromEntityName,
            message: null
        };

        fromEntityVertex = this.findMetaModelVertex(fromEntityName);
        fromEntityDisplayName = fromEntityVertex.get('displayName');

        if (relationType === 'ManyToMany' || relationType === 'References') {
            if (fromRights.get('read') && !toRights.get('read')) {
                rightWarning = {
                    securedResource: toEntityName,
                    rights: 'read',
                    relatedSecuredResource: fromEntityName,
                    message: Unidata.i18n.t('admin.security>groupsNotApprovedWithEntiry', {name: fromEntityDisplayName}) + ' ' +
                             Unidata.i18n.t('admin.security>isRelationWithName', {name: relationName}) + ' ' +
                             Unidata.i18n.t('admin.security>typeWithName', {name: relationTypeDisplayName})

                };
            }
        } else if (relationType === 'Contains') {
            if (!Unidata.model.user.Right.rightsEqual(fromRights, toRights)) {
                rightListSubstraction = Ext.Array.difference(fromRights.getRightList(), toRights.getRightList());
                rightWarning = {
                    securedResource: toEntityName,
                    rights: rightListSubstraction,
                    relatedSecuredResource: fromEntityName,
                    message: Unidata.i18n.t('admin.security>groupsNotApprovedWithEntiry', {name: fromEntityDisplayName}) + ' ' +
                             Unidata.i18n.t('admin.security>isRelationWithName', {name: relationName}) + ' ' +
                             Unidata.i18n.t('admin.security>typeWithName', {name: relationTypeDisplayName})
                };
            }
        }

        return rightWarning;
    },

    /**
     * Сравнить права реестра/справочника, связанного с другим справочником, по ссылке
     *
     * @param fromEntityName {String} Имя реестра/справочника на левом конце связи
     * @param toEntityName {String} Имя реестра/справочника на правом конце связи
     * @returns {RightWarning} Предупреждение о несогласованности прав
     */
    compareLookupRights: function (fromEntityName, toEntityName) {
        var fromRights,
            toRights,
            viewModel = this.getViewModel(),
            currentRole = viewModel.get('currentRole'),
            fromEntityVertex,
            fromEntityDisplayName,
            rightWarning;

        fromRights = this.findRight(currentRole.rights(), fromEntityName);
        toRights = this.findRight(currentRole.rights(), toEntityName);

        fromEntityVertex = this.findMetaModelVertex(fromEntityName);
        fromEntityDisplayName = fromEntityVertex.get('displayName');

        rightWarning = {
            securedResource: toEntityName,
            rights: null,
            relatedSecuredResource: fromEntityName,
            message: null
        };

        if (fromRights.get('read') && !toRights.get('read')) {
            rightWarning = {
                securedResource: toEntityName,
                rights: 'read',
                relatedSecuredResource: fromEntityName,
                message: Unidata.i18n.t('admin.security>groupsNotApprovedWithEntiry', {name: fromEntityDisplayName}) + ' ' +
                         Unidata.i18n.t('admin.security>isLink')
            };
        }

        return rightWarning;
    },

    /**
     * Найти по имени vertex типа ENTITY или LOOKUP в metaDependencyGraph
     *
     * @param name
     * @returns {Unidata.model.entity.metadependency.Vertex|null}
     */
    findMetaModelVertex: function (name) {
        var metaDependencyGraph = this.metaDependencyGraph,
            vertexes = metaDependencyGraph.vertexes(),
            found,
            index;

        index = vertexes.findBy(function (vertex) {
            var VertexType = Unidata.model.entity.metadependency.Vertex.type,
                id = vertex.get('id'),
                type = vertex.get('type');

            return id  === name && (type === VertexType.ENTITY || type === VertexType.LOOKUP);
        });

        if (index > -1) {
            found = vertexes.getAt(index);
        }

        return found;
    },

    /**
     * Найти связи, которые являются входящими для заданного реестра/справочника
     * @param toId Имя реестра
     */
    findInboundVertexesByRelation: function (toId) {
        var EdgeDirection = Unidata.view.admin.security.role.RoleEdit.EDGE_DIRECTION;

        return this.findVertexesByRelation(toId, EdgeDirection.INBOUND);
    },

    /**
     * Найти связи, которые являются исходящими для заданного реестра/справочника
     * @param fromId Имя реестра
     */
    findOutboundVertexesByRelation: function (fromId) {
        var EdgeDirection = Unidata.view.admin.security.role.RoleEdit.EDGE_DIRECTION;

        return this.findVertexesByRelation(fromId, EdgeDirection.OUTBOUND);
    },

    // TODO: Refactoring. Заменить на Unidata.util.MetaDependencyGraph.findVertexesRelatedToId
    /**
     * Найти в графе зависимостей вершины, отражающие связи для заданного реестра/справочника
     * @param entityName {String} Имя реестра
     * @param direction {String} Направление связи INBOUND|OUTBOUND
     * @return {Unidata.model.entity.metadependency.Vertex[]}
     */
    findVertexesByRelation: function (entityName, direction) {
        var VertexType = Unidata.model.entity.metadependency.Vertex.type,
            EdgeDirection = Unidata.view.admin.security.role.RoleEdit.EDGE_DIRECTION,
            metaDependencyGraph = this.metaDependencyGraph,
            vertexes = metaDependencyGraph.vertexes().getRange(),
            vertexesByRelation,
            propName;

        if (direction !== EdgeDirection.INBOUND && direction !== EdgeDirection.OUTBOUND) {
            throw new Error('Edge direction is not defined');
        }

        propName = (direction === EdgeDirection.INBOUND) ? 'TO' : 'FROM';

        vertexesByRelation = Ext.Array.filter(vertexes, function (vertex) {
            return vertex.get('type') === VertexType.RELATION && vertex.getCustomPropValue(propName) === entityName;
        }, this);

        return vertexesByRelation;
    },

    // TODO: Refactoring. Заменить на Unidata.util.MetaDependencyGraph.findVertexesRelatedToId
    /**
     * Найти в графе зависимостей вершины, отражающие справочника, находящиеся в отношении "Ссылка "для заданного реестра/справочника
     * @param entityName {String} Имя реестра/справочника
     * @param direction {String} Направление связи INBOUND|OUTBOUND
     * @return {Unidata.model.entity.metadependency.Vertex[]}
     */
    findVertexesByLookup: function (entityName, direction) {
        var EdgeDirection = Unidata.view.admin.security.role.RoleEdit.EDGE_DIRECTION,
            VertexType = Unidata.model.entity.metadependency.Vertex.type,
            metaDependencyGraph = this.metaDependencyGraph,
            edges = metaDependencyGraph.edges().getRange(),
            edgesByLookup,
            vertexesByLookup;

        if (direction !== EdgeDirection.INBOUND && direction !== EdgeDirection.OUTBOUND) {
            throw new Error('Edge direction is not defined');
        }

        edgesByLookup = Ext.Array.filter(edges, function (edge) {
            var fromVertex     = edge.getFrom(),
                toVertex       = edge.getTo(),
                fromVertexType = fromVertex.get('type'),
                toVertexType   = toVertex.get('type'),
                toVertexId     = toVertex.get('id'),
                fromVertexId   = fromVertex.get('id'),
                typesCondition,
                idCondition;

            typesCondition =  (fromVertexType === VertexType.ENTITY || fromVertexType === VertexType.LOOKUP) &&
                (toVertexType === VertexType.LOOKUP);

            if (direction === EdgeDirection.INBOUND) {
                idCondition = toVertexId === entityName;
            } else {
                idCondition = fromVertexId === entityName;
            }

            return idCondition && typesCondition;
        }, this);

        vertexesByLookup = Ext.Array.map(edgesByLookup, function (edge) {
            return direction === EdgeDirection.INBOUND ? edge.getFrom() : edge.getTo();
        });

        return vertexesByLookup;
    },

    /**
     * Найти входящие ссылки для заданного реестра/справочника
     * @param toId Имя реестра
     */
    findInboundVertexesByLookup: function (toId) {
        var EdgeDirection = Unidata.view.admin.security.role.RoleEdit.EDGE_DIRECTION;

        return this.findVertexesByLookup(toId, EdgeDirection.INBOUND);
    },

    /**
     * Найти исходящие ссылки для заданного реестра/справочника
     * @param fromId Имя реестра
     */
    findOutboundVertexesByLookup: function (fromId) {
        var EdgeDirection = Unidata.view.admin.security.role.RoleEdit.EDGE_DIRECTION;

        return this.findVertexesByLookup(fromId, EdgeDirection.OUTBOUND);
    },

    getTreePanelGridRowClass: function (node) {
        var rightWarnings,
            securedResource = node.get('record'),
            securedResourceName,
            cls = null;

        if (!securedResource) {
            return null;
        }

        securedResourceName = securedResource.get('name');
        rightWarnings = this.getRightWarnings(securedResourceName);

        if (Ext.isObject(rightWarnings) && Ext.Object.getKeys(rightWarnings).length > 0) {
            cls = 'un-right-warning';
        }

        return cls;
    },

    getRightWarnings: function (securedResourceName) {
        var rightWarningMap = this.rightWarningMap;

        if (!rightWarningMap[securedResourceName]) {
            return null;
        }

        return rightWarningMap[securedResourceName];
    },

    /**
     * Перкелючает флаг refresh для перерисовки элемента в дереве
     * Реализовано на основе Unidata.view.admin.entity.wizard.step.modelimport.SettingsStep.refreshTreeRow
     * @param securedResourceName {String} Имя ресурса безопасности
     */
    refreshTreeRow: function (securedResourceName) {
        var view = this.getView(),
            grid = view.treePanel,
            store  = grid.getStore(),
            securedResourceNode;

        securedResourceNode = this.findSecuredResourceNode(store, securedResourceName);
        // найден только если не скрыт
        if (securedResourceNode) {
            securedResourceNode.set('refresh', !securedResourceNode.get('refresh'), {commit: true});
        }
    },

    /**
     * Получить имя класса для ячейки, в которой расположен чекбокс прав
     *
     * @param node {Ext.data.TreeModel} Узел дерева ресурсов безопасности
     * @param right {String} create|read|update|delete
     * @returns {String} Имя класса
     */
    getCheckRightCellClass: function (node, right) {
        var cls = '',
            highlightMap,
            WARNING_CLS = 'un-grid-td-right-warning';

        highlightMap = this.calcHighlightMap(node);

        if (highlightMap[right].length > 0) {
            cls = WARNING_CLS;
        }

        return cls;
    },

    /**
     * Получить текст тултипа для ячейки, в которой расположен чекбокс прав
     *
     * @param node {Ext.data.TreeModel} Узел дерева ресурсов безопасности
     * @param right {String} create|read|update|delete
     * @returns {String} Имя класса
     */
    getCheckRightCellTooltip: function (node, right) {
        var tooltip = '',
            highlightMap;

        highlightMap = this.calcHighlightMap(node);

        if (highlightMap[right].length > 0) {
            tooltip = '<ul><li>' + highlightMap[right].join('</li><li>') + '</li></ul>';
        }

        return tooltip;
    },

    /**
     * Вычислить карту подсветки для заданного узла
     *
     * Highlight map:
     * Example:
     * {
     *   create: [],
     *   read: ['Права несогласованы с реестром Entity1', 'Права несогласованы с реестром Entity2'],
     *   update: ['Права несогласованы с реестром Entity1'],
     *   delete []
     * }
     * @param node {Ext.data.TreeModel}
     * @returns {Object}
     */
    calcHighlightMap: function (node) {
        var rightWarningMap = this.rightWarningMap,
            securedResource = node.get('record'),
            securedResourceName,
            rightWarnings,
            highlightMap;

        highlightMap = {
            create: [],
            read: [],
            update: [],
            delete: []
        };

        if (!securedResource) {
            return highlightMap;
        }

        securedResourceName = securedResource.get('name');

        if (!rightWarningMap[securedResourceName]) {
            return highlightMap;
        }

        rightWarnings = Ext.Object.getValues(rightWarningMap[securedResourceName]);

        Ext.Array.each(rightWarnings, function (rightWarning) {
            var message,
                rights;

            message = rightWarning.message;
            rights = rightWarning.rights;

            Ext.Array.each(rights, function (right) {
                highlightMap[right].push(message);
            });
        });

        return highlightMap;
    },

    updateRole: function (newRole) {
        var viewModel = this.getViewModel();

        viewModel.set('currentRole', newRole);

        viewModel.notify();
    }
});
