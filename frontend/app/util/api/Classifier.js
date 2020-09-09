/**
 * API взаимодействия с классификаторами
 *
 * @author Ivan Marshalkin
 * @date 2016-08-09
 */

Ext.define('Unidata.util.api.Classifier', {
    extend: 'Unidata.util.api.AbstractApi',
    singleton: true,

    /**
     * Возвращает глобальный стор с классификаторами
     * @returns {Ext.data.Store}
     */
    getClassifiersStore: function () {
        var store = Ext.data.StoreManager.lookup('classifiers');

        if (!store) {
            store = Ext.create('Unidata.store.classifier.ClassifierStore', {
                storeId: 'classifiers',
                autoLoad: true
            });
        }

        return store;
    },

    /**
     * Загрузка стора с классификаторами
     * @param forceReload
     * @returns {Ext.promise.Promise}
     */
    loadClassifiersStore: function (forceReload) {
        return this.loadStore(this.getClassifiersStore(), forceReload);
    },

    /**
     * Возвращает промис с классификатором по имени классификатора
     * @param classifierName
     * @returns {Ext.promise.Promise}
     */
    getClassifier: function (classifierName) {
        var deferred = Ext.create('Ext.Deferred'),
            store = this.getClassifiersStore(),
            errorMsg;

        this.loadStore(store)
            .then(
                function (classifiersStore) {
                    var classifier = classifiersStore.findRecord('name' , classifierName, 0, false, false, true);

                    if (classifier) {
                        deferred.resolve(classifier);
                    } else {
                        errorMsg = Unidata.i18n.t('classifier>classifierInMetamodelNotExists', {
                            classifierName: classifierName
                        });
                        deferred.reject(errorMsg);
                    }

                },
                function () {
                    deferred.reject();
                }
            )
            .done();

        return deferred.promise;
    },

    /**
     * Возвращает модель узла классификатора по идентификатору
     *
     * @param classifierName - кодовое имя классификатора
     * @param classifierNodeId - идентификатор ноды в классификаторе
     * @param view{string} - представление ноды (EMPTY|PATH|DATA|META)
     *
     * @returns {null|Ext.promise|*|Ext.promise.Promise}
     */
    getClassifierNodeById: function (classifierName, classifierNodeId, view) {
        var node;

        node = Ext.create('Unidata.model.classifier.ClassifierNode', {
            id: classifierNodeId,
            classifierName: classifierName
        });

        return this.getClassifierNode(node, view);
    },

    /**
     * Возвращает модель узла классификатора
     *
     * @param {Unidata.model.classifier.ClassifierNode} node - нода в классификаторе
     * @param {string} view - представление ноды (EMPTY|PATH|DATA|META)
     *
     * @returns {Ext.promise.Promise}
     */
    getClassifierNode: function (node, view) {
        var deferred = new Ext.Deferred(),
            classifierName = node.get('classifierName'),
            proxy;

        proxy = node.getProxy();
        proxy.setExtraParam('classifierName', classifierName);

        // TODO: use default from proxy
        view = view || 'EMPTY';

        if (view) {
            proxy.setExtraParam('view', view);
        }

        // узел загружается повторно но с точки зрения сенчи это не совсем корректно поэтому ручками удаляем все hasmany
        // если не удалять то производится задвоение
        Ext.Object.each(node.associations, function (associationName, association) {
            if (association.association instanceof Ext.data.schema.ManyToOne) {
                if (association && association.getterName && Ext.isFunction(node[association.getterName])) {
                    node[association.getterName]().removeAll();

                    //dirty hack for UUN-8129
                    node[association.getterName]().removed = [];

                }
            }
        });

        node.load({
                success: function (node) {
                    deferred.resolve(node);
                },
                failure: function () {
                    deferred.reject();
                }
            }
        );

        return deferred.promise;
    },

    /**
     * Удалить узел классификатора
     * @param node
     * @returns {null|Ext.Deferred.Promise}
     */
    deleteClassifierNode: function (node) {
        var deferred = new Ext.Deferred(),
            classifierName = node.get('classifierName'),
            classifierNodeId = node.getId(),
            tpl,
            tplParams,
            url;

        tpl = new Ext.Template('internal/data/classifier/{classifierName}/node/{classifierNodeId}');
        tpl.compile();

        tplParams = {
            classifierName: classifierName,
            classifierNodeId: classifierNodeId
        };

        url = Unidata.Config.getMainUrl() + tpl.apply(tplParams);

        Ext.Ajax.request({
            url: url,
            method: 'DELETE',
            success: function (response) {
                var responseJson;

                responseJson = Ext.util.JSON.decode(response.responseText, true);

                if (responseJson.success === true) {
                    deferred.resolve(node);
                } else {
                    deferred.reject(node);
                }
            },
            failure: function () {
                deferred.reject(node);
            }
        });

        return deferred.promise;
    },

    /**
     * Возвращает массив моделей узлов классификаторов
     *
     * Пример параметров функции
     *
     * cfgClassifiers = [
     *      {
     *          classifierName: 'firstClassifier',
     *          nodeId: 'firstClassifierNodeId',
     *          view{string} - представление ноды (EMPTY|PATH|DATA|META)
     *      },
     *      {
     *          classifierName: 'secondClassifier',
     *          nodeId: 'secondClassifierNodeId',
     *          view{string} - представление ноды (EMPTY|PATH|DATA|META)
     *      }
     * ]
     *
     * @param {Object[]|Unidata.model.classifier.ClassifierNode[]} cfgClassifiers - массив параметров для функции получения модели узла классификатора
     * @param cfgClassifiers.classifierName
     * @param cfgClassifiers.classifierNodeId
     * @param cfgClassifiers.view - представление ноды (EMPTY|PATH|DATA|META)
     *
     * @param view - представление ноды (EMPTY|PATH|DATA|META)
     *
     * @returns {Ext.promise.Promise}
     */
    getClassifierNodes: function (cfgClassifiers, view) {
        var me       = this,
            promises = [];

        Ext.Array.each(cfgClassifiers, function (cfg) {
            var promise;

            if (cfg instanceof Unidata.model.classifier.ClassifierNode) {
                promise = me.getClassifierNode(cfg, view);
            } else {
                promise = me.getClassifierNodeById(cfg.classifierName, cfg.classifierNodeId, view);
            }

            promises.push(promise);
        });

        return Ext.Deferred.all(promises);
    },

    /**
     * Загрузить и отобразить root node (в режиме "Бродилка")
     * @param classifierName
     * @param classifierNodeView META|DATA|PATH
     * @returns {*}
     */
    getRootNode: function (classifierName, classifierNodeView) {
        return this.getRootNodeForClassifierNode(classifierName, 'root', classifierNodeView);
    },

    /**
     * Загрузить и отобразить путь от корня для заданного узла (в режиме "Открывалка")
     *
     * @param classifierName
     * @param classifierNodeId
     * @param classifierNodeView META|DATA|PATH
     * @returns {*}
     */
    getRootNodeForClassifierNode: function (classifierName, classifierNodeId, classifierNodeView) {
        var promise,
            tpl,
            tplParams,
            url,
            params;

        tpl = new Ext.Template('internal/data/classifier/{classifierName}/node/{nodeId}');
        tpl.compile();

        tplParams = {
            classifierName: classifierName,
            nodeId: classifierNodeId
        };

        url = Unidata.Config.getMainUrl() + tpl.apply(tplParams);

        classifierNodeView = classifierNodeView || 'EMPTY';

        params = {
            view: classifierNodeView,
            classifierName: classifierName
        };

        promise = this.getClassifierTreeView(url, params);

        return promise;
    },

    /**
     * Получить путь от корня к найденнм узлам на основании поискового запроса
     *
     * @param classifierName
     * @param text
     * @returns {*}
     */
    getRootNodeBySearchRequest: function (classifierName, text) {
        var promise,
            tpl,
            tplParams,
            url,
            params;

        tpl = new Ext.Template('internal/data/classifier/{classifierName}/tree');
        tpl.compile();

        tplParams = {
            classifierName: classifierName
        };

        url = Unidata.Config.getMainUrl() + tpl.apply(tplParams);

        params = {
            text: text,
            classifierName: classifierName
        };

        promise = this.getClassifierTreeView(url, params);

        return promise;
    },

    getClassifierTreeView: function (url, params) {
        var deferred = new Ext.Deferred();

        // TODO: extract to a class method
        function onAjaxRequestSuccess (response) {
            var result;

            function cleanChildren (children) {
                var str,
                    search,
                    replacement;

                str = JSON.stringify(children);
                search = '"children":[]';
                replacement = '"children":null';
                children = str.split(search).join(replacement);

                children = JSON.parse(children);
                children.forEach(function (child) {
                    if (child.parentId === null) {
                        child.parentId = 'root';
                    }
                });

                return children;
            }

            function applyRootNodeWrapperJson (rootCfg) {
                rootCfg.children = cleanChildren(rootCfg.children);

                rootCfg.expanded = true;
                rootCfg.leaf = false;
                rootCfg.root = true;
                rootCfg.phantom = false;
                rootCfg.childCount = rootCfg.childCount ? rootCfg.childCount : 1;

                return rootCfg;
            }

            if (!response) {
                deferred.reject();

                return;
            }

            result = Ext.decode(response.responseText, true);

            result = result.content;

            if (result) {
                result = applyRootNodeWrapperJson(result);
            }
            deferred.resolve(result);
        }

        function onAjaxRequestFailure () {
            deferred.reject();
        }

        Ext.Ajax.request({
            url: url,
            method: 'GET',
            params: params,
            success: onAjaxRequestSuccess,
            failure: onAjaxRequestFailure
        });

        return deferred.promise;
    },

    /**
     * Получить информацию по классифицированным сущностям
     *
     * cfg.classifierName
     * cfg.classifierNodeId
     *
     * @param cfg
     * @returns {null|*|promise.promise|jQuery.promise|Ext.promise|d.promise}
     */
    getClassifierEntityStats: function (cfg) {
        var store,
            deferred;

        store = this.createClassifiedEntitiesStore(cfg);
        deferred = new Ext.Deferred();

        // подгружаем информацию о связи
        store.on('load', function (store, records, successful) {
            if (!successful) {
                deferred.reject();
            } else {
                deferred.resolve(records);
            }
        }, this, {
            single: true
        });

        store.load();

        return deferred.promise;
    },

    createClassifiedEntitiesStore: function (cfg) {
        var store,
            urlPrefix      = 'internal/data/classifier/entities-stat',
            classifierName = cfg.classifierName,
            classifierNodeId = cfg.classifierNodeId,
            url,
            mainUrl = Unidata.Config.getMainUrl();

        classifierNodeId = classifierNodeId || null;

        if (classifierNodeId) {
            url = Ext.String.format('{0}{1}/{2}/node/{3}', mainUrl, urlPrefix, classifierName, classifierNodeId);
        } else {
            url = Ext.String.format('{0}{1}/{2}/node/null', mainUrl, urlPrefix, classifierName);
        }

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.classifier.ClassifierEntityStat',
            proxy: {
                type: 'ajax',
                url: url,
                reader: {
                    rootProperty: 'content'
                },
                pageParam: '',
                limitParam: '',
                startParam: ''
            }
        });

        return store;
    },

    /**
     * Найти классифицированные записи
     *
     * cfg.classifierName
     * cfg.classifierNode
     * cfg.searchResultMap (path -> attribute)
     * cfg.store (optional)
     *
     * @param cfg
     * @returns {null|*|promise.promise|jQuery.promise|Ext.promise|d.promise}
     */
    searchClassifiedRecords: function (cfg) {
        var deferred = Ext.create('Ext.Deferred'),
            classifierName = cfg.classifierName,
            classifierNode = cfg.classifierNode,
            searchResultAttrMap = cfg.searchResultAttrMap,
            returnFields,
            store = cfg.store,
            proxy,
            extraParams;

        if (!classifierName || !classifierNode || !searchResultAttrMap) {
            return;
        }

        if (!store) {
            store = this.createSearchClassifiedRecordsStore(cfg);
        }

        proxy = store.getProxy();

        returnFields = Ext.Object.getKeys(cfg.metaRecordAttrMap);
        cfg.returnFields = returnFields;

        extraParams = this.buildSearchClassifiedRecordsExtraParams(cfg);
        proxy.setExtraParams(extraParams);

        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    deferred.resolve(store);
                } else {
                    deferred.reject();
                }
            }
        });

        return deferred.promise;
    },

    createSearchClassifiedRecordsStore: function () {
        var store;

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.search.SearchHit',
            pageSize: Unidata.Config.getCustomerCfg()['SEARCH_ROWS'],
            proxy: {
                type: 'data.searchproxycomplex'
            }
        });

        return store;
    },

    buildSearchClassifiedRecordsExtraParams: function (cfg) {
        var entityName = cfg.entityName,
            extraParams;

        extraParams = {
            entity: entityName,
            formFields: [],
            returnFields: Ext.Object.getKeys(cfg.metaRecordAttrMap),
            //sortFields  : sortFields;
            fetchAll: true,
            operator: 'AND',
            qtype: 'MATCH',
            supplementaryRequests: this.buildClassifierSearchSupplementaryRequest(cfg)
        };

        extraParams['@type'] = 'COMPLEX';  // нечто нужное бекенду

        return extraParams;
    },

    buildClassifierSearchSupplementaryRequest: function (cfg) {
        var request = {},
            entityName = cfg.entityName,
            classifierName = cfg.classifierName,
            classifierNode = cfg.classifierNode,
            classifierNodeId = classifierNode.get('id'),
            formFields;

        if (classifierNodeId) {
            formFields = [{
                inverted: false,
                name: [classifierName, '$nodes.$node_id'].join('.'),
                type: 'String',
                value: classifierNodeId
            }];
        } else {
            formFields = [{
                inverted: false,
                name: '$classifiers.$name',
                type: 'String',
                value: classifierName
            }];
        }

        request['@type'] = 'FORM';
        request['dataType'] = 'CLASSIFIER';
        request['entity'] = entityName;

        request['searchFields'] = [];
        request['returnFields'] = Ext.Object.getKeys(cfg.classifierAttrMap);
        request['fetchAll'] = false;
        request['qtype'] = 'TERM';
        request['operator'] = 'AND';

        request['formFields'] = formFields;

        return [request];
    },

    buildClassifierNodeAttrPaths: function (classifierName, classifierNode) {
        var nodeAttrs = classifierNode.nodeAttrs(),
            inheritedNodeAttrs = classifierNode.inheritedNodeAttrs(),
            nodeAttrNames,
            inheritedNodeNames,
            mapFn,
            paths;

        mapFn = this.mapNodeAttrToPath.bind(this, classifierName);
        nodeAttrNames = Ext.Array.map(nodeAttrs.getRange(), mapFn, this);
        inheritedNodeNames = Ext.Array.map(inheritedNodeAttrs.getRange(), mapFn, this);
        paths = Ext.Array.merge(nodeAttrNames, inheritedNodeNames);

        return paths;
    },

    buildClassifierNodeAttrs: function (classifierName, classifierNode) {
        var ownNodeAttrs = classifierNode.nodeAttrs(),
            ownNodeArrayAttrs = classifierNode.nodeArrayAttrs(),
            inheritedNodeAttrs = classifierNode.inheritedNodeAttrs(),
            inheritedNodeArrayAttrs = classifierNode.inheritedNodeArrayAttrs(),
            nodeAttrs;

        nodeAttrs = Ext.Array.merge(
            ownNodeAttrs.getRange(),
            ownNodeArrayAttrs.getRange(),
            inheritedNodeAttrs.getRange(),
            inheritedNodeArrayAttrs.getRange()
        );

        return nodeAttrs;
    },

    mapNodeAttrToPath: function (classifierName, nodeAttr) {
        var path;

        path = Ext.String.format('{0}.{1}', classifierName, nodeAttr.get('name'));

        return path;
    },

    buildSearchResultAttrMap: function (metaRecord, classifierName, classifierNode) {
        var attributes,
            attributePaths,
            attributeMap,
            classifierAttributes,
            classifierAttributeMap,
            resultMap;

        classifierAttributes = this.buildClassifierNodeAttrs(classifierName, classifierNode);
        classifierAttributes = Ext.Array.sort(classifierAttributes, function (a, b) {
            return a.get('order') - b.get('order');
        });
        classifierAttributeMap = Ext.Array.toValueMap(classifierAttributes, function (classifierName, classifierAttribute) {
            return this.mapNodeAttrToPath(classifierName, classifierAttribute);
        }.bind(this, classifierName), this);

        // TODO: заменить на метод getAttributesByFilter
        attributePaths = Unidata.util.UPathMeta.buildSimpleAttributePaths(metaRecord, [{
            property: 'displayable',
            value: true
        }]);
        attributes = Unidata.util.UPathMeta.findAttributesByPaths(metaRecord, attributePaths);
        attributes = Ext.Array.sort(attributes, function (a, b) {
            return a.get('order') - b.get('order');
        });
        attributeMap = Ext.Array.toValueMap(attributes, function (metaRecord, attribute) {
            return Unidata.util.UPathMeta.buildAttributePath(metaRecord, attribute);
        }.bind(this, metaRecord), this);

        resultMap = {
            all: Ext.Object.merge({}, attributeMap, classifierAttributeMap),
            metaRecord: attributeMap,
            classifier: classifierAttributeMap
        };

        return resultMap;
    },

    buildReturnFields: function (metaRecord, classifierName, classifierNode) {
        var attributePaths,
            classifierAttrPaths,
            returnFields;

        classifierAttrPaths = this.buildClassifierNodeAttrPaths(classifierName, classifierNode);

        attributePaths = Unidata.util.UPathMeta.buildAttributePaths(metaRecord, [{
            property: 'displayable',
            value: true
        }]);

        returnFields = Ext.Array.merge(classifierAttrPaths, attributePaths);

        return returnFields;
    }
});
