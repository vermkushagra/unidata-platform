/**
 * Информация о системе
 *
 * @author Aleksandr Bavin
 * @date 2017-06-19
 */
Ext.define('Unidata.view.main.menu.elements.AboutWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Unidata.uiuserexit.overridable.UnidataPlatform'
    ],

    alias: 'widget.about',

    layout: 'fit',

    ui: 'un-about',

    width: 450,
    resizable: false,
    modal: true,
    closable: false,
    draggable: false,

    closeOnOutsideClick: true,

    title: null,

    header: {
        titlePosition: 1
    },

    tpl: [
        '<table>',
        '<tr><td>' + Unidata.i18n.t('menu>version') + ':</td><td>{unidataVersion}</td></tr>',
        '<tr><td>' + Unidata.i18n.t('menu>backendVersion') + ':</td><td>{backendVersion}</td></tr>',
        '<tr><td>' + Unidata.i18n.t('menu>buildDate') + ':</td><td>{unidataBuildDate}</td></tr>',
        '<tr><td>' + Unidata.i18n.t('menu>source') + ':</td><td>{source}</td></tr>',
        '<tr><td>' + Unidata.i18n.t('menu>date') + ':</td><td>{dataToday}</td></tr>',
        '<tpl if="modules">',
        '<tr><td>' + Unidata.i18n.t('menu>modules') + ':</td><td>{modules}</td></tr>',
        '</tpl>',
        '<tpl if="owner">',
        '<tr><td>' + Unidata.i18n.t('menu>owner') + ':</td><td>{owner}</td></tr>',
        '</tpl>',
        '<tr><td>' + Unidata.i18n.t('menu>edition') + ':</td><td>{licenseVersion}</td></tr>',
        '<tr><td>' + Unidata.i18n.t('menu>licenseVersion') + ':</td><td>{licenseModeDisplayName}</td></tr>',
        '<tr>' +
            '<td>' + Unidata.i18n.t('menu>licenseDate') + ':</td>' +
            '<td><tpl if="dateLicense">' + Unidata.i18n.t('common:to').toLowerCase() + ' {dateLicense}</tpl></td>' +
        '</tr>',
        '</table>'
    ],

    initItems: function () {
        var UiUeUnidataPlatform = Unidata.uiuserexit.overridable.UnidataPlatform,
            authenticateData = Unidata.Config.getAuthenticateData(),
            unidataBuildDateFormatted,
            parsedDate,
            dateLicense,
            authenticateData,
            backendVersion;

        this.callParent(arguments);

        if (unidataBuildDate) {
            parsedDate = Ext.Date.parse(unidataBuildDate, 'Y-m-d H:i:s');

            if (parsedDate) {
                unidataBuildDateFormatted = Ext.Date.format(parsedDate, Unidata.Config.getDateTimeFormat());
            } else {
                unidataBuildDateFormatted = unidataBuildDate;
            }
        }

        dateLicense = Unidata.License.getLicenseExpirationDate();

        if (dateLicense) {
            dateLicense = Ext.Date.format(
                new Date(dateLicense),
                Unidata.Config.getDateFormat()
            );
        }

        backendVersion =  authenticateData ?  authenticateData.buildVersion : '';

        this.update({
            unidataVersion: unidataVersion,
            backendVersion: backendVersion,
            unidataBuildDate: unidataBuildDateFormatted,
            source: window.location.origin,
            dataToday: Ext.Date.format(new Date(), Unidata.Config.getDateTimeFormat()),
            modules: Unidata.License.getLicenseModules().join('<br>'),
            owner: Unidata.License.getLicenseOwner(),
            licenseVersion: Unidata.License.getLicenseVersion(),
            licenseModeDisplayName: Unidata.License.getLicenseModeDisplayName(),
            dateLicense: dateLicense
        });

        this.addTool([
            {
                xtype: 'container',
                cls: 'un-window-logo',
                height: 76,
                html: UiUeUnidataPlatform.getLogoPlatformHtml()
            },
            {
                xtype: 'un.fontbutton',
                cls: 'un-close-icon',
                iconCls: 'icon-cross',
                handler: 'close',
                scope: this
            }
        ]);

        this.addDocked([
            {
                xtype: 'container',
                dock: 'bottom',
                layout: {
                    type: 'hbox',
                    pack: 'center'
                },
                items: [
                    {
                        xtype: 'button',
                        scale: 'large',
                        color: 'transparent',
                        text: Unidata.i18n.t('common:close'),
                        handler: 'close',
                        scope: this
                    }
                ]
            }
        ]);
    },

    listeners: {
        boxready: function (wnd) {
            new Ext.KeyMap(wnd.getEl(), {
                key: Ext.EventObject.ESC,
                fn: function () {
                    this.close();
                },
                scope: wnd
            });
        }
    }
});
