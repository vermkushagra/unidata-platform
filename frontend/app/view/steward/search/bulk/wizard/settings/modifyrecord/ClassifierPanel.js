/**
 * @author Aleksandr Bavin
 * @date 2016-08-26
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.settings.modifyrecord.ClassifierPanel', {
    extend: 'Unidata.view.steward.dataclassifier.ClassifierPanel',

    requires: [
        'Unidata.view.steward.search.bulk.wizard.settings.modifyrecord.ClassifierItem'
    ],

    alias: 'widget.steward.search.bulk.wizard.settings.modifyrecords.classifierpanel',

    classifierItemClass: 'Unidata.view.steward.search.bulk.wizard.settings.modifyrecord.ClassifierItem'

});
