/**
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.editor.BackendPropertiesEditorModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.beproperties.editor',

    data: {},

    stores: {
        backendPropertiesStore: {
            model: 'Unidata.model.beproperties.BackendProperties',
            groupField: 'group'
        }
    }
});
