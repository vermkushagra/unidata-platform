/**
 * Доп. запрос для поиска по связям
 *
 * @author Aleksandr Bavin
 * @date 2018-02-15
 */
Ext.define('Unidata.module.search.term.relation.SupplementaryRequest', {

    extend: 'Unidata.module.search.term.SupplementaryRequest',

    config: {
        dateAsOfTerm: null,
        unRangedTerm: null,
        relationFormFieldsCount: 0
    },

    dateFromTerm: null,
    dateToTerm: null,

    constructor: function () {
        var getSupplementarySearchQuery;

        this.callParent(arguments);

        getSupplementarySearchQuery = this.getSupplementarySearchQuery();

        getSupplementarySearchQuery.bind('relationFormFieldsCount', this.setRelationFormFieldsCount, this);
        this.setRelationFormFieldsCount(getSupplementarySearchQuery.getRelationFormFieldsCount());

        this.initDateFromToTerms();
    },

    initDateFromToTerms: function () {
        var type = Unidata.module.search.term.FormFieldStatics.type.DATE,
            searchType = Unidata.module.search.term.FormFieldStatics.searchType.RANGE;

        this.dateFromTerm = new Unidata.module.search.term.relation.FormField({
            name: '$from',
            searchType: searchType,
            type: type
        });

        this.dateToTerm = new Unidata.module.search.term.relation.FormField({
            name: '$to',
            searchType: searchType,
            type: type
        });

        this.addTerm(this.dateFromTerm);
        this.addTerm(this.dateToTerm);

        this.updateDateFromToTerms();
    },

    getRelName: function () {
        return this.getSupplementarySearchQuery().getRelName();
    },

    updateDateAsOfTerm: function (newTerm, oldTerm) {
        if (oldTerm) {
            oldTerm.destroy();
        }

        if (newTerm) {
            newTerm.bind('termIsActive', this.updateDateFromToTerms, this);
            newTerm.bind('value', this.updateDateFromToTerms, this);
            this.addTerm(newTerm);
            this.updateDateFromToTerms();
        }
    },

    updateUnRangedTerm: function (newTerm, oldTerm) {
        if (oldTerm) {
            oldTerm.destroy();
        }

        if (newTerm) {
            this.addTerm(newTerm);
            newTerm.bind('termIsActive', this.updateDateFromToTerms, this);
            this.updateDateFromToTerms();
        }
    },

    updateDateFromToTerms: function () {
        var dateAsOfTerm = this.getDateAsOfTerm(),
            unRangedTerm = this.getUnRangedTerm(),
            termIsActive,
            dateValue;

        if (!this.dateFromTerm || !this.dateToTerm || !dateAsOfTerm || !unRangedTerm) {
            return;
        }

        // если автивен фасет, то форм-филды отключаем
        termIsActive = unRangedTerm.getTermIsActive();

        this.dateFromTerm.setTermDisabled(termIsActive);
        this.dateToTerm.setTermDisabled(termIsActive);

        dateValue = dateAsOfTerm.getValue();
        dateValue = dateValue ? dateValue : new Date();
        dateValue = Ext.Date.format(dateValue, Unidata.Config.getDateTimeFormatProxy());

        this.dateFromTerm.setRangeTo(dateValue);
        this.dateToTerm.setRangeFrom(dateValue);
    },

    applySupplementarySearchQuery: function () {
        var supplementarySearchQuery = this.callParent(arguments);

        if (!supplementarySearchQuery) {
            supplementarySearchQuery = new Unidata.module.search.RelationSearchQuery();
        }

        return supplementarySearchQuery;
    },

    /**
     * TODO: очень грязно - переделать
     */
    getTermData: function () {
        var me = this,
            supplementaryRequestPromise = this.callParent(arguments),
            supplementaryRequest,
            innerTermsData,
            promise;

        promise = this.getTermsData()
            .then(
                function (termsData) {
                    innerTermsData = termsData;

                    return supplementaryRequestPromise;
                }
            )
            .then(
                function (supplementaryRequestsData) {
                    supplementaryRequest = supplementaryRequestsData['supplementaryRequests'][0];

                    supplementaryRequestsData['supplementaryRequests'][0] = me.mergeObjects(
                        supplementaryRequest,
                        innerTermsData
                    );

                    return supplementaryRequestsData;
                }
            );

        return promise;
    }

});
