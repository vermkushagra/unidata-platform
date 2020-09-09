/**
 * @author Aleksandr Bavin
 * @date 10.06.2016
 */
Ext.define('Unidata.event.manager.Approve', {

    extend: 'Unidata.event.AbstractEventManager',

    events: [
        'approvesuccess',
        'approvefailure',
        'declinesuccess',
        'declinefailure'
    ]

});
