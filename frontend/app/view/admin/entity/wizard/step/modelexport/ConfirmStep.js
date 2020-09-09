/**
 * @author Aleksandr Bavin
 * @date 2017-01-31
 */

Ext.define('Unidata.view.admin.entity.wizard.step.modelexport.ConfirmStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    alias: 'widget.admin.entity.wizard.step.modelexport.confirm',

    includeUsers: null,
    includeRoles: null,

    title: Unidata.i18n.t('common:confirmation'),

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'component',
            maxWidth: 400,
            html: Unidata.i18n.t('admin.metamodel>exportDescription')
        },
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'begin'
            },
            securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
            securedEvent: 'create',
            items: [
                {
                    xtype: 'label',
                    text: Unidata.i18n.t('admin.metamodel>exportMetaModelWith') + ':',
                    style: {
                        'font-weight': '600',
                        'line-height': '28px'
                    }
                },
                {
                    xtype: 'checkbox',
                    reference: 'includeUsers',
                    boxLabel: Unidata.i18n.t('glossary:users'),
                    margin: '0 0 0 15'
                },
                {
                    xtype: 'checkbox',
                    reference: 'includeRoles',
                    boxLabel: Unidata.i18n.t('glossary:roles'),
                    margin: '0 0 0 15'
                }
            ]
        }
    ],

    createDockedButtons: function () {
        return [
            {
                xtype: 'button',
                text: Unidata.i18n.t('common:back'),
                reference: 'prevButton',
                color: 'transparent',
                listeners: {
                    click: 'onPrevClick'
                }
            },
            {
                xtype: 'container',
                flex: 1
            },
            {
                xtype: 'button',
                text: Unidata.i18n.t('common:confirm'),
                reference: 'confirmButton',
                listeners: {
                    click: this.onConfirmButtonClick,
                    scope: this
                }
            }
        ];
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
        this.includeUsers = this.lookupReference('includeUsers');
        this.includeRoles = this.lookupReference('includeRoles');
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
        this.callParent(arguments);

        this.includeUsers = null;
        this.includeRoles = null;
    },

    onConfirmButtonClick: function () {
        this.runExport();
    },

    runExport: function () {
        Unidata.showMessage(Unidata.i18n.t('admin.metamodel>startExportProcess'));

        Ext.Ajax.unidataRequest({
            url: Unidata.Config.getMainUrl() + 'internal/meta/model-ie/export',
            method: 'POST',
            headers: {
                'Accept':       'application/json',
                'Content-Type': 'application/json'
            },
            jsonData: Ext.util.JSON.encode({
                users: Boolean(this.includeUsers.getValue()),
                roles: Boolean(this.includeRoles.getValue())
            }),
            success: function () {
            }
        });

        this.finish();
    }

});
