/**
 * @author Aleksandr Bavin
 * @date 2016-07-01
 */
Ext.define('Unidata.view.admin.audit.log.AuditLogModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.log',

    stores: {
        auditEventTypeStore: {
            fields: ['value', 'text'],
            sorters: [
                {
                    property: 'text',
                    direction: 'ASC'
                }
            ]
        },
        userStore: {
            autoLoad: true,
            model: 'Unidata.model.user.User',
            sorters: [
                {
                    property: 'fullName',
                    direction: 'ASC'
                }
            ]
        },
        auditStore: {

            autoLoad: true,

            fields: [
                'auditEventId',
                'operationId',
                'createdBy',
                'createDate',
                'details',
                'auditEventType',
                'originId',
                'etalonId',
                'registry'
            ],

            listeners: {
                load: 'onAuditStoreLoad'
            },

            proxy: {
                type: 'ajax',

                limitParam: 'count',
                startParam: '',
                pageParam: 'page',

                url: Unidata.Config.getMainUrl() + 'internal/audit/search/',

                extraParams: {
                    // username: null,
                    // registry: null,
                    // operation: null,
                    // startDate: null,
                    // endDate: null
                },

                reader: {
                    type: 'json',
                    rootProperty: 'hits',
                    totalProperty: 'total_count',
                    transform: {
                        fn: function (data) {

                            // собираем плоский массив объектов
                            Ext.Array.each(data.hits, function (hit, index, hits) {
                                var plainData = {};

                                Ext.Array.each(hit.preview, function (previewItem) {
                                    var value;

                                    if (Ext.isArray(previewItem['values']) && previewItem['values'].length > 1) {
                                        value = previewItem['values'];
                                    } else {
                                        value = previewItem['value'];
                                    }

                                    plainData[previewItem['field']] = value;
                                });

                                hits[index] = plainData;
                            });

                            return data;
                        }
                    }
                }
            }
        }
    }

});
