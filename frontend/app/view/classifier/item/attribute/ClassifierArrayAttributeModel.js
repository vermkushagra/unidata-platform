/**
 * @author Ivan Marshalkin
 * @date 2018-05-11
 */

Ext.define('Unidata.view.classifier.item.attribute.ClassifierArrayAttributeModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.classifier.item.arrayattribute',

    data: {
        deletable: null,
        classifierNode: null,
        nodeAttribute: null,
        ownAttribute: null,
        inheritedAttribute: null,
        readOnly: null,
        allowLiftUp: true,
        allowLiftDown: true
    },

    stores: {},

    formulas: {
        /**
         * Определяет видиость кнопки удаления атрибута
         */
        deleteAttributeButtonVisible: {
            bind: {
                deletable: '{deletable}',
                nodeAttribute: '{nodeAttribute}',
                ownAttribute: '{ownAttribute}',
                inheritedAttribute: '{inheritedAttribute}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var result = false,
                    nodeAttribute = getter.nodeAttribute,
                    ownAttribute = getter.ownAttribute,
                    inheritedAttribute = getter.inheritedAttribute;

                // только чтение - всегда ro
                if (getter.readOnly) {
                    return false;
                }

                if (ownAttribute && !inheritedAttribute && nodeAttribute === ownAttribute) {
                    result = getter.deletable ? true : false;
                }

                result = Ext.coalesceDefined(result, false);

                return result;
            }
        },

        /**
         * Определяет видиость кнопки восстановления атрибута
         */
        restoreAttributeButtonVisible: {
            bind: {
                nodeAttribute: '{nodeAttribute}',
                ownAttribute: '{ownAttribute}',
                inheritedAttribute: '{inheritedAttribute}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var result = false,
                    nodeAttribute = getter.nodeAttribute,
                    ownAttribute = getter.ownAttribute,
                    inheritedAttribute = getter.inheritedAttribute;

                // только чтение - всегда ro
                if (getter.readOnly) {
                    return false;
                }

                if (ownAttribute && inheritedAttribute && nodeAttribute === ownAttribute) {
                    result = true;
                }

                result = Ext.coalesceDefined(result, false);

                return result;
            }
        },

        /**
         * Атирбут созданный или сохранялся на сервере?
         */
        nodeAttributePhantom: {
            bind: {
                nodeAttribute: '{nodeAttribute}',
                deep: true
            },
            get: function (getter) {
                var phantom = true;

                if (getter.nodeAttribute) {
                    phantom = getter.nodeAttribute.phantom;
                }

                phantom = Ext.coalesceDefined(phantom, false);

                return phantom;
            }
        },

        /**
         * Определяет состояние поля для свойства "значение" атрибута
         */
        attributeFieldNotUseHasDataReadOnly: {
            bind: {
                nodeAttribute: '{nodeAttribute}',
                inheritedAttribute: '{inheritedAttribute}',
                deletable: '{deletable}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var readOnly = false,
                    inheritedAttribute = getter.inheritedAttribute;

                // только чтение - всегда ro
                if (getter.readOnly) {
                    readOnly = true;
                }

                if (inheritedAttribute && inheritedAttribute.get('value') !== null) {
                    readOnly = true;
                }

                readOnly = Ext.coalesceDefined(readOnly, true);

                return readOnly;
            }
        },

        /**
         * Определяет состояние поля для свойства "значение" атрибута
         */
        displayNameFieldReadOnly: {
            bind: {
                nodeAttribute: '{nodeAttribute}',
                inheritedAttribute: '{inheritedAttribute}',
                deletable: '{deletable}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var readOnly = false,
                    inheritedAttribute = getter.inheritedAttribute;

                // только чтение - всегда ro
                if (getter.readOnly) {
                    readOnly = true;
                }

                // если атрибут наследованый то переопределять отображаемое имя нельзя
                if (inheritedAttribute) {
                    readOnly = true;
                }

                return Ext.coalesceDefined(readOnly, true);
            }
        },

        /**
         * Определяет состояние для свойств атрибута (все исключая свойство "значение")
         */
        attributeFieldReadOnly: {
            bind: {
                nodeAttributePhantom: '{nodeAttributePhantom}',
                classifierNode: '{classifierNode}',
                deletable: '{deletable}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var readOnly = false,
                    hasData = false;

                // только чтение - всегда ro
                if (getter.readOnly) {
                    readOnly = true;
                }

                if (!getter.deletable) {
                    readOnly = true;
                }

                if (getter.classifierNode) {
                    hasData = getter.classifierNode.get('hasData');

                    if (hasData && !getter.nodeAttributePhantom) {
                        readOnly = true;
                    }
                }

                readOnly = Ext.coalesceDefined(readOnly, true);

                return readOnly;
            }
        },

        /**
         * Заголовок панели
         */
        classifierNodeAttributePanelTitle: {
            bind: {
                bindTo: '{nodeAttribute}',
                deep: true
            },
            get: function (nodeAttribute) {
                var title = '',
                    tpl = new Ext.XTemplate('<b>{prefix:htmlEncode} [{array}]</b>'),
                    displayName;

                // TODO: extract to buildTitle method (?)
                if (nodeAttribute) {
                    displayName = nodeAttribute.get('displayName');

                    if (nodeAttribute.phantom && displayName === '') {
                        title = tpl.apply({
                            prefix: Unidata.i18n.t('glossary:newAttribute').toUpperCase(),
                            array: '<span style="font-weight: normal;">' + Unidata.i18n.t('glossary:array').toLowerCase() + '</span>'
                        });
                    } else {
                        title = tpl.apply({
                            prefix: displayName.toUpperCase(),
                            array: '<span style="font-weight: normal;">' + Unidata.i18n.t('glossary:array').toLowerCase() + '</span>'
                        });
                    }
                }

                title = Ext.coalesceDefined(title, '');

                return title;
            }
        }
    }
});
