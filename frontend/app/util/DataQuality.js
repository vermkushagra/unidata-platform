/**
 * Утилитный класс для работы с правилами  качества
 *
 * @author Ivan Marshalkin
 * @date 2018-02-09
 */

Ext.define('Unidata.util.DataQuality', {
    singleton: true,

    portApplicationModeEnumList:  {
        MODE_ALL: 'MODE_ALL',
        MODE_ALL_WITH_INCOMPLETE: 'MODE_ALL_WITH_INCOMPLETE',
        MODE_ONCE: 'MODE_ONCE'
    },
    portApplicationModeLabels:  {
        MODE_ALL: Unidata.i18n.t('admin.dq>modeAll'),
        MODE_ALL_WITH_INCOMPLETE: Unidata.i18n.t('admin.dq>modeAllWithIncomplete'),
        MODE_ONCE: Unidata.i18n.t('admin.dq>modeOnce')
    },

    errorCodes: {
        DQ_VALIDATOR_INVALID: 'DQ_VALIDATOR_INVALID',
        DQ_EMPTY_TYPE: 'DQ_EMPTY_TYPE',
        DQ_EMPTY_USER_MESSAGE: 'DQ_EMPTY_USER_MESSAGE',
        DQ_EMPTY_DATASOURCE: 'DQ_EMPTY_DATASOURCE',
        DQ_RAISE_INVALID: 'DQ_RAISE_INVALID',
        DQ_ENRICH_INVALID: 'DQ_ENRICH_INVALID',
        DQ_REQUIRED_PORT_INVALID: 'DQ_REQUIRED_PORT_INVALID',
        DQ_APPLICABLE_INVALID: 'DQ_APPLICABLE_INVALID'
    },

    executionContextEnumList: {
        GLOBAL: 'GLOBAL',
        LOCAL: 'LOCAL'
    },

    executionContextLabels: {
        GLOBAL: Unidata.i18n.t('admin.dq>executionContextGlobal'),
        LOCAL: Unidata.i18n.t('admin.dq>executionContextLocal')
    },

    /**
     * Возвращает истину если dq - валидация
     *
     * @param dqRule
     * @returns {boolean}
     */
    isRaise: function (dqRule) {
        return dqRule.getRaise() !== null && dqRule.getRaise() !== undefined;
    },

    /**
     * Возвращает истину если dq - обогащение
     *
     * @param dqRule
     * @returns {boolean}
     */
    isEnrich: function (dqRule) {
        return dqRule.getEnrich() !== null && dqRule.getEnrich() !== undefined;
    },

    /**
     * Возвращает истину если правило системное
     *
     * @param dqRule
     * @returns {boolean}
     */
    isSpecial: function (dqRule) {
        return Boolean(dqRule.get('special'));
    },

    /**
     * * Возвращает массив ошибок правил качества с дополнительными данными
     *
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity}
     * @param draft Признак режим черновика
     * @returns {Array}
     */
    getMetaRecordDqErrorList: function (metaRecord, draft) {
        var me = this,
            errors = [];

        metaRecord.dataQualityRules().each(function (dqRule) {
            var cleanseFunction,
                cfName;

            // валидируем только измененные cleanse функции
            if (dqRule.checkDirty()) {
                cfName = dqRule.get('cleanseFunctionName');
                // считаем, что если dqRule dirty, то CF подгружена
                cleanseFunction = Unidata.util.api.CleanseFunction.getFromCacheByName(cfName, draft);

                errors = Ext.Array.merge(errors, me.getDqErrorList(dqRule, cleanseFunction));
            }
        });

        return errors;
    },

    /**
     * Возвращает массив ошибок правил качества с дополнительными данными
     *
     * @param dqRule
     * @param cleanseFunction
     * @returns {Array}
     */
    getDqErrorList: function (dqRule, cleanseFunction) {
        var me = this,
            errors = [],
            dqRuleRaise = dqRule.getRaise(),
            dqApplicable = dqRule.get('applicable'),
            dqName = dqRule.get('name'),
            metaPorts,
            dataPorts;

        // специальное правило всегда считается валидным
        if (me.isSpecial(dqRule)) {
            return [];
        }

        if (!dqRule.isValid()) {
            // сообщения валидаторов сенчи

            // !!! эквивалент старой проверки в metarecord/dataquality/DataQualityController.js удалить после первичного тестирования

            // if (view.hideDqTab) {
            //     errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidDqSettingsAskAdministrator')]);
            // } else {
            //     errorMsgs = Ext.Array.merge(errorMsgs, dqRule.getErrorMessages().map(addPrefix));
            // }

            Ext.Array.each(dqRule.getErrorMessages(), function (message) {
                errors.push({
                    code: me.errorCodes.DQ_VALIDATOR_INVALID,
                    dqName: dqName,
                    message: message
                });

            });
        }

        if (!Ext.isArray(dqApplicable) || !Ext.Array.contains(dqApplicable, 'ETALON') && !Ext.Array.contains(dqApplicable, 'ORIGIN')) {
            errors.push({
                code: me.errorCodes.DQ_APPLICABLE_INVALID,
                dqName: dqName
            });
        }

        // должен быть указан тип правила: валидация или обогащение

        // !!! эквивалент старой проверки в metarecord/dataquality/DataQualityController.js удалить после первичного тестирования

        // if (view.hideDqTab) {
        //     errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidDqSettingsAskAdministrator')]);
        // } else {
        //     errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>selectTypeRule'));
        // }
        if (!me.isEnrich(dqRule) && !me.isRaise(dqRule)) {
            errors.push({
                code: me.errorCodes.DQ_EMPTY_TYPE,
                dqName: dqName
            });
        }

        // правило валидации?
        if (me.isRaise(dqRule)) {
            // сообщение пользователю должно быть задано

            // !!! эквивалент старой проверки в metarecord/dataquality/DataQualityController.js удалить после первичного тестирования

            // if (isRaise()) {
            //     dqRaiseValid = dqRule.getRaise().isValid();
            //
            //     if (dqRuleRaise.get('messagePort') == '' && Ext.String.trim(dqRuleRaise.get('messageText')) == '') {
            //         if (view.hideDqTab) {
            //             errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidDqSettingsAskAdministrator')]);
            //         } else {
            //             errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>notSetUserMessage'));
            //         }
            //     }
            // }
            if (Ext.String.trim(dqRuleRaise.get('messagePort')) == '' && Ext.String.trim(dqRuleRaise.get('messageText')) == '') {
                errors.push({
                    code: me.errorCodes.DQ_EMPTY_USER_MESSAGE,
                    dqName: dqName
                });
            }

            // валидность правила валидации

            // !!! эквивалент старой проверки в metarecord/dataquality/DataQualityController.js удалить после первичного тестирования

            // if (!dqRaiseValid) {
            //     if (view.hideDqTab) {
            //         errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidDqSettingsAskAdministrator')]);
            //     } else {
            //         errorMsgs = Ext.Array.merge(errorMsgs, dqRule.getRaise().getErrorMessages().map(addPrefix));
            //     }
            // }
            if (!dqRule.getRaise().isValid()) {
                Ext.Array.each(dqRule.getRaise().getErrorMessages(), function (message) {
                    errors.push({
                        code: me.errorCodes.DQ_RAISE_INVALID,
                        dqName: dqName,
                        message: message
                    });
                });
            }
        }

        // правило обогащения?
        if (me.isEnrich(dqRule)) {
            // валидность правила обогащения

            // !!! эквивалент старой проверки в metarecord/dataquality/DataQualityController.js удалить после первичного тестирования

            // if (!dqEnrichValid) {
            //     if (view.hideDqTab) {
            //         errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidDqSettingsAskAdministrator')]);
            //     } else {
            //         errorMsgs = Ext.Array.merge(errorMsgs, dqRule.getEnrich().getErrorMessages().map(addPrefix));
            //     }
            // }
            if (!dqRule.getEnrich().isValid()) {
                Ext.Array.each(dqRule.getEnrich().getErrorMessages(), function (message) {
                    errors.push({
                        code: me.errorCodes.DQ_ENRICH_INVALID,
                        dqName: dqName,
                        message: message
                    });
                });
            }

            // применимость

            // !!! эквивалент старой проверки в metarecord/dataquality/DataQualityController.js удалить после первичного тестирования

            // if (isEnrich() && Ext.Array.contains(dqRule.get('applicable'), 'ETALON') && !dqRule.getEnrich().get('sourceSystem')) {
            //     if (view.hideDqTab) {
            //         errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidDqSettingsAskAdministrator')]);
            //     } else {
            //         errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>notSetDataSource'));
            //     }
            // }
            if (Ext.Array.contains(dqApplicable, 'ETALON') && !dqRule.getEnrich().get('sourceSystem')) {
                errors.push({
                    code: me.errorCodes.DQ_EMPTY_DATASOURCE,
                    dqName: dqName
                });
            }
        }

        // проверяем обязательные порты

        // !!! эквивалент старой проверки в metarecord/dataquality/DataQualityController.js удалить после первичного тестирования

        // if (dqRule.getCleanseFunction() && me.getInvalidInputPortNames(dqRule).length > 0) {
        //     if (view.hideDqTab) {
        //         errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidDqSettingsAskAdministrator')]);
        //     } else {
        //         errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>requiredPortsInvalid'));
        //     }
        // }

        // CF передается извне
        // cleanseFunction = dqRule.getCleanseFunction();

        if (cleanseFunction) {
            metaPorts = cleanseFunction.inputPorts();
            dataPorts = dqRule.inputs();

            metaPorts.each(function (metaPort) {
                var portName = metaPort.get('name'),
                    portData = dataPorts.findRecord('functionPort', portName),
                    isValid;

                // обходим только обязательные порты
                if (metaPort.get('required') !== true) {
                    return;
                }

                isValid = portData && (portData.get('attributeName') || portData.getAttributeConstantValue());

                if (!isValid) {
                    errors.push({
                        code: me.errorCodes.DQ_REQUIRED_PORT_INVALID,
                        dqName: dqName,
                        portName: portName
                    });
                }
            });
        }

        return errors;
    },

    /**
     * Возвращает массив кодов ошибок
     *
     * @param errors
     * @returns {Array}
     */
    getErrorCodeList: function (errors) {
        var list = [];

        Ext.Array.each(errors, function (error) {
            list.push(error.code);
        });

        return list;
    },

    /**
     * Используется ли атрибут хоть в одном DQ правиле
     *
     * @param metaAttribute {Unidata.model.attribute.AbstractAttribute} Атрибут
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Метамодель
     * @return {Boolean}
     */
    attributeUsedDq: function (metaAttribute, metaRecord) {
        var MetaRecordUtil = Unidata.util.MetaRecord,
            isAttributeUsed,
            attributePath,
            dqRules;

        attributePath   = Unidata.util.UPathMeta.buildAttributePath(metaRecord, metaAttribute);

        if (!metaAttribute || !metaRecord || !MetaRecordUtil.isEntity(metaRecord)) {
            return false;
        }

        dqRules = metaRecord.dataQualityRules().getRange();

        isAttributeUsed = Ext.Array.some(dqRules, this.isDqRuleUseAttribute.bind(this, attributePath, metaRecord), this);

        return isAttributeUsed;
    },

    /**
     * Используется ли атрибут в определенном DQ правиле
     *
     * @param attributePath {String} Путь к атрибуту
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Метамодель
     * @param dqRule {Unidata.model.dataquality.DqRule} DQ правило
     * @return {Boolean}
     */
    isDqRuleUseAttribute: function (attributePath, metaRecord, dqRule) {
        var inputs = dqRule.inputs().getRange(),
            outputs  = dqRule.outputs().getRange(),
            special = dqRule.get('special'),
            isUsed;

        if (special) {
            return false;
        }

        isUsed = Ext.Array.some(inputs, this.isAttributeUsedInPort.bind(this, attributePath, metaRecord)) ||
                 Ext.Array.some(outputs, this.isAttributeUsedInPort.bind(this, attributePath, metaRecord));

        return isUsed;
    },

    /**
     * Используется ли атрибут в определенном порте
     *
     * @param attributePath
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Метамодель
     * @param port {Unidata.model.dataquality.Input|Unidata.model.dataquality.Output}
     */
    isAttributeUsedInPort: function (attributePath, metaRecord, port) {
        var portUPath,
            portCanonicalPath,
            UPath;

        portUPath = port.get('attributeName');

        if (!portUPath) {
            return false;
        }
        UPath = Ext.create('Unidata.util.upath.UPath', {
            entity: metaRecord
        });
        UPath.fromUPath(portUPath);
        portCanonicalPath = UPath.toCanonicalPath();

        return portCanonicalPath === attributePath;
    },

    /**
     * Построить имя типа для сабмита константы
     *
     * @param  {Unidata.model.cleansefunction.InputPort|Unidata.model.cleansefunction.OutputPort} port
     * @return {String}
     */
    buildSubmitDataType: function (port) {
        var dataType = port.get('dataType');

        if (dataType === 'Any') {
            return 'String';
        }

        return dataType;
    }
});

