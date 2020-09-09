Ext.define('Unidata.view.admin.entity.metarecord.relation.RelationModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.entity.metarecord.relation',

    data: {
        hasData: false,
        phantom: false
    },

    stores: {
        simpleAttributes: {
            model: 'Unidata.model.attribute.SimpleAttribute',
            source: '{currentRelation.simpleAttributes}'
        }
    },

    formulas: {
        relations: {
            bind: {
                bindTo: '{currentRecord}',
                deep: true
            },
            get: function (entity) {
                var SorterUtil = Unidata.util.Sorter,
                    store =  (entity && entity.relations) ? entity.relations() : null,
                    defaultRelationTypeOrder = ['References', 'Contains', 'ManyToMany'];

                if (store) {
                    store.setSorters([
                        {
                            sorterFn: SorterUtil.byListSorterFn.bind(this, defaultRelationTypeOrder, 'relType', null),
                            direction: 'ASC'
                        },
                        {
                            property: 'displayName',
                            direction: 'ASC'
                        }
                    ]);
                }

                return store;
            }
        },
        currentRelation: {
            bind: {
                bindTo: '{relationGrid.selection}',
                deep: true
            },
            get: function (relation) {
                return relation;
            }
        },
        selectedRelationEntity: {
            bind: {
                bindTo: '{entityRelation.selection}'
            },
            get: function (entity) {
                return entity;
            }
        },
        isDisabledAttribute: {
            bind: {
                bindTo: '{currentRelation.relType}'
            },
            get: function (relType) {
                return relType === 'Contains' ? true : false;
            }
        },
        isRelTypeContains: {
            bind: {
                bindTo: '{currentRelation.relType}'
            },
            get: function (relType) {
                return relType === 'Contains' ? true : false;
            }
        },

        relationTypeEditable: {
            bind: {
                phantom: '{phantom}',
                hasData: '{hasData}',
                metaRecordViewReadOnly: '{metaRecordViewReadOnly}',
                deep: true
            },
            get: function (getter) {
                var editable = false;

                if (getter.metaRecordViewReadOnly) {
                    return false;
                }

                // разрешаем редактирование для новых
                if (getter.phantom) {
                    return true;
                }

                if (!getter.hasData) {
                    editable = true;
                }

                return Ext.coalesceDefined(editable, false);
            }
        },

        relationNameEditable: {
            bind: {
                phantom: '{phantom}',
                hasData: '{hasData}',
                metaRecordViewReadOnly: '{metaRecordViewReadOnly}',
                deep: true
            },
            get: function (getter) {
                var editable = false;

                if (getter.metaRecordViewReadOnly) {
                    return false;
                }

                // разрешаем редактирование для новых
                if (getter.phantom) {
                    return true;
                }

                if (!getter.hasData) {
                    editable = true;
                }

                return Ext.coalesceDefined(editable, false);
            }
        },
        removeButtonEnabled: {
            bind: {
                selection: '{relationGrid.selection}',
                readOnly: '{metaRecordViewReadOnly}',
                deep: true
            },
            get: function (getter) {
                var enable = false;

                if (getter.selection && !getter.readOnly) {
                    enable = true;
                }

                return Ext.coalesceDefined(enable, false);
            }
        }
    }

});
