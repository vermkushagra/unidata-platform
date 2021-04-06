/**
 * Модель для контейнера, реализующего отображение связи типа reference
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-10
 */

Ext.define('Unidata.view.steward.relation.reference.ReferenceModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.relation.reference',

    data: {
        readOnly: true,
        referenceData: null,
        dropped: false,
        droppedReferenceData: null
    }
});
