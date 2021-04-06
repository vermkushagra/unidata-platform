/**
 * @author Aleksandr Bavin
 * @date 2017-05-17
 */
Ext.define('Unidata.view.main.menu.elements.ExitItem', {

    extend: 'Unidata.view.main.menu.MainMenuItem',

    alias: 'widget.un.list.item.mainmenu.item.exit',

    iconCls: 'icon-power-switch',

    reference: 'exit',

    text: Unidata.i18n.t('menu>exit'),

    onClick: function () {
        var promise;

        this.callParent(arguments);

        Unidata.util.Router
            .suspendTokenEvents()
            .removeTokens()
            .resumeTokenEvents();

        promise = Unidata.util.api.Authenticate.logout();

        promise
            .then(function () {
                var application = Unidata.getApplication();

                application.fireEvent('deauthenticate');

                application.showViewPort('login');
            })
            .otherwise(function () {
            })
            .done();
    }

});
