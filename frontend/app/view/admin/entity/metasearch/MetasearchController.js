/**
 * @author Aleksandr Bavin
 * @date 30.05.2016
 */
Ext.define('Unidata.view.admin.entity.metasearch.MetasearchController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.metasearch',

    init: function () {
        this.getView().enableBubble('metasearchresultclick');
        this.canSearch(false);
    },

    /**
     * Рендерим колонку грида - "Где нашли"
     * @param value
     * @returns {*}
     */
    searchObjectRenderer: function (value) {
        var newValue = this.getView().searchObjectMapper[value];

        return newValue ? newValue : value;
    },

    /**
     * При клике на ряд в результатах поиска
     */
    onRowClick: function (table, record) {
        var metaRecordName = record.get('keyValue');

        this.getView().fireEvent('metasearchresultclick', record, metaRecordName);
    },

    /**
     * Возвращает стор с результатами поиска
     * @returns {Ext.data.Store}
     */
    getSearchResultStore: function () {
        return this.getStore('searchResultStore');
    },

    /**
     * Возвращает прокси для поиска
     * @returns {Ext.data.proxy.Proxy}
     */
    getSearchResultProxy: function () {
        return this.getSearchResultStore().getProxy();
    },

    /**
     * При наборе текста
     * @param {Ext.form.field.Text} textfield
     */
    onTextFieldChange: function (textfield) {
        var textfieldValue = textfield.getValue(),
            canSearch = (textfield.isValid() && !Ext.isEmpty(textfieldValue));

        this.canSearch(canSearch);
        this.setSearchText(textfieldValue);
    },

    /**
     * При изменении фильтра
     * @param {Ext.form.field.ComboBox} searchFilterCombobox
     */
    onSearchFilterChange: function (searchFilterCombobox) {
        var plainArray = [].concat.apply([], searchFilterCombobox.getValue()),
            fields = plainArray.join('|');

        this.setSearchFilter(fields);
    },

    /**
     * Устанавливает фильтр для поиска - fields
     * @param fields
     */
    setSearchFilter: function (fields) {
        this.getSearchResultProxy().setExtraParam('fields', fields);
        this.startSearch();
    },

    /**
     * Устанавливает текст для поиска
     * @param text
     */
    setSearchText: function (text) {
        this.getSearchResultProxy().setExtraParam('text', text);
        this.startSearch();
    },

    /**
     * Проверка запрета на поиск (например, если невалидный текст поиска)
     * Возвращает установлнный флаг.
     * Если передать флаг, то установит его.
     * @param {boolean} [flag] - опционально
     * @returns {boolean}
     */
    canSearch: function (flag) {
        if (flag !== undefined) {
            this.canSearchFlag = flag;
        }

        return this.canSearchFlag;
    },

    /**
     * Запуск поиска по таймауту, если это возможно
     */
    startSearch: function () {
        var searchResultGrid = this.lookupReference('searchResultGrid'),
            searchResultStore = this.getSearchResultStore();

        if (this.timer) {
            clearTimeout(this.timer);
        }

        if (this.canSearch()) {
            searchResultGrid.setLoading(true);
        } else {
            this.getViewModel().set('totalCount', 0);
            searchResultGrid.setLoading(false);
            searchResultStore.removeAll();

            return false;
        }

        this.timer = Ext.defer(this.startSearchForced, 1500, this);

        return true;
    },

    /**
     * Принудительно запускаем поиск
     * @private
     */
    startSearchForced: function () {
        var view = this.getView();

        this.getSearchResultStore().reload({
            params: {
                draft: view.getDraftMode()
            },
            scope: this,
            callback: this.onSearchDone
        });
    },

    /**
     * После завершения поиска
     */
    onSearchDone: function () {
        var searchResultGrid = this.lookupReference('searchResultGrid');

        this.getViewModel().set('totalCount', this.getSearchResultStore().getTotalCount());
        searchResultGrid.setLoading(false);
    }

});
