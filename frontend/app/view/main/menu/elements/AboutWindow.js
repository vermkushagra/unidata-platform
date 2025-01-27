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
        '<tr><td>' + Unidata.i18n.t('menu>buildDate') + ':</td><td>{unidataBuildDate}</td></tr>',
        '<tr><td>' + Unidata.i18n.t('menu>source') + ':</td><td>{source}</td></tr>',
        '<tr><td>' + Unidata.i18n.t('menu>date') + ':</td><td>{dataToday}</td></tr>',
        '<tr><td>' + Unidata.i18n.t('menu>licenseVersion') + ':</td><td>{licenseVersion}</td></tr>',
        '<tr>' +
            '<td>' + Unidata.i18n.t('menu>licenseDate') + ':</td>' +
            '<td><tpl if="dateLicense">' + Unidata.i18n.t('common:to').toLowerCase() + ' {dateLicense}</tpl></td>' +
        '</tr>',
        '</table>'
    ],

    initItems: function () {
        var UiUeUnidataPlatform = Unidata.uiuserexit.overridable.UnidataPlatform,
            unidataBuildDateFormatted,
            parsedDate,
            dateLicense,
            locale;

        this.callParent(arguments);

        if (unidataBuildDate) {
            parsedDate = Ext.Date.parse(unidataBuildDate, 'Y-m-d H:i:s');

            if (parsedDate) {
                unidataBuildDateFormatted = Ext.Date.format(parsedDate, Unidata.Config.getDateTimeFormat());
            } else {
                unidataBuildDateFormatted = unidataBuildDate;
            }
        }

        dateLicense = Unidata.Config.getLicenseExpirationDate();
        locale = Unidata.Config.getLocale();

        if (dateLicense) {
            dateLicense = Ext.Date.format(
                new Date(dateLicense),
                Unidata.Config.getDateFormat()
            );
        }

        this.update({
            unidataVersion: unidataVersion,
            unidataBuildDate: unidataBuildDateFormatted,
            source: window.location.origin,
            dataToday: Ext.Date.format(new Date(), Unidata.Config.getDateTimeFormat()),
            licenseVersion: Unidata.Config.getLicenseVersion(),
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
