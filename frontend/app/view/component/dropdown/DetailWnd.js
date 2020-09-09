/**
 * Окно с детализированной информацией по записи
 *
 * @author Ivan Marshalkin
 * @date 2018-07-30
 */

Ext.define('Unidata.view.component.dropdown.DetailWnd', {
    extend: 'Ext.window.Window',

    width: 600,
    height: 400,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        metaRecord: null,
        referencedDisplayAttributes: null,
        etalonId: null,
        validFrom: null,
        validTo: null
    },

    modal: true,
    title: Unidata.i18n.t('ddpickerfield>dataRecordDetailWndTitle'),

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
        this.callParent(arguments);
    },

    initItems: function () {
        this.callParent(arguments);

        this.add({
            xtype: 'dropdownpickerfield.detail',
            metaRecord: this.getMetaRecord(),
            referencedDisplayAttributes: this.getReferencedDisplayAttributes(),
            etalonId: this.getEtalonId(),
            validFrom: this.getValidFrom(),
            validTo: this.getValidTo(),
            asModal: true,
            gridHeight: 266
        });
    },

    items: [],

    dockedItems: [
        {
            xtype: 'un.toolbar',
            reference: 'buttonItems',
            dock: 'bottom',
            autoHide: true,
            items: [
                {
                    xtype: 'container',
                    flex: 1
                },
                {
                    xtype: 'button',
                    reference: 'nextButton',
                    margin: '0 0 0 10',
                    text: Unidata.i18n.t('ddpickerfield>dataRecordDetailCloseBtn'),
                    color: 'transparent',
                    handler: function () {
                        this.up('window').close();
                    }
                },
                {
                    xtype: 'container',
                    flex: 1
                }
            ]
        }
    ]
});
