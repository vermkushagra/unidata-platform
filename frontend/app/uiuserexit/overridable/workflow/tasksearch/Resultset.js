/**
 * Реализация функции для скрытия атрибутов в результатах поиска
 *
 * @author Denis Makarov
 * @date 2018-05-29
 */
Ext.define('Unidata.uiuserexit.overridable.workflow.tasksearch.Resultset', {
    singleton: true,

    /**
     * Скрываемые атрибуты поисковой выдачи для задач
     * Свойство для переопределения кастомером
     * @returns []
     */
    taskHiddenAttributes: [],

    /**
     * Скрываемые атрибуты поисковой выдачи для процессов
     * Свойство для переопределения кастомером
     * @returns []
     */
    processHiddenAttributes: [],

    /**
     * Стандартная реализация функции для удаления лишних атрибутов
     * @returns {*}
     */
    modifyTplData: function (data, resultType) {
        var tplData = Ext.clone(data),
            hiddenAttributes = (resultType === 'TASK') ? this.taskHiddenAttributes : this.processHiddenAttributes;

        Ext.Object.each(data, function (attribute) {
            if (data.hasOwnProperty(attribute) && Ext.Array.contains(hiddenAttributes, attribute)) {
                delete tplData[attribute];
            }
        }, this);

        return tplData;
    }
});
