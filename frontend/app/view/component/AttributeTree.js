Ext.define('Unidata.view.component.AttributeTree', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.component.attributeTree',

    bodyCls: 'attribute-tree',

    config: {
        data: null,
        isSimpleAttributesHidden: false,
        isComplexAttributesHidden: false,
        isArrayAttributesHidden: false,
        isAttributeFieldsDisplay: true,
        hideAttributeFilter: null, // функция - фильтр атрибутов, если возвращает true, то атрибут не отображается
        lookupEntities: null
    },

    buildNodeContent: function (record, treepanel) {
        var value,
            fields = [],
            fieldsStr,
            lookupEntities,
            enumerations,
            measurementValues,
            typeDisplayText,
            defaultDisplayName,
            icon = '',
            name = '',
            className = record.$className.split('.').slice(2).join('.'),
            MeasurementValuesApi = Unidata.util.api.MeasurementValues,
            EnumerationApi = Unidata.util.api.Enumeration;

        switch (className) {
            case 'attribute.CodeAttribute':
            case 'attribute.SimpleAttribute':
            case 'attribute.AliasCodeAttribute':
            case 'attribute.ArrayAttribute':
                lookupEntities = treepanel.getLookupEntities();
                enumerations      = EnumerationApi.getStore();
                measurementValues = MeasurementValuesApi.getStore();

                typeDisplayText = Unidata.util.MetaAttributeFormatter
                                    .buildTypeDisplayText(record, lookupEntities, enumerations, measurementValues);

                if (typeDisplayText) {
                    fields.push(typeDisplayText);
                }

                if (!record.get('nullable')) {
                    fields.push(Unidata.i18n.t('admin.metamodel>required'));
                }

                /* Поле "Уникальный" временно деактивировано*/
                /*
                if (record.get('unique')) {
                    fields.push('уникальный');
                }
                */

                if (className === 'attribute.CodeAttribute') {
                    fields.push(Unidata.i18n.t('admin.metamodel>coded'));
                } else if (className === 'attribute.AliasCodeAttribute') {
                    fields.push(Unidata.i18n.t('admin.metamodel>codedAlternate'));
                }

                defaultDisplayName = Unidata.i18n.t('glossary:newAttribute');
                name = record.get('displayName');
                break;
            case 'attribute.ComplexAttribute':
                if (record.get('minCount') > 0) {
                    fields.push(Unidata.i18n.t('admin.metamodel>min') + ': ' + record.get('minCount'));
                }

                if (record.get('maxCount') > 0) {
                    fields.push(Unidata.i18n.t('admin.metamodel>max') + ': ' + record.get('maxCount'));
                }

                if (record.get('subEntityKeyAttribute') !== '') {
                    fields.push(Unidata.i18n.t('admin.metamodel>key') + ': \"' + record.get('subEntityKeyAttribute') + '\"');
                }
                defaultDisplayName = Unidata.i18n.t('glossary:newAttribute');
                name = record.get('displayName');
                break;
            case 'entity.Entity':
            case 'entity.LookupEntity':
            case 'entity.NestedEntity':
                name = record.get('displayName');
                defaultDisplayName = Unidata.i18n.t('glossary:newRecord').toLowerCase();
                break;
            case 'entity.Relation':
                name = record.get('displayName');
                defaultDisplayName = Unidata.i18n.t('glossary:newRelation').toLowerCase();
                break;
            case 'cleansefunction.Group':
                icon = Unidata.model.cleansefunction.CleanseFunction.createTypeIcon(record.get('type'));
                defaultDisplayName = Unidata.i18n.t('glossary:newGroup').toLowerCase();
                name = record.get('name');
                break;
            case 'cleansefunction.CleanseFunction':
                icon = Unidata.model.cleansefunction.CleanseFunction.createTypeIcon(record.get('type'));
                defaultDisplayName = Unidata.i18n.t('glossary:newFunction').toLowerCase();
                name = record.get('name');
                break;
            default:
                defaultDisplayName = Unidata.i18n.t('glossary:newEntity');
        }

        fieldsStr = fields.length > 0 ? ' (' + fields.join(', ') + ')' : '';

        if (name) {
            // не забываем предотвращение XSS
            value = Ext.String.format(
                '{0}<span class="un-tree-text">{1:htmlEncode}</span><span>{2:htmlEncode}</span>',
                icon,
                name,
                fieldsStr);
        } else {
            // не забываем предотвращение XSS
            value = Ext.String.format('{0}<i>- {1:htmlEncode} -</i>', icon, defaultDisplayName);
        }

        return value;
    },

    columns: [{
        xtype: 'treecolumn',
        dataIndex: 'text',
        flex: 1,
        height: 0,

        _isSimpleOrCodeAttribute: function (record) {
            return record instanceof Unidata.model.attribute.SimpleAttribute ||
                record instanceof Unidata.model.attribute.CodeAttribute ||
                record instanceof Unidata.model.attribute.AliasCodeAttribute;
        },

        _isComplexAttribute: function (record) {
            return record instanceof Unidata.model.attribute.ComplexAttribute;
        },

        renderer: function (value, metaData, treeRecord) {
            var record = treeRecord.get('record'),
                treepanel = metaData.column.findParentByType('treepanel');

            return record ? treepanel.buildNodeContent(record, treepanel) : value;
        }
    }],

    onRender: function () {
        this.autoEl = Ext.apply({}, this.initialConfig, this.config);
        this.callParent(arguments);
        this.el.on('load', this.onLoad, this);
    },

    onLoad: function () {
        this.fireEvent('load', this);
    },

    buildPath: function (parentPath, name) {
        var path;

        path = parentPath ? parentPath + '.' + name : name;

        return path;
    },

    prepareTreeData: function (parent, parentPath) {
        var node,
            record,
            nodes = [],
            path,
            me = this,
            attributeName;

        parentPath = parentPath || '';

        if (!parent) {
            return [];
        }

        function addSimpleAttributeToTree (record) {
            if (me.hideAttributeFilter && me.hideAttributeFilter(record)) {
                return;
            }

            attributeName = record.get('name');
            path = me.buildPath(parentPath, attributeName);

            nodes.push({
                record: record,
                path: path,
                name: record.get('name'),
                leaf: true,
                iconCls: 'simple-node-icon'
            });
        }

        if (typeof parent.functions === 'function') {
            parent.functions().each(function (record) {
                attributeName = record.get('name');
                path = me.buildPath(parentPath, attributeName);

                nodes.push({
                    record: record,
                    path: path,
                    name: record.get('name'),
                    leaf: true,
                    iconCls: 'simple-node-icon'
                });
            });
        }

        if (typeof parent.groups === 'function' &&
            parent.groups().count !== undefined) {
            parent.groups().each(function (record) {
                attributeName = record.get('name');
                path = me.buildPath(parentPath, attributeName);

                node = {
                    record: record,
                    name: record.get('name'),
                    path: path
                };

                //@TODO check if exists nested group
                node.children = me.prepareTreeData(record, path);
                nodes.push(node);
            });
        }

        if (!me.config.isSimpleAttributesHidden &&
            typeof parent.getCodeAttribute === 'function') {
            record = parent.getCodeAttribute();

            if (record !== null && record !== undefined) {
                addSimpleAttributeToTree(record);
            }
        }

        if (!me.config.isSimpleAttributesHidden &&
            typeof parent.aliasCodeAttributes === 'function') {
            parent.aliasCodeAttributes().each(addSimpleAttributeToTree);
        }

        if (!me.config.isSimpleAttributesHidden &&
            typeof parent.simpleAttributes === 'function') {
            parent.simpleAttributes().each(addSimpleAttributeToTree);
        }

        if (!me.isComplexAttributesHidden && typeof parent.complexAttributes === 'function') {
            parent.complexAttributes().each(function (record) {
                attributeName = record.get('name');
                path = me.buildPath(parentPath, attributeName);

                node = {
                    record: record,
                    name: record.get('name'),
                    path: path
                };

                if (record.getNestedEntity() !== undefined) {
                    node.children = me.prepareTreeData(record.getNestedEntity(), path);
                }
                nodes.push(node);
            });
        }

        if (!me.isArrayAttributesHidden && typeof parent.arrayAttributes === 'function') {
            parent.arrayAttributes().each(addSimpleAttributeToTree);
        }

        nodes.sort(function (a, b) {
            return a.record.get('order') - b.record.get('order');
        });

        return nodes;
    },

    setData: function (record) {
        var nodes = this.prepareTreeData(record),
            rootNode,
            rootNodeData;

        rootNode = this.getRootNode();

        // если метарекорд тот же то перестраивать дерево не нужно
        // при изменении содержимого метарекорда этот метот срабатываем многократно
        if (rootNode) {
            rootNodeData = rootNode.getData();

            if (rootNodeData.record === record) {
                return;
            }
        }

        this.setRootNode({
            text: Unidata.i18n.t('glossary:attributes'),
            expanded: true,
            record: record,
            children: nodes
        });
    },

    appendChildAttribute: function (parent, record) {
        var me = this,
            cfg,
            max;

        if (!(me._isSimpleOrCodeAttribute(record) ||
            me._isArrayAttribute(record) ||
            record instanceof Unidata.model.attribute.ComplexAttribute)) {
            return null;
        }

        // find max order
        max = 0;

        parent.eachChild(function (child) {
            var val = child.get('record').get('order');

            max = val > max ? val : max;
        });

        record.set('order', ++max);

        cfg = {
            record: record,
            leaf: me._isSimpleOrCodeAttribute(record) || me._isArrayAttribute(record)
        };

        if (!Unidata.util.MetaAttribute.isComplexAttribute(record)) {
            cfg = Ext.apply(cfg, {
                iconCls: 'simple-node-icon'
            });
        }

        return parent.appendChild(cfg);
    },

    appendChildAttributeToSelection: function (record) {
        var selection, node;

        selection = this.getSelection()[0];
        selection.expand();
        node = this.appendChildAttribute(selection, record);
        this.setSelection(node);
    },

    appendChildAttributeToNode: function (node, record) {
        var newNode;

        node.expand();
        newNode = this.appendChildAttribute(node, record);

        this.getSelectionModel().select(newNode);
    },

    getNodeNestedEntity: function (node) {
        var record,
            nestedEntity,
            obj;

        record = node.get('record');

        if (record instanceof Unidata.model.attribute.ComplexAttribute) {
            if (record.getNestedEntity() === undefined) {
                obj = Unidata.model.entity.NestedEntity.create();

                record.setNestedEntity(obj);
            }
            nestedEntity = record.getNestedEntity();
        } else {
            nestedEntity = record;
        }

        return nestedEntity;
    },

    getNodeToAppend: function () {
        var node,
            selection,
            record;

        selection = this.getSelection();

        if (!selection.length) {
            node = this.getRootNode();
        } else {
            node = selection[0];
            record = node.get('record');

            if (!node.isRoot() && Ext.getClassName(record) !== 'Unidata.model.attribute.ComplexAttribute') {
                node = node.parentNode;
            }
        }

        return node;
    },

    orderUp: function (node) {
        this.swapNode(node, node.previousSibling);
    },

    orderDown: function (node) {
        this.swapNode(node, node.nextSibling);
    },

    swapNode: function (node1, node2) {
        var store = this.getStore(),
            parentNode = (node1.parentNode === node2.parentNode && node1.parentNode);

        if (node1 && node2 && parentNode) {
            if (store.indexOf(node1) > store.indexOf(node2)) {
                parentNode.insertBefore(node1, node2);
            } else {
                parentNode.insertBefore(node2, node1);
            }

            this.reorderNode();
        }
    },

    reorderNode: function () {
        var store = this.getStore();

        store.each(function (record, index) {
            var recordRecord = record.get('record');

            if (recordRecord) {
                recordRecord.set('order', index);
            }
        });
    },

    removeAttributeNode: function (node) {
        this.suspendEvent('beforedeselect');

        if (node.nextSibling !== null && node.nextSibling !== undefined) {
            this.setSelection(node.nextSibling);
        } else if (node.previousSibling !== null && node.previousSibling !== undefined) {
            this.setSelection(node.previousSibling);
        } else {
            this.setSelection();
        }

        this.resumeEvent('beforedeselect');

        node.remove();

        this.reorderNode();
    },

    _isSimpleOrCodeAttribute: function (record) {
        return record instanceof Unidata.model.attribute.SimpleAttribute ||
            record instanceof Unidata.model.attribute.CodeAttribute ||
            record instanceof Unidata.model.attribute.AliasCodeAttribute;
    },

    _isArrayAttribute: function (record) {
        return record instanceof Unidata.model.attribute.ArrayAttribute;
    },

    _isComplexAttribute: function (record) {
        return record instanceof Unidata.model.attribute.ComplexAttribute;
    }
});
