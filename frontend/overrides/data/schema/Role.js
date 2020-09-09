/**
 * @author Aleksandr Bavin
 * @date 2018-04-13
 */
Ext.define('Ext.overrides.data.schema.Role', {

    override: 'Ext.data.schema.Role',

    getAssociatedStore: function (inverseRecord, options, scope, records, isComplete) {
        // Consider the Comment entity with a ticketId to a Ticket entity. The Comment
        // is on the left (the FK holder's side) so we are implementing the guts of
        // the comments() method to load the Store of Comment entities. This trek
        // begins from a Ticket (inverseRecord).

        var me = this,
            storeName = me.getStoreName(),
            store = inverseRecord[storeName],
            load = options && options.reload,
            source = inverseRecord.$source,
            session = inverseRecord.session,
            args, i, len, raw, rec, sourceStore;

        if (!store) {
            // We want to check whether we can automatically get the store contents from the parent session.
            // For this to occur, we need to have a parent in the session, and the store needs to be created
            // and loaded with the initial dataset.
            if (!records && source) {
                source = source[storeName];

                if (source && !source.isLoading()) {
                    sourceStore = source;
                    records = [];
                    raw = source.getData().items;

                    for (i = 0, len = raw.length; i < len; ++i) {
                        rec = raw[i];
                        records.push(session.getRecord(rec.self, rec.id));
                    }
                    isComplete = true;
                }
            }
            store = me.createAssociationStore(session, inverseRecord, records, isComplete);
            store.$source = sourceStore;

            if (!records && (me.autoLoad || options)) {
                load = true;
            }

            inverseRecord[storeName] = store;
        } else {
            // если стор уже есть, то просто тихо(!) добавляем новые записи, предварительно вычистив старые
            if (records) {
                store.suspendEvents();
                store.removeAll();
                store.add(records);
                store.resumeEvents();
            }
        }

        if (options) {
            // We need to trigger a load or the store is already loading. Defer
            // callbacks until that happens
            if (load || store.isLoading()) {
                store.on('load', function (store, records, success, operation) {
                    args = [store, operation];
                    scope = scope || options.scope || inverseRecord;

                    if (success) {
                        Ext.callback(options.success, scope, args);
                    } else {
                        Ext.callback(options.failure, scope, args);
                    }
                    args.push(success);
                    Ext.callback(options, scope, args);
                    Ext.callback(options.callback, scope, args);
                }, null, {single: true});
            } else {
                // Trigger straight away
                args = [store, null];
                scope = scope || options.scope || inverseRecord;

                Ext.callback(options.success, scope, args);
                args.push(true);
                Ext.callback(options, scope, args);
                Ext.callback(options.callback, scope, args);
            }
        }

        if (load && !store.isLoading()) {
            store.load();
        }

        return store;
    }

});
