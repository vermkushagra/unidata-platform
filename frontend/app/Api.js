Ext.define('Unidata.Api', {

    statics: {

        createUrl: function (path) {
            return Unidata.Config.getMainUrl() + 'internal' + path;
        },

        getJobsUrl: function () {
            return this.createUrl('/jobs');
        },

        getJobExecutionsUrl: function () {
            return this.createUrl('/jobs/executions');
        },

        getJobMetaNamesUrl: function () {
            return this.createUrl('/jobs/jobmetanames');
        },

        getJobStartUrl: function () {
            return this.createUrl('/jobs/start');
        },

        getJobStopUrl: function () {
            return this.createUrl('/jobs/stop');
        },

        getJobMarkUrl: function () {
            return this.createUrl('/jobs/mark');
        },

        getJobExecutionStepsUrl: function () {
            return this.createUrl('/jobs/stepexecutions');
        },

        getRestartExecutionUrl: function () {
            return this.createUrl('/jobs/restart');
        },

        getClassifierUrl: function () {
            return this.createUrl('/data/classifier');

        },

        getClassifierNodeUrl: function () {
            return this.createUrl('/data/classifier');
        },

        getEnumerationsUrl: function () {
            return this.createUrl('/meta/enumerations');
        },

        getMergeUrl: function () {
            return this.createUrl('/data/entities/merge');
        },

        getBlockClusterRecordUrl: function (entityName, clusterId) {
            return this.getClusterUrl(entityName) + '/blockList/' + clusterId;
        },

        getClusterUrl: function (entityName) {
            var tpl = '/clusters/{0}',
                url;

            url = Ext.String.format(tpl, entityName);

            return this.createUrl(url);
        },

        getClusterCountUrl: function (entityName) {
            var tpl = new Ext.XTemplate('/clusters/{entityName}/count'),
                url;

            url = tpl.apply({
                entityName: entityName
            });

            return this.createUrl(url);
        },

        getClusterListUrl: function (entityName) {
            var tpl = new Ext.XTemplate('/clusters/{entityName}/records'),
                url;

            url = tpl.apply({
                entityName: entityName
            });

            return this.createUrl(url);
        },

        getMeasurementValuesUrl: function () {
            return this.createUrl('/measurementValues');
        },

        getMeasurementValueUrl: function (valueId) {
            var tpl = new Ext.XTemplate('/measurementValues/{valueId}/units'),
                url;

            url = tpl.apply({
                valueId: valueId
            });

            return this.createUrl(url);
        },

        getMeasurementValueExportUrl: function () {
            return this.createUrl('/measurementValues/export');
        },

        getMeasurementValueDeleteUrl: function (valueId) {
            var tpl = new Ext.XTemplate('/measurementValues/{valueId}'),
                url;

            url = tpl.apply({
                valueId: valueId
            });

            return this.createUrl(url);
        },

        getMeasurementValueBatchDeleteUrl: function () {
            return this.createUrl('/measurementValues/batchDelete');
        },

        getSystemLogUrl: function () {
            return this.createUrl('/system/logs');
        },

        getMetaDependencyGraphUrl: function () {
            return this.createUrl('/meta/model/dependency');
        },

        getTaskStatUrl: function () {
            return this.createUrl('/data/workflow/tasks/stat');
        },

        getJobTriggerUrl: function (jobId, triggerId) {
            var tpl,
                tplText,
                url;

            if (triggerId) {
                tplText = '/jobs/{jobId}/triggers/{triggerId}';
            } else {
                tplText = '/jobs/{jobId}/triggers';
            }

            tpl = new Ext.XTemplate(tplText);

            url = tpl.apply({
                jobId: jobId,
                triggerId: triggerId
            });

            return this.createUrl(url);
        }

    }
});
