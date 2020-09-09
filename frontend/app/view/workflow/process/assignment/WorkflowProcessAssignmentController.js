/**
 * @author Aleksandr Bavin
 * @date 2016-08-05
 */
Ext.define('Unidata.view.workflow.process.assignment.WorkflowProcessAssignmentController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.assignment',

    init: function () {
        this.loadAll();
    },

    loadAll: function () {
        var me = this;

        this.showProgress(true);

        this.loadStores().always(function () {
            me.showProgress(false);
            me.refreshGrid();
        }).done();
    },

    // Приходится перерисовывать, т.к. из-за группировки ломается отображение
    refreshGrid: function () {
        this.getView().grid.getView().refresh();
    },

    showProgress: function (flag) {
        var view = this.getView();

        view.setLoading(flag);
        view.buttons.setDisabled(flag);
    },

    /**
     * Формирует данные для стора assignments
     * @param {Unidata.model.workflow.Assignment[]} loadedAssignments
     */
    initAssignments: function (loadedAssignments) {
        var entity = this.getStore('entity'),
            typesStore = this.getStore('types'),
            assignmentsStore = this.getStore('assignments'),
            assignments = [];

        this.showProgress(true);
        assignmentsStore.removeAll();

        entity.each(function (metaItem) {
            assignments = assignments.concat(this.initAssignmentItem(loadedAssignments, typesStore, metaItem));
        }, this);

        assignmentsStore.add(assignments);
    },

    initAssignmentItem: function (loadedAssignments, typesStore, metaItem) {
        var assignments = [];

        typesStore.each(function (typeItem) {
            var entityName = metaItem.get('name'),
                displayName = metaItem.get('displayName'),
                processType = typeItem.get('code'),
                emptyAssignment,
                loadedAssignment;

            Ext.Array.each(loadedAssignments, function (assignment) {
                if (assignment.get('entityName') == entityName && assignment.get('processType') == processType) {
                    loadedAssignment = assignment;

                    return false;
                }
            });

            if (loadedAssignment) {
                loadedAssignment.set('displayName', displayName);
                assignments.push(loadedAssignment);
            } else {
                emptyAssignment = Ext.create('Unidata.model.workflow.Assignment', {
                    displayName: displayName,
                    entityName: entityName,
                    processType: processType
                });
                assignments.push(emptyAssignment);
            }
        });

        return assignments;
    },

    /**
     * Загрузка всех сторов в нужном порядке
     * @returns {Ext.promise.Promise}
     */
    loadStores: function () {
        var me = this,
            deferred = Ext.create('Ext.Deferred');

        this.loadStoresByNames(['entity', 'types', 'processes']).then(
            function () {
                me.loadAssignments().then(
                    function () {
                        deferred.resolve();
                    },
                    function () {
                        deferred.reject();
                    }
                );
            },
            function () {
                deferred.reject();
            }
        );

        return deferred.promise;
    },

    loadAssignments: function () {
        var me = this,
            deferred = Ext.create('Ext.Deferred'),
            realAssignmentsStore;

        realAssignmentsStore = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.workflow.Assignment'
        });

        realAssignmentsStore.reload({
            scope: this,
            callback: function (records, operation, success) {
                if (!me.getView()) {
                    return;
                }

                if (success) {
                    me.initAssignments(records);
                    deferred.resolve();
                } else {
                    deferred.reject();
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Загрузка сторов по массиву имён
     * @param {String[]} names
     * @returns {Ext.promise.Promise}
     */
    loadStoresByNames: function (names) {
        var promises = [];

        Ext.Array.each(names, function (name) {
            var deferred = Ext.create('Ext.Deferred');

            promises.push(deferred.promise);

            this.getStore(name).reload({
                scope: this,
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve();
                    } else {
                        deferred.reject();
                    }
                }
            });
        }, this);

        return Ext.Deferred.all(promises);
    },

    /**
     * Сохранение assignments
     */
    onSaveClick: function () {
        var me = this,
            assignmentsStore = this.getStore('assignments'),
            dataToSave = [];

        // собираем данные
        assignmentsStore.each(function (assignment) {
            var assignmentData = assignment.getData({persist: true});

            if (!assignment.dirty) {
                return;
            }

            if (assignment.phantom) {
                delete assignmentData.id;
            }

            if (assignment.get('processDefinitionId') != '') {
                dataToSave.push(assignmentData);
            } else {
                if (!assignment.phantom) {
                    assignmentData.processDefinitionId = null;
                    dataToSave.push(assignmentData);
                }
            }
        });

        if (dataToSave.length) {
            this.showProgress(true);

            Ext.Ajax.request({
                method: 'PUT',
                url: Unidata.Config.getMainUrl() + 'internal/data/workflow/assign',
                jsonData: dataToSave,
                success: function (response) {
                    var jsonResp = Ext.util.JSON.decode(response.responseText);

                    if (jsonResp.success) {
                        this.loadAssignments().always(function () {
                            me.showProgress(false);
                            me.refreshGrid();
                        }).done();
                        Unidata.showMessage(Unidata.i18n.t('glossary:saveDataSuccess'));
                    } else {
                        Unidata.showError(Unidata.i18n.t('workflow>saveDataError'));
                        me.showProgress(false);
                    }
                },
                failure: function () {
                    me.showProgress(false);
                },
                scope: this
            });
        } else {
            Unidata.showMessage(Unidata.i18n.t('workflow>noChanges'));
        }
    },

    /**
     * Рендер колонки грида - processType
     * @param value
     * @returns {*}
     */
    processTypeRenderer: function (value) {
        var typesStore = this.getStore('types'),
            result = typesStore.findExact('code', value);

        if (result != -1) {
            return typesStore.getAt(result).get('name');
        }

        return value;
    }

});
