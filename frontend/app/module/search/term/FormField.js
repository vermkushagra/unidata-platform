/**
 * formFields для поиска по атрибутам
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.FormField', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    requires: [
        'Unidata.module.search.term.FormFieldStatics'
    ],

    config: {
        name: null, // attributePath,
        value: null,
        rangeFrom: null,
        rangeTo: null,
        type: null,
        inverted: false,
        searchType: Unidata.module.search.term.FormFieldStatics.searchType.EXACT,
        termIsActive: true
    },

    termDisabled: false,

    /**
     * Принудительное отключение терма
     * @param {boolean} flag
     */
    setTermDisabled: function (flag) {
        this.termDisabled = flag;
    },

    getTermIsActive: function () {
        if (this.termDisabled) {
            return false;
        }

        return !this.valueIsEmpty() || !this.rangeIsEmpty() || this.searchTypeIsExist();
    },

    valueIsEmpty: function () {
        return Ext.isEmpty(this.getValue());
    },

    rangeIsEmpty: function () {
        return (Ext.isEmpty(this.getRangeFrom()) && Ext.isEmpty(this.getRangeTo()));
    },

    /**
     * @returns {boolean}
     */
    searchTypeIsExist: function () {
        return this.getSearchType() === Unidata.module.search.term.FormFieldStatics.searchType.EXIST;
    },

    getTermData: function () {
        var value = this.getValue(),
            formField,
            result;

        formField = {
            name: this.getName(),
            type: this.getType(),
            searchType: this.getSearchType(),
            inverted: this.getInverted()
        };

        if (this.searchTypeIsExist()) {
            formField.value = null;
        } else {
            if (value) {
                formField.value = this.formatValue(value);
            } else {
                formField.range = this.getRangeData();
            }
        }

        result = {
            formFields: [formField]
        };

        return result;
    },

    getRangeData: function () {
        return [
            this.formatValue(this.getRangeFrom()),
            this.formatValue(this.getRangeTo())
        ];
    },

    formatValue: function (value) {
        var types = Unidata.module.search.term.FormFieldStatics.type,
            type = this.getType();

        if (Ext.isEmpty(value)) {
            return value;
        }

        switch (type) {
            case types.DATE:
            case types.TIMESTAMP:
                value = Ext.Date.format(new Date(value), Unidata.Config.getDateTimeFormatProxy());
                break;
            case types.TIME:
                value = 'T' + Ext.Date.format(new Date(value), 'H:i:s.u') + 'Z';
                break;
        }

        return value;
    }

});
