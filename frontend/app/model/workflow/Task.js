/**
 * Задача
 * @author Aleksandr Bavin
 * @date 06.06.2016
 */
Ext.define('Unidata.model.workflow.Task', {

    requires: [
        'Unidata.model.workflow.TaskAction',
        'Unidata.model.workflow.TaskVariables'
    ],

    extend: 'Unidata.model.Base',

    idProperty: 'taskId',

    fields: [
        {
            name: 'taskId',
            type: 'int'
        },
        {
            name: 'taskKey',
            type: 'string'
        },
        {
            name: 'taskTitle',
            type: 'string'
        },
        {
            name: 'taskDescription',
            type: 'string'
        },
        {
            name: 'taskAssignee',
            type: 'string'
        },

        // Кем завершена
        {
            name: 'taskCompletedBy',
            type: 'string'
        },
        {
            name: 'finished',
            type: 'boolean'
        },
        {
            name: 'finishedDate',
            type: 'date',
            dateReadFormat: 'Y-m-d\\TH:i:s.uZ'
        },

        // Процесс
        {
            name: 'processId',
            type: 'int'
        },
        {
            name: 'processTitle',
            type: 'string'
        },
        {
            name: 'processType',
            type: 'string'
        },
        {
            name: 'processDefinitionId', // пример: approvalProcess
            type: 'string'
        },

        // Кто стартовал процесс
        {
            name: 'originator',
            type: 'string'
        },
        {
            name: 'originatorEmail',
            type: 'string'
        },
        {
            name: 'createDate',
            type: 'date',
            dateReadFormat: 'Y-m-d\\TH:i:s.uZ'
        },

        // Статус записи, вычисляется на основе других полей
        {
            name: 'recordState',
            calculate: function (data) {
                var Task = Unidata.model.workflow.Task;

                if (data['processType'] === undefined) {
                    return null;
                }

                if (data['processType'] === Task.processType.RECORD_DELETE) {
                    return Task.recordState.DELETED;
                }

                if (data['processType'] === Task.processType.RECORD_EDIT) {

                    if (data['variables'] === undefined) {
                        return null;
                    }

                    if (data['variables']['publishedState'] === undefined) {
                        return null;
                    }

                    if (data['variables']['publishedState']) {
                        return Task.recordState.UPDATED;
                    } else {
                        return Task.recordState.CREATED;
                    }
                }

                return null;
            }
        },

        // Дополнительные параметры
        {
            name: 'variables',
            reference: 'workflow.TaskVariables'
        }
    ],

    hasMany: [
        {
            name: 'actions',
            model: 'Unidata.model.workflow.TaskAction'
        }
    ],

    /**
     * Загружает задачу по id
     * @returns {Ext.promise.Promise}
     */
    load: function () {
        var deferred = Ext.create('Ext.Deferred'),
            task = this;

        this.self.load(task.getId()).then(
            function (newTask) {
                var data = newTask.getData(),
                    associatedData = newTask.getAssociatedData();

                task.beginEdit();
                task.set(data);

                Ext.Object.each(associatedData, function (key, value) {
                    var association = task.associations[key],
                        setterName,
                        getterName,
                        storeName,
                        store;

                    if (association) {
                        storeName = association.storeName;
                        setterName = association.setterName;
                        getterName = association.getterName;
                        store = task[storeName];

                        if (store) {
                            store.removeAll();
                            store.add(value);
                        } else if (setterName) {
                            task[getterName]().set(value);
                        }
                    }
                });

                task.endEdit();

                deferred.resolve(task);
            },
            function () {
                deferred.reject(task);
            }
        );

        return deferred.promise;
    },

    /**
     * Выполняет действие с задачей
     * @param action - Unidata.model.workflow.TaskAction.code
     * @returns {Ext.promise.Promise}
     */
    complete: function (action) {
        return this.self.complete({
            action: action,
            processKey: this.get('processId'),
            processDefinitionKey: this.get('processDefinitionId'),
            taskId: this.get('taskId')
        });
    },

    /**
     * Взять задачу
     * @returns {Ext.promise.Promise}
     */
    assign: function () {
        return this.self.assign(this.get('taskId'));
    },

    /**
     * В общие задачи
     * @returns {Ext.promise.Promise}
     */
    unassign: function () {
        return this.self.unassign(this.get('taskId'));
    },

    statics: {
        recordState: {
            CREATED: 'CREATED',
            UPDATED: 'UPDATED',
            DELETED: 'DELETED'
        },

        processType: {
            RECORD_EDIT: 'RECORD_EDIT',
            RECORD_DELETE: 'RECORD_DELETE'
        },

        /**
         * Выполняет действие с задачей
         *
         * @param params
         * @param params.action - Unidata.model.workflow.TaskAction.code
         * @param params.processKey - Unidata.model.workflow.Task.processId
         * @param params.processDefinitionKey - Unidata.model.workflow.Task.processDefinitionId
         * @param params.taskId - Unidata.model.workflow.Task.taskId
         * @returns {Ext.promise.Promise}
         */
        complete: function (params) {
            var deferred = Ext.create('Ext.Deferred'),
                jsonData = Ext.util.JSON.encode({
                    action: params.action,
                    processKey: params.processKey,
                    processDefinitionKey: params.processDefinitionKey,
                    taskId: params.taskId
                });

            Ext.Ajax.request({
                url: Unidata.Config.getMainUrl() + 'internal/data/workflow/complete',
                method: 'POST',
                jsonData: jsonData,
                success: function (response) {
                    var jsonResp = Ext.util.JSON.decode(response.responseText);

                    if (jsonResp.success) {
                        Unidata.showMessage(Unidata.i18n.t('model>actionComplete'));
                        deferred.resolve(jsonResp);
                    } else {
                        Unidata.showError(Unidata.i18n.t('model>actionError'));
                        deferred.reject(jsonResp);
                    }
                },
                failure: function () {
                    deferred.reject();
                }
            });

            return deferred.promise;
        },

        /**
         * Взять задачу
         * @param taskId
         * @returns {Ext.promise.Promise}
         */
        assign: function (taskId) {
            var deferred = Ext.create('Ext.Deferred');

            Ext.Ajax.request({
                url: Unidata.Config.getMainUrl() + 'internal/data/workflow/tasks/' + taskId + '/assign',
                method: 'POST',
                jsonData: {},
                success: function (response) {
                    var jsonResp = Ext.util.JSON.decode(response.responseText);

                    if (jsonResp.success) {
                        deferred.resolve(jsonResp);
                    } else {
                        Unidata.showError(Unidata.i18n.t('model>takeTaskError'));
                        deferred.reject(jsonResp);
                    }
                },
                failure: function () {
                    deferred.reject();
                }
            });

            return deferred.promise;
        },

        /**
         * Перевести задачу в общие
         * @param taskId
         * @returns {Ext.promise.Promise}
         */
        unassign: function (taskId) {
            var deferred = Ext.create('Ext.Deferred');

            Ext.Ajax.request({
                url: Unidata.Config.getMainUrl() + 'internal/data/workflow/tasks/' + taskId + '/unassign',
                method: 'POST',
                jsonData: {},
                success: function (response) {
                    var jsonResp = Ext.util.JSON.decode(response.responseText);

                    if (jsonResp.success) {
                        deferred.resolve(jsonResp);
                    } else {
                        Unidata.showError(Unidata.i18n.t('model>returnTaskError'));
                        deferred.reject(jsonResp);
                    }
                },
                failure: function () {
                    deferred.reject();
                }
            });

            return deferred.promise;
        },

        /**
         * Загружает задачу по taskId
         * @param taskId
         * @returns {Ext.promise.Promise}
         */
        load: function (taskId) {
            var deferred = Ext.create('Ext.Deferred'),
                store, proxy;

            proxy = Ext.create('Unidata.proxy.workflow.task.SearchProxy');

            store = Ext.create('Ext.data.Store', {
                model: 'Unidata.model.workflow.Task',
                proxy: 'workflow.task.search',
                pageSize: 1
            });

            store.load({
                scope: this,
                callback: function (records, operation, success) {
                    if (success && records && records.length) {
                        deferred.resolve(records[0]);
                    } else {
                        Unidata.showError(Unidata.i18n.t('model>loadTaskError'));
                        deferred.reject();
                    }
                },
                params: {
                    taskId: taskId
                }
            });

            return deferred.promise;
        }
    }

});
