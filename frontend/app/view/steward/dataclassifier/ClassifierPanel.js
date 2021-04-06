/**
 * Панель отображающая список классификаторов доступных для записи
 *
 * @author Ivan Marshalkin
 * @date 2016-08-08
 */

Ext.define('Unidata.view.steward.dataclassifier.ClassifierPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.dataclassifier.item.ClassifierItem'
    ],

    alias: 'widget.dataclassifier.classifierpanel',

    referenceHolder: true,

    eventBusHolder: true,
    bubbleBusEvents: [],

    cls: 'un-dataclassifier-panel',

    config: {
        metaRecord: null,
        dataRecord: null,
        classifierNodes: null,
        // стор с классификаторами, который отфильтрован по metaRecord.classifiers
        classifierStore: null,
        readOnly: null
    },

    classifierItemClass: 'Unidata.view.steward.dataclassifier.item.ClassifierItem',

    items: [],

    /**
     * Инициализация компонента
     */
    initComponent: function () {
        this.callParent(arguments);

        this.addComponentListener('datarecordclassifiernodechange', this.onClassifierNodeChange, this);
    },

    updateMetaRecord: function (metaRecord) {
        if (metaRecord) {
            this.initClassifierStore();
        }
    },

    /**
     * Создаёт стор с классификаторами, если его нет
     * Фильтрует стор по metaRecord.classifiers
     */
    initClassifierStore: function () {
        var metaRecord  = this.getMetaRecord(),
            classifiers = metaRecord.get('classifiers'),
            classifierStore = this.getClassifierStore();

        if (!classifierStore) {
            classifierStore = Ext.create('Ext.data.Store', {
                model: 'Unidata.model.classifier.Classifier',
                proxy: 'un.classifier',
                autoLoad: true
            });

            this.setClassifierStore(classifierStore);
        }

        classifierStore.clearFilter(true);

        // фильтруем данными из metaRecord.classifiers
        classifierStore.addFilter(Ext.create('Ext.util.Filter', {
            operator: 'in',
            property: 'name',
            value: classifiers
        }));
    },

    updateClassifierStore: function (classifierStore) {
        // обязательно нужен metaRecord для нинициализации, если его нет, инициализация запустится в updateMetaRecord
        if (classifierStore && this.getMetaRecord()) {
            this.initClassifierStore();
        }
    },

    /**
     * После загрузки классификаторов
     * @returns {Ext.promise.Promise}
     */
    whenClassifierStoreLoaded: function () {
        var deferred = Ext.create('Ext.Deferred'),
            classifierStore = this.getClassifierStore();

        if (classifierStore.isLoaded()) {
            deferred.resolve(classifierStore);
        } else {
            classifierStore.on('load', function (store, records, successful) {
                if (successful) {
                    deferred.resolve(classifierStore);
                } else {
                    deferred.reject(classifierStore);
                    Unidata.showMessage(Unidata.i18n.t('classifier>loadClassifiersError'));
                }
            }, this, {single: true});
        }

        return deferred.promise;
    },

    onClassifierNodeChange: function () {
        this.refreshClassifierNodesFromItemsPanel();
    },

    updateReadOnly: function (readOnly) {
        var items = this.items;

        if (items && items.isMixedCollection) {
            items.each(function (item) {
                item.setReadOnly(readOnly);
            });
        }
    },

    /**
     * Отображает информацию по узклам которыми классифицированна запись
     * @returns {Ext.promise.Promise}
     */
    displayClassifiers: function () {
        var me = this,
            deferred = Ext.create('Ext.Deferred');

        this.whenClassifierStoreLoaded().then(
            function (classifierStore) {
                me.removeAll();

                classifierStore.each(function (classifier) {
                    me.add(me.createClassifierItem(classifier));
                });

                deferred.resolve();
            }
        );

        return deferred.promise;
    },

    /**
     * Создает инстанс панельки с информацией по узлу которым классифицировали запись
     *
     * @param {Unidata.model.classifier.Classifier} classifier
     * @returns {Unidata.view.steward.dataclassifier.item.ClassifierItem}
     */
    createClassifierItem: function (classifier) {
        var item,
            classifierNode,
            classifierNodeId = null,
            classifierName   = classifier.get('name'),
            classifierDisplayName = classifier.get('displayName'),
            title            = classifierDisplayName;

        classifierNode = this.findClassifierNodeByClassifierName(classifierName);

        if (classifierNode) {
            classifierNodeId = classifierNode.get('id');

            title += ': ' + classifierNode.get('text');
        }

        item = Ext.create(this.classifierItemClass, {
            metaRecord: this.getMetaRecord(),
            dataRecord: this.getDataRecord(),
            classifier: classifier,
            classifierNode: classifierNode,
            classifierName: classifierName,
            classifierNodeId: classifierNodeId,
            title: title,
            readOnly: this.getReadOnly(),
            collapsed: classifierNode ? true : false
        });

        return item;
    },

    /**
     * Ищем ноду по имени классификатора
     *
     * @param classifierName
     * @returns {*|Number|Object}
     */
    findClassifierNodeByClassifierName: function (classifierName) {
        var nodes      = this.getClassifierNodes(),
            resultNode = null;

        if (nodes) {
            resultNode = Ext.Array.findBy(nodes, function (node) {
                return node.get('classifierName') === classifierName;
            });
        }

        return resultNode;
    },

    refreshClassifierNodesFromItemsPanel: function () {
        var items           = this.items,
            classifierNodes = [];

        if (!items) {
            return;
        }

        items.each(function (item) {
            var classifierNode = item.getClassifierNode();

            if (classifierNode) {
                classifierNodes.push(classifierNode);
            }
        });

        this.setClassifierNodes(classifierNodes);

        this.fireEvent('classifiernodeschange', classifierNodes);
    }
});
