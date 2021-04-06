Ext.define('Unidata.view.admin.entity.metarecord.model.ModelModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.metarecord.model',

    data: {
        relations: {},
        entities: []
    },

    constructor: function (config) {

        this.callParent([config]);

        this.bind('{currentRecord}', this.onCurrentRecordChanged, this, {
            deep: true
        });

    },

    onCurrentRecordChanged: function (currentRecord) {
        var view = this.getView();

        view.diagram.setLocalStorageNamespace(currentRecord.get('name'));

        this.collectData(currentRecord);
    },

    collectData: function (currentRecord, recursion) {
        var me = this,
            deferred = new Ext.Deferred(),
            view = this.getView(),
            draftMode = view.draftMode,
            promises = [],
            loadingCount = 0,
            entities = recursion ? [] : [currentRecord],
            relations = [],
            loadingEntites = {},
            loadingLookupEntites = {};

        promises.push(deferred.promise);

        loadingEntites[currentRecord.get('name')] = true;

        function onSuccessEntityLoad (entity) {
            entities.push(entity);

            loadingCount--;

            checkLoadingStatus();
        }

        function onFailureEntityLoad () {
            deferred.resolve([], []);
        }

        function checkLoadingStatus () {

            if (loadingCount === 0) {
                deferred.resolve([entities, relations]);
            }

        }

        if (typeof currentRecord.relations === 'function') {

            currentRecord.relations().each(function (relation) {

                var toEntityId = relation.get('toEntity');

                if (Ext.isEmpty(toEntityId)) {
                    return;
                }

                relations.push(relation);

                if (loadingEntites.hasOwnProperty(toEntityId)) {
                    return;
                }

                loadingEntites[toEntityId] = true;

                loadingCount++;

                Unidata.model.entity.Entity.load(toEntityId, {
                    params: {
                        draft: draftMode
                    },
                    success: onSuccessEntityLoad
                });

            });

        }

        if (typeof currentRecord.simpleAttributes === 'function') {

            currentRecord.simpleAttributes().each(function (attr) {

                var toEntityId;

                if (attr.get('typeCategory') !== 'lookupEntityType') {
                    return;
                }

                toEntityId = attr.get('typeValue');

                if (loadingLookupEntites.hasOwnProperty(toEntityId)) {
                    return;
                }

                loadingLookupEntites[toEntityId] = true;

                loadingCount++;

                Unidata.model.entity.LookupEntity.load(toEntityId, {
                    params: {
                        draft: draftMode
                    },
                    success: onSuccessEntityLoad,
                    failure: onFailureEntityLoad
                });

            });
        }

        if (typeof currentRecord.arrayAttributes === 'function') {

            currentRecord.arrayAttributes().each(function (attr) {

                var toEntityId;

                if (attr.get('typeCategory') !== 'lookupEntityType') {
                    return;
                }

                toEntityId = attr.get('typeValue');

                if (loadingLookupEntites.hasOwnProperty(toEntityId)) {
                    return;
                }

                loadingLookupEntites[toEntityId] = true;

                loadingCount++;

                Unidata.model.entity.LookupEntity.load(toEntityId, {
                    success: onSuccessEntityLoad,
                    failure: onFailureEntityLoad
                });

            });
        }

        if (typeof currentRecord.complexAttributes === 'function') {

            currentRecord.complexAttributes().each(function (complexAttribute) {
                promises = promises.concat(
                    me.collectData(complexAttribute.getNestedEntity(), true)
                );
            });
        }

        // при повторном вызове не трогаем данные
        if (!recursion) {
            Ext.Deferred.all(promises).then(function (promisedData) {
                var entities = [],
                    relations = [];

                Ext.Array.each(promisedData, function (data) {
                    entities = entities.concat(data[0]);
                    relations = relations.concat(data[1]);
                });

                me.set('entities', entities);
                me.set('relations', relations);
            });
        }

        checkLoadingStatus();

        return promises;
    }

});
