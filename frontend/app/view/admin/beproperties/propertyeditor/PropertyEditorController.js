/**
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.propertyeditor.PropertyEditorController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.beproperties.propertyeditor',

    init: function () {
        var view = this.getView();

        this.callParent(arguments);

        this.updatePropertyEditorType(view.property);

        this.syncPropertyLabel(view.property);
        this.setReadOnlyComponentState(view.getReadOnly());
    },

    onPropertyEditorAfterRender: function () {
        var view = this.getView(),
            property = view.property;

        view.propertyToolTip = Ext.create('Ext.tip.ToolTip', {
            target: view.propertyLabel.getEl(),
            html: Ext.String.format('{0} : {1}', Unidata.i18n.t('glossary:parameter'), property.get('name'))
        });
    },

    updatePropertyEditorType: function (property) {
        var view = this.getView(),
            type = property.get('type'),
            editorCfg = {},
            editorReadOnly = false,
            editorClass,
            editor,
            meta;

        meta = property.get('meta');

        if (meta && !Ext.isEmpty(meta.availableValues)) {
            type = 'Enumeration';
        }

        switch (type) {
            case 'String':
                editorClass = 'Unidata.view.admin.beproperties.propertyeditor.PropertyString';
                break;

            case 'Number':
                editorClass = 'Unidata.view.admin.beproperties.propertyeditor.PropertyNumber';
                break;

            case 'Integer':
                editorClass = 'Unidata.view.admin.beproperties.propertyeditor.PropertyInteger';
                break;

            case 'Boolean':
                editorClass = 'Unidata.view.admin.beproperties.propertyeditor.PropertyBoolean';
                break;

            case 'Enumeration':
                editorClass = 'Unidata.view.admin.beproperties.propertyeditor.PropertyEnumeration';
                break;

            default:
                editorClass = 'Unidata.view.admin.beproperties.propertyeditor.PropertyUnknown';
                break;
        }

        if (meta.readonly) {
            editorReadOnly = true;
        } else {
            editorReadOnly = view.getReadOnly();
        }

        editorCfg = Ext.apply({
            property: property,
            readOnly: editorReadOnly
        }, editorCfg);

        editor = Ext.create(editorClass, editorCfg);

        view.propertyContainer.removeAll();
        view.propertyContainer.add(editor);
    },

    /**
     * Обновляет текст в лейбле
     *
     * @param property
     */
    syncPropertyLabel: function (property) {
        var view = this.getView();

        if (view.propertyLabel) {
            view.propertyLabel.setText(property.get('displayName'));
        }
    },

    updateReadOnly: function (readOnly) {
        this.setReadOnlyComponentState(readOnly);
    },

    setReadOnlyComponentState: function (readOnly) {
        var view = this.getView(),
            items;

        if (!view.propertyContainer) {
            return;
        }

        items = view.propertyContainer.items;

        if (!items.isMixedCollection) {
            return;
        }

        items.each(function (item) {
            item.setReadOnly(readOnly);
        });
    }
});
