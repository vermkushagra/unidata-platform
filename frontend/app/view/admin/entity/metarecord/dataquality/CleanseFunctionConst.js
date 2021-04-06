/**
 * Константы для cleanse functions
 *
 * @author Sergey Shishigin
 * @date 2017-01-12
 */

Ext.define('Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionConst', {
    singleton: true,

    CONSTANT_PORTS: [
        {
            cfJavaClass: 'com.unidata.mdm.cleanse.misc.CFInnerFetch',
            ports: [
                'fetchMode',
                'entityName',
                'returnFieldName',
                'orderFieldName',
                'searchFieldName'
            ]
        },
        {

            cfJavaClass: 'com.unidata.mdm.cleanse.misc.CFOuterFetch',
            ports: ['port3', 'port4']
        }
    ],

    CFInnerFetch: {
        PORT_FETCH_MODE: 'fetchMode',
        PORT_ENTITY: 'entityName',
        PORT_RETURN_ATTRIBUTE: 'returnFieldName',
        PORT_ORDER_ATTRIBUTE: 'orderFieldName',
        PORT_SEARCH_ATTRIBUTE: 'searchFieldName',
        constantPorts: ['fetchMode']
    },
    CFOuterFetch: {
        PORT_DATA_TYPE: 'port4',
        PORT_FETCH_MODE: 'port3',
        constantPorts: ['port3', 'port4']
    }
});
