Ext.define('Unidata.view.admin.entity.metarecord.MetaRecordModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.entity.metarecord',

    data: {
        readOnly: false,
        metaRecordViewReadOnly: false,
        entityKey: '',
        entityDisplayName: '',
        currentRecord: null,
        dqLoading: false,
        canSave: false,
        saving: false,
        dirty: false,
        tabAttributeDone: false,
        tabDqDone: false,
        tabConsolidationDone: false,
        tabPropertyDone: false,
        draftMode: false
    },

    stores: {
        lookupEntities: {
            model: 'Unidata.model.entity.LookupEntity',
            autoLoad: false
        },
        entities: {
            model: 'Unidata.model.entity.Entity',
            autoLoad: false
        },
        attributesStore: {
            fields: ['path', 'displayName'],
            autoLoad: false
        }
    },

    formulas: {
        changed: {
            bind: {
                bindTo: '{dirty}',
                deep: true
            },
            get: function (dirty) {
                return dirty ? '*' : '';
            }
        },
        typeIcon: {
            get: function () {
                var record, iconHtml = '';

                record = this.get('currentRecord');

                if (record !== null) {
                    iconHtml = Unidata.model.entity.AbstractEntity.createTypeIcon(record.getType());
                }

                return iconHtml;
            }
        },
        isLookupEntity: {
            bind: {
                bindTo: '{currentRecord}',
                deep: true
            },
            get: function (record) {
                return record && record instanceof Unidata.model.entity.LookupEntity;
            }
        },
        tabName: {
            bind: {
                bindTo: '{currentRecord}',
                deep: true
            },
            get: function (record) {
                var tabName = '',
                    typeName;

                if (record) {
                    typeName = record.getType() === 'Entity' ? Unidata.i18n.t('glossary:entity').toLowerCase() : Unidata.i18n.t('glossary:lookupEntity');

                    tabName = Unidata.i18n.t('admin.metamodel>new') + ' ' + typeName;

                    if (!record.phantom) {
                        tabName = record.isModified('displayName') ? record.getModified('displayName') : record.get('displayName');
                    }
                }

                return tabName;
            }
        },
        isMetaRecordPhantom: {
            bind: {
                bindTo: '{currentRecord}',
                deep: true
            },
            get: function (record) {
                return record && record.phantom;
            }
        },
        isMetaRecordDone: {
            bind: {
                currentRecord: '{currentRecord}',
                tabAttributeDone: '{tabAttributeDone}',
                tabDqDone: '{tabDqDone}',
                tabConsolidationDone: '{tabConsolidationDone}',
                deep: true
            },
            get: function (getter) {
                return getter.currentRecord && getter.tabAttributeDone && getter.tabDqDone && getter.tabConsolidationDone;
            }
        },
        saveButtonEnabled: {
            bind: {
                dqLoading: '{dqLoading}',
                canSave: '{canSave}',
                saving: '{saving}',
                readOnly: '{readOnly}'
            },
            get: function (getter) {
                var enable = false;

                if (!getter.readOnly && getter.canSave && !getter.saving && !getter.dqLoading) {
                    enable = true;
                }

                return Ext.coalesceDefined(enable, false);
            }
        },
        saveButtonVisible: {
            bind: {
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                var visible = false;

                if (getter.draftMode) {
                    visible = true;
                }

                return Ext.coalesceDefined(visible, false);
            }
        },
        removeButtonEnabled: {
            bind: {
                canSave: '{canSave}',
                readOnly: '{readOnly}'
            },
            get: function (getter) {
                var enable = false;

                if (!getter.readOnly && getter.canSave) {
                    enable = true;
                }

                return Ext.coalesceDefined(enable, false);
            }
        },
        removeButtonVisible: {
            bind: {
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                var visible = false;

                if (getter.draftMode) {
                    visible = true;
                }

                return Ext.coalesceDefined(visible, false);
            }
        },
        openDraftVisible: {
            bind: {
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                if (!getter.draftMode) {
                    return true;
                }

                return false;
            }
        },
        validityPeriodStart: {
            bind: {
                bindTo: '{currentRecord.validityPeriod}',
                deep: true
            },
            get: function (validityPeriod) {
                var start = null;

                if (Ext.isObject(validityPeriod)) {
                    start = validityPeriod.start;
                }

                return start;
            },
            set: function (start) {
                var metaRecord = this.get('currentRecord'),
                    validityPeriod = metaRecord.get('validityPeriod');

                if (!Ext.isObject(validityPeriod)) {
                    validityPeriod = {
                        start: null,
                        end: null
                    };
                }

                validityPeriod.start = start;

                metaRecord.set('validityPeriod', validityPeriod);
            }
        },
        validityPeriodEnd: {
            bind: {
                bindTo: '{currentRecord.validityPeriod}',
                deep: true
            },
            get: function (validityPeriod) {
                var end = null;

                if (Ext.isObject(validityPeriod)) {
                    end = validityPeriod.end;
                }

                return end;
            },
            set: function (end) {
                var metaRecord = this.get('currentRecord'),
                    validityPeriod = metaRecord.get('validityPeriod');

                if (!validityPeriod) {
                    validityPeriod = {
                        start: null,
                        end: null
                    };
                }

                validityPeriod.end = end;

                metaRecord.set('validityPeriod', validityPeriod);
            }
        },
        isValidityPeriod: {
            bind: {
                currentRecord: '{currentRecord}',
                validityPeriod: '{currentRecord.validityPeriod}',
                deep: true
            },
            get: function (getter) {
                return getter.validityPeriod !== null && getter.validityPeriod !== undefined;
            },
            set: function (value) {
                var metaRecord = this.get('currentRecord'),
                    validityPeriod = metaRecord.get('validityPeriod');

                if (value && !validityPeriod) {
                    metaRecord.set('validityPeriod', {
                        start: null,
                        end: null
                    });
                } else if (!value && validityPeriod) {
                    metaRecord.set('validityPeriod', null);
                }
                this.notify();
            }
        },
        isValidityPeriodEditable: {
            bind: {
                isValidityPeriod: '{isValidityPeriod}',
                hasData: '{currentRecord.hasData}'
            },
            get: function (getter) {
                var isValidityPeriod = getter.isValidityPeriod,
                    hasData = getter.hasData;

                return isValidityPeriod && !hasData;
            }
        }
    }
});
