/**
 * UI редактор backend properties
 *
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.editor.BackendPropertiesEditor', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.beproperties.editor',

    viewModel: {
        type: 'admin.beproperties.editor'
    },
    controller: 'admin.beproperties.editor',

    requires: [
        'Unidata.view.admin.beproperties.editor.BackendPropertiesEditorController',
        'Unidata.view.admin.beproperties.editor.BackendPropertiesEditorModel',
        'Unidata.view.admin.beproperties.propertyeditor.PropertyEditor',
        'Unidata.view.admin.beproperties.propertygroup.PropertyGroup'
    ],

    referenceHolder: true,

    groupContainer: null,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        readOnly: false
    },

    cls: 'un-beprop-editor',

    title: Unidata.i18n.t('backendProperties>propertiesPanelTitle'),
    scrollable: 'vertical',
    bodyPadding: 20,

    methodMapper: [
        {
            method: 'setBackendProperties'
        },
        {
            method: 'getBackendProperties'
        },
        {
            method: 'updateReadOnly'
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();
    },

    initReferences: function () {
        this.groupContainer = this.lookupReference('groupContainer');
    },

    onDestroy: function () {
        this.groupContainer = null;

        this.callParent(arguments);
    },

    items: []
});
