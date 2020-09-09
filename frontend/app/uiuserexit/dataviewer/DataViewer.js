/**
 * Класс для корректировки логики DataViewer
 *
 * @author Denis Makarov
 * @date 2018-06-20
 */

Ext.define('Unidata.uiuserexit.dataviewer.DataViewer', {
    singleton: true

    /**
     * Вызывается перед сохранением записи. Наобходим для дополнительной логики перед сохранением записи.
     * Для продолжения процесса сохранения необходимо вызывать dataViewer.saveAll()
     *
     *
     * @param {Unidata.view.steward.dataentity.DataViewer} dataViewer
     */
    // onSave: function (dataViewer) {
    //     dataViewer.saveAll();
    // }
});

