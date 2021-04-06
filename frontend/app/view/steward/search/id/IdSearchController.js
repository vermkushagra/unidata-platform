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

    onSubmitstart: function (form) {
        var view = this.getView(),
            formFieldValues = form.getFieldValues(),
            searchBy = formFieldValues['searchBy'],
            baseUrl = Unidata.Config.getMainUrl(),
            url;

        view.setLoading(true);

        switch (searchBy) {
            case 'etalonId':
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
                url = Ext.String.format(
                    '{0}internal/data/entities/keys/origin/{1}',
                    baseUrl,
                    formFieldValues['originId']
                );
                break;
        }

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
