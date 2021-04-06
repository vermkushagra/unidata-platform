Ext.define('Unidata.view.admin.entity.metarecord.dataquality.DataQualityModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.metarecord.dataquality',

    isPortSelected: function (currentDqRaise, portName) {
        if (currentDqRaise === null || currentDqRaise === undefined) {
            return false;
        }

        return currentDqRaise.get(portName) !== '';
    },
    data: {
        dqLoading: false,
        adminSystemName: '',
        currentDqRule: null
    },

    stores: {
        cleanseGroupsStore: {
            model: 'cleansefunction.Group',
            autoLoad: false,
            proxy: {
                type: 'rest',
                url: Unidata.Config.getMainUrl() + 'internal/meta/cleanse-functions'
            }
        },
        sourceSystems: {
            model: 'Unidata.model.sourcesystem.SourceSystem',
            autoLoad: false,
            listeners: {
                load: 'onSourceSystemsLoad'
            },
            proxy: {
                type: 'ajax',
                url: Unidata.Config.getMainUrl() + 'internal/meta/source-systems',
                reader: {
                    type: 'json',
                    rootProperty: 'sourceSystem'
                }
            }
        },
        functionRaiseErrorPorts: {
            model: 'Unidata.model.cleansefunction.OutputPort',
            remoteFilter: false,
            filters: [
                {
                    property: 'dataType',
                    value: 'Boolean'
                }
            ],
            proxy: {
                type: 'memory'
            }
        },
        messagePorts: {
            model: 'Unidata.model.cleansefunction.OutputPort',
            filters: [
                {
                    property: 'dataType',
                    value: 'String'
                }
            ],
            proxy: {
                type: 'memory'
            },
            listeners: {
                load: 'onMessagePortsStoreLoad'
            }
        },
        severityPorts: {
            model: 'Unidata.model.cleansefunction.OutputPort',
            proxy: {
                type: 'memory'
            },
            listeners: {
                load: 'onSeverityPortsStoreLoad'
            }
        },
        categoryPorts: {
            model: 'Unidata.model.cleansefunction.OutputPort',
            filters: [
                {
                    property: 'dataType',
                    value: 'String'
                }
            ],
            proxy: {
                type: 'memory'
            },
            listeners: {
                load: 'onCategoryPortsStoreLoad'
            }
        },
        lookupEntities: {
            model: 'Unidata.model.entity.LookupEntity',
            autoLoad: false
        },
        entities: {
            model: 'Unidata.model.entity.Entity',
            autoLoad: false
        },
        simpleDataTypes: {
            fields: ['name', 'displayName'],
            data: Unidata.Constants.getSimpleDataTypes()
        }
    },
    formulas: {
        cleanseGroups: {
            bind: {
                bindTo: '{cleanseGroupsStore}',
                deep: true
            },
            get: function (record) {
                return record && record.first();
            }
        },
        dqRules: {
            bind: {
                bindTo: '{currentRecord}',
                deep: true
            },
            get: function (entity) {
                var store = null;

                if (entity) {
                    store = entity.dataQualityRules();

                    store.setRemoteFilter(false);
                    store.clearFilter();
                    store.filter('special', false);
                }

                return store;
            }
        },
        currentDqRaise: {
            bind: {
                bindTo: '{currentDqRule}',
                deep: true
            },
            get: function (currentDqRule) {
                return currentDqRule ? currentDqRule.getRaise() : null;
            }
        },
        currentDqEnrich: {
            bind: {
                bindTo: '{currentDqRule}',
                deep: true
            },
            get: function (currentDqRule) {
                return currentDqRule ? currentDqRule.getEnrich() : null;

            }
        },
        isMessagePortSelected: {
            bind: {
                bindTo: '{currentDqRaise}',
                deep: true
            },
            get: function (currentDqRaise) {
                return this.isPortSelected(currentDqRaise, 'messagePort');
            }
        },
        isSeverityPortSelected: {
            bind: {
                bindTo: '{currentDqRaise}',
                deep: true
            },
            get: function (currentDqRaise) {
                return this.isPortSelected(currentDqRaise, 'severityPort');
            }
        },
        isCategoryPortSelected: {
            bind: {
                bindTo: '{currentDqRaise}',
                deep: true
            },
            get: function (currentDqRaise) {
                return this.isPortSelected(currentDqRaise, 'categoryPort');
            }
        },
        removeButtonEnabled: {
            bind: {
                currentDqRule: '{currentDqRule}',
                readOnly: '{metaRecordViewReadOnly}'
            },
            get: function (getter) {
                var enable = false;

                if (getter.currentDqRule && !getter.readOnly) {
                    enable = true;
                }

                return Ext.coalesceDefined(enable, false);
            }
        }
    }
});
