Ext.define('Unidata.view.admin.schema.SchemaController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.admin.schema',

    entities: null,

    init: function () {
        var store = this.getViewModel().getStore('schemaStore');

        store.load();
    },

    /**
     * @param {Ext.data.Store} store
     * @param {Ext.data.Model[]} records
     */
    onSchemaLoad: function (store, records, success) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            relations = viewModel.getStore('relationsStore'),
            entities = [],
            entityCount = 0,
            entityLoadCount = 0,
            i,
            record,
            me = this;

        function onSuccessEntityLoad (entity) {
            entityLoadCount++;
            entities.push(entity);

            if (entityLoadCount === entityCount) {
                if (view.getEl() && view.getEl().isVisible(true)) {
                    viewModel.set('entities', entities);

                    relations.load();
                } else {
                    me.entities = entities;
                }
            }
        }

        // TODO: переделать на промисы
        if (success) {
            for (i in records) {
                if (records.hasOwnProperty(i)) {
                    record = records[i].mapToObject();

                    entityCount++;

                    if (record['value'] === 'Entity') {
                        Unidata.model.entity.Entity.load(record.name, {
                            success: onSuccessEntityLoad
                        });
                    } else if (record['value'] === 'LookupEntity') {
                        Unidata.model.entity.LookupEntity.load(record.name, {
                            success: onSuccessEntityLoad
                        });
                    }
                }
            }
        }
    },

    /**
     * @param {Ext.data.Store} store
     * @param {Ext.data.Model[]} records
     */
    onRelationLoad: function (store, records, success) {
        if (success) {
            this.getViewModel().set('relations', records);
        }
    },

    onComponentAdded: function () {
        var viewModel = this.getViewModel(),
            relations = viewModel.getStore('relationsStore');

        if (this.entities) {
            viewModel.set('entities', this.entities);

            relations.load();

            this.entities = null;
        }
    }
});
