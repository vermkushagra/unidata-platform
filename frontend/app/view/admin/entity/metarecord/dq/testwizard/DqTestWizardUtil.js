/**
 * Вспомогательный класс для визарда тестирования правил качества
 *
 * @author Ivan Marshalkin
 * @date 2018-03-14
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.testwizard.DqTestWizardUtil', {
    singleton: true,

    /**
     * Возвращает заголовок панелей по главным отображаемым атрибутам записи
     *
     * @param metaRecord
     * @param dataRecord
     * @param opt
     *
     * @returns {string}
     */
    buildDataRecordTitle: function (metaRecord, dataRecord, opt) {
        var title = '',
            etalonId = '';

        opt = opt || {};

        if (dataRecord) {
            title = Unidata.util.DataAttributeFormatter.buildEntityTitleFromDataRecord(metaRecord, dataRecord);
            etalonId = dataRecord.getId();

            if (Ext.isEmpty(title)) {
                title = Unidata.i18n.t('admin.dqtest>mainDisplayableIsUndefined');
            }

            if (opt.displayEtalonId) {
                title += ' (ID: ' + Ext.htmlEncode(etalonId) + ')';
            }
        }

        if (dataRecord && dataRecord.phantom) {
            title = Unidata.i18n.t('admin.dqtest>newDataRecord');
        }

        return title;
    },

    /**
     * Модифицирует рендер функцию для панели результатов поиска. Необходимо отображать дополнительную информацию - идентификатор записи
     *
     * @param resultPanel
     */
    transformResultPanelColumnRenderer: function (resultPanel) {
        var columnManager = resultPanel.resultsetGrid.getColumnManager(),
            column = columnManager.getColumns()[0],
            oldRenderer = column.renderer;

        column.renderer = function (value, metadata, record) {
            var value = '';

            if (Ext.isFunction(oldRenderer)) {
                value = oldRenderer.apply(column, arguments);
            }

            value += 'ID: ' + Ext.htmlEncode(record.getId());

            return value;
        };
    }
});
