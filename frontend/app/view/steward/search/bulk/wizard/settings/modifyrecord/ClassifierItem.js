/**
 * @author Aleksandr Bavin
 * @date 2016-08-26
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.settings.modifyrecord.ClassifierItem', {
    extend: 'Unidata.view.steward.dataclassifier.item.ClassifierItem',

    requires: [
        'Unidata.view.steward.search.bulk.wizard.settings.modifyrecord.ClassifierItemController'
    ],

    ui: 'card',

    alias: 'widget.steward.search.bulk.wizard.settings.modifyrecords.classifieritempanel',

    controller: 'classifieritempanel',

    listeners: {
        added: 'onClassifierItemAdded'
    }

});
