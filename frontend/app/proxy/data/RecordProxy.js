/**
 * Proxy for urls like http://www.myurl.com/id123/{this.date}?params
 */
Ext.define('Unidata.proxy.data.RecordProxy', {
    extend: 'Unidata.proxy.Base',

    alias: 'proxy.data.recordproxy',

    date: null,
    lastUpdateDate: null,

    etalonId: null,

    dateFormat: Unidata.Config.getDateTimeFormatProxy(),

    setDate: function (date) {
        this.date = date;
    },

    setEtalonId: function (etalonId) {
        this.etalonId = etalonId;
    },

    setLastUpdateDate: function (date) {
        this.lastUpdateDate = date;
    },

    /**
     * Build url like http://www.myurl.com/id123/{this.date}?params
     * @returns {*}
     */
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

        if (this.date) {
            urlParts = url.split('?');
            date = Ext.Date.format(this.date, this.dateFormat);
            urlBase += '/' + date;
        }

        if (this.lastUpdateDate) {
            urlParts = url.split('?');
            date = Ext.Date.format(this.lastUpdateDate, this.dateFormat);
            urlBase += '/' + date;
        }

        url = urlBase + (urlParts.length > 1 ? '?' + urlParts[1] : '');

        return url;
    }
});
