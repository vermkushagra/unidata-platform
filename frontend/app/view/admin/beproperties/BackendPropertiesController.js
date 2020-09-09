/**
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.BackendPropertiesController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.beproperties',

    init: function () {
        var view = this.getView(),
            editor = view.backendPropertiesEditor,
            promise;

        this.callParent(arguments);

        view.setLoading(true);

        promise = Unidata.util.api.BackendProperties.getBackendProperties();
        promise
            .then(function (backendPropertyRecords) {
                editor.setBackendProperties(backendPropertyRecords);
            })
            .otherwise(function () {
            })
            .always(function () {
                view.setLoading(false);
            })
            .done();
    },

    /**
     * Обработчик нажатия на кнопку сохранения
     */
    onSaveBackendPropertiesButtonClick: function () {
        var view = this.getView(),
            editor = view.backendPropertiesEditor,
            promise;

        view.setLoading(true);

        promise = Unidata.util.api.BackendProperties.saveBackendProperties(editor.getBackendProperties());
        promise
            .then(function () {
                Unidata.showMessage(Unidata.i18n.t('backendProperties>propertiesSaveSuccess'));
            })
            .always(function () {
                view.setLoading(false);
            })
            .done();
    }
});
