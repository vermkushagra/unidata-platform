/**
 * @author Ivan Marshalkin
 * @date 2016-08-08
 */

Ext.define('Unidata.view.steward.dataclassifier.item.ClassifierItemModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.dataclassifier.classifieritempanel',

    data: {
        readOnly: null,
        classifierNode: null
    },

    formulas: {
        isDisabled: {
            bind: {
                readOnly: '{readOnly}',
                classifierNode: '{classifierNode}',
                classifierNodeId: '{classifierNodeId}'
            },
            get: function (getter) {
                var readOnly = getter.readOnly,
                    classifierNode = getter.classifierNode,
                    classifierNodeId = getter.classifierNodeId,
                    disabled;

                disabled = !classifierNodeId && !classifierNode && readOnly;

                disabled = Ext.coalesceDefined(disabled, false);

                return disabled;
            }
        }
    }
});
