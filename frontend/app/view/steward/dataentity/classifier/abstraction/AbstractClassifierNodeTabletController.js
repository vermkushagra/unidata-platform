/**
 * Абстрактный компонент представления коллекции узлов классификации
 * @author Sergey Shishigin
 * @date 2018-05-031
 */
Ext.define('Unidata.view.steward.dataentity.classifier.abstraction.AbstractClassifierNodeTabletController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.dataentity.classifiernode.abstractclassifiernodetablet',

    /**
     *
     * @return {Unidata.model.classifier.ClassifierNode[] }
     */
    buildTablet: function () {
        var me = this,
            view = this.getView(),
            dataClassifierNodes = view.getDataClassifierNodes(),
            metaClassifierNodes = view.getMetaClassifierNodes(),
            containers;

        containers = Ext.Array.map(metaClassifierNodes, function (metaClassifierNode) {
            var container,
                dataClassifierNode;

            dataClassifierNode = Ext.Array.findBy(dataClassifierNodes, function (node) {
                return node.get('classifierNodeId') === metaClassifierNode.get('id');
            });

            container = me.createClassifierAttributeTablet(metaClassifierNode, dataClassifierNode);

            return container;
        });

        return containers;
    },

    createClassifierAttributeTablet: function (metaClassifierNode, dataClassifierNode) {
        var view = this.getView(),
            preventMarkField = view.getPreventMarkField(),
            metaRecord = view.getMetaRecord(),
            dataRecord = view.getDataRecord(),
            readOnly = view.getReadOnly(),
            hiddenAttribute = view.getHiddenAttribute(),
            classifier = view.getClassifier(),
            qaId = null,
            attributePath,
            container;

        attributePath = this.buildAttributePath(classifier, dataClassifierNode);

        if (metaClassifierNode) {
            qaId = metaClassifierNode.get('id');
        }

        container = Ext.create('Unidata.view.steward.dataentity.simple.ClassifierAttributeTablet', {
            classifier: classifier,
            metaClassifierNode: metaClassifierNode,
            dataClassifierNode: dataClassifierNode,
            metaRecord: metaRecord,
            dataRecord: dataRecord,
            readOnly: readOnly,
            hiddenAttribute: hiddenAttribute,
            preventMarkField: preventMarkField,
            attributePath: attributePath,
            qaId: qaId
        });

        container.on('removeclassifiernode', this.onRemoveClassifierNode, this);

        return container;
    },

    buildAttributePath: function (classifier, dataClassifierNode) {
        var attributePath;

        if (!classifier || !dataClassifierNode || !dataClassifierNode.get('etalonId')) {
            return '';
        }

        attributePath = [classifier.get('name'), dataClassifierNode.get('etalonId')].join('.');

        return attributePath;
    },

    onRemoveClassifierNode: function (self, classifierNode) {
        var view = this.getView(),
            dataRecord = view.getDataRecord(),
            dataClassifierNodes = dataRecord.classifiers();

        if (!classifierNode) {
            return;
        }

        dataClassifierNodes.dirty = true;
    }
});
