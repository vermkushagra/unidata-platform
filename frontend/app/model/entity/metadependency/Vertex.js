/**
 * @author Aleksandr Bavin
 * @date 2017-02-17
 */
Ext.define('Unidata.model.entity.metadependency.Vertex', {

    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'action',
            type: 'string'
        },
        {
            name: 'displayName',
            type: 'string'
        },
        {
            name: 'statuses',
            type: 'auto'
        },
        {
            name: 'existence',
            type: 'string'
        },
        {
            name: 'type',
            type: 'string'
        },
        {
            name: 'customProps',
            type: 'auto'
        }
    ],

    statics: {
        status: {                   // наличие тех или иных статусов и сообщений к ним
            OK: 'OK',
            ERROR: 'ERROR',
            WARNING: 'WARNING'
        },
        existence: {
            NEW: 'NEW',             // есть только в импортируемых данных
            EXIST: 'EXIST',         // есть только в системе
            UPDATE: 'UPDATE',       // есть и в системе и в импортируемых данных
            NOT_FOUND: 'NOT_FOUND'  // нет нигде
        },
        action: {
            NONE: 'NONE',           // ничего не делаем
            UPSERT: 'UPSERT'        // импортируем
        },
        type: {
            ENTITY: 'ENTITY',
            LOOKUP: 'LOOKUP',
            ENUM: 'ENUM',
            CLASSIFIER: 'CLASSIFIER',
            MEASURE: 'MEASURE',
            CUSTOM_CF: 'CUSTOM_CF',
            COMPOSITE_CF: 'COMPOSITE_CF',
            MATCH_RULE: 'MATCH_RULE',
            MERGE_RULE: 'MERGE_RULE',
            SOURCE_SYSTEM: 'SOURCE_SYSTEM',
            ZIP: 'ZIP',
            RELATION: 'RELATION',
            NESTED_ENTITY: 'NESTED_ENTITY',
            GROUPS: 'GROUPS'
        }
    },

    flags: [], // массив из флагов на основе status, action, existence

    // результат проверок на ui
    uiError: false,
    uiWarning: false,

    /**
     * Собирает массив из флагов на основе status, action, existence
     *
     * @returns {boolean} если флаги изменились - true, если нет - false
     */
    updateFlags: function () {
        var Vertex = Unidata.model.entity.metadependency.Vertex,
            status = Vertex.status,
            action = Vertex.action,
            existence = Vertex.existence,
            vertexAction = this.get('action'),
            vertexStatuses = this.get('statuses'),
            vertexExistence = this.get('existence'),
            oldFlags = this.flags;

        this.flags = [];

        this.flags.push(vertexAction);
        this.flags.push(vertexExistence);

        Ext.Object.each(vertexStatuses, function (key, value) {
            if (value.length) {
                this.flags.push(key);
            }
        });

        return !Ext.Array.equals(this.flags, oldFlags);
    },

    /**
     * Делает проверку на наличие статуса
     *
     * @param statusToCheck
     * @returns {boolean}
     */
    hasStatus: function (statusToCheck) {
        var vertexStatuses = this.get('statuses'),
            result = false;

        Ext.Array.each(vertexStatuses, function (status) {
            if (status.status === statusToCheck) {
                result = true;

                return false;
            }
        }, this);

        return result;
    },

    /**
     * @returns {boolean}
     */
    hasError: function () {
        var status = Unidata.model.entity.metadependency.Vertex.status;

        return this.hasStatus(status.ERROR);
    },

    /**
     * @returns {boolean}
     */
    hasWarning: function () {
        var status = Unidata.model.entity.metadependency.Vertex.status;

        return this.hasStatus(status.WARNING);
    },

    getCustomPropValue: function (key) {
        var customProps = this.get('customProps'),
            map,
            value = null;

        if (!Ext.isArray(customProps)) {
            return value;
        }

        map = customProps.reduce(function (map, obj) {
            map[obj.key] = obj.value;

            return map;
        }, {});

        return map[key];
    }
});
