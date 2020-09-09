Ext.define('Unidata.view.steward.dataentity.mixin.SearchableComponent', {

    extend: 'Ext.Mixin',

    mixinConfig: {
        on: {
            constructor: 'initSearchableComponent'
        }
    },

    initSearchableComponent: function () {

        var me = this,
            baseCls = 'un-dataentity-searchable';

        me.CLASS_SEARCHED            = baseCls + '__searched';
        me.CLASS_SEARCHED_AND_ACTIVE = baseCls + '__searchedAndActive';
    },

    /**
     * Нужно в некоторых случаях переопределить эту функцию для аттрибутов
     *
     * @returns {String[]}
     */
    getDataForSearch: function () {
        return [];
    },

    search: function (text) {

        var data = this.getDataForSearch(),
            i;

        data.push(this.title);

        for (i = 0; i < data.length; i++) {
            if (data[i].replace(/\s+/g, ' ').toLowerCase().indexOf(text) !== -1) {
                return true;
            }
        }

        return false;

    },

    setSearched: function (searched) {

        var me = this,
            CLASS_SEARCHED = me.CLASS_SEARCHED;

        me.searched = searched;

        if (searched) {
            me.el.addCls(CLASS_SEARCHED);
        } else {
            me.el.removeCls(CLASS_SEARCHED);
        }
    },

    setSearchedAndActive: function (searchedAndActive) {

        var me = this,
            CLASS_SEARCHED_AND_ACTIVE = me.CLASS_SEARCHED_AND_ACTIVE;

        me.searchedAndActive = searchedAndActive;

        if (searchedAndActive) {
            me.el.addCls(CLASS_SEARCHED_AND_ACTIVE);
        } else {
            me.el.removeCls(CLASS_SEARCHED_AND_ACTIVE);
        }
    }

}, function () {

    var key = 'isSearchableComponent' + Ext.timestamp();

    /**
     * Проверяет, применён ли mixin для контейнера
     * @param obj
     * @returns {*}
     */
    this.isSearchableComponent = function (obj) {
        return obj[key];
    };

    this.prototype[key] = true;

});
