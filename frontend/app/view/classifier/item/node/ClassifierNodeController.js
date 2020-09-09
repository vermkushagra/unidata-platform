/**
 * Экран "Узел классификатора" (контроллер)
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */

Ext.define('Unidata.view.classifier.item.node.ClassifierNodeController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.classifier.item.node',

    /**
     * Обработчик добавления атрибута к узлу классификатора
     */
    onClassifierNodeAttributeAddButtonClick: function () {
        var view = this.getView(),
            attributeContainer = view.attributeContainer,
            classifierNode = view.getClassifierNode(),
            deletable = true,
            customPropertiesReadOnly = false,
            scroller,
            panel,
            nodeAttribute;

        attributeContainer.expand();
        this.collapseAllAttributeTablet();

        nodeAttribute = Ext.create('Unidata.model.attribute.ClassifierNodeAttribute', {
            simpleDataType: null,
            value: null,
            searchable: false,
            nullable: true,
            displayable: false,
            mainDisplayable: false,
            mask: null,
            name: null,
            displayName: null,
            description: null,
            readOnly: false,
            hidden: false,
            userAdded: true
        });

        panel = this.createAndShowAttributePanel(nodeAttribute, null, deletable, customPropertiesReadOnly);
        panel.expand();

        if (attributeContainer.rendered) {
            scroller = attributeContainer.getScrollable();

            Ext.defer(function () {
                scroller.scrollTo(0, panel.el.dom.offsetTop + panel.el.getHeight());
            }, 100);
        }

        classifierNode.nodeAttrs().add(nodeAttribute);

        this.updateAttributesOrder();
        this.updateAllowedAttributeOrderOperation();
    },

    /**
     * Слопывает все панельски с свойствами атрибутов
     */
    collapseAllAttributeTablet: function () {
        var view = this.getView(),
            attributeContainer = view.attributeContainer;

        attributeContainer.items.each(function (panel) {
            panel.collapse();
        });
    },

    /**
     * Обновляет допустимые операции для таблеток атрибутов
     */
    updateAllowedAttributeOrderOperation: function () {
        var view = this.getView(),
            attributeContainer = view.attributeContainer;

        attributeContainer.items.each(function (panel) {
            var allowLiftUp = Boolean(attributeContainer.prevChild(panel)),
                allowLiftDown = Boolean(attributeContainer.nextChild(panel));

            panel.setAllowLiftUp(allowLiftUp);
            panel.setAllowLiftDown(allowLiftDown);
        });
    },

    /**
     * Обработчик добавления array трибута к узлу классификатора
     */
    onClassifierNodeArrayAttributeAddButtonClick: function () {
        var view = this.getView(),
            attributeContainer = view.attributeContainer,
            classifierNode = view.getClassifierNode(),
            deletable = true,
            customPropertiesReadOnly = false,
            scroller,
            panel,
            nodeAttribute;

        attributeContainer.expand();
        this.collapseAllAttributeTablet();

        nodeAttribute = Ext.create('Unidata.model.attribute.ClassifierNodeArrayAttribute', {
            arrayDataType: null,
            value: null,
            searchable: false,
            nullable: true,
            displayable: false,
            mainDisplayable: false,
            // mask: null,
            name: null,
            displayName: null,
            description: null,
            readOnly: false,
            hidden: false,
            userAdded: true
        });

        panel = this.createAndShowAttributePanel(nodeAttribute, null, deletable, customPropertiesReadOnly);
        panel.expand();

        if (attributeContainer.rendered) {
            scroller = attributeContainer.getScrollable();

            Ext.defer(function () {
                scroller.scrollTo(0, panel.el.dom.offsetTop + panel.el.getHeight());
            }, 100);
        }

        classifierNode.nodeArrayAttrs().add(nodeAttribute);

        this.updateAttributesOrder();
        this.updateAllowedAttributeOrderOperation();
    },

    updateClassifierNode: function (classifierNode) {
        var view = this.getView(),
            attributeContainer = view.attributeContainer;

        attributeContainer.removeAll();

        this.buildAttributePanels(classifierNode);
        this.updateAllowedAttributeOrderOperation();
    },

    /**
     * Создает и отображает панальку с атрибутом
     *
     * @param nodeAttribute
     * @param deletable
     */
    createAndShowAttributePanel: function (ownAttribute, inheritedAttribute, deletable, customPropertiesReadOnly) {
        var view = this.getView(),
            attributeContainer = view.attributeContainer,
            panel;

        if ((ownAttribute && ownAttribute.isArrayDataType()) || (inheritedAttribute && inheritedAttribute.isArrayDataType())) {
            panel = this.buildArrayAttributePanel(ownAttribute, inheritedAttribute, deletable, customPropertiesReadOnly);
        } else {
            panel = this.buildAttributePanel(ownAttribute, inheritedAttribute, deletable, customPropertiesReadOnly);
        }

        attributeContainer.add(panel);

        return panel;
    },

    /**
     * Создает панельки с атрибутами
     */
    buildAttributePanels: function (classifierNode) {
        var usedAttributes = [],
        inheritedNodeAttrs = classifierNode.inheritedNodeAttrs().getRange(),
        inheritedNodeArrayAttrs = classifierNode.inheritedNodeArrayAttrs().getRange(),
        nodeAttrs = classifierNode.nodeAttrs().getRange(),
        nodeArrayAttrs = classifierNode.nodeArrayAttrs().getRange();

        // наследованный атрибутивный состав
        inheritedNodeAttrs.sort(this.sortByOrderAndDisplayName).forEach(function (nodeAttribute) {
            usedAttributes = this.createAttributePanel(classifierNode, nodeAttribute, usedAttributes, {
                deletable: false,
                customPropertiesReadOnly: true
            });
        }, this);

        // наследованный array атрибутивный состав
        inheritedNodeArrayAttrs.sort(this.sortByOrderAndDisplayName).forEach(function (nodeAttribute) {
            usedAttributes = this.createAttributePanel(classifierNode, nodeAttribute, usedAttributes, {
                deletable: false,
                customPropertiesReadOnly: true
            });
        }, this);

        // собственный атрибутивный состав
        nodeAttrs.sort(this.sortByOrderAndDisplayName).forEach(function (nodeAttribute) {
            usedAttributes = this.createAttributePanel(classifierNode, nodeAttribute, usedAttributes, {
                deletable: true,
                customPropertiesReadOnly: false
            });
        }, this);

        // собственный array атрибутивный состав
        nodeArrayAttrs.sort(this.sortByOrderAndDisplayName).forEach(function (nodeAttribute) {
            usedAttributes = this.createAttributePanel(classifierNode, nodeAttribute, usedAttributes, {
                deletable: true,
                customPropertiesReadOnly: false
            });
        }, this);
    },

    sortByOrderAndDisplayName: function (a, b) {
        if (a.get('order') === b.get('order')) {
            return (a.get('displayName') < b.get('displayName')) ? -1 : 1;
        } else {
            return (a.get('order') < b.get('order')) ? -1 : 1;
        }
    },

    /**
     * Обновляет порядок атрибутов
     */
    updateAttributesOrder: function () {
        var view = this.getView(),
            attributeContainer = view.attributeContainer;

        attributeContainer.items.each(function (item, index) {
            item.setAttributeOrder(index);
        });
    },

    /**
     * Перемещает редактор свойств атрибута вверх
     *
     * @param attributeTablet
     */
    liftAttributeTabletUp: function (attributeTablet) {
        var view = this.getView(),
            attributeContainer = view.attributeContainer,
            prevChild;

        prevChild = attributeContainer.prevChild(attributeTablet);

        if (!prevChild) {
            return;
        }

        attributeContainer.moveBefore(attributeTablet, prevChild);

        this.updateAttributesOrder();
        this.updateAllowedAttributeOrderOperation();
    },

    /**
     * Перемещает редактор свойств атрибута вниз
     *
     * @param attributeTablet
     */
    liftAttributeTabletDown: function (attributeTablet) {
        var view = this.getView(),
            attributeContainer = view.attributeContainer,
            nextChild;

        nextChild = attributeContainer.nextChild(attributeTablet);

        if (!nextChild) {
            return;
        }

        attributeContainer.moveBefore(nextChild, attributeTablet);

        this.updateAttributesOrder();
        this.updateAllowedAttributeOrderOperation();
    },

    /**
     * Создаёт панельку с атрибутами
     *
     * @see buildAttributePanels
     */
    createAttributePanel: function (classifierNode, nodeAttribute, usedAttributes, cfg) {
        var nodeName = nodeAttribute.get('name'),
            operationCfg = {},
            attributePairs,
            deletable,
            customPropertiesReadOnly;

        cfg = cfg || {};
        deletable = Boolean(cfg.deletable);
        customPropertiesReadOnly = cfg.customPropertiesReadOnly;

        if (!Ext.Array.contains(usedAttributes, nodeName)) {

            if (nodeAttribute.isArrayDataType()) {
                operationCfg.type = 'array';
            }

            attributePairs = this.getAttributePairs(classifierNode, nodeName, operationCfg);

            this.createAndShowAttributePanel(attributePairs.ownAttribute, attributePairs.inheritedAttribute, deletable, customPropertiesReadOnly);

            usedAttributes.push(nodeName);
        }

        return usedAttributes;
    },

    /**
     * Возвращает пару собственный / наследованный атрибут
     *
     * @param classifierNode - узел классификатора
     * @param nodeAttributeName - имя атрибута
     * @param cfg - конфиг операции
     *
     *     cfg = {
     *          type: 'array' || undefined // тип выполняемой операции: 'array' для атрибутов с типом массив
     *     }
     *
     * @returns {*}
     */
    getAttributePairs: function (classifierNode, nodeAttributeName, cfg) {
        var ownAttribute = null,
            inheritedAttribute = null,
            result = null,
            nodeAttrsStore = classifierNode.nodeAttrs(),
            inheritedNodeAttrsStore = classifierNode.inheritedNodeAttrs(),
            index;

        cfg = cfg || {};

        if (cfg.type === 'array') {
            nodeAttrsStore = classifierNode.nodeArrayAttrs();
            inheritedNodeAttrsStore = classifierNode.inheritedNodeArrayAttrs();
        }

        index = inheritedNodeAttrsStore.findExact('name', nodeAttributeName);

        if (index !== -1) {
            inheritedAttribute = inheritedNodeAttrsStore.getAt(index);
        }

        index = nodeAttrsStore.findExact('name', nodeAttributeName);

        if (index !== -1) {
            ownAttribute = nodeAttrsStore.getAt(index);
        }

        if (ownAttribute || inheritedAttribute) {
            result = {
                ownAttribute: ownAttribute,
                inheritedAttribute: inheritedAttribute
            };
        }

        return result;
    },

    /**
     * Создает панельку с атрибутами
     *
     * @param nodeAttribute
     * @param deletable
     * @returns {Unidata.view.classifier.item.attribute.AttributeNode|*}
     */
    buildAttributePanel: function (ownAttribute, inheritedAttribute, deletable, customPropertiesReadOnly) {
        var me = this,
            view = this.getView(),
            readOnly = view.getReadOnly(),
            classifierNode = view.getClassifierNode(),
            panel;

        panel = Ext.create('Unidata.view.classifier.item.attribute.ClassifierAttribute', {
            nodeAttribute: null,
            ownAttribute: ownAttribute,
            inheritedAttribute: inheritedAttribute,
            classifierNode: classifierNode,
            deletable: deletable,
            customPropertiesReadOnly: customPropertiesReadOnly,
            collapsed: true,
            readOnly: readOnly,
            listeners: {
                removeattribute: {
                    fn: me.onClassifierAttributeRemove,
                    scope: me
                },
                liftmeup: {
                    fn: this.liftAttributeTabletUp,
                    scope: me
                },
                liftmedown: {
                    fn: this.liftAttributeTabletDown,
                    scope: me
                }
            }
        });

        return panel;
    },

    /**
     * Создает панельку с array-атрибутами
     *
     * @param nodeAttribute
     * @param deletable
     * @returns {Unidata.view.classifier.item.attribute.AttributeNode|*}
     */
    buildArrayAttributePanel: function (ownAttribute, inheritedAttribute, deletable, customPropertiesReadOnly) {
        var me = this,
            view = this.getView(),
            readOnly = view.getReadOnly(),
            classifierNode = view.getClassifierNode(),
            panel;

        panel = Ext.create('Unidata.view.classifier.item.attribute.ClassifierArrayAttribute', {
            nodeAttribute: null,
            ownAttribute: ownAttribute,
            inheritedAttribute: inheritedAttribute,
            classifierNode: classifierNode,
            deletable: deletable,
            customPropertiesReadOnly: customPropertiesReadOnly,
            collapsed: true,
            readOnly: readOnly,
            listeners: {
                removeattribute: {
                    fn: me.onClassifierAttributeRemove,
                    scope: me
                },
                liftmeup: {
                    fn: this.liftAttributeTabletUp,
                    scope: me
                },
                liftmedown: {
                    fn: this.liftAttributeTabletDown,
                    scope: me
                }
            }
        });

        return panel;
    },

    /**
     * Обработка события removeattribute от панелей отображающих свойства атрибута
     *
     * @param nodeAttribute
     */
    onClassifierAttributeRemove: function (nodeAttribute) {
        this.removePanelByAttributeNode(nodeAttribute);
        this.removeAttributeNode(nodeAttribute);
    },

    /**
     * Удаляем панель отображающую свойства атрибута
     *
     * @param nodeAttribute
     */
    removePanelByAttributeNode: function (nodeAttribute) {
        var view = this.getView(),
            attributeContainer = view.attributeContainer;

        if (!attributeContainer.items) {
            return;
        }

        attributeContainer.items.each(function (item) {
            if (item.getNodeAttribute() === nodeAttribute) {
                attributeContainer.remove(item);

                return false;
            }
        });
    },

    /**
     * Удаляет атрибут из собственного атрибутивного состава
     *
     * @param nodeAttribute
     */
    removeAttributeNode: function (nodeAttribute) {
        var view = this.getView(),
            classifierNode = view.getClassifierNode();

        if (nodeAttribute.isArrayDataType()) {
            classifierNode.nodeArrayAttrs().remove(nodeAttribute);
        } else {
            classifierNode.nodeAttrs().remove(nodeAttribute);
        }
    },

    /**
     * Подсвечивает ошибки в атрибутах по выбранной ноде
     */
    highlightErrors: function () {
        var view = this.getView(),
            attributeContainer = view.attributeContainer,
            scrollerEl = view.attributeContainer.getScrollerEl(),
            scroller = attributeContainer.getScrollable(),
            prevHeight = scrollerEl.getHeight(),
            newHeight;

        attributeContainer.items.each(function (item) {
            item.highlightErrors();
        });

        newHeight = scrollerEl.getHeight();

        Ext.defer(function () {
            scroller.scrollTo(0, attributeContainer.getScrollY() + Number(newHeight) - Number(prevHeight));
        }, 100);
    },

    /**
     * Сбрасывает подсвеченые ошибки в атрибутах по выбранной ноде
     */
    resetErrors: function () {
        var view = this.getView(),
            attributeContainer = view.attributeContainer;

        attributeContainer.items.each(function (item) {
            item.resetErrors();
        });
    },

    /**
     * Обработка изменения readOnly панельки
     * @param readOnly
     */
    updateReadOnly: function (readOnly) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            attributeContainer = view.attributeContainer;

        viewModel.set('readOnly', readOnly);

        attributeContainer.items.each(function (item) {
            item.setReadOnly(readOnly);
        });
    }
});
