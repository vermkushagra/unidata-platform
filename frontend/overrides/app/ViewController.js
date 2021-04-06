/**
 * Оверайд базового класса ViewController
 *
 * @author Igor Redkin
 * @date 2015-06-23
 */

Ext.define('Ext.overrides.app.ViewController', {
    override: 'Ext.app.ViewController',

    //TODO: объект будет лежать в прототипе, необходимо проанализировать эту ситуацию нужно ли чтоб он был в прототипе
    //если не нужно => присвоить первончальное значение null, инициализировать в методе init
    //провести тестирование на утечку памяти
    dirtyViewModels: {},

    checkRecordDirty: function (record, viewModel) {
        var currentViewModel;

        if (!this.dirtyViewModels[viewModel.id]) {
            this.dirtyViewModels[viewModel.id] = {};
        }

        currentViewModel = this.dirtyViewModels[viewModel.id];

        if (record.dirty && !record.phantom) {
            currentViewModel[record.id] = record.modified;
        } else if (record.phantom) {
            // check new phantom record
            if (record.store) {
                currentViewModel[record.id] = {
                    phantom: true
                };
            } else {
                delete currentViewModel[record.id];
            }
        } else {
            delete currentViewModel[record.id];
        }

        viewModel.set('dirty', !!Object.keys(currentViewModel).length);
    },

    clearDirty: function (viewModel) {
        this.dirtyViewModels[viewModel.id] = {};
        viewModel.set('dirty', false);
    },

    getDirtyViewModels: function (id) {
        return this.dirtyViewModels[id];
    },

    showError: function () {
        return Unidata.util.UserDialog.showError.apply(Unidata.util.UserDialog, arguments);
    },

    showWarning: function () {
        return Unidata.util.UserDialog.showWarning.apply(Unidata.util.UserDialog, arguments);
    },

    showMessage: function () {
        return Unidata.util.UserDialog.showMessage.apply(Unidata.util.UserDialog, arguments);
    },

    showPrompt: function () {
        return Unidata.util.UserDialog.showPrompt.apply(Unidata.util.UserDialog, arguments);
    },

    /**
     * Пробросить событие с подставленным первым аргументом текущего view
     * Вызывается из обработчика
     *
     * @param eventName Имя события
     * @param view Текущий view
     * @param args Арументы, пришедшие в обработчик
     */
    //TODO: Заменить на функция из коробки Observable.createRelayer
    appendViewToListener: function (eventName, view, args) {
        args = Array.prototype.slice.call(args, 0, -1);
        args.unshift(eventName, view);

        view.fireEvent.apply(view, args);
    }
});
