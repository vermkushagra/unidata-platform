Ext.define('Unidata.model.data.RelationTimeline', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'etalonId',
            type: 'string'
        }
    ],

    hasMany: [
        {
            name: 'timeline',
            model: 'data.TimeInterval'
        }
    ],

    statics: {
        getRelationTypeDisplayName: function (name) {
            var relTypes = Unidata.Constants.getRelTypes(),
                found;

            found = Ext.Array.findBy(relTypes, function (item) {
                return item.key === name;
            });

            return found ? found.value : '';
        },
        /**
         * Возвращает alias для типа связи
         * @param name
         * @param fallback Если true, то возвращается name
         * @returns {*}
         */
        getRelationTypeAlias: function (name, fallback) {
            var relTypes = Unidata.Constants.getRelTypes(),
                alias,
                found;

            fallback = Ext.isBoolean(fallback) ? fallback : true;

            found = Ext.Array.findBy(relTypes, function (item) {
                return item.key === name;
            });

            if (found) {
                alias = found.alias;

                if (found && !alias && fallback) {
                    alias = found.key;
                }
            }

            return alias;
        }
    }
});
