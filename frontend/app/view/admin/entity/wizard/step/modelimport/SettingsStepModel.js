/**
 * @author Aleksandr Bavin
 * @date 2017-01-31
 */
Ext.define('Unidata.view.admin.entity.wizard.step.modelimport.SettingsStepModel', {
    extend: 'Unidata.view.component.wizard.step.StepModel',

    requires: [
        'Unidata.model.entity.metadependency.Vertex'
    ],

    alias: 'viewmodel.admin.entity.wizard.step.modelimport.settings',

    data: {
        errorCount: 0
    },

    stores: {
        vertexes: {
            model: 'Unidata.model.entity.metadependency.Vertex',
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json'
                }
            }
        }
    }

});
