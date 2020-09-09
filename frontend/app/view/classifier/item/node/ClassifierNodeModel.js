/**
 * Экран "Узел классификатора" (модель)
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */

Ext.define('Unidata.view.classifier.item.node.ClassifierNodeModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.classifier.item.node',

    data: {
        classifier: null,
        classifierNode: null,
        readOnly: null
    },

    stores: {},

    formulas: {
        classifierNodePanelTitle: {
            bind: {
                classifierNode: '{classifierNode}',
                classifier: '{classifier}',
                deep: true
            },
            get: function (getter) {
                var classifier = getter.classifier,
                    classifierNode = getter.classifierNode,
                    title = '',
                    name;

                // TODO: extract to buildTitle method (?)
                if (classifierNode) {
                    name = classifierNode.get('name');

                    if (classifierNode.phantom && name === '') {
                        title = Unidata.i18n.t('classifier>newClassifierNode');
                    } else {
                        title = Unidata.util.Classifier.buildClassifierNodeShortTitle(classifier, classifierNode);
                    }
                }

                title = Ext.coalesceDefined(title, '');

                return title;
            }
        },

        classifierNodePhantom: {
            bind: {
                bindTo: '{classifierNode}',
                deep: true
            },
            get: function (classifierNode) {
                var phantom = true;

                if (classifierNode) {
                    phantom = classifierNode.phantom;
                }

                phantom = Ext.coalesceDefined(phantom, true);

                return phantom;
            }
        },

        classifierNodeReadOnly: {
            bind: {
                classifierNode: '{classifierNode}',
                classifier: '{classifier}',
                classifierNodePhantom: '{classifierNodePhantom}'
            },
            get: function (getter) {
                var readOnly = true,
                    phantom = getter.classifierNodePhantom,
                    classifierNode = getter.classifierNode,
                    classifier = getter.classifier,
                    classifierName,
                    userHasCreateRight,
                    userHasUpdateRight;

                if (classifierNode) {
                    classifierName = classifier.get('name');

                    userHasCreateRight = Unidata.Config.userHasRight(classifierName, 'create');
                    userHasUpdateRight = Unidata.Config.userHasRight(classifierName, 'update');

                    readOnly = (phantom && !userHasCreateRight) || (!phantom && !userHasUpdateRight);
                }

                readOnly = Ext.coalesceDefined(readOnly, true);

                return readOnly;
            }
        },

        classifierNodeNameAndCodeReadOnly: {
            bind: {
                classifierNodeReadOnly: '{classifierNodeReadOnly}',
                classifierNode: '{classifierNode}'
            },
            get: function (getter) {
                var classifierNodeReadOnly = getter.classifierNodeReadOnly,
                    classifierNode = getter.classifierNode,
                    isRoot = false,
                    readOnly;

                if (classifierNode) {
                    isRoot = classifierNode.isRoot();
                }

                readOnly = classifierNodeReadOnly || isRoot;

                readOnly = Ext.coalesceDefined(readOnly, true);

                return readOnly;
            }
        },

        classifierPatternCodeEmpty: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var empty = true,
                    codePattern;

                if (classifier) {
                    codePattern = classifier.get('codePattern');
                    empty = !codePattern;
                }

                empty = Ext.coalesceDefined(empty, true);

                return empty;
            }
        }
    }
});
