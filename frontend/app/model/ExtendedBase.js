Ext.define('Unidata.model.ExtendedBase', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'createdAt',
            type: 'date',
            dateFormat: 'c',
            persist: false,
            allowNull: true
        },
        {
            name: 'updatedAt',
            type: 'date',
            dateFormat: 'c',
            persist: false,
            allowNull: true
        },
        {
            name: 'createdBy',
            type: 'string',
            persist: false
        },
        {
            name: 'updatedBy',
            type: 'string',
            persist: false
        },
        {
            name: 'createdInfo',
            type: 'string',
            persist: false,
            calculate: function (data) {
                var byWhom = data.createdBy ? ' (' + data.createdBy + ')' : '';

                return Ext.Date.format(data.createdAt, Unidata.Config.getDateTimeFormat()) + byWhom;
            }
        },
        {
            name: 'updatedInfo',
            type: 'string',
            persist: false,
            calculate: function (data) {
                var byWhom = data.updatedBy ? ' (' + data.updatedBy + ')' : '';

                return Ext.Date.format(data.updatedAt, Unidata.Config.getDateTimeFormat()) + byWhom;
            }
        }
    ],

    getCreatedAt: function () {
        return Ext.Date.format(this.get('createdAt'), Unidata.Config.getDateTimeFormat());
    }
});
