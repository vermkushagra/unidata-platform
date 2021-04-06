/**
 * @author Ivan Marshalkin
 * 2016-08-23
 */

Ext.define('Unidata.view.component.search.classifierattribute.ClassifierFilterPanelController', {
    extend: 'Unidata.view.component.search.attribute.FilterPanelController',

    alias: 'controller.component.search.classifierattribute.classifierfilterpanel',

    getSearchableAttributes: function (classifierNode) {
        var searchableAttribute      = [],
            processedAttributeNames = [],
            classifierName          = classifierNode.get('classifierName'),
            attributeName,
            item;

        classifierNode.nodeAttrs().each(function (nodeAttribute) {
            attributeName = nodeAttribute.get('name');

            item = {
                attribute: nodeAttribute,
                path: [classifierName, attributeName].join('.')
            };

            if (!Ext.isEmpty(nodeAttribute.get('value'))) {
                Ext.apply(item, {
                    value: {
                        value: nodeAttribute.get('value')
                    }
                });
            }

            searchableAttribute.push(item);

            processedAttributeNames.push(attributeName);
        });

        classifierNode.inheritedNodeAttrs().each(function (nodeAttribute) {
            var attributeValue = nodeAttribute.get('attributeValue');

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

        return searchableAttribute;
    },

    getTabletItemByAttribute: function (attrInfo) {
        var tabletDataType = attrInfo.attribute.get('simpleDataType'),
            attribute = attrInfo.attribute,
            tablet;

        tablet = {
            xtype: 'component.search.attribute.tablet.tablet',
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
