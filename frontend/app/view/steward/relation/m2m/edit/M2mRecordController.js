/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-05-14
 */

Ext.define('Unidata.view.steward.relation.m2m.edit.M2mRecordController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.relation.m2mrecord',

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
    },

    /**
     * Возвращает конфиг элементов которые необходимо вставить во view
     *
     * @param metaRelation
     * @param dataRelation
     * @returns {*}
     */
    getItemsConfig: function (metaRelation, dataRelation) {
        var view = this.getView(),
            readOnly = view.getReadOnly(),
            validFrom = dataRelation.get('validFrom'),
            validTo = dataRelation.get('validTo'),
            etalonIdTo = dataRelation.get('etalonIdTo') || null,
            cfg,
            picker,
            entityTextfield;

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
            openLookupRecordHidden: false,
            addLookupRecordHidden: false, // TODO: исправить на оригинальное условие ddLookupRecordHidden: !createAwailable,
            cls: 'un-relation-record-attribute un-dataentity-attribute-input un-dataentity-attribute-input-lookup',
            fieldLabel: Unidata.i18n.t('glossary:relation'),
            displayAttributes: metaRelation.get('toEntityDefaultDisplayAttributes'),
            useAttributeNameForDisplay: metaRelation.get('useAttributeNameForDisplay'),
            entityType: 'entity',
            entityName: metaRelation.get('toEntity'),
            publishes: ['rawValue'],
            codeValue: etalonIdTo,
            value: etalonIdTo,
            readOnly: !dataRelation.phantom,
            flex: 1
        });

        picker.on('loadmetarecord', function (picker, metaRecord) {
            entityTextfield.setValue(metaRecord.get('displayName'));
        }, this);

        cfg = [
            {
                xtype: 'container',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
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
                                readOnly: readOnly,
                                cls: 'un-dataentity-attribute-input un-dataentity-attribute-input-date',
                                listeners: {
                                    change: this.onValidityPeriodStartChange.bind(this)
                                }
                            },
                            end: {
                                labelWidth: 20,
                                maxWidth: 200,
                                value: validTo,
                                readOnly: readOnly,
                                cls: 'un-dataentity-attribute-input un-dataentity-attribute-input-date',
                                listeners: {
                                    change: this.onValidityPeriodEndChange.bind(this)
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
            dataRelation;

        if (!etalonId) {
            return false;
        }

        dataRelation = view.getDataRelation();
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
    },

    onValidityPeriodStartChange: function () {
        var view = this.getView(),
            dataRelation = view.getDataRelation();

        // для новых связей сохраняем значения в модель
        if (dataRelation && dataRelation.phantom) {
            dataRelation.set('validFrom', view.fromDate.getValue());
        }
    },

    onValidityPeriodEndChange: function () {
        var view = this.getView(),
            dataRelation = view.getDataRelation();

        // для новых связей сохраняем значения в модель
        if (dataRelation && dataRelation.phantom) {
            dataRelation.set('validTo', view.toDate.getValue());
        }
    },

    /**
     * Обработка клика по кнопке удаления записи
     *
     * @param button
     * @param e
     */
    onRemoveRelationClick: function (button, e) {
        var view = this.getView();

        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.preventDefault();
        e.stopPropagation();

        if (view.fireEvent('beforeremovem2m', view) != false) {
            this.removeRelation();
        }
    },

    /**
     * Удаление инстанса связи
     */
    removeRelation: function () {
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
                        model: 'Unidata.model.data.RelationsTo',
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
    }
});
