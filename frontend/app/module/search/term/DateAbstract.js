/**
 * Общий класс для DateCreated / DateUpdated
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.DateAbstract', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    config: {
        value: null // дата
    },

    name: null, // $created_at | $updated_at
    formField: null,

    constructor: function () {
        this.callParent(arguments);

        this.addTerm(new Unidata.module.search.term.SearchField({
            name: this.name
        }));
    },

    getFormField: function () {
        if (!this.formField) {
            this.formField = new Unidata.module.search.term.FormField({
                name: this.name,
                searchType: Unidata.module.search.term.FormFieldStatics.searchType.RANGE,
                type: Unidata.module.search.term.FormFieldStatics.type.DATE
            });

            this.addTerm(this.formField);
        }

        return this.formField;
    },

    updateValue: function (value) {
        var valueFrom,
            valueTo,
            formField;

        if (value) {
            value = new Date(value);

            valueFrom = Ext.Date.format(value, Unidata.Config.getDateTimeFormatProxy());

            value.setDate(value.getDate() + 1);
            valueTo = Ext.Date.format(value, Unidata.Config.getDateTimeFormatProxy());

            formField = this.getFormField();

            formField.setRangeFrom(valueFrom);
            formField.setRangeTo(valueTo);
        }
    },

    getTermIsActive: function () {
        return !Ext.isEmpty(this.getValue());
    },

    getTermData: function () {
        var promise;

        promise = this.getTermsData();

        return promise;
    }

});
