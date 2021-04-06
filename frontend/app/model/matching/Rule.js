Ext.define('Unidata.model.matching.Rule', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.matching.MatchingAlgorithm'
    ],

    idProperty: 'id',

    /*отрицательные идентификаторы для новых записей*/
    identifier: 'negative',

    fields: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'entityName',
            type: 'string'
        }
    ],

    hasMany: [
        {
            name: 'matchingAlgorithms',
            model: 'matching.MatchingAlgorithm'
        }
    ],

    proxy: {
        type: 'rest',
        url: Unidata.Config.getMainUrl() + 'internal/matching/rules/',
        writer: {
            type: 'json',
            writeAllFields: true,
            allDataOptions: {
                persist: true,
                associated: true
            }
        },
        reader: {
            type: 'json',
            rootProperty: 'content'
        }
    },

    isRuleDirty: function () {
        var rule  = this,
            dirty = false,
            matchingAlgorithms,
            updated,
            created,
            deleted;

        if (!rule) {
            return false;
        }

        if (rule.dirty /*|| rule.phantom*/) {
            dirty = true;
        }

        matchingAlgorithms = rule.matchingAlgorithms();

        updated = matchingAlgorithms.getUpdatedRecords();
        created = matchingAlgorithms.getNewRecords();
        deleted = matchingAlgorithms.getRemovedRecords();

        if (updated.length || created.length || deleted.length) {
            dirty = true;
        }

        rule.matchingAlgorithms().each(function (matchingAlgorithm) {
            var matchingFields = matchingAlgorithm.matchingFields(),
                updated        = matchingFields.getUpdatedRecords(),
                created        = matchingFields.getNewRecords(),
                deleted        = matchingFields.getRemovedRecords();

            if (updated.length || created.length || deleted.length) {
                dirty = true;
            }
        });

        return dirty;
    },

    rejectRuleChanges: function () {
        var rule = this;

        rule.matchingAlgorithms().each(function (matchingAlgorithm) {
            var matchingFields = matchingAlgorithm.matchingFields();

            matchingFields.rejectChanges();
        });

        rule.matchingAlgorithms().rejectChanges();
    }
});
