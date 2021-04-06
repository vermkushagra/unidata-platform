/**
 * @author Aleksandr Bavin
 * @date 2017-07-03
 */
Ext.define('Unidata.view.steward.dataimport.wizard.step.SettingsStepModel', {

    extend: 'Unidata.view.component.wizard.step.StepModel',

    alias: 'viewmodel.dataimport.wizard.settings',

    data: {
        adminSystemName: null
    },

    stores: {
        sourceSystems: {
            model: 'Unidata.model.sourcesystem.SourceSystem',
            autoLoad: true,
            proxy: {
                type: 'rest',

                url: Unidata.Config.getMainUrl() + 'internal/meta/source-systems',
                reader: {
                    type: 'json',
                    rootProperty: 'sourceSystem'
                }
            },
            listeners: {
                load: 'onSourceSystemsStoreLoad'
            }
        }
    }

});
