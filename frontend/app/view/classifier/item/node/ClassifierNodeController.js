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
    onAddClassifierNodeAddAttribute: function () {
        var view               = this.getView(),
            attributeContainer = view.attributeContainer,
            classifierNode     = view.getClassifierNode(),
            deletable          = true,
            scroller,
            panel,
            nodeAttribute;

        attributeContainer.expand();

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

        panel = this.createAndShowAttributePanel(nodeAttribute, null, deletable);
        panel.expand();

        if (attributeContainer.rendered) {
            scroller = attributeContainer.getScrollable();

            Ext.defer(function () {
                scroller.scrollTo(0, panel.el.dom.offsetTop + panel.el.getHeight());
            }, 100);
        }

        classifierNode.nodeAttrs().add(nodeAttribute);
    },

    updateClassifierNode: function (classifierNode) {
        var view               = this.getView(),
            attributeContainer = view.attributeContainer;

        attributeContainer.removeAll();

        this.buildAttributePanels(classifierNode);
    },

    /**
     * Создает и отображает панальку с атрибутом
     *
     * @param nodeAttribute
     * @param deletable
     */
    createAndShowAttributePanel: function (ownAttribute, inheritedAttribute, deletable) {
        var view               = this.getView(),
            attributeContainer = view.attributeContainer,
            panel;

        panel = this.buildAttributePanel(ownAttribute, inheritedAttribute, deletable);

        attributeContainer.add(panel);

        return panel;
    },

    /**
     * Создает панельки с атрибутами
     */
    buildAttributePanels: function (classifierNode) {
        var attributesInUse = [];

        // наследованный атрибутивный состав
        classifierNode.inheritedNodeAttrs().each(function (nodeAttribute) {
            attributesInUse = this.createAttributePanel(classifierNode, nodeAttribute, attributesInUse);
        }, this);

        // собственный атрибутивный состав
        classifierNode.nodeAttrs().each(function (nodeAttribute) {
            attributesInUse = this.createAttributePanel(classifierNode, nodeAttribute, attributesInUse);
        }, this);
    },

    /**
     * Создаёт панельку с атрибутами
     * @see buildAttributePanels
     */
    createAttributePanel: function (classifierNode, nodeAttribute, attributesInUse) {
        var deletable = true,
            nodeName = nodeAttribute.get('name'),
            attributePairs;

        if (!Ext.Array.contains(attributesInUse, nodeName)) {
            attributePairs = this.getAttributePairs(classifierNode, nodeName);

            this.createAndShowAttributePanel(attributePairs.ownAttribute, attributePairs.inheritedAttribute, deletable);

            attributesInUse.push(nodeName);
        }

        return attributesInUse;
    },

    /**
     * Возвращает пару собственный / наследованный атрибут
     *
     * @param classifierNode
     * @param nodeAttributeName
     * @returns {*}
     */
    getAttributePairs: function (classifierNode, nodeAttributeName) {
        var ownAttribute       = null,
            inheritedAttribute = null,
            result             = null,
            index;

        index = classifierNode.inheritedNodeAttrs().findExact('name', nodeAttributeName);

        if (index !== -1) {
            inheritedAttribute = classifierNode.inheritedNodeAttrs().getAt(index);
        }

        index = classifierNode.nodeAttrs().findExact('name', nodeAttributeName);

        if (index !== -1) {
            ownAttribute = classifierNode.nodeAttrs().getAt(index);
        } /*else {
            if (inheritedAttribute) {
                ownAttribute = Ext.create('Unidata.model.attribute.ClassifierNodeAttribute', inheritedAttribute.getData());
            }
        }*/

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
    buildAttributePanel: function (ownAttribute, inheritedAttribute, deletable) {
        var me             = this,
            view           = this.getView(),
            readOnly       = view.getReadOnly(),
            classifierNode = view.getClassifierNode(),
            panel;

        panel = Ext.create('Unidata.view.classifier.item.attribute.ClassifierAttribute', {
            nodeAttribute: null,
            ownAttribute: ownAttribute,
            inheritedAttribute: inheritedAttribute,
            classifierNode: classifierNode,
            deletable: deletable,
            collapsed: true,
            readOnly: readOnly,
            listeners: {
                removeattribute: {
                    fn: me.onClassifierAttributeRemove,
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
        var view               = this.getView(),
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
        var view           = this.getView(),
            classifierNode = view.getClassifierNode();

        classifierNode.nodeAttrs().remove(nodeAttribute);
    },

    /**
     * Подсвечивает ошибки в атрибутах по выбранной ноде
     */
    highlightErrors: function () {
        var view               = this.getView(),
            attributeContainer = view.attributeContainer;

        attributeContainer.items.each(function (item) {
            item.highlightErrors();
        });
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
        var view               = this.getView(),
            viewModel          = this.getViewModel(),
            attributeContainer = view.attributeContainer;

        viewModel.set('readOnly', readOnly);

        attributeContainer.items.each(function (item) {
            item.setReadOnly(readOnly);
        });
    }
});
