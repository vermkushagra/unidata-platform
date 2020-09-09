/**
 * @author Aleksandr Bavin
 * @date 2016-08-05
 */
Ext.define('Unidata.view.workflow.process.assignment.WorkflowProcessAssignmentModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.assignment',

    stores: {
        entity: {
            fields: [
                {name: 'name', type: 'string'},
                {name: 'displayName', type: 'string'}
            ],
            proxy: {
                type: 'ajax',
                url: Unidata.Config.getMainUrl() + 'internal/data/workflow/assignable-entities',
                limitParam: '',
                startParam: '',
                pageParam: '',
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            }
        },
        types: {
            model: 'Unidata.model.workflow.Type'
        },
        processes: {
            model: 'Unidata.model.workflow.Process'
        },
        assignments: {
            model: 'Unidata.model.workflow.Assignment',
            groupField: 'displayName'
        },
        triggerType: {
            model: 'Unidata.model.workflow.TriggerType',
            data: [
                {
                    name: 'ALL',
                    displayName: Unidata.i18n.t('workflow>startProcessForAllChanges')
                },
                {
                    name: 'VERSION_CONFLICT',
                    displayName: ' ' + Unidata.i18n.t('workflow>startProcessIfVersionConflict')
                }
            ]
        }
    }
});
