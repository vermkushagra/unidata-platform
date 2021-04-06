/**
 * Класс реализующий представление записи
 *
 * @author Ivan Marshalkin
 * @date 2016-02-18
 */

Ext.define('Unidata.view.steward.dataentity.DataEntity', {
    extend: 'Ext.container.Container',

    alias: 'widget.dataentity',

    requires: [
        'Unidata.view.steward.dataentity.simple.AttributeTablet',
        'Unidata.view.steward.dataentity.simple.ClassifierAttributeTablet',
        'Unidata.view.steward.dataentity.complex.flat.FlatAttributeTablet',
        'Unidata.view.steward.dataentity.complex.carousel.CarouselAttributeTablet',
        'Unidata.view.steward.dataentity.util.ComplexAttribute',
        'Unidata.view.steward.dataentity.layout.AutoLayout',
        'Unidata.util.MetaRecord'
    ],

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    config: {
        attributeViewMode: null,
        metaRecord: null,
        dataRecord: null,
        classifierNodes: null,
        readOnly: null,
        inconsistentAttributePaths: null,
        hiddenAttribute: null,                     // признак необходимости отображать скрытые атрибуты (true - отображать / false - скрывать)
        preventMarkField: null,                    // предупреждает макркирование поля с текстом ошибки смотри документацию Ext.form.Labelable.preventMark
        depth: 0,
        hideAttributeTitle: null,
        noWrapTitle: false,
        classifierHidden: false
    },

    etalonId: null, // хринит etalonId записи для нужд QA отдела

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-dataentity',

    attributeContainers: null,                     // кэш с атрибутами
    callbackEventsMap: null,                       // кэш событий для корректной очистки эвентов при работе с executeByAttributePath

    useCarousel: false,                            // признак использования карусели
    useAttributeGroup: false,                      // признак использования группировки атрибутов
    showEmptyClassifierAttributeTablet: false,     // признак необходимости отображения классификации без атрибутов
    showClassifierAttributeGroup: false,           // признак того что необходимо отображать группы от классификаторов даже если флаг useAttributeGroup == false

    simpleContainers: null,                        // массив контейнеров с простыми атрибутами
    complexContainers: null,                       // массив контейнеров с комплексными атрибутами
    classifierContainers: null,                    // массив контейнеров с простыми атрибутами пришедших из классификаторов

    initComponent: function () {
        this.callParent(arguments);

        this.clearAttributeContainerLink();

        this.callbackEventsMap = {};

        // кэш с атрибутами
        this.attributeContainers = new Ext.util.Collection();
    },

    /**
     * Заполняем кэш с атрибутами
     *
     * @param components
     */
    componentsAdded: function (components) {
        var dataRecord = this.getDataRecord();

        Ext.Array.each(components, function (cmp) {
            var attributePath;

            if (cmp.getAttributePath) {
                attributePath = cmp.getAttributePath();

                this.attributeContainers.add(cmp);

                // оповещаем о том, что добавился атрибут по attributePath
                this.fireEvent('attributeadded', cmp);
                this.fireEvent(attributePath, cmp);

                // при удалении атрибута - убираем из кэша
                cmp.on('destroy', function () {
                    this.attributeContainers.remove(cmp);
                }, this);

                cmp.on('externalinputchange', function (dataMapping) {
                    var ded = this;

                    Ext.Object.each(dataMapping, function (attributePath, attributeValue) {
                        var dataAttribute;

                        // необходимо заполнять dataAttribute сразу, т.к. могут сохранять запись, не отрендерив контейнер
                        dataAttribute = Unidata.util.UPathData.findFirstAttributeByPath(dataRecord, attributePath);
                        dataAttribute.set('value', attributeValue);

                        ded.executeByAttributePath(function (attributeContainer, attributeValue) {
                            attributeContainer.setValue(attributeValue, true);
                        }, attributePath, [attributeValue]);
                    });
                }, this);
            }
        }, this);

        // Переключаем viewMode, если установлен
        this.executettributeViewMode();
    },

    updateAttributeViewMode: function (attributeViewMode) {
        if (!this.rendered || !attributeViewMode) {
            return;
        }

        this.executettributeViewMode();
    },

    /**
     * Применяет установленный режим просмотра атрибутов, если установлен
     */
    executettributeViewMode: function () {
        var attributeViewMode = this.getAttributeViewMode();

        if (!attributeViewMode) {
            return;
        }

        this.executeByAttributePath(function (attributeContainer, attributeViewMode) {
            if (attributeContainer.setViewMode) {
                attributeContainer.setViewMode(attributeViewMode);
            }
        }, null, [attributeViewMode]);
    },

    /**
     * Возвращает массив attributeContainers, по attributePath
     *
     * @param [attributePath] - если не задан, то возвращает всё, что закэшировано
     * @returns {Array}
     */
    getAttributeContainersFromCache: function (attributePath) {
        var attributeContainers = [];

        if (!attributePath) {
            return this.attributeContainers.getRange();
        }

        this.attributeContainers.each(function (attributeContainer) {
            if (attributeContainer.getAttributePath() === attributePath) {
                attributeContainers.push(attributeContainer);
            }
        });

        return attributeContainers;
    },

    /**
     * Выполняет метод callback для attributeContainer с attributePath, когда он готов к работе
     *
     * @param callback
     * @param [attributePath] - если не указан, то будет вызвано для всех атрибутов
     * @param {Array} [args]
     * @param [scope]
     * @returns {Function}
     */
    executeByAttributePath: function (callback, attributePath, args, scope) {
        var attributeContainersCache = this.getAttributeContainersFromCache(attributePath),
            eventName = attributePath || 'attributeadded',
            scope = scope || this,
            args = args || [],
            i = 0,
            ln = attributeContainersCache.length,
            callbackWithArgs = Ext.bind(callback, scope, args, true);

        for (; i < ln; i++) {
            callback.apply(scope, [attributeContainersCache[i]].concat(args));
        }

        this.cleanupExecuteStack(callback, eventName);

        if (!this.callbackEventsMap[eventName]) {
            this.callbackEventsMap[eventName] = [];
        }

        this.callbackEventsMap[eventName].push([callback, callbackWithArgs]);

        this.on(eventName, callbackWithArgs);

        return callbackWithArgs;
    },

    /**
     * Очистка лишних колбэков при выполнении executeByAttributePath
     *
     * @param callback
     * @param eventName
     * @private
     */
    cleanupExecuteStack: function (callback, eventName) {
        var eventMap = this.callbackEventsMap[eventName];

        if (eventMap && eventMap.length) {
            Ext.Array.each(eventMap, function (eventMapItem, index, array) {
                if (eventMapItem[0] == callback) {
                    this.un(eventName, eventMapItem[1]);
                    array[index] = null;
                }
            }, this);

            this.callbackEventsMap[eventName] = Ext.Array.clean(eventMap);
        }
    },

    /**
     * Очистка всех колбэков, которые были добавлены через executeByAttributePath
     */
    clearExecuteEvents: function () {
        Ext.Object.each(this.callbackEventsMap, function (eventName, eventMap) {
            Ext.Array.each(eventMap, function (eventMapItem) {
                this.un(eventName, eventMapItem[1]);
            }, this);
        }, this);

        this.callbackEventsMap = {};
    },

    onDestroy: function () {
        this.clearAttributeContainerLink();
        this.clearEntityData();

        this.clearExecuteEvents();
        delete this.callbackEventsMap;

        this.attributeContainers.clear();
        delete this.attributeContainers;

        this.callParent(arguments);
    },

    clearAttributeContainerLink: function () {
        this.simpleContainers     = [];
        this.complexContainers    = [];
        this.classifierContainers = [];
    },

    setEntityData: function (metaRecord, dataRecord, classifierNodes) {
        this.setMetaRecord(metaRecord);
        this.setDataRecord(dataRecord);
        this.setClassifierNodes(classifierNodes);
    },

    clearEntityData: function () {
        var metaRecord      = null,
            dataRecord      = null,
            classifierNodes = null;

        this.setEntityData(metaRecord, dataRecord, classifierNodes);
    },

    /**
     * Удаляет все визуальные объекты из dataEntity
     */
    clearDataEntity: function () {
        this.removeAll();
        this.clearExecuteEvents();
    },

    /**
     * Точка входа для построения содержимого компонента
     */
    displayDataEntity: function () {
        var me         = this,
            metaRecord = me.getMetaRecord(),
            dataRecord = me.getDataRecord();

        // удаляем все вложенные элементы перед пересозданием
        me.clearDataEntity();

        // чистим сохраненные ссылки
        me.clearAttributeContainerLink();

        if (metaRecord && dataRecord) {
            //таблетки простых атрибутов пришедших с классификаторов
            me.buildClassifierSimpleAttributeTablet()
                .then(
                    function (containers) {
                        me.classifierContainers = Ext.Array.merge(me.classifierContainers, containers);

                        // таблетки простых атрубутов
                        containers = me.buildSimpleAttributeTablet();
                        me.simpleContainers = Ext.Array.merge(me.simpleContainers, containers);

                        // компануем размещение элементов
                        containers = Ext.Array.merge(me.classifierContainers, me.simpleContainers);
                        containers = me.layoutSimpleAttributeGroup(containers);

                        me.add(containers);

                        if (Unidata.util.MetaRecord.isEntity(metaRecord)) {
                            // сортируем complexAttributes по полю order в метамодели
                            Unidata.util.MetaRecord.localSortComplexAttributeByOder(metaRecord, 'ASC');

                            // таблетки комплексных атрибутов
                            containers = me.buildComplexAttributeTablet();
                            me.complexContainers = Ext.Array.merge(me.complexContainers, containers);

                            me.add(containers);
                        }

                        // подсвечиваем индикаторы
                        me.showDqErrorsIndicator();
                    },
                    function () {
                        //TODO: что-то пошло не так
                    }
                )
                .done();

        }
    },

    /**
     * Производит группировку атрибутов согласно настройкам метамодели
     *
     * @param metaRecord
     * @returns {Array}
     */
    buildGroupSimpleAttributeGroup: function (metaRecord) {
        var groups = [],
            metaAttributeGroups;

        metaAttributeGroups = metaRecord.attributeGroups();

        // формируем группы
        metaAttributeGroups.each(function (group) {
            var group = group.getData();

            group.groupType = 'METAMODEL';

            groups.push(group);
        });

        // неизвестная группа
        groups = this.buildUnknownGroupSimpleAttributeGroup(metaRecord, groups);

        // финальная зачистка групп
        groups = this.cleanGroupSimpleAttributeGroup(groups);

        groups = Ext.Array.sort(groups, function (a, b) {
            var result = 0;

            if (a.row < b.row) {
                result = -1;
            } else if (a.row > b.row) {
                result = 1;
            }

            return result;
        });

        return groups;
    },

    /**
     * Удаляем пустые группы (нет атрибутов для отображения)
     *
     * @param groups
     * @returns {Array}
     */
    cleanGroupSimpleAttributeGroup: function (groups) {
        var copy = [];

        Ext.Array.each(groups, function (group) {
            if (group.attributes.length) {
                copy.push(group);
            }
        });

        return copy;
    },

    /**
     * Производит построение группы для атрибутов без группы
     *
     * @param groups
     * @returns {*}
     */
    buildUnknownGroupSimpleAttributeGroup: function (metaRecord, groups) {
        var list              = [],
            unknownGroupTitle = Unidata.i18n.t('dataentity>unknownGroupAttribute'),
            unknownGroupAttributePaths,
            rows,
            unknownGroup;

        // если групп нет, то нет необходимости выводить заголовок для единственной группы
        if (!groups.length) {
            unknownGroupTitle = '';
        }

        rows = Ext.Array.pluck(groups, 'row');
        rows = Ext.Array.unique(rows);

        unknownGroupAttributePaths = Ext.Array.merge(
            Unidata.util.UPathMeta.buildSimpleAttributePaths(metaRecord),
            Unidata.util.UPathMeta.buildArrayAttributePaths(metaRecord)
        );

        // сортируем в порядке определения в метамодели
        unknownGroupAttributePaths = this.sortUnknownGroupAttributes(unknownGroupAttributePaths);

        // все что не вошло в группы
        unknownGroup = {
            row: Ext.Array.max(rows) + 1,
            column: this.getUnknownGroupColumnNo(groups),
            groupType: 'METAMODELUNGROUP',
            title: unknownGroupTitle,
            attributes: unknownGroupAttributePaths
        };

        groups.push(unknownGroup);

        // обходим все группы чтоб выкинуть повторы на всякий случай :)
        Ext.Array.each(groups, function (group) {
            // вычитаем элементы которые были заюзаны в предыдущих группах
            group.attributes = Ext.Array.difference(group.attributes, list);

            // расшираяем список атрибутов из предыдущих групп
            list = Ext.Array.merge(list, group.attributes);
        });

        return groups;
    },

    /**
     * Сортирует "атрибуты без группы" в порядке определенным на метамодели
     */
    sortUnknownGroupAttributes: function (attributePaths) {
        var metaRecord = this.getMetaRecord(),
            sorted = [],
            resultAttributePaths;

        // строим массив индексо атрибутов
        Ext.Array.each(attributePaths, function (attributePath) {
            var attribuite = Unidata.util.UPathMeta.findAttributeByName(metaRecord, attributePath);

            sorted.push({
                index: attribuite.get('order'),
                attribuite: attribuite,
                attributePath: attributePath
            });
        });

        // сортируем по порядку определения в модели
        sorted.sort(function (a, b) {
            return a['index'] - b['index'];
        });

        resultAttributePaths = Ext.Array.pluck(sorted, 'attributePath');

        return resultAttributePaths;
    },

    /**
     * TODO: учитывать, что в первую колонку размещает классификаторы
     * Возвращает номер колонки в которой будет распологаться группа unknown
     * Колонка определяется исходя из минимального количества атрибутов
     *
     * @param groups
     */
    getUnknownGroupColumnNo: function (groups) {
        var no     = 0,
            counts = {},
            min    = null;

        // подсчитываем количество атрибутов расположенных в колонках
        Ext.Array.each(groups, function (group) {
            var column;

            column = group.column;

            if (!counts[column]) {
                counts[column] = 0;
            }

            counts[column] += group.attributes.length;
        });

        // находим индекс в котором должна располагаться группа
        Ext.Object.each(counts, function (key, value) {
            if (min > value || min === null) {
                min = value;
                no  = Number(key);
            }
        });

        return no;
    },

    /**
     * Обрабатывает таблетки простых атрибутов, если необходимо оборачивает в панельки согласно настройкам
     * групп атрибутов в модели реестра/справочника
     *
     * @returns {Unidata.view.steward.dataentity.simple.AttributeTablet[]}
     */
    buildSimpleAttributeTablet: function () {
        var containers;

        if (this.useAttributeGroup) {
            containers = this.buildGroupSimpleAttributeTablet();
        } else {
            containers = this.buildUngroupSimpleAttributeTablet();
        }

        return containers;
    },

    /**
     * Возвращает промис с контейнерами таблеток с простыми атрибутами пришедших из классификаторов
     *
     * @returns {Ext.promise.Promise}
     */
    buildClassifierSimpleAttributeTablet: function () {
        var classifierHidden  = this.getClassifierHidden(),
            readOnly         = this.getReadOnly(),
            metaRecord       = this.getMetaRecord(),
            dataRecord       = this.getDataRecord(),
            classifierNodes  = this.getClassifierNodes() || [],
            hiddenAttribute  = this.hiddenAttribute,
            preventMarkField = this.getPreventMarkField(),
            promises = [],
            title,
            promisedClassifierStore,
            classifiersList;

        classifiersList = metaRecord.get('classifiers');

        if (Ext.isArray(classifiersList)) {

            if (classifiersList.length) {
                // перезагружаем стор с классификаторами
                promisedClassifierStore = Unidata.util.api.Classifier.loadClassifiersStore(true);
            }

            Ext.Array.each(classifiersList, function (classifierName) {
                var deferred = Ext.create('Ext.Deferred'),
                    ClassifierDataRecordUtil = Unidata.util.ClassifierDataRecord,
                    container,
                    dataClassifierNode,
                    metaClassifierNode;

                dataClassifierNode = ClassifierDataRecordUtil.getFirstClassifierNodeByClassifierName(dataRecord, classifierName);  // jscs:ignore maximumLineLength

                metaClassifierNode = Ext.Array.findBy(classifierNodes, function (item) {
                    return item.get('classifierName') === classifierName;
                });

                // пропускаем классификацию без атрибутов, если не установлен флаг отображения пустой классификации
                // if (!me.showEmptyClassifierAttributeTablet) {
                //     if (!metaClassifierNode || !metaClassifierNode.nodeAttrs() || !metaClassifierNode.nodeAttrs().getCount()) {
                //         return;
                //     }
                // }

                promises.push(deferred.promise);

                // нужен классификатор для отображения displayName
                promisedClassifierStore
                    .then(function () {
                        return Unidata.util.api.Classifier.getClassifier(classifierName);
                    })
                    .then(
                        function (classifier) {
                            var classifierName = classifier.get('name'),
                                rootId = classifierName + '.root',
                                headerTooltip = null,
                                titlePostfix;

                            title = classifier.get('displayName');

                            if (metaClassifierNode) {
                                titlePostfix = metaClassifierNode.get('text');

                                if (metaClassifierNode.get('parentId') !== rootId) {
                                    titlePostfix = '../' + titlePostfix;
                                }
                                title += ': ' + titlePostfix;
                            }

                            container = Ext.create('Unidata.view.steward.dataentity.simple.ClassifierAttributeTablet', {
                                metaRecord: metaRecord,
                                dataRecord: dataRecord,
                                classifier: classifier,
                                metaClassifierNode: metaClassifierNode,
                                dataClassifierNode: dataClassifierNode,
                                attributeGroup: {
                                    row: 1,
                                    column: 0, // закидываем в первую колонку
                                    title: title,
                                    groupType: 'CLASSIFIER',
                                    attributes: null,
                                    headerTooltip: headerTooltip
                                },
                                readOnly: readOnly,
                                hidden: classifierHidden,
                                hiddenAttribute: hiddenAttribute,
                                preventMarkField: preventMarkField
                            });

                            deferred.resolve(container);
                        }
                    )
                    .otherwise(function (errorMsg) {
                        deferred.reject();
                        errorMsg = errorMsg || Unidata.i18n.t('dataentity>classifierLoadError');
                        Unidata.showError(errorMsg);
                    })
                    .done();
            });
        }

        // возвращаем промис с контейнерами
        return Ext.Deferred.all(promises);
    },

    /**
     * Возвращает таблетки простых атрибутов с учетом групп
     *
     * @returns {Unidata.view.steward.dataentity.simple.AttributeTablet[]}
     */
    buildGroupSimpleAttributeTablet: function () {
        var readOnly         = this.getReadOnly(),
            metaRecord       = this.getMetaRecord(),
            dataRecord       = this.getDataRecord(),
            containers       = [],
            hiddenAttribute  = this.hiddenAttribute,
            preventMarkField = this.getPreventMarkField(),
            hideAttributeTitle = this.getHideAttributeTitle(),
            noWrapTitle = this.getNoWrapTitle(),
            attributeGroups;

        // строим группы
        attributeGroups = this.buildGroupSimpleAttributeGroup(metaRecord);

        // создаем таблетки согласно построенным группам
        Ext.Array.each(attributeGroups, function (group) {
            var container,
                cfg;

            cfg = {
                metaRecord: metaRecord,
                dataRecord: dataRecord,
                metaNested: metaRecord,
                dataNested: dataRecord,
                attributeGroup: group,
                readOnly: readOnly,
                hiddenAttribute: hiddenAttribute,
                preventMarkField: preventMarkField,
                noWrapTitle: noWrapTitle
            };

            if (hideAttributeTitle !== null) {
                cfg = Ext.apply(cfg, {
                    hideAttributeTitle: hideAttributeTitle
                });
            }

            container = Ext.create('Unidata.view.steward.dataentity.simple.AttributeTablet', cfg);

            containers.push(container);
        });

        return containers;
    },

    /**
     * Возвращает таблетки простых атрибутов БЕЗ учета групп
     *
     * @returns {Unidata.view.steward.dataentity.simple.AttributeTablet|*}
     */
    buildUngroupSimpleAttributeTablet: function () {
        var readOnly         = this.getReadOnly(),
            metaRecord       = this.getMetaRecord(),
            dataRecord       = this.getDataRecord(),
            hiddenAttribute  = this.hiddenAttribute,
            preventMarkField = this.getPreventMarkField(),
            hideAttributeTitle = this.getHideAttributeTitle(),
            noWrapTitle     = this.getNoWrapTitle(),
            container;

        container = Ext.create('Unidata.view.steward.dataentity.simple.AttributeTablet', {
            metaRecord: metaRecord,
            dataRecord: dataRecord,
            metaNested: metaRecord,
            dataNested: dataRecord,
            attributeGroup: {
                row: 1,
                column: 0, // закидываем в последнюю колонку
                title: '',
                attributes: null
            },
            readOnly: readOnly,
            hiddenAttribute: hiddenAttribute,
            preventMarkField: preventMarkField,
            hideAttributeTitle: hideAttributeTitle,
            noWrapTitle: noWrapTitle
        });

        return container;
    },

    /**
     * Размещает таблетки с группами простых атрибутов и возвращает контейнеры для отображения
     *
     * @param {Unidata.view.steward.dataentity.simple.AttributeTablet[]} simpleAttributeTablets
     * @returns {*}
     */
    layoutSimpleAttributeGroup: function (simpleAttributeTablets) {
        var containers,
            layout,
            layoutCfg,
            layoutType;

        layoutCfg = {
            tablets: simpleAttributeTablets
        };

        // создаем карточку для группы только когда явно указано
        // иначе не нужно оборачивать - элемент должен выглядеть как inline
        if (this.useAttributeGroup) {
            // оставлено пустым мало ли что :)

            this.useAttributeGroup = this.useAttributeGroup;
        } else {
            layoutCfg.layoutType = 'NONE';

            if (this.showClassifierAttributeGroup) {
                layoutCfg.layoutType = 'ORIGIN';
            }
        }

        layout = Ext.create('Unidata.view.steward.dataentity.layout.AutoLayout', layoutCfg);

        containers = layout.getContainers();
        layout.destroy();

        return containers;
    },

    /**
     * Возвращает таблетки комплексных атрибутов
     *
     * @returns {Array}
     */
    buildComplexAttributeTablet: function () {
        var me               = this,
            containers       = [],
            readOnly         = this.getReadOnly(),
            metaRecord       = this.getMetaRecord(),
            dataRecord       = this.getDataRecord(),
            depth            = this.getDepth(),
            hiddenAttribute  = this.hiddenAttribute,
            preventMarkField = this.getPreventMarkField(),
            tabletClass;

        // создаем таблетки для всех комплексных атрибутов из модели
        metaRecord.complexAttributes().each(function (metaComplexAttribute) {
            var ComponentTypes = Unidata.module.ComponentState.componentTypes,
                metaComplexAttributeName = metaComplexAttribute.get('name'),
                dataAttribute,
                container,
                stateableCfg,
                cfg,
                stateComponentKey,
                entityName,
                complexAttributeName;

            // создаем комплексный атрибут, если не существует
            Unidata.view.steward.dataentity.util.ComplexAttribute.createDataComplexAttributeIfNotExist(
                dataRecord,
                metaComplexAttributeName);

            // находим комплексный атрибут по имени. 100% существует, т.к. его создали выше
            dataAttribute = Unidata.view.steward.dataentity.util.ComplexAttribute.findDataComplexAttributeByName(
                dataRecord,
                metaComplexAttributeName);

            // есои необходимо переключаем вид таблетки для комплексного атрибута
            tabletClass = 'Unidata.view.steward.dataentity.complex.carousel.CarouselAttributeTablet';

            if (!me.useCarousel) {
                tabletClass = 'Unidata.view.steward.dataentity.complex.flat.FlatAttributeTablet';
                // для плоского представления начинаем раскрашивать с уровня 1
                depth++;
            }

            cfg = {
                metaRecord: metaRecord,
                dataRecord: dataRecord,
                metaNested: metaRecord,
                dataNested: dataRecord,
                metaAttribute: metaComplexAttribute,
                dataAttribute: dataAttribute,
                readOnly: readOnly,
                depth: depth,
                hiddenAttribute: hiddenAttribute,
                preventMarkField: preventMarkField
            };

            // для плоского представления состояние не сохраняем
            if (me.useCarousel) {
                entityName           = metaRecord.get('name');
                complexAttributeName = metaComplexAttribute.get('name');
                // формируем ключ получения состояния панели
                stateComponentKey = [entityName, ComponentTypes.COMPLEX_ATTRIBUTE_TABLET, complexAttributeName];
                // формируем конфиг для stateable панели (name + состояние)
                stateableCfg = Unidata.mixin.PanelStateable.getStateableCfg(stateComponentKey);
                // применяем
                Ext.apply(cfg, stateableCfg);
            }

            container = Ext.create(tabletClass, cfg);

            // для плоского представления состояния не сохраняем
            if (me.useCarousel) {
                container.enableStateable();
            }

            containers.push(container);
        });

        return containers;
    },

    /**
     * Включает / выключает режим только для чтения
     *
     * @param readOnly
     */
    updateReadOnly: function (readOnly) {
        var items = this.getContainers();

        Ext.Array.each(items, function (item) {
            item.setReadOnly(readOnly);
        });
    },

    /**
     * Включает / выключает режим подсвечивания ошибок на ошибочных полях
     *
     * @param value
     */
    updatePreventMarkField: function (value) {
        var items = this.getContainers();

        Ext.Array.each(items, function (item) {
            item.setPreventMarkField(value);
        });
    },

    /**
     * Включает / выключает отображение скрытых атрибутов
     *
     * @param hiddenAttribute
     */
    updateHiddenAttribute: function (hiddenAttribute) {
        var items = this.getContainers();

        Ext.Array.each(items, function (item) {
            item.setHiddenAttribute(hiddenAttribute);
        });
    },

    /**
     * Обновление datarecord
     */
    updateDataRecord: function () {
        this.etalonId = this.getEtalonId();
    },

    /**
     * Возвращает etalonId записи. Методы добавлены для QA отдела. Используются в автотестах
     *
     * добавлены по задаче UN-3928
     * @returns {*}
     */
    getEtalonId: function () {
        var dataRecord = this.getDataRecord(),
            etalonId = null;

        if (dataRecord) {
            etalonId = dataRecord.get('etalonId');
        }

        return etalonId;
    },

    /**
     * Возвращает массив дочерних элементов
     *
     * @returns {Array}
     */
    getContainers: function () {
        var containers = [],
            items      = this.items;

        if (items && items.isMixedCollection) {
            containers = items.getRange();
        }

        return containers;
    },

    /**
     * Возвращает массив компонентов отображающих простые атрибуты простых атрибутов первого уровня
     *
     * @returns {Array}
     */
    getSimpleAttributeContainersFirstLevel: function () {
        var containers = [];

        Ext.Array.each(this.simpleContainers, function (container) {
            containers = Ext.Array.merge(containers, container.getSimpleAttributeContainers());
        });

        return containers;
    },

    /**
     * Возвращает массив компонентов отображающих простые атрибуты простых атрибутов пришедших из классификаторов
     *
     * @returns {Array}
     */
    getSimpleAttributeContainersClassifier: function () {
        var containers = [];

        Ext.Array.each(this.classifierContainers, function (container) {
            containers = Ext.Array.merge(containers, container.getSimpleAttributeContainers());
        });

        return containers;
    },

    /**
     * Возвращает массив компонентов отображающих простые атрибуты для комплексных атрибутов
     *
     * @returns {Array}
     */
    getSimpleAttributeContainersComplex: function () {
        var containers = [];

        Ext.Array.each(this.complexContainers, function (container) {
            containers = Ext.Array.merge(containers, container.getSimpleAttributeContainers());
        });

        return containers;
    },

    /**
     * Возвращает массив компонентов отображающих простые атрибуты
     *
     * @returns {Array}
     */
    getSimpleAttributeContainers: function () {
        return this.attributeContainers.filterBy(function (item) {
            return item instanceof Unidata.view.steward.dataentity.attribute.AbstractAttribute;
        }).getRange();
    },

    /**
     * Возвращает массив контейнеров с простыми атрибутами отфильтрованных по путю к атрубуту
     *
     * @param path
     * @returns {*|Array}
     */
    findSimpleAttributeContainerByPath: function (path) {
        if (!Ext.isArray(path)) {
            path = [path];
        }

        return this.findSimpleAttributeContainersByPath(path);
    },

    /**
     * Возвращает массив контейнеров с простыми атрибутами отфильтрованных по массиву переданных путей
     *
     * @param paths
     * @returns {Array}
     */
    findSimpleAttributeContainersByPath: function (paths) {
        var attributeContainers = this.getSimpleAttributeContainers();

        return this.filterContainersByPath(attributeContainers, paths);
    },

    /**
     * Фильтрует контейнеры атрибутов по полному пути к атрибут
     *
     * @param containers
     * @param paths
     * @returns {Array}
     * @private
     */
    filterContainersByPath: function (containers, paths) {
        return Ext.Array.filter(containers, function (attributeContainer) {
            var attributePath = attributeContainer.getAttributePath();

            return Ext.Array.contains(paths, attributePath);
        });
    },

    /**
     * Устанавливает индикатор для контейнера с простым атрибутом
     *
     * @param attributeContainer
     * @param indicatorName
     * @param indicatorState
     */
    setSimpleAttributeContainerIndicator: function (attributeContainer, indicatorName, indicatorState) {
        attributeContainer.changeIndicator(indicatorName, indicatorState);
    },

    /**
     * Устанавливает индикатор для контейнеров с простым атрибутом
     *
     * @param attributePaths
     * @param indicatorName
     * @param indicatorState
     */
    setSimpleAttributeContainersIndicator: function (attributePaths, indicatorName, indicatorState) {
        Ext.Array.each(attributePaths, function (path) {
            this.executeByAttributePath(
                this.setSimpleAttributeContainerIndicator,
                path,
                [indicatorName, indicatorState]
            );
        }, this);
    },

    /**
     * Сбрасывает все индикаторы для контейнера с простым атрибутом
     */
    clearSimpleAttributeContainerIndicator: function (attributeContainer) {
        if (attributeContainer.clearIndicator) {
            attributeContainer.clearIndicator();
        }
    },

    /**
     * Сбрасывает все индикаторы для контейнеров с простым атрибутом
     */
    clearSimpleAttributeContainersIndicator: function () {
        this.executeByAttributePath(
            this.clearSimpleAttributeContainerIndicator
        );
    },

    /**
     * Отображает индикаторы для полей на которых были ошибки
     */
    showDqErrorsIndicator: function () {
        var metaRecord          = this.getMetaRecord(),
            dataRecord          = this.getDataRecord(),
            dqRuleNames         = [],
            dqAttributePaths    = [];

        // сбрасываем все индикаторы в контейнерах простых атрибутов
        this.clearSimpleAttributeContainersIndicator();

        // строим список имен правил качества на которых были ошибки
        dataRecord.dqErrors().each(function (dqRule) {
            dqRuleNames.push(dqRule.get('ruleName'));
        });

        // получаем пути атрибутов с ошибками правил качества
        dqAttributePaths = Unidata.util.MetaRecord.getDqAttributePathsByNames(metaRecord, dqRuleNames);

        // подсвечиваем индикатор с ошибками
        this.setSimpleAttributeContainersIndicator(dqAttributePaths, 'error', 'on');
    },

    /**
     * Отображает индикаторы неконсистентных полей
     */
    showInconsistentAttributesIndicator: function () {
        var inconsistentAttributePaths = this.getInconsistentAttributePaths(),
            attributeContainers        = [];

        // сбрасываем все индикаторы в контейнерах простых атрибутов
        // TODO: продумать - могут ли индикаторы сочетаться с incosistent индикаторами
        this.clearSimpleAttributeContainersIndicator();

        // подсвечиваем индикатор с ошибками
        // TODO: изменить типы индикаторов на более общие ([error, warning] вместо [error, approve]) ?
        this.setSimpleAttributeContainersIndicator(inconsistentAttributePaths, 'error', 'led');
    },

    /**
     * Отображает яркие индикаторы для полей по выбранному правилу качества
     *
     * @param dqName
     */
    showDqErrorIndicatorByDqName: function (dqName) {
        var metaRecord          = this.getMetaRecord(),
            dqRuleNames         = [dqName],
            dqAttributePaths    = [];

        // получаем пути атрибутов с ошибками правил качества
        dqAttributePaths = Unidata.util.MetaRecord.getDqAttributePathsByNames(metaRecord, dqRuleNames);

        // включаем индикатор для полей по всем свалившимся правилам качествао
        this.showDqErrorsIndicator();

        // включаем яркий индикатор для полей конкретного правила качества
        this.setSimpleAttributeContainersIndicator(dqAttributePaths, 'error', 'led');
    },

    /**
     * Возвращает карту значений простых атрибутов первого уровня для проверки валидности по меткам безопасности
     *
     * Пример возвращаемой карты:
     * {
     *     ADMZHDOR.ADM_KOD: "1",
     *     ADMZHDOR.STRAN_KOD: 12,
     *     ADMZHDOR.NAME: "1",
     *     ADMZHDOR.SNAME_RUS: "1",
     *     ADMZHDOR.SNAME_LAT: "1"
     * }
     *
     * @returns {{}}
     */
    getAttributeValuesMap4SecurityLabel: function () {
        var attributeContainers = this.getSimpleAttributeContainersFirstLevel(),
            map                 = {};

        // строим карту значений
        Ext.Array.each(attributeContainers, function (container) {
            var key   = container.getFullAttributePath(),
                value = container.getDataAttributeValue();

            map[key] = value;
        });

        return map;
    },

    /**
     * Возвращает массив контейнеров простых атрибутов не подходящим под метки безопасности
     */
    getIncorrectSecurityLabelSimpleAttributeContainers: function () {
        var me                           = this,
            attributeContainers          = this.getSimpleAttributeContainersFirstLevel(),
            user                         = Unidata.Config.getUser(),
            securityLabels               = user.get('securityLabels'),
            incorrectAttributeContainers = [];

        if (Unidata.Config.isUserAdmin()) {
            return incorrectAttributeContainers;
        }

        Ext.Array.each(attributeContainers, function (container) {
            var fullAttributePath  = container.getFullAttributePath(),
                attributeValuesMap = me.getAttributeValuesMap4SecurityLabel(),
                SecurityLabelUtil = Unidata.util.SecurityLabel;

            if (!SecurityLabelUtil.checkSecurityLabels(fullAttributePath, attributeValuesMap, securityLabels)) {
                incorrectAttributeContainers.push(container);
            }
        });

        return incorrectAttributeContainers;
    },

    /**
     * Проверят валидность dataEntity по меткам безопасности
     *
     * Возвращает: true - если валидна, иначе false
     */
    isSecurityLabelValid: function () {
        var attributeContainers = this.getIncorrectSecurityLabelSimpleAttributeContainers(),
            isValid             = true;

        if (attributeContainers.length) {
            isValid = false;
        }

        return isValid;
    },

    /**
     * Отображает яркие индикаторы для полей содержащих не корректные значения простых атрибутов
     */
    showSecurityLabelErrorIndicator: function () {
        var incorrectAttributeContainers = this.getIncorrectSecurityLabelSimpleAttributeContainers(),
            incorrectAttributePaths = [];

        // строим массив путей атрибутов которые нужно подсветить
        Ext.Array.each(incorrectAttributeContainers, function (container) {
            var path = container.getAttributePath();

            incorrectAttributePaths.push(path);
        });

        // сбрасываем все индикаторы в контейнерах простых атрибутов
        this.clearSimpleAttributeContainersIndicator();

        // подсвечиваем контейнеры с невалидными значениями
        this.setSimpleAttributeContainersIndicator(incorrectAttributePaths, 'error', 'led');
    },

    /**
     * Подсветка атрибутов цветом
     *
     * @param attributePaths
     * @param color
     */
    highlightAttributeWinners: function (attributePaths) {
        if (attributePaths) {
            Ext.Array.each(attributePaths, function (path) {
                this.executeByAttributePath(this.highlightItem, path);
            }, this);
        } else {
            this.executeByAttributePath(this.highlightItem, null);
        }
    },

    highlightItem: function (item) {
        var HighlightTypes = Unidata.view.steward.dataentity.DataEntity.highlightTypes;

        if (item.setHighlight) {
            item.setHighlight(HighlightTypes.HIGHLIGHT_WINNER);
        }
    },

    /**
     * Проверят валидность dataEntity по полям
     * (валидность по обязательности заполнения)
     *
     * Возвращает: true - если валидна, иначе false
     * TODO: проводить валидацию dataRecord, а не элементов формы
     */
    isFieldsValid: function () {
        var valid,
            validContainers,
            attributeContainers;

        attributeContainers = this.getSimpleAttributeContainers();

        // отключаем layout т.к.  на большом количестве атрибутов начинает тупить проверка валидации смотри UN-3444
        Ext.suspendLayouts();

        // строим карту признаков валидности каждого поля
        validContainers = Ext.Array.map(attributeContainers, function (attributeContainer) {
            return attributeContainer.validate();
        });

        // смотри UN-3444
        Ext.resumeLayouts(true);

        // валидно если все поля валидны
        valid = Ext.Array.every(validContainers, function (valid) {
            return valid == true;
        });

        return valid;
    },

    /**
     * Отображает атрибуты отмеченные в метамодели как скрытые
     */
    showHiddenAttribute: function () {
        var visible = true;

        this.setHiddenAttribute(visible);
    },

    /**
     * Скрывает атрибуты отмеченные в метамодели как скрытые
     */
    hideHiddenAttribute: function () {
        var visible = false;

        this.setHiddenAttribute(visible);
    },

    /**
     * Переключает отображение скрытых атрибутов
     */
    toggleHiddenAttribute: function () {
        this.hiddenAttribute ? this.hideHiddenAttribute() : this.showHiddenAttribute();
    },

    statics: {
        highlightTypes: {
            HIGHLIGHT_WINNER: 'winner',
            HIGHLIGHT_DIFF: 'diff'
        }
    }
});
