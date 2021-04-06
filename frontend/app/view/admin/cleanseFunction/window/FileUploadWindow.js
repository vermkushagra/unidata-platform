Ext.define('Unidata.view.admin.cleanseFunction.window.FileUploadWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Unidata.view.admin.cleanseFunction.window.FileUploadWindowController',
        'Unidata.view.admin.cleanseFunction.window.FileUploadWindowModel',

        'Ext.form.field.File'
    ],

    controller: 'admin.cleanseFunction.window',
    viewModel: {
        type: 'admin.cleanseFunction.window'
    },

    config: {
        draftMode: null
    },

    bind: {
        title: Unidata.i18n.t('admin.cleanseFunction>loadThirdPartyFunction')
    },

    width: 400,
    height: 150,

    autoScroll: true,
    bodyPadding: 10,
    constrain: true,
    closable: false,
    referenceHolder: true,
    cls: 'unidata-file-upload-window',
    modal: true,
    layout: 'vbox',
    items: [
        {
            xtype: 'form',
            width: '100%',
            reference: 'fileUploadForm',
            referenceHolder: true,
            url: Unidata.Config.getMainUrl() + 'internal/meta/cleanse-functions/upload',
            items: [
                {
                    xtype: 'filefield',
                    cls: 'file-upload-field',
                    reference: 'fileUploadField',
                    fieldLabel: Unidata.i18n.t('admin.cleanseFunction>pressButtonUploadJarFile'),
                    labelSeparator: '',
                    buttonConfig: {
                        glyph: 'xf093@FontAwesome',
                        tooltip: Unidata.i18n.t('common:loadFile'),
                        text: ''
                    },
                    buttonOnly: true,
                    msgTarget: 'file-upload-error',
                    listeners: {
                        change: 'onFileUpload'
                    },
                    validator: function (val) {
                        var res = true,
                            extensions = ['jar'],
                            extension = '',
                            charIndex;

                        charIndex = val.lastIndexOf('.');

                        if (charIndex !== -1) {
                            extension = val.substr(charIndex + 1);
                        }

                        if (Ext.isArray(extensions) &&
                            extensions.length > 0 &&
                            extensions.indexOf(extension) === -1) {

                            res = Unidata.i18n.t('admin.cleanseFunction>selectFileOfCorrectType', {correctTypes: extensions.join(',')});
                        }

                        return res;
                    }
                },
                {
                    xtype: 'container',
                    cls: 'x-form-error-wrap x-form-error-wrap-default x-form-error-wrap-under ' +
                    'x-form-error-wrap-under-side-label',
                    items: [
                        {
                            xtype: 'container',
                            cls: 'x-form-error-msg x-form-invalid-under x-form-invalid-under-default',
                            id: 'file-upload-error'
                            //html: '&nbsp;'
                        }
                    ]
                }
            ],
            bind: {
                hidden: '{isSuccess}'
            }
        },
        {
            xtype: 'container',
            hidden: true,
            bind: {
                html: '<p class="success-loading">' +
                          Unidata.i18n.t('admin.cleanseFunction>uploadFunctionSuccess', {name: '<b>{loadedCleanseFunction.name}</b>'}) +
                      '</p>',
                hidden: '{!isSuccess}'
            }
        }
        /*
        Загрузка нескольких функций временно деактивирована
        {
            xtype: 'grid',
            width: '100%',
            margin: '10 0 0 0',
            hidden: true,
            bind: {
                store: '{loadedCleanseFunctions}',
                hidden: '{!isSuccess}'
            },
            hideHeaders: true,
            title: 'Функции успешно загружены из файла',
            cls: 'load-cleanse-function-statuses',
            columns: [
                {
                    text: 'Имя',
                    dataIndex: 'name',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true,
                    draggable: false,
                    resizable: false
                },
                {
                    text: 'Статус',
                    dataIndex: 'state',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true,
                    draggable: false,
                    resizable: false,
                    renderer: 'stateColumnRenderer'
                }
            ]
        }*/
    ],
    buttons: [
        {
            text: Unidata.i18n.t('common:close'),
            handler: 'onFileUploadWindowClose'
        }
    ]
});
