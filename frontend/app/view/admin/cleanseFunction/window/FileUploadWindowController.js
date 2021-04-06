Ext.define('Unidata.view.admin.cleanseFunction.window.FileUploadWindowController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.cleanseFunction.window',

    promptMessageBoxes: [],

    onFileUpload: function (component) {
        var view = this.getView(),
            uploadXhr = new XMLHttpRequest(),
            uploadUrl = Unidata.Config.getMainUrl() + 'internal/meta/cleanse-functions/upload',
            uploadGetParams,
            fileUploadField,
            files,
            formData,
            btn, me,
            viewModel,
            store,
            cfName,
            loadedCfName;

        fileUploadField = view
                                .lookupReference('fileUploadForm')
                                .lookupReference('fileUploadField');
        files = fileUploadField.fileInputEl.dom.files;
        formData = new FormData();
        btn = this.getView().lookupReference('loadButton');
        me = this;
        viewModel = this.getViewModel();
        cfName = viewModel.get('cleanseFunctionName');

        if (!fileUploadField.isValid()) {
            return;
        }

        formData.append('jarFile', files[0], files[0].name);

        view.setLoading(Unidata.i18n.t('common:fileLoadingEllipsis'));

        /**
         * Apply cleanse functions from jar file
         * @param temporaryId
         */
        function applyCleanseFunctions (temporaryId) {
            var url = Unidata.Config.getMainUrl() + 'internal/meta/cleanse-functions/apply/' + temporaryId,
                getParams;

            getParams = {
                draft: view.getDraftMode()
            };

            url = Ext.urlAppend(url, Ext.Object.toQueryString(getParams));

            store.clearFilter();
            store.commitChanges();
            view.setLoading(Unidata.i18n.t('common:fileLoadingEllipsis'));

            function createRequest (data) {
                var replaceMap, request;

                request = [];
                replaceMap = {
                    'NEW': 'CREATE',
                    'DUPLICATE': 'DO_NOTHING',
                    'OVERWRITE': 'OVERWRITE'
                };
                data.each(function (item) {
                    request.push({
                        name: item.get('name'),
                        action: replaceMap[item.get('state')]
                    });
                });

                return request;
            }

            Ext.Ajax.request({
                url: url,
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                jsonData: createRequest(store.getData()),
                callback: function (options, success) {
                    view.setLoading(false);
                    viewModel.set('isSuccess', success);

                    if (success) {
                        me.getView().close();
                    } else {
                        fileUploadField.markInvalid(Unidata.i18n.t('admin.cleanseFunction>callFunctionError'));
                    }
                }
            });
        }

        /**
         * Ask for overwrite of cleanse function, duplicated on serverside
         * @param item cleanse function info
         * @param msgboxIndex index of prompt msgbox
         * @param temporaryId operation id
         */
        function askDuplicateFunctionOverwrite (item, msgboxIndex, temporaryId) {
            var msgbox = me.promptMessageBoxes[msgboxIndex];

            msgbox.show({
                title: Unidata.i18n.t('admin.cleanseFunction>foundDuplicate'),
                message: Unidata.i18n.t('admin.cleanseFunction>confirmRestarDuplicatedFunction', {name: item.get('name')}),
                buttons: Ext.MessageBox.YESNO,
                buttonText: {
                    yes: Unidata.i18n.t('common:yes'),
                    no: Unidata.i18n.t('common:no')
                },
                scope: this,
                animateTarget: btn,
                defaultFocus: 3,
                fn: function (btn) {
                    Ext.Array.removeAt(me.promptMessageBoxes, msgboxIndex);

                    if (btn === 'yes') {
                        item.set('state', 'OVERWRITE');
                        // the last message box is removed
                        if (me.promptMessageBoxes.length === 0) {
                            applyCleanseFunctions(temporaryId);
                        }
                    }
                }
            });
        }

        uploadGetParams = {
            draft: view.getDraftMode()
        };

        uploadUrl = Ext.urlAppend(uploadUrl, Ext.Object.toQueryString(uploadGetParams));

        uploadXhr.open('POST', uploadUrl, true);
        uploadXhr.onload = function () {
            component.suspendEvent('change');
            component.reset();
            component.resumeEvent('change');

            if (uploadXhr.status === 200 || uploadXhr.status === 202) {
                var response = JSON.parse(uploadXhr.response);

                if (response.status !== 'SUCCESS') {
                    fileUploadField.markInvalid(response.errorText);
                } else if (response.functions === null || response.functions.length === 0) {
                    fileUploadField.markInvalid(Unidata.i18n.t('admin.cleanseFunction>noFunctionsInFile'));
                } else if (cfName !== null && (loadedCfName = response.functions[0].name) !== cfName) {
                    var msg = Unidata.i18n.t(
                        'admin.cleanseFunction>invalidFunctionName',
                        {loadedName: loadedCfName, requiredName: cfName}
                    );

                    fileUploadField.markInvalid(msg);
                } else {
                    store = viewModel.getStore('loadedCleanseFunctions');
                    store.getProxy().setData(response.functions);
                    //TODO: add load failure handle
                    store.load();
                    store.getData();

                    if (cfName !== null) {
                        // apply first CF with dqAction OVERWRITE
                        store.getData().first().set('state', 'OVERWRITE');
                        applyCleanseFunctions(response.temporaryId);
                    } else {
                        // ask user about overwriting multiple functions
                        store.filter('state', 'DUPLICATE');

                        if (store.getData().length === 0) {
                            applyCleanseFunctions(response.temporaryId);
                        } else {
                            store.getData().each(function (item) {
                                me.promptMessageBoxes.push(Ext.window.MessageBox.create({}));
                                askDuplicateFunctionOverwrite(item, me.promptMessageBoxes.length - 1, response.temporaryId);
                            });
                        }
                    }
                }
            } else {
                fileUploadField.markInvalid(Unidata.i18n.t('common:fileLoadError'));
            }
            view.setLoading(false);
        };
        uploadXhr.setRequestHeader('Authorization', Unidata.Config.getToken());
        uploadXhr.send(formData);
    },

    onFileUploadWindowClose: function () {
        this.getView().close();
    },

    stateColumnRenderer: function (value) {
        var replaceMap = {
            'NEW': '<span style="color:green;">' + Unidata.i18n.t('admin.cleanseFunction>new') + '</span>',
            'DUPLICATE': '<span style="color:orange;">' + Unidata.i18n.t('admin.cleanseFunction>duplicate') + '</span>',
            'OVERWRITE': '<span style="color:green;">' + Unidata.i18n.t('admin.cleanseFunction>owerwrite') + '</span>'
        };

        return replaceMap[value];
    }
});
