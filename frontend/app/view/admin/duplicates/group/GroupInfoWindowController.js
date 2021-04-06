/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.list.GroupInfoWindowController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.duplicates.groupinfowindow',

    /**
     * Инициализирует значения полей для редактирования
     */
    initFieldValueByGroupModel: function () {
        var view       = this.getView(),
            groupModel = view.groupModel;

        view.groupName.setValue(groupModel.get('name'));
        view.groupDescription.setValue(groupModel.get('description'));
        view.groupAutoMerge.setValue(groupModel.get('autoMerge'));
    },

    /**
     * Обработчик клика по сохранения изменений
     */
    onSaveButtonClick: function () {
        var view       = this.getView(),
            groupModel = view.groupModel;

        if (view.groupName.isValid()) {
            groupModel.set('name', view.groupName.getValue());
            groupModel.set('description', view.groupDescription.getValue());
            groupModel.set('autoMerge', view.groupAutoMerge.getValue());

            view.fireEvent('modeledited', groupModel);
        }
    },

    /**
     * Обработчик клика по кнопке отмены
     */
    onCancelButtonClick: function () {
        this.getView().close();
    }
});
