/**
 * Миксин для отображения информации об отсутствии данных
 * @author Sergey Shishigin
 * @date 2016-09-02
 */
Ext.define('Unidata.mixin.NoDataDisplayable', {
    extend: 'Ext.Mixin',

    noDataComponent: null,                          // компонент отображения
    noDataTpl: '<div class="un-no-data">{0}</div>', // шаблон отображения
    noDataText: Unidata.i18n.t('common:noData'),                       // текст

    /**
     * Отобразить информацию об отсутствии данных
     * @param text Текст
     * @param parentComponent Родительский компонент в рамках которого отображается сообщение
     */
    showNoData: function (text) {
        var noDataComponent = this.noDataComponent,
            noDataTpl  = this.noDataTpl,
            noDataText = text || this.noDataText,
            html;

        if (!noDataComponent) {
            this.suspendEvent('add');
            html = Ext.String.format(noDataTpl, noDataText);
            this.noDataComponent = this.add({
                xtype: 'component',
                html: html
            });
            this.resumeEvent('add');
        }
    },

    /**
     * Скрыть информацию об отсутствии данных
     */
    hideNoData: function () {
        var noDataComponent = this.noDataComponent;

        if (noDataComponent) {
            this.suspendEvent('remove');
            this.remove(noDataComponent);
            this.noDataComponent = null;
            this.resumeEvent('remove');
        }
    }
});
