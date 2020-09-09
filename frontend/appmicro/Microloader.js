window['Unidata'] = window['Unidata'] || {};

Unidata.Microloader = (function () {
    var Microloader = {
        isFunction: function (value) {
            return Boolean(value) && typeof value === 'function';
        },
        Array: {
            indexOf: function (array, item, from) {
                return Array.prototype.indexOf.call(array, item, from);
            },
            unique: function (array) {
                var clone = [],
                    i = 0,
                    ln = array.length,
                    item;

                for (; i < ln; i++) {
                    item = array[i];

                    if (Microloader.Array.indexOf(clone, item) === -1) {
                        clone.push(item);
                    }
                }

                return clone;
            },
            each: function (array, fn, scope, reverse) {
                var i,
                    ln = array.length;

                if (reverse !== true) {
                    for (i = 0; i < ln; i++) {
                        if (fn.call(scope || array[i], array[i], i, array) === false) {
                            return i;
                        }
                    }
                } else {
                    for (i = ln - 1; i > -1; i--) {
                        if (fn.call(scope || array[i], array[i], i, array) === false) {
                            return i;
                        }
                    }
                }

                return true;
            }
        },
        Object: {
            isEmpty: function (object) {
                var key;

                for (key in object) {
                    if (object.hasOwnProperty(key)) {
                        return false;
                    }
                }

                return true;
            }
        },
        String: {
            trim: function (string) {
                var trimRegex;

                // jscs:disable
                trimRegex = /^[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u2028\u2029\u202f\u205f\u3000]+|[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u2028\u2029\u202f\u205f\u3000]+$/g;
                // jscs:enable

                if (string) {
                    string = string.replace(trimRegex, '');
                }

                return string || '';
            }
        },
        chain: (function () {
            // см. https://jsfiddle.net/rcknr/fv289szj/
            function bind (f, g) {
                return function (a, callback, errback) {
                    f(a, function (result) {
                        return g(result, callback, errback);
                    }, errback);
                };
            }

            function chain () {
                var args = Array.prototype.slice.call(arguments),
                    f = args.shift();

                while (args.length > 0) {
                    f = bind(f, args.shift());
                }

                return f;
            }

            return chain;
        }()),
        loadJson: function (url, successCallback, errorCallback) {
            aja()
                .url(url)
                .cache(false)
                .type('html')
                .on('success', function (jsonData) {
                    successCallback(jsonData);
                })
                .on('error', function () {
                    errorCallback();
                })
                .on('40x', function () {
                    errorCallback();
                })
                .on('50x', function () {
                    errorCallback();
                })
                .go();
        },
        loadScript: function (url, successCallback, errorCallback) {
            aja()
                .url(url)
                .cache(false)
                .type('script')
                .on('success', function (data) {
                    successCallback(data);
                })
                .on('error', function () {
                    errorCallback();
                })
                .on('40x', function () {
                    errorCallback();
                })
                .on('50x', function () {
                    errorCallback();
                })
                .go();
        },
        runMicroloader: function () {
            Unidata.Microloader.chain(
                Unidata.Microloader.loadInternationalizationLib,
                Unidata.Microloader.initUnidataInternationalizationClass,
                Unidata.Microloader.loadInternationalizationJson,
                Unidata.Microloader.fetchPlatformConfig,
                Unidata.Microloader.bootstrapExtJsApplication
            )();
        },
        loadCustomerJson: function (data, nextCallback) {
            var url;

            if (!Microloader.Object.isEmpty(window.customerConfig)) {
                if (Microloader.isFunction(nextCallback)) {
                    nextCallback();
                }

                return;
            }

            url = Unidata.Microloader.buildCustomerUrl('customer.json');

            Microloader.loadJson(url,
                function (jsonData) {
                    try {
                        window.customerConfig = eval('(' + jsonData + ')');
                    } catch (e) {
                        console.log('Unidata microloader: can not parse customer.json');

                        return;
                    }

                    window.customerConfig.LOCALE = window.customerConfig.LOCALE ? window.customerConfig.LOCALE : 'ru';

                    if (Microloader.isFunction(nextCallback)) {
                        nextCallback();
                    }
                },
                function () {
                    console.log('Error: customer.json load failure');
                    alert('customer.json load failure');

                    return;
                });
        },
        initUnidataCustomerCustomization: function (data, nextCallback) {
            var match = window.location.search.match(/(\?|&)action\=([^&]*)/),
                customerCfg = window.customerConfig,
                serverUrl = customerCfg.serverUrl,
                logoutUrl = serverUrl + 'internal/authentication/logout',
                token = localStorage.getItem('ud-token');

            // выход из системы при наличии GET-параметра ?action=logout
            if (match && match[2] === 'logout') {
                aja()
                    .url(logoutUrl)
                    .method('POST')
                    .header('Authorization', token)
                    .header('Content-Type', 'application/json')
                    .cache(false)
                    .on('success', function () {
                        document.write('Произошел выход из системы');
                    })
                    .on('error', function () {
                        document.write('Выход из системы не произведен из-за ошибки');
                    })
                    .go();
            } else {
                if (Microloader.isFunction(nextCallback)) {
                    nextCallback();
                }
            }
        },
        loadInternationalizationLib: function (data, nextCallback) {
            var url = 'vendors/i18next.min.js';

            Microloader.loadScript(url,
                function () {
                    console.log('Unidata microloader: i18next loaded');

                    i18next
                        .init({
                            fallbackLng: 'ru',
                            ns: ['common', 'default', 'glossary', 'validation'],
                            defaultNS: 'default',
                            initImmediate: true,
                            debug: true,
                            lng: window.customerConfig.LOCALE,
                            keySeparator: '>',
                            interpolation: {
                                format: function (value, format, lng) {
                                    if (format === 'uppercase') return value.toUpperCase();
                                    if (format === 'lowercase') return value.toLowerCase();
                                    return value;
                                }
                            }
                        }, function (err) {
                            if (err) {
                                console.log('Unidata microloader: i18next something went wrong', err);

                                return;
                            }
                        });

                    if (Microloader.isFunction(nextCallback)) {
                        nextCallback();
                    }
                },
                function () {
                });
        },
        initUnidataInternationalizationClass: function (data, nextCallback) {
            Unidata.i18n = window.i18next;

            if (Microloader.isFunction(nextCallback)) {
                nextCallback();
            }
        },
        loadInternationalizationJson: function (data, nextCallback) {
            var itemCount = 0,
                loadedCount = 0,
                notLoadedCount = 0,
                languages = [],
                namespaces = ['common', 'default', 'glossary', 'validation'],
                defaultLng = 'ru',
                locale;

            locale = window.customerConfig.LOCALE;

            languages.push(defaultLng);
            languages.push(locale);

            languages = Microloader.Array.unique(languages);
            namespaces = Microloader.Array.unique(namespaces);

            itemCount = languages.length * namespaces.length;

            Microloader.Array.each(languages, function (language) {
                Microloader.Array.each(namespaces, function (namespace) {
                    Microloader.loadJson('resources/locale/' + language + '/' + language + '-' + namespace + '.json',
                        function (data) {
                            loadedCount += 1;

                            console.log('Unidata microloader: i18next json loaded', language, namespace);

                            try {
                                data = JSON.parse(data);

                                data = Unidata.Micrologin.transformResourceBundle(language, namespace, data);

                                i18next.addResourceBundle(language, namespace, data);
                            } catch (e) {
                                console.log('Unidata microloader: can not parse i18next json', language, namespace);
                            }

                            if (loadedCount === itemCount) {
                                if (Microloader.isFunction(nextCallback)) {
                                    nextCallback();
                                }
                            } else if (loadedCount + notLoadedCount === itemCount) {
                                console.log('Unidata microloader: can not load i18n json');

                                return;
                            }
                        },
                        function () {
                            notLoadedCount += 1;
                        }
                    );
                });
            });
        },

        /**
         * Создаем platformConfig для хранения данных с backend
         * И получаем данные периодов актуальности,
         */
        fetchPlatformConfig: function (data, nextCallback) {
            var backendPropertyGroup = 'unidata.properties.group.validity',
                validityStart = 'unidata.validity.period.start',
                validityEnd = 'unidata.validity.period.end',
                url = Unidata.Microloader.buildServerUrl('internal/configuration/' + backendPropertyGroup),
                token = localStorage.getItem('ud-token');

            $.ajax({
                url: url,
                method: 'GET',
                data: {
                    '_dc': Number(new Date())
                },
                headers: {
                    'Authorization': token
                },
                success: function (data, textStatus, xhr) {
                    var validityDatesRecords = data,
                        start,
                        end;

                    if (xhr && xhr.status === 200 && validityDatesRecords) {
                        Microloader.Array.each(validityDatesRecords, function (date) {
                            if (date.name === validityStart) {
                                start = date.value;

                                return false;
                            };
                        });

                        Microloader.Array.each(validityDatesRecords, function (date) {
                            if (date.name === validityEnd) {
                                end = date.value;

                                return false;
                            };
                        });

                        window.platformConfig = {
                            'VALIDITY_DATES': {}
                        };
                        window.platformConfig['VALIDITY_DATES']['START'] = start;
                        window.platformConfig['VALIDITY_DATES']['END'] = end;
                    }

                    if (Microloader.isFunction(nextCallback)) {
                        nextCallback();
                    }
                }, failure: function (err) {
                    if (err) {
                        console.log('Unidata microloader: error while trying to fetch platform config', err);
                    }
                }
            });

        },

        bootstrapExtJsApplication: function () {
            var script = document.createElement('script'),
                element = document.querySelectorAll('body')[0],
                locale = window.customerConfig.LOCALE;

            window['Ext'] = window['Ext'] || {};

            // только для production сборки
            if (locale && window['unidataBuildDate'] !== '@BUILD_DATE@') {
                window['Ext'].manifest = 'app-' + locale + '.json';
            }

            script.type = 'text/javascript';
            script.src = 'bootstrap.js?_dc=' + Number(new Date());

            // is IE?
            if (element.appendChild) {
                element.appendChild(script);
            } else {
                element.append(script);
            }
        },
        buildServerUrl: function (url) {
            var customerCfg = window.customerConfig,
                serverUrl = '';

            if (customerCfg) {
                serverUrl = customerCfg.serverUrl;
            }

            return serverUrl +  url;
        },
        buildCustomerUrl: function (url) {
            var customerUrl = Unidata.Microloader.String.trim(String(window.customerUrl));

            customerUrl = customerUrl.replace(/\/+$/, '').replace(/\\+$/, '');

            if (customerUrl.length) {
                customerUrl += '/';
            }

            return customerUrl + url;
        }
    };

    return Microloader;
})();
