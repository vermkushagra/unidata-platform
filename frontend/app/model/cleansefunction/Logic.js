Ext.define('Unidata.model.cleansefunction.Logic', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false}
    ],

    hasMany: [
        {
            name: 'nodes',
            model: 'cleansefunction.Node'
        },
        {
            name: 'links',
            model: 'cleansefunction.Link'
        }
    ]

});
