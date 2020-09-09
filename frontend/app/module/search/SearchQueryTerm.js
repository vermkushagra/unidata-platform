/**
 * Минимальная единица данных для поискового запроса - общий класс
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.SearchQueryTerm', {

    mixins: [
        'Unidata.mixin.ConfigBind',
        'Ext.mixin.Observable',
        'Unidata.mixin.search.TermsCollection'
    ],

    /**
     * @see {getTermName}
     * @type {string | null}
     * @protected
     */
    termName: null,

    config: {
        termIsActive: true // если false, то не используются в запросе
    },

    termIsSavable: true, // если false, то не сохраняется
    termIsSearchable: true, // если false, то не входит в поисковые данные

    constructor: function (config) {
        this.callParent(arguments);

        this.initConfig(config);

        this.mixins.observable.constructor.call(this, arguments);
    },

    destroy: function () {
        this.destroyTerms();
        this.callParent(arguments);
        this.fireEvent('destroy', this);
    },

    applyTermIsActive: function (value) {
        return Boolean(value);
    },

    /**
     * Имя терма, для маппинга и последующего биндинга по имени
     *
     * @returns {string | null}
     */
    getTermName: function () {
        return this.termName;
    },

    /**
     * Возвращает данные для формирования запроса
     *
     * @returns {Object | Ext.promise.Promise} - возвращает лидо данные, либо промис с данными
     */
    getTermData: function () {
        throw new Error('getTermData is not implemented');
    },

    /**
     * @returns {boolean}
     */
    isSavable: function () {
        return this.termIsSavable;
    },

    /**
     * @returns {boolean}
     */
    isSearchable: function () {
        return this.termIsSearchable;
    },

    /**
     * Возвращает данные для сохранения и последующего восстановления
     *
     * @returns {Object}
     */
    getTermSaveData: function () {
        return this.getSaveConfigData();
    },

    /**
     * Возвращает текущие данные конфига
     *
     * @param {Object} [options]
     * @param {string[]} [options.include] - только эти значения
     * @param {string[]} [options.exclude] - исключить значения
     */
    getConfigData: function (options) {
        var data = this.getConfig(),
            result = {};

        options = options || {};

        if (options.include) {
            Ext.Array.each(options.include, function (includeName) {
                if (data[includeName] !== undefined) {
                    result[includeName] = data[includeName];
                }
            });
        } else if (options.exclude) {
            Ext.Array.each(options.exclude, function (excludeName) {
                delete data[excludeName];
            });

            result = data;
        } else {
            result = data;
        }

        return Ext.clone(result);
    },

    /**
     * Возвращает текущие данные конфига для сохранения
     *
     * @param {Object} [options]
     * @param {string[]} [options.include] - только эти значения
     * @param {string[]} [options.exclude] - исключить значения
     */
    getSaveConfigData: function (options) {
        var result = this.getConfigData(options),
            deleteResults = [];

        Ext.Object.each(result, function (key, value) {
            if (value instanceof Ext.Base) {
                deleteResults.push(key);
            }
        });

        Ext.Array.each(deleteResults, function (name) {
            delete result[name];
        });

        result['xclass'] = this.$className;
        result['termIsActive'] = this.getTermIsActive();

        return result;
    }

});
