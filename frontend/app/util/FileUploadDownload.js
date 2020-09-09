Ext.define('Unidata.util.FileUploadDownload', {
    mixins: ['Ext.mixin.Observable'],

    config: {
        inputName: 'file'
    },

    constructor: function (config) {
        this.mixins.observable.constructor.call(this, config);
    },

    buildFormDataItems: function (file, fileData, params, isJson) {
        var name, data, formDataItems = [];

        // add formData for file
        formDataItems.push({
            headers: this.buildFileHeaders(file.name, file.type),
            data: fileData
        });

        for (name in params) {
            if (params.hasOwnProperty(name)) {
                data = params[name];

                formDataItems.push({
                    headers: isJson ? this.buildHeaders(name, 'application/json') : this.buildHeaders(name),
                    data: this.str2arr(data.toString())
                });
            }
        }

        return formDataItems;
    },

    //TODO: refactoring, move to a special lib
    appendBuffer: function (buffer1, buffer2) {
        var tmp = new Uint8Array(buffer1.byteLength + buffer2.byteLength);

        tmp.set(new Uint8Array(buffer1), 0);
        tmp.set(new Uint8Array(buffer2), buffer1.byteLength);

        return tmp.buffer;
    },

    //TODO: refactoring, move to a special lib
    str2arr: function (str) {
        var uint = new Uint8Array(str.length),
            i,
            j;

        for (i = 0, j = str.length; i < j; ++i) {
            uint[i] = str.charCodeAt(i);
        }

        return uint;
    },

    buildFileHeaders: function (filename, contentType) {
        if (contentType == '') {
            contentType = this.getContentTypeByFileName(filename);
        }

        return [
            'Content-Disposition: ' + this.buildContentDispositionHeaders(this.getInputName(), filename).join('; '),
            'Content-Type:' + contentType
        ];
    },

    /**
     * Подбираем Content-Type по расширению
     * @param filename
     * @returns {String}
     */
    getContentTypeByFileName: function (filename) {
        var extension = filename.split('.').pop();

        switch (extension) {
            case 'xlsx':
                return 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
        }

        return '';
    },

    buildHeaders: function (name, contentType) {
        var headers = [
            'Content-Disposition: ' + this.buildContentDispositionHeaders(name).join('; ')
        ];

        if (contentType !== undefined) {
            headers.push('Content-Type:' + contentType);
        }

        return headers;
    },

    buildContentDispositionHeaders: function (name, filename) {
        var headers = ['form-data',
            'name="' + name + '"'];

        if (filename !== undefined) {
            headers.push('filename*=UTF-8\'\'' + encodeURI(filename));
        }

        return headers;
    },

    buildPayload: function (boundary, formDataItems) {
        var boundaryMiddle, boundaryLast, footerBytes, buffer, NEXT_LINE, me = this;

        NEXT_LINE = '\r\n';
        buffer = new Uint8Array(0);
        boundaryMiddle = '--' + boundary + NEXT_LINE;
        boundaryLast = '--' + boundary + '--' + NEXT_LINE;

        formDataItems.forEach(function (formDataItem) {
            var headersStr, headersBytes, dataBytes;

            headersStr = NEXT_LINE + boundaryMiddle + formDataItem.headers.join(NEXT_LINE) + NEXT_LINE + NEXT_LINE;
            headersBytes = me.str2arr(headersStr);
            dataBytes = new Uint8Array(formDataItem.data);
            buffer = me.appendBuffer(buffer, me.appendBuffer(headersBytes, dataBytes));
        });

        footerBytes = this.str2arr(NEXT_LINE + boundaryLast);

        return this.appendBuffer(buffer, footerBytes);
    },

    uploadFiles: function (fileUploadField, url, params, isJson) {
        var files,
            me,
            reader;

        files = fileUploadField.fileInputEl.dom.files;
        me = this;
        params = params || [];

        //TODO: check extension (see FileUploadDownloadField.js)

        function sendFile (formDataItems) {
            var CONTENT_TYPE,
                boundary,
                payload,
                xhr,
                errors = null;

            CONTENT_TYPE = 'multipart/form-data; charset=utf-8; boundary=';
            boundary = String(Math.random()).slice(2);

            payload = me.buildPayload(boundary, formDataItems);

            xhr = new XMLHttpRequest();

            xhr.open('POST', url, true);
            xhr.onload = function () {
                var response = Ext.decode(xhr.response, true),
                    responseText = xhr.responseText,
                    error,
                    msg;

                if (xhr.status === 200 || xhr.status === 202) {
                    if (!response['success']) {
                        errors = Unidata.util.ErrorMessageFactory.getErrorUserMessagesFromResponse(response);

                        // хак для корректной работы метода Ext.data.Connection.onFailure
                        response.request = {options: {url: xhr.responseURL}};
                        response.status = xhr.status;
                        Ext.create('Ext.data.Connection').onFailure(response);
                    }
                    me.fireEvent('uploadsuccess', response['success'], errors, response['content']);
                } else {
                    if (xhr.status === 500) {
                        msg = Unidata.i18n.t('util>internalError');

                        if (response && response['errors'] && response['errors'].length) {
                            error = response['errors'][0];
                            error.stackTrace = response['stackTrace'];
                            error.severity = 'CRITICAL';
                            msg = error.userMessage || msg;
                        } else {
                            error = {};
                            error.userMessageDetails = responseText;
                            error.severity = 'CRITICAL';
                            msg = Unidata.i18n.t('util>error.unknownServerError');
                        }

                        Unidata.showError(msg, false, error);
                    }

                    me.fireEvent('uploaderror');
                }
                me.fireEvent('uploadfinish');
            };
            //TODO: refactoring, auth
            xhr.setRequestHeader('Authorization', Unidata.Config.getToken());
            xhr.setRequestHeader('Content-type', CONTENT_TYPE + boundary);
            xhr.send(payload);
        }

        this.fireEvent('uploadstart');

        reader = new FileReader();

        reader.addEventListener('loadend', function () {
            sendFile(me.buildFormDataItems(files[0], reader.result, params, isJson));
        });
        reader.addEventListener('onerror', function () {
            me.fireEvent('uploaderror');
        });
        reader.readAsArrayBuffer(files[0]);
    },

    statics: {
        createLink: function (url) {
            return url + (url.indexOf('?') === -1 ? '?' : '&') + 'token=' + Unidata.Config.getToken();
        }
    }
});
