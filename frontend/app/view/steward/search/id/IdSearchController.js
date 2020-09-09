/**
 * @author Aleksandr Bavin
 * @date 2016-12-21
 */
Ext.define('Unidata.view.steward.search.id.IdSearchController', {

    extend: 'Ext.app.ViewController',
    alias: 'controller.idsearch',

    init: function () {
        var view = this.getView();

        view.on('submitstart', this.onSubmitstart, this);
        view.on('show', this.onWindowShow, this);
    },

    onWindowShow: function () {
        this.lookupReference('form').reset();
    },

    isUUID: function (str) {
        var pattern = /^[0-9A-F]{8}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12}$/i;

        return pattern.test(str);
    },

    onSubmitstart: function (form) {
        var view = this.getView(),
            formFieldValues = form.getFieldValues(),
            searchBy = formFieldValues['searchBy'],
            baseUrl = Unidata.Config.getMainUrl(),
            url;

        switch (searchBy) {
            case 'etalonId':

                if (!this.isUUID(formFieldValues['etalonId'])) {
                    Unidata.showError(Unidata.i18n.t('search>query.incorrectUUIDPattern'));

                    return false;
                }

                url = Ext.String.format(
                    '{0}internal/data/entities/keys/etalon/{1}',
                    baseUrl,
                    formFieldValues['etalonId']
                );
                break;
            case 'externalId':
                url = Ext.String.format(
                    '{0}internal/data/entities/keys/external/{1}/{2}/{3}',
                    baseUrl,
                    formFieldValues['externalId'],
                    formFieldValues['sourceSystem'],
                    formFieldValues['name']
                );
                break;
            case 'originId':
                if (!this.isUUID(formFieldValues['originId'])) {
                    Unidata.showError(Unidata.i18n.t('search>query.incorrectUUIDPattern'));

                    return false;
                }

                url = Ext.String.format(
                    '{0}internal/data/entities/keys/origin/{1}',
                    baseUrl,
                    formFieldValues['originId']
                );
                break;
        }

        view.setLoading(true);

        Ext.Ajax.unidataRequest({
            method: 'GET',
            url: url,
            success: function (response) {
                var error = true,
                    content,
                    responseJson;

                if (response) {
                    responseJson = Ext.JSON.decode(response.responseText, true);
                }

                if (responseJson && responseJson.success) {
                    content = responseJson.content;

                    if (content && content.etalonId) {
                        error = false;
                        this.redirectToEtalonId(content.etalonId);
                    }
                }

                if (error) {
                    form.markInvalid([
                        {
                            id: searchBy,
                            msg: Unidata.i18n.t('search>query.recordNotFound', {searchBy: searchBy})
                        }
                    ]);
                }
            },
            callback: function () {
                view.setLoading(false);
            },
            scope: this
        });

        return false;
    },

    redirectToEtalonId: function (etalonId) {
        var view = this.getView();

        Unidata.util.Router
            .setToken('main', {section: 'data', reset: true})
            .setToken('etalon', {
                etalonId: etalonId
            });

        view.hide();
    }

});
