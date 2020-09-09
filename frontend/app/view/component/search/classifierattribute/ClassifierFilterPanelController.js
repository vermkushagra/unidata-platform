/**
 * @author Ivan Marshalkin
 * 2016-08-23
 */

Ext.define('Unidata.view.component.search.classifierattribute.ClassifierFilterPanelController', {
    extend: 'Unidata.view.component.search.attribute.FilterPanelController',

    alias: 'controller.component.search.classifierattribute.classifierfilterpanel',

    TermClass: Unidata.module.search.term.classifier.FormField,

    getAttributePath: function (attribute) {
        var view = this.getView(),
            attributeName = attribute.get('name'),
            classifierNode = view.getClassifierNode(),
            classifierName = classifierNode.get('classifierName');

        return [classifierName, attributeName].join('.');
    },

    getSearchableAttributes: function (classifierNode) {
        var searchableAttribute = [],
            processedAttributeNames = [],
            classifierName = classifierNode.get('classifierName'),
            attributeName,
            attributeStores,
            item;

        attributeStores = [
            classifierNode.nodeAttrs(),
            classifierNode.inheritedNodeAttrs(),
            classifierNode.nodeArrayAttrs(),
            classifierNode.inheritedNodeArrayAttrs()
        ];

        Ext.Array.each(attributeStores, function (attributeStore) {
            attributeStore.each(function (nodeAttribute) {
                var attributeValue = nodeAttribute.get('value');

                attributeName = nodeAttribute.get('name');

                if (!Ext.Array.contains(processedAttributeNames, attributeName)) {
                    item = {
                        attribute: nodeAttribute,
                        path: [classifierName, attributeName].join('.')
                    };

                    if (!Ext.isEmpty(attributeValue)) {
                        Ext.apply(item, {
                            value: {
                                value: attributeValue
                            }
                        });
                    }

                    searchableAttribute.push(item);
                }

                processedAttributeNames.push(attributeName);
            });
        });

        return searchableAttribute;
    },

    getTabletItemByAttribute: function (attrInfo, term) {
        var attribute = attrInfo.attribute,
            tabletDataType,
            tablet;

        if (!term) {
            term = new this.TermClass;
        }

        if (attribute.isSimpleDataType()) {
            tabletDataType = attribute.get('simpleDataType');
        } else if (attribute.isLookupEntityType()) {
            tabletDataType = 'Lookup';
        } else if (attribute.isEnumDataType()) {
            tabletDataType = 'Enumeration';
        } else if (attribute.isArrayDataType()) {
            switch (attribute.get('typeCategory')) {
                case 'arrayDataType':
                    tabletDataType = attribute.get('arrayDataType');
                    break;
                case 'lookupEntityType':
                    tabletDataType = 'Lookup';
                    break;
            }
        }

        if (Ext.isEmpty(tabletDataType)) {
            Ext.Error.raise('Type of attribute ' + attribute.get('name') + ' is not supported');
        }

        tablet = {
            xtype: 'component.search.attribute.tablet.tablet',
            term: term,
            tabletDataType: tabletDataType,
            attributeName: attrInfo.attribute.get('name'),
            attributeDisplayName: attrInfo.attribute.get('displayName'),
            value: attrInfo.value,
            path: attrInfo.path,
            attribute: attribute,
            params: {}
        };

        return tablet;
    }
});
