Ext.define('Unidata.util.ValidityPeriod', {
    singleton: true,

    getMinDate: function (validityPeriod) {
        var minDate = null,
            globalMinDate = Unidata.Config.getMinDate();

        if (validityPeriod && validityPeriod.start) {
            minDate = validityPeriod.start;
        } else if (globalMinDate) {
            minDate = globalMinDate;
        }

        return minDate;
    },

    getMaxDate: function (validityPeriod) {
        var maxDate = null,
            globalMaxDate = Unidata.Config.getMaxDate();

        if (validityPeriod && validityPeriod.end) {
            maxDate = validityPeriod.end;
        } else if (globalMaxDate) {
            maxDate = globalMaxDate;
        }

        return maxDate;
    }
});
