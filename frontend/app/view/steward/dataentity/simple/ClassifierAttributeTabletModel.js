Ext.define('Unidata.view.steward.dataentity.simple.ClassifierAttributeTabletModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.dataentity.simple.classifierattributetablet',

    data: {
        metaClassifierNode: null,
        classifier: null,
        readOnly: false
    },

    stores: {},

    formulas: {
        title: {
            bind: {
                classifier: '{classifier}',
                metaClassifierNode: '{metaClassifierNode}'
            },
            get: function (getter) {
                return Unidata.util.Classifier.buildClassifierNodeShortTitle(getter.classifier, getter.metaClassifierNode);
            }
        }
    }
});
