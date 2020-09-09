/**
 * Точка расширения для DataEntity
 *
 * @author Aleksandr Bavin
 * @date 2018-06-06
 */
Ext.define('Unidata.uiuserexit.overridable.dataentity.DataEntity', {

    singleton: true,

    /**
     * Вызывается при отображении карточки
     *
     * @param {Unidata.view.steward.dataentity.DataEntity} dataEntity
     */
    onDataEntityDisplay: function (dataEntity) { // jscs:ignore disallowUnusedParams
    },

    /**
     * Вызывается при выборе узла классификатора пользователем
     *
     * @param {Unidata.view.steward.dataentity.DataEntity} dataEntity
     * @param {string} classifierName - имя классификатора, для которого измениля узел
     * @param {Unidata.model.classifier.ClassifierNode | null} selectedNode - новый узел классификатора
     * @param {Unidata.model.classifier.ClassifierNode | null} deselectedNode - старый узел классификатора
     * @param {Ext.Deferred} deferredNodeChange - отложенная перезагрузка карточки, необходимо зарезолвить для продолжения работы
     * @returns {boolean} - если вернули false, то перезагрузка карточки будет приостановлена до резолва deferredNodeChange
     */
    onClassifierNodeChange: function (dataEntity, classifierName, selectedNode, deselectedNode, deferredNodeChange) { // jscs:ignore disallowUnusedParams
    }

});
