/**
 * Прокси для работы с классификатором
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.proxy.classifier.ClassifierNodeProxy', {

    extend: 'Ext.data.proxy.Rest',

    alias: 'proxy.un.classifiernode',

    api: {
        create: Unidata.Api.getClassifierNodeUrl(),
        read: Unidata.Api.getClassifierNodeUrl(),
        update: Unidata.Api.getClassifierNodeUrl(),
        destroy: Unidata.Api.getClassifierNodeUrl()
    },

    isExactNodeLoad: false, // ставить true, если используем proxy в model, false - если в store
    appendId: true,
    extraParams: {
        view: 'EMPTY'
    },

    buildUrl: function (request) {
        var classifierName,
            defaultUrl = this.getUrl(request),
            url        = defaultUrl,
            operation,
            params,
            reader,
            oldRootFn;

        if (request) {
            params    = request.getParams();
            operation = request.getOperation();
            reader    = this.getReader();

            // Пришлось вставить "велосипед" в связи с тем, что при операции сохранения
            // ExtJS пытается считать response для ноды из поля children
            // Надо поразбираться
            if (!(operation instanceof Ext.data.operation.Read)) {
                oldRootFn      = reader.getRoot;
                reader.getRoot = function (o) {
                    return o;
                };
            }

            classifierName = params.classifierName;
            url            = Ext.String.format('{0}/{1}/node', defaultUrl, classifierName);

            request.setUrl(url);
        }

        url = Ext.data.proxy.Rest.prototype.buildUrl.call(this, request);

        return url;
    },

    reader: {
        transform: function (value) {
            var content;

            if (!value.content) {
                return value;
            }

            content = value.content;
            //delete value.content;
            //Ext.apply(value, content);
            Unidata.proxy.classifier.ClassifierNodeProxy.makeObjectCorrect(content);

            return content;
        }
    },

    writer: {
        writeRecordId: false,
        writeAllFields: true,
        transform: {
            fn: function (data) {
                delete data.localVersion;
                // do some manipulation of the unserialized data object
                return data;
            },
            scope: this
        },
        allDataOptions: {
            persist: true,
            associated: true
        }
    },

    statics: {
        /**
         * Коррекция считанного конфигурационного объекта
         * Бекенд присылает пустой массив children независимо от того,
         * есть ли children в действительности (незагруженные).
         * Однако, ExtJs интерпретирует пустой массив, как отсутствие
         * children в принципе. А признаком незагруженных детей является
         * children = null. Бекенд сказал, что вычислять ему затруднительно
         * Поэтому приходится самим на этапе считывания, на основании childCount
         *
         * @param obj
         */
        makeObjectCorrect: function (obj) {
            var children   = obj.children,
                childCount = obj.childCount;

            if (Ext.isArray(children)) {
                if (children.length !== childCount) {
                    if (childCount !== 0) {
                        obj.children = null;
                    } else {
                        obj.children = [];
                    }
                } else {
                    obj.children.forEach(Unidata.proxy.classifier.ClassifierNodeProxy.makeObjectCorrect);
                }
            } else {
                obj.children = null;
            }
        }
    }
});
