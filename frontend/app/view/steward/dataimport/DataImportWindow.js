/**
 * Окно импорта данных
 *
 * @author Aleksandr Bavin
 * @date 2017-07-03
 */
Ext.define('Unidata.view.steward.dataimport.DataImportWindow', {

    extend: 'Ext.window.Window',

    requires: [
        'Unidata.view.steward.dataimport.wizard.DataImportWizard'
    ],

    alias: 'widget.dataimport.window',

    layout: 'fit',

    width: 550,
    resizable: false,
    modal: true,
    draggable: false,
    monitorResize: true,
    alwaysCentered: true,

    // closeOnOutsideClick: true,

    title: Unidata.i18n.t('common:importData'),

    config: {
        entityName: null
    },

    wizard: null,

    updateEntityName: function (entityName) {
        if (this.wizard) {
            this.wizard.setEntityName(entityName);
        }
    },

    initItems: function () {
        this.callParent(arguments);
        this.initWizard();
    },

    onDestroy: function () {
        this.wizard = null;
    },

    initWizard: function () {
        this.wizard = this.add({
            xtype: 'dataimport.wizard',
            entityName: this.getEntityName(),
            listeners: {
                blockchange: this.onBlockchange,
                finish: this.close,
                scope: this
            }
        });
    },

    onBlockchange: function (wizard, blocked) {
        this.setLoading(blocked);
    }

});
