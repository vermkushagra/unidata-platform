/**
 * Окно редактирования группы
 *
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.group.GroupInfoWindow', {
    extend: 'Ext.window.Window',

    alias: 'widget.admin.duplicates.groupinfowindow',

    requires: [
        'Unidata.view.admin.duplicates.group.GroupListController',
        'Unidata.view.admin.duplicates.group.GroupListModel'
    ],

    controller: 'admin.duplicates.groupinfowindow',
    viewModel: {
        type: 'admin.duplicates.groupinfowindow'
    },

    referenceHolder: true,

    groupName: null,               // поле ввода наименования группы
    groupDescription: null,        // поле ввода описания группы
    groupAutoMerge: null,        // поле ввода описания группы

    entityName: null,              // имя реестра / справочника к которому привязана группа (не обязательный параметр используется только если создается модель)
    groupModel: null,              // редактируемая модель

    modal: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    bodyPadding: 10,

    width: 500,
    height: 300,

    initComponent: function () {
        var controller = this.getController(),
            viewModel = this.getViewModel(),
            wndTitle = Unidata.i18n.t('admin.duplicates>groupInfoWndTitleExistGroup');

        this.callParent(arguments);

        // создаем редактируемую модель
        if (!this.groupModel) {
            this.groupModel = Ext.create('Unidata.model.matching.Group', {
                entityName: this.entityName
            });
        }

        if (this.groupModel.phantom) {
            wndTitle = Unidata.i18n.t('admin.duplicates>groupInfoWndTitleNewGroup');
        }

        viewModel.set('wndTitle', wndTitle);

        this.initComponentReference();

        controller.initFieldValueByGroupModel();
    },

    initComponentReference: function () {
        var me = this;

        me.groupName        = me.lookupReference('groupName');
        me.groupDescription = me.lookupReference('groupDescription');
        me.groupAutoMerge = me.lookupReference('groupAutoMerge');
    },

    onDestroy: function () {
        var me = this;

        me.groupName        = null;
        me.groupDescription = null;
        me.groupAutoMerge = null;

        me.groupModel       = null;

        me.callParent(arguments);
    },

    bind: {
        title: '{wndTitle}'
    },

    items: [
        {
            xtype: 'textfield',
            reference: 'groupName',
            labelAlign: 'top',
            fieldLabel: Unidata.i18n.t('glossary:naming'),
            allowBlank: false
        },
        {
            xtype: 'textarea',
            reference: 'groupDescription',
            labelAlign: 'top',
            fieldLabel: Unidata.i18n.t('glossary:description'),
            flex: 1
        },
        {
            xtype: 'checkbox',
            reference: 'groupAutoMerge',
            fieldLabel: Unidata.i18n.t('admin.duplicates>groupAutoMerge'),
            labelWidth: 220
        }
    ],

    dockedItems: {
        xtype: 'toolbar',
        reference: 'toolbar',
        ui: 'footer',
        dock: 'bottom',
        layout: {
            pack: 'end'
        },
        items: [
            {
                xtype: 'button',
                reference: 'saveButton',
                text: Unidata.i18n.t('common:save'),
                scope: this,
                listeners: {
                    click: 'onSaveButtonClick'
                }
            },
            {
                xtype: 'button',
                reference: 'cancelButton',
                text: Unidata.i18n.t('common:cancel'),
                scope: this,
                listeners: {
                    click: 'onCancelButtonClick'
                }
            }
        ]
    }
});
