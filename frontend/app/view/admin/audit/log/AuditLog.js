/**
 * @author Aleksandr Bavin
 * @date 2016-07-01
 */
Ext.define('Unidata.view.admin.audit.log.AuditLog', {
    extend: 'Ext.form.Panel',

    requires: [
        'Unidata.view.admin.audit.log.AuditLogController',
        'Unidata.view.admin.audit.log.AuditLogModel'
    ],

    alias: 'widget.admin.audit.log',

    cls: 'un-auditlog',

    viewModel: {
        type: 'log'
    },

    controller: 'log',

    referenceHolder: true,

    layout: 'fit',

    items: {
        xtype: 'grid',
        reference: 'searchResultGrid',

        plugins: [
            {
                ptype: 'cellediting',
                clicksToEdit: 1,
                listeners: {
                    beforeedit: function (editor, e) {
                        if (Ext.isEmpty(e.record.get(e.field))) {
                            return false;
                        }
                    },
                    edit: function (editor, e) {
                        e.record.reject();
                    }
                }
            },
            {
                ptype: 'grid.hideColumnIfEmpty'
            }
        ],

        sortableColumns: false,

        flex: 1,

        viewConfig: {
            getRowClass: function (record) {
                return (record.get('success')) ? 'un-success-true' : 'un-success-false';
            }
        },

        columns: {
            defaults: {
                flex: 1,
                renderer: function (value, column) {
                    if (Ext.isArray(value)) {
                        value = value.join(', ');
                    }

                    return this.defaultRenderer ? this.defaultRenderer(value, column) : value;
                }
            },
            items: [
                {
                    text: Unidata.i18n.t('admin.audit>operationId'),
                    dataIndex: 'operationId',
                    plugins: 'grid.column.headeritemswitcher',
                    minWidth: 270,
                    renderer: function (value, column, record) {
                        var url = Unidata.Config.getAppModeUrl(Unidata.Config.APP_MODE.USER),
                            hash;

                        if (value && record.get('action') == 'UPSERT') {
                            hash = Unidata.util.Router.buildHash([
                                {
                                    name: 'main',
                                    values: {
                                        section: 'data'
                                    }
                                },
                                {
                                    name: 'etalon',
                                    values: {
                                        etalonId: record.get('etalonId'),
                                        operationId: value
                                    }
                                }
                            ]);

                            return '<a class="etalon-link icon-link2" href="' + url + '#' + hash + '"></a>' + value;
                        }

                        return value;
                    },
                    editor: {
                        xtype: 'textfield',
                        readOnly: true
                    },
                    items: {
                        xtype: 'textfield',
                        emptyText: Unidata.i18n.t('admin.audit>operationId'),
                        name: 'operation',
                        padding: '0 10 0 10',
                        triggers: {
                            reset: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.reset();
                                }
                            }
                        },
                        listeners: {
                            change: 'onFilterChange'
                        }
                    }
                },
                {
                    text: Unidata.i18n.t('admin.audit>initiator'),
                    dataIndex: 'user',
                    plugins: 'grid.column.headeritemswitcher',
                    items: {
                        xtype: 'combobox',
                        width: '100%',
                        padding: '0 10 0 10',
                        displayField: 'fullName',
                        valueField: 'login',
                        queryMode: 'local',
                        editable: false,
                        bind: {
                            store: '{userStore}'
                        },
                        listeners: {
                            change: 'onFilterChange'
                        },
                        triggers: {
                            reset: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.reset();
                                    this.collapse();
                                }
                            }
                        },
                        emptyText: Unidata.i18n.t('admin.audit>initiator'),
                        name: 'username'
                    }
                },
                {
                    text: Unidata.i18n.t('admin.audit>creationDate'),
                    dataIndex: 'date',
                    renderer: function (dateString) {
                        var date, time, dateTime;

                        dateTime = dateString.toString().split('T');

                        date = dateTime[0].split('-');
                        time = dateTime[1].split(':');

                        return Ext.String.format(
                            '{0}.{1}.{2} {3}:{4}:{5}',
                            date[2],
                            date[1],
                            date[0],
                            time[0],
                            time[1],
                            time[2].split('.')[0]
                        );
                    },
                    plugins: 'grid.column.headeritemswitcher',
                    minWidth: 250,
                    items: {
                        xtype: 'container',
                        layout: {
                            type: 'hbox',
                            align: 'stretch'
                        },
                        padding: '0 10 0 10',
                        defaultType: 'datefield',
                        defaults: {
                            submitFormat: 'Y-m-d',
                            flex: 1,
                            hideLabel: true,
                            listeners: {
                                change: 'onFilterChange'
                            },
                            triggers: {
                                reset: {
                                    cls: 'x-form-clear-trigger',
                                    handler: function () {
                                        this.reset();
                                    }
                                }
                            },
                            width: '100%'
                        },
                        items: [
                            {
                                emptyText: Unidata.i18n.t('admin.audit>after'),
                                name: 'startDate',
                                margin: '0 5 0 0'
                            },
                            {
                                emptyText: Unidata.i18n.t('admin.audit>to'),
                                name: 'endDate',
                                margin: '0 0 0 5'
                            }
                        ]
                    }
                },
                {
                    text: Unidata.i18n.t('admin.audit>details'),
                    dataIndex: 'details'
                },
                {
                    text: Unidata.i18n.t('admin.audit>eventType'),
                    dataIndex: 'action',
                    renderer: 'auditEventTypeRenderer',
                    plugins: 'grid.column.headeritemswitcher',
                    items: {
                        padding: '0 10 0 10',
                        xtype: 'combobox',
                        emptyText: Unidata.i18n.t('admin.audit>eventType'),
                        valueField: 'value',
                        queryMode: 'local',
                        editable: false,
                        bind: {
                            store: '{auditEventTypeStore}'
                        },
                        hideLabel: true,
                        name: 'type',
                        listeners: {
                            change: 'onFilterChange'
                        },
                        triggers: {
                            reset: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.reset();
                                }
                            }
                        }
                    }
                },
                {
                    text: Unidata.i18n.t('admin.audit>originId'),
                    dataIndex: 'originId'
                },
                {
                    text: Unidata.i18n.t('admin.audit>etalonId'),
                    dataIndex: 'etalonId',
                    plugins: 'grid.column.headeritemswitcher',
                    minWidth: 270,
                    renderer: function (value) {
                        var url = Unidata.Config.getAppModeUrl(Unidata.Config.APP_MODE.USER),
                            hash;

                        if (value) {
                            hash = Unidata.util.Router.buildHash([
                                {
                                    name: 'main',
                                    values: {
                                        section: 'data'
                                    }
                                },
                                {
                                    name: 'etalon',
                                    values: {
                                        etalonId: value
                                    }
                                }
                            ]);

                            return '<a class="etalon-link icon-link2" href="' + url + '#' + hash + '"></a>' + value;
                        }

                        return value;
                    },
                    editor: {
                        xtype: 'textfield',
                        readOnly: true
                    },
                    items: {
                        xtype: 'textfield',
                        emptyText: Unidata.i18n.t('admin.audit>etalonId'),
                        name: 'etalon',
                        padding: '0 10 0 10',
                        triggers: {
                            reset: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.reset();
                                }
                            }
                        },
                        listeners: {
                            change: 'onFilterChange'
                        }
                    }
                },
                {
                    text: Unidata.i18n.t('admin.audit>externalId'),
                    dataIndex: 'externalId',
                    plugins: 'grid.column.headeritemswitcher',
                    minWidth: 270,
                    editor: {
                        xtype: 'textfield',
                        readOnly: true
                    },
                    items: {
                        xtype: 'textfield',
                        emptyText: Unidata.i18n.t('admin.audit>externalId'),
                        name: 'externalId',
                        padding: '0 10 0 10',
                        triggers: {
                            reset: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.reset();
                                }
                            }
                        },
                        listeners: {
                            change: 'onFilterChange'
                        }
                    }
                },
                {
                    text: Unidata.i18n.t('glossary:entityOrLookupEntity'),
                    dataIndex: 'entity',
                    plugins: 'grid.column.headeritemswitcher',
                    items: {
                        padding: '0 10 0 10',
                        xtype: 'un.entitycombo',
                        emptyText: Unidata.i18n.t('glossary:entityOrLookupEntity'),
                        hideLabel: true,
                        name: 'registry',
                        autoSelect: true,
                        forceSelection: true,
                        listeners: {
                            change: 'onFilterChange'
                        },
                        triggers: {
                            reset: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.reset();
                                    this.collapse();
                                }
                            }
                        }
                    }
                },
                {
                    text: Unidata.i18n.t('admin.audit>taskId'),
                    dataIndex: 'taskId'
                },
                {
                    text: Unidata.i18n.t('admin.audit>serverIp'),
                    dataIndex: 'serverIp'
                },
                {
                    text: Unidata.i18n.t('admin.audit>clientIp'),
                    dataIndex: 'clientIp'
                },
                {
                    text: Unidata.i18n.t('admin.common>interface'),
                    dataIndex: 'endpoint'
                },
                {
                    text: Unidata.i18n.t('admin.audit>subSystem'),
                    dataIndex: 'subSystem',
                    renderer: 'subSystemRenderer'
                },
                {
                    text: Unidata.i18n.t('glossary:dataSource'),
                    dataIndex: 'sourceSystem'
                }
            ]
        },
        dockedItems: [
            {
                xtype: 'pagingtoolbar',
                inputItemWidth: 85,
                bind: {
                    store: '{auditStore}'
                },
                dock: 'bottom'
            }
        ],
        bind: {
            store: '{auditStore}'
        }
    },

    auditEventTypeMapper: {
        DECLINE: Unidata.i18n.t('admin.audit>decline'),
        ACCEPT: Unidata.i18n.t('admin.audit>accept'),
        CREATE: Unidata.i18n.t('admin.audit>createdUser'),
        UPDATE: Unidata.i18n.t('admin.audit>updatedUser'),
        LOGIN: Unidata.i18n.t('admin.audit>userLogin'),
        LOGOUT: Unidata.i18n.t('admin.audit>userLogout'),
        UPSERT: Unidata.i18n.t('admin.audit>changeRecord'),
        DELETE: Unidata.i18n.t('glossary:removeRecord'),
        MERGE: Unidata.i18n.t('admin.audit>mergeRecords'),
        GET: Unidata.i18n.t('admin.audit>getRecord'),
        MULTI_GET: Unidata.i18n.t('admin.audit>getFewRecord'),
        RESTORE: Unidata.i18n.t('glossary:restoreRecord')
    },

    subSystemMapper: {
        WORKFLOW: Unidata.i18n.t('admin.audit>workflows'),
        USERS: Unidata.i18n.t('glossary:users'),
        DATA: Unidata.i18n.t('admin.audit>dataHandling'),
        AUTH: Unidata.i18n.t('admin.audit>authorization'),
        ROLES: Unidata.i18n.t('admin.audit>security')
    },

    /**
     * Что бы не появлялось окно с подтверждением при уходе с экрана
     * @returns {boolean}
     */
    isDirty: function () {
        return false;
    },

    initComponent: function () {
        this.callParent(arguments);
    }

});
