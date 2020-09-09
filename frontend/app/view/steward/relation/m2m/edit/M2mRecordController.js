/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-05-14
 */

Ext.define('Unidata.view.steward.relation.m2m.edit.M2mRecordController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.relation.m2mrecord',

    tiIntersectTypeInvalid: false,
    delayedTask: null,

    init: function () {
        var me = this,
            viewModel = me.getViewModel();

        this.callParent(arguments);

        this.delayedTask = Ext.create('Ext.util.DelayedTask');

        viewModel.bind('{dataRelation}', function () {
            // магия с Ext.defer нужна для того,
            // чтобы не происходил рекурсивный вызов viewModel.notify
            // так, this.computeDirty вызывается после выполнения настоящей анонимной функции
            Ext.defer(this.computeDirty, 1, this);
        }, this, {deep: true});
    },

    checkValid: function () {
        var view = this.getView(),
            dataEntity = view.dataEntity,
            pickerField = view.pickerField,
            pickerFieldValid,
            valid;

        pickerFieldValid = pickerField.validate();
        valid = dataEntity.isFieldsValid() && pickerFieldValid;

        return valid;
    },

    /**
     * Вычислить dirty и обновить значение переменной
     */
    computeDirty: function () {
        var view = this.getView(),
            dirty,
            dataRelation;

        dataRelation = view.getDataRelation();
        dirty = dataRelation.checkDirty() || this.isChanged();
        view.setDirty(dirty);
    },

    updateDirty: function (dirty, oldDirty) {
        var view = this.getView();

        view.fireEvent('m2mdirtychange', view, dirty, oldDirty);
    },

    /**
     * Инициализирует ссылки на компоненты
     */
    initComponentReference: function () {
        var view = this.getView();

        view.pickerField  = this.lookupReference('pickerField');
        view.dataEntity   = this.lookupReference('dataEntity');
        view.saveButton   = this.lookupReference('saveButton');
        view.removeButton = this.lookupReference('removeButton');

        view.periodPanel  = this.lookupReference('periodPanel');
        view.fromDate     = view.periodPanel.dateFieldStart;
        view.toDate       = view.periodPanel.dateFieldEnd;
    },

    /**
     * Обновляет состояние "только для чтения"
     *
     * @param readOnly
     */
    updateReadOnly: function (readOnly) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            dataRelation = view.getDataRelation(),
            items = view.items;

        viewModel.set('readOnly', readOnly);

        if (!items) {
            return;
        }

        view.pickerField.setReadOnly(!dataRelation.phantom);
        view.dataEntity.setReadOnly(readOnly);
        view.fromDate.setReadOnly(readOnly);
        view.toDate.setReadOnly(readOnly);
    },

    /**
     * Отображает связь
     */
    displayRelationRecord: function () {
        var view = this.getView(),
            metaRelation = view.getMetaRelation(),
            dataRelation = view.getDataRelation(),
            dataEntity;

        this.initItems(metaRelation, dataRelation);

        Unidata.util.DataRecord.bindManyToOneAssociationListeners(dataRelation);

        dataEntity = view.dataEntity;

        // выводим запись
        dataEntity.setEntityData(metaRelation, dataRelation);
        dataEntity.displayDataEntity();
    },

    /**
     * Добавляет во view элементы для отображения записи
     *
     * @param metaRelation
     * @param dataRelation
     */
    initItems: function (metaRelation, dataRelation) {
        var view = this.getView(),
            cfg;

        cfg = this.getItemsConfig(metaRelation, dataRelation);

        view.removeAll();
        view.add(cfg);

        this.initComponentReference();

        view.pickerField.on('changecodevalue', this.onCodeValueChange, this);
        view.pickerField.on('valueclear', this.onPickerFieldValueClear, this);
    },

    /**
     * Возвращает конфиг элементов которые необходимо вставить во view
     *
     * @param metaRelation
     * @param dataRelation
     * @returns {*}
     */
    getItemsConfig: function (metaRelation, dataRelation) {
        var me = this,
            view = this.getView(),
            readOnly = view.getReadOnly(),
            validFrom = dataRelation.get('validFrom'),
            validTo = dataRelation.get('validTo'),
            etalonIdTo = dataRelation.get('etalonIdTo') || null,
            required = metaRelation.get('required'),
            timeIntervalIntersectType,
            cfg,
            picker,
            entityTextfield,
            dateFieldReadOnly;

        if (required) {
            timeIntervalIntersectType = Unidata.view.component.DropdownPickerField.timeIntervalIntersectType.FULL;
        } else {
            timeIntervalIntersectType = Unidata.view.component.DropdownPickerField.timeIntervalIntersectType.PARTIAL;
        }

        entityTextfield = Ext.widget({
            xtype: 'textfield',
            readOnly: true,
            flex: 1,
            fieldLabel: Unidata.i18n.t('glossary:entity'),
            cls: 'un-relation-record-attribute un-dataentity-attribute-input un-dataentity-attribute-input-string'
        });

        picker = Ext.widget({
            xtype: 'dropdownpickerfield',
            reference: 'pickerField',
            emptyText: Unidata.i18n.t(
                'common:defaultSelect',
                {entity: Unidata.i18n.t('glossary:relation').toLowerCase()}
            ),
            openLookupRecordHidden: false,
            findTriggerHidden: false,
            cls: 'un-relation-record-attribute un-dataentity-attribute-input un-dataentity-attribute-input-lookup',
            fieldLabel: Unidata.i18n.t('glossary:relation'),
            displayAttributes: metaRelation.get('toEntityDefaultDisplayAttributes'),
            searchAttributes: metaRelation.get('toEntitySearchAttributes'),
            useAttributeNameForDisplay: metaRelation.get('useAttributeNameForDisplay'),
            timeIntervalIntersectType: timeIntervalIntersectType,
            entityType: 'entity',
            entityName: metaRelation.get('toEntity'),
            publishes: ['rawValue'],
            codeValue: etalonIdTo,
            value: etalonIdTo,
            readOnly: !dataRelation.phantom,
            allowBlank: false,
            msgTarget: 'under',
            flex: 1,
            listeners: {
                validitychange: this.onDropdownValidityChange.bind(this),
                valuechange: this.onDropdownValueChange.bind(this),
                valueclear: this.onDropdownValueChange.bind(this)
            },
            findCfgDelegate: function () {
                var cfg;

                cfg = {
                    validFrom: dataRelation.get('validFrom'),
                    validTo: dataRelation.get('validTo')
                };

                return cfg;
            },
            detailCfgDelegate: function () {
                var cfg;

                cfg = {
                    validFrom: dataRelation.get('validFrom'),
                    validTo: dataRelation.get('validTo')
                };

                return cfg;
            }
        });

        picker.on('loadmetarecord', function (picker, metaRecord) {
            entityTextfield.setValue(metaRecord.get('displayName'));
        }, this);

        dateFieldReadOnly = readOnly || !dataRelation.phantom;
        cfg = [
            {
                xtype: 'container',
                padding: '0 0 0 110',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                componentCls: 'un-m2m-timeinterval',
                items: [
                    {
                        xtype: 'validityperiodpanel',
                        reference: 'periodPanel',
                        layout: {
                            type: 'hbox',
                            align: 'stretch'
                        },
                        fieldLabels: {
                            start: Unidata.i18n.t('relation>from'),
                            end: Unidata.i18n.t('relation>to')
                        },
                        fieldNames: {
                            start: 'validityPeriodStart',
                            end: 'validityPeriodEnd'
                        },
                        customConfigs: {
                            start: {
                                labelWidth: 20,
                                maxWidth: 200,
                                value: validFrom,
                                readOnly: dateFieldReadOnly,
                                cls: 'un-dataentity-attribute-input un-dataentity-attribute-input-date',
                                msgTarget: 'none',
                                validator: function () {
                                    if (me.tiIntersectTypeInvalid) {
                                        return false;
                                    }

                                    return true;
                                },
                                listeners: {
                                    blur: this.onValidityPeriodStartChange.bind(this),
                                    specialkey: this.onSpecialKeyFromDateField.bind(this)
                                }
                            },
                            end: {
                                labelWidth: 20,
                                maxWidth: 200,
                                value: validTo,
                                readOnly: dateFieldReadOnly,
                                cls: 'un-dataentity-attribute-input un-dataentity-attribute-input-date',
                                msgTarget: 'none',
                                validator: function () {
                                    if (me.tiIntersectTypeInvalid) {
                                        return false;
                                    }

                                    return true;
                                },
                                listeners: {
                                    blur: this.onValidityPeriodEndChange.bind(this),
                                    specialkey: this.onSpecialKeyToDateField.bind(this)
                                }
                            }
                        }
                    }
                ]
            },
            {
                xtype: 'dataentity',
                reference: 'dataEntity',
                useCarousel: false,
                depth: 1,
                readOnly: readOnly
            }
        ];

        cfg = Ext.Array.merge([entityTextfield, picker], cfg);

        return cfg;
    },

    isChanged: function () {
        if (!this.pickerField) {
            return false;
        }

        return !this.pickerField.isInitialValue();
    },

    /**
     * Обработчик смены кодового значения "запись на которую ссылаемся"
     *
     * @param ddfield
     * @returns {boolean}
     */
    onCodeValueChange: function (ddfield) {
        var view = this.getView(),
            etalonId = ddfield.getEtalonId(),
            relationDisplayName,
            dataRelation = view.getDataRelation();

        if (!etalonId) {
            this.computeDirty();

            return false;
        }

        // для новых связей сохраняем значения в модель
        if (dataRelation && dataRelation.phantom) {
            relationDisplayName = ddfield.getValue();
            dataRelation.set('etalonIdTo', etalonId);
            dataRelation.set('etalonDisplayNameTo', relationDisplayName);
        }

        if (view.fireEvent('changerelto', view, etalonId) === false) {
            view.pickerField.clearValue();

            Unidata.showError(Unidata.i18n.t('relation>valueUsesInAnotherRelation'));
        }

        this.computeDirty();
    },

    onPickerFieldValueClear: function () {
        var view = this.getView(),
            dataRelation = view.getDataRelation();

        dataRelation.set('etalonIdTo', null);
        dataRelation.set('etalonDisplayNameTo', null);
    },

    onValidityPeriodStartChange: function () {
        var view = this.getView(),
            dataRelation = view.getDataRelation();

        // для новых связей сохраняем значения в модель
        if (dataRelation && dataRelation.phantom) {
            dataRelation.set('validFrom', view.fromDate.getValue());

            this.checkByTimeIntervalIntersectTypeDelayed();
        }
    },

    onValidityPeriodEndChange: function () {
        var view = this.getView(),
            dataRelation = view.getDataRelation();

        // для новых связей сохраняем значения в модель
        if (dataRelation && dataRelation.phantom) {
            dataRelation.set('validTo', view.toDate.getValue());

            this.checkByTimeIntervalIntersectTypeDelayed();
        }
    },

    /**
     * Обработка клика по кнопке удаления записи
     *
     * @param button
     * @param e
     */
    onRemoveRelationClick: function (button, e) {
        var view = this.getView(),
            title = Unidata.i18n.t('admin.metamodel>removeRelation'),
            msg = Unidata.i18n.t('admin.metamodel>confirmRemoveRelation');

        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.preventDefault();
        e.stopPropagation();

        if (view.fireEvent('beforeremove') != false) {
            this.showPrompt(title, msg, this.removeRelation, this, button);
        }
    },

    onDropdownValidityChange: function (self, valid) {
        var view = this.getView();

        view.setValid(valid);
    },

    updateValid: function (self, valid, oldValid) {
        var view = this.getView();

        view.fireEvent('m2mrecordvaliditychange', view, valid, oldValid);
    },

    /**
     * Удаление инстанса связи
     */
    removeRelation: function () {
        var view = this.getView(),
            dataRelation = view.getDataRelation(),
            saveAtomic = view.getSaveAtomic();

        if (saveAtomic) {
            view.fireEvent('removem2m', view, dataRelation);
        } else {
            this.removeRelationOneByOne();
        }
    },

    /**
     * Удаление инстанса связи
     */
    removeRelationOneByOne: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            dataRelation = view.getDataRelation(),
            etalonId = dataRelation.get('etalonId');

        function removeHandler () {
            view.fireEvent('removem2m', view, dataRelation);

            Unidata.showMessage(Unidata.i18n.t('relation>removeRelationSuccess'));
        }

        if (etalonId) {
            viewModel.set('allowClickRemove', false);

            Ext.Ajax.request({
                method: 'DELETE',
                url: Unidata.Config.getMainUrl() + 'internal/data/relations/relation/' + etalonId,
                success: function () {
                    view.fireComponentEvent('checkworkflow');
                    removeHandler();
                },
                failure: function () {
                    // флаг имеет смысл сбрасывать только если удаление не успешное в противном случае панель удаляется
                    viewModel.set('allowClickRemove', true);
                },
                callback: function () {
                },
                scope: this
            });
        } else {
            removeHandler();
        }
    },

    /**
     * Обработка клика по кнопке сохранения записи
     */
    onSaveRelationClick: function (btn, e) {
        var me = this,
            view = this.getView(),
            viewModel = this.getViewModel(),
            dataRelation = view.getDataRelation(),
            dataRecord = view.getDataRecord(),
            etalonId = dataRecord.get('etalonId'),
            etalonIdTo,
            data;

        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.preventDefault();
        e.stopPropagation();

        etalonIdTo = view.pickerField.getEtalonId();

        dataRelation.set('etalonIdTo', etalonIdTo);

        if (!dataRelation.get('etalonIdTo')) {
            Unidata.showError(Unidata.i18n.t('relation>invalidRelationForm'));

            return;
        }

        dataRelation.set('validFrom', view.fromDate.getValue());
        dataRelation.set('validTo', view.toDate.getValue());

        data = dataRelation.getFilteredData({associated: true, serialize: true, persist: true});

        viewModel.set('allowClickSave', false);

        Ext.Ajax.unidataRequest({
            method: 'POST',
            url: Unidata.Config.getMainUrl() + 'internal/data/relations/relation/relto/' + etalonId,
            jsonData: Ext.util.JSON.encode(data),
            success: function (response) {
                var newDataRelation,
                    oldDataRelation,
                    reponseJson,
                    reader,
                    readerData;

                reponseJson = JSON.parse(response.responseText);

                oldDataRelation = dataRelation;

                if (reponseJson) {
                    reader = Ext.create('Ext.data.JsonReader', {
                        model: 'Unidata.model.data.RelationReference',
                        rootProperty: 'content'
                    });

                    readerData = reader.readRecords(reponseJson);
                    newDataRelation = readerData.records[0];

                    view.setDataRelation(newDataRelation);
                }

                Unidata.showMessage(Unidata.i18n.t('relation>saveRelationSuccessfully'));

                me.displayRelationRecord();
                this.updateReadOnly(view.getReadOnly());

                view.fireEvent('replacedatarelation', newDataRelation, oldDataRelation);
                view.fireComponentEvent('checkworkflow');
            },
            callback: function () {
                viewModel.set('allowClickSave', true);
            },
            scope: this
        });
    },

    checkByTimeIntervalIntersectTypeDelayed: function () {
        var delayedTask = this.delayedTask;

        delayedTask.cancel();
        delayedTask.delay(500, this.checkByTimeIntervalIntersectType, this);
    },

    checkByTimeIntervalIntersectType: function () {
        var me = this,
            Util = Unidata.util.api.DataRecord,
            view = this.getView(),
            metaRelation = view.getMetaRelation(),
            required = metaRelation.get('required'),
            dataRelation = view.getDataRelation(),
            etalonIdTo = dataRelation.get('etalonIdTo'),
            validFrom = dataRelation.get('validFrom'),
            validTo = dataRelation.get('validTo'),
            timeIntervalIntersectType = Unidata.view.component.DropdownPickerField.timeIntervalIntersectType.FULL,
            promise;

        if (!required) {
            timeIntervalIntersectType = Unidata.view.component.DropdownPickerField.timeIntervalIntersectType.PARTIAL;
        }

        if (!etalonIdTo) {
            return;
        }

        view.fromDate.suspendEvent('blur');
        view.toDate.suspendEvent('blur');

        view.fireEvent('timeintervalcheckstart');

        promise = Util.allowedDataRecordByTimeIntervalIntersectType([etalonIdTo], validFrom, validTo, timeIntervalIntersectType);

        promise
            .then(
                function (etalonIds) {
                    if (Ext.isArray(etalonIds) && Ext.Array.contains(etalonIds, etalonIdTo)) {
                        me.tiIntersectTypeInvalid = false;
                    } else {
                        me.tiIntersectTypeInvalid = true;
                    }

                    me.markFieldInvalidByTimeIntervalIntersectTypeCheck();

                    view.fireEvent('timeintervalcheckstop');

                    view.fromDate.resumeEvent('blur');
                    view.toDate.resumeEvent('blur');
                },
                function () {
                    view.fireEvent('timeintervalcheckstop');

                    view.fromDate.resumeEvent('blur');
                    view.toDate.resumeEvent('blur');
                })
            .done();
    },

    markFieldInvalidByTimeIntervalIntersectTypeCheck: function () {
        var view = this.getView(),
            fields;

        if (this.tiIntersectTypeInvalid) {
            view.pickerField.markInvalid(Unidata.i18n.t('relation>m2m>recordWithInvalidTimeInterval'));
            view.fromDate.markInvalid(true);
            view.toDate.markInvalid(true);
        } else {
            fields = [
                view.pickerField,
                view.fromDate,
                view.toDate
            ];

            Ext.Array.each(fields, function (field) {
                if (field.isValid()) {
                    field.clearInvalid();
                }
            });
        }
    },

    onDropdownValueChange: function () {
        var view = this.getView(),
            etalonIdTo = view.pickerField.getEtalonId();

        if (!etalonIdTo) {
            this.tiIntersectTypeInvalid = false;
            this.markFieldInvalidByTimeIntervalIntersectTypeCheck();
        } else {
            this.checkByTimeIntervalIntersectTypeDelayed();
        }
    },

    onSpecialKeyFromDateField: function (component, e) {
        var view = this.getView();

        this.handleSpecSpecialKeyResetPasswordInput(component, view.toDate, e);
    },

    onSpecialKeyToDateField: function (component, e) {
        var view = this.getView();

        this.handleSpecSpecialKeyResetPasswordInput(component, view.fromDate, e);
    },

    /**
     * Должен работать переход по табу между полями дата с - дата по
     *
     * @param field - поле на котором обрабатывается событие
     * @param nextField - поле на которое должен перейти фокус
     * @param event - событие
     */
    handleSpecSpecialKeyResetPasswordInput: function (field, nextField, event) {
        var key = event.getKey();

        if (key === Ext.event.Event.TAB) {
            event.stopEvent();

            nextField.focus(true, 50);
        }
    }
});
