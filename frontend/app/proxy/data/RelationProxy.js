/**
 * Proxy for urls like http://www.myurl.com/id123/{this.date}/{this.date}?params
 */
Ext.define('Unidata.proxy.data.RelationProxy', {
    extend: 'Unidata.proxy.Base',

    appendId: false,

    alias: 'proxy.data.relationproxy',

    dateFrom: null,

    dateTo: null,

    etalonId: null,

    relName: null,

    dateFormat: Unidata.Config.getDateTimeFormatProxy(),

    setDateFrom: function (date) {
        this.dateFrom = date;
    },

    setDateTo: function (date) {
        this.dateTo = date;
    },

    setEtalonId: function (etalonId) {
        this.etalonId = etalonId;
    },

    setRelName: function (relName) {
        this.relName = relName;
    },

    buildUrl: function () {
        var url,
            me = this,
            urlParts,
            urlBase,
            date;

        url = me.callParent(arguments);
        urlParts = url.split('?');
        urlBase = urlParts[0];

        if (this.etalonId) {
            urlBase += '/' + this.etalonId;
        }

        if (this.relName) {
            urlBase += '/' + this.relName;
        }

        if (this.dateFrom) {
            urlParts = url.split('?');
            date = Ext.Date.format(this.dateFrom, this.dateFormat);
            urlBase += '/' + date;
        }

        if (this.dateTo) {
            urlParts = url.split('?');
            date = Ext.Date.format(this.dateTo, this.dateFormat);
            urlBase += '/' + date;
        }

        url = urlBase + (urlParts.length > 1 ? '?' + urlParts[1] : '');

        return url;
    }
});
