/**
 * Контроллер для контейнера, реализующего отображение связи типа reference
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-10
 */

Ext.define('Unidata.view.steward.relation.reference.ReferenceController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.relation.reference',

    dataEntity: null,
    entityTextfield: null,
    input: null,

    init: function () {

        var me            = this,
            view          = me.getView(),
            viewModel     = me.getViewModel(),
            referenceData = view.getReferenceData();

        if (!referenceData) {
            me.createNewRelation();
            view.expand();
        } else {
            me.setReferenceData(referenceData);
        }

        me.callParent(arguments);
        viewModel.bind('{referenceData}', function () {
            var dirty;

            dirty = this.checkDirty() || this.isChanged();
            view.fireEvent('attributedirtychange', dirty);
        }, this, {deep: true});
    },

    setReferenceData: function (referenceData, cfg) {
        Unidata.util.DataRecord.bindManyToOneAssociationListeners(referenceData);
        this.getViewModel().set('referenceData', referenceData);
        this.refresh(cfg);
    },

    createNewRelation: function (cfg) {

        var referenceMeta,
            referenceData;

        referenceMeta = this.getView().getReferenceMeta();

        referenceData = new Unidata.model.data.RelationReference({
            relName: referenceMeta.get('name')
        });

        this.setReferenceData(referenceData, cfg);
    },

    /**
     *
     * @param cfg
     * preserveDropdown - не пересоздавать dropdownpickerfield
     */
    refresh: function (cfg) {
        var me                = this,
            view              = me.getView(),
            viewModel         = this.getViewModel(),
            dataRecord        = view.getDataRecord(),
            referenceMeta     = view.getReferenceMeta(),
            referenceData     = viewModel.get('referenceData'),
            readOnly          = view.getReadOnly(),
            etalonIdTo        = referenceData.get('etalonIdTo'),
            required          = referenceMeta.get('required'),
            timeIntervalIntersectType,
            preserveDropdown;

        cfg = cfg || {};
        preserveDropdown = cfg.preserveDropdown;
        preserveDropdown = Ext.isBoolean(preserveDropdown) ? preserveDropdown : false;

        if (!preserveDropdown) {
            view.remove(this.entityTextfield);
            view.remove(me.input);
        }
        view.remove(me.dataEntity);

        // значение null в этом случае необходимо чтоб ddpickerfield не делал запросы на сервер
        // см UN-2219
        if (Ext.isEmpty(etalonIdTo)) {
            etalonIdTo = null;
        }

        this.refreshTitle();

        this.entityTextfield = Ext.widget({
            xtype: 'textfield',
            readOnly: true,
            flex: 1,
            fieldLabel: Unidata.i18n.t('glossary:entity'),
            labelWidth: 110,
            cls: 'un-relation-record-attribute un-dataentity-attribute-input un-dataentity-attribute-input-string'
        });

        me.dataEntity = Ext.create('Unidata.view.steward.dataentity.DataEntity', {
            metaRecord: referenceMeta,
            dataRecord: referenceData,
            readOnly:   readOnly
        });

        me.dataEntity.displayDataEntity();

        if (required) {
            timeIntervalIntersectType = Unidata.view.component.DropdownPickerField.timeIntervalIntersectType.FULL;
        } else {
            timeIntervalIntersectType = Unidata.view.component.DropdownPickerField.timeIntervalIntersectType.PARTIAL;
        }

        if (!preserveDropdown) {
            me.input = Ext.widget({
                xtype: 'dropdownpickerfield',
                entityName: referenceMeta.get('toEntity'),
                entityType: 'entity',
                emptyText: Unidata.i18n.t(
                    'common:defaultSelect',
                    {entity: Unidata.i18n.t('glossary:relation').toLowerCase()}
                ),
                displayAttributes: referenceMeta.get('toEntityDefaultDisplayAttributes'),
                searchAttributes: referenceMeta.get('toEntitySearchAttributes'),
                fieldLabel: Unidata.i18n.t('glossary:relation'),
                useAttributeNameForDisplay: referenceMeta.get('useAttributeNameForDisplay'),
                timeIntervalIntersectType: timeIntervalIntersectType,
                labelSeparator: '',
                labelWidth: 110,
                allowBlank: !required,
                validateBlank: true,
                msgTarget: 'under',
                value: etalonIdTo,
                codeValue: etalonIdTo,
                openLookupRecordHidden: false,
                findTriggerHidden: false,
                readOnly: readOnly,
                listeners: {
                    validitychange: this.onDropdownValidityChange.bind(this)
                },
                cls: 'un-relation-record-attribute un-dataentity-attribute-input un-dataentity-attribute-input-lookup',
                findCfgDelegate: function () {
                    var cfg;

                    cfg = {
                        validFrom: dataRecord.get('validFrom'),
                        validTo: dataRecord.get('validTo')
                    };

                    return cfg;
                },
                detailCfgDelegate: function () {
                    var cfg;

                    cfg = {
                        validFrom: dataRecord.get('validFrom'),
                        validTo: dataRecord.get('validTo')
                    };

                    return cfg;
                }
            });
        }

        view.relayEvents(me.input, ['valuechange']);
        me.input.on('valueclear', this.removeReference, this);

        me.input.on('loadmetarecord', function (picker, metaRecord) {
            this.entityTextfield.setValue(metaRecord.get('displayName'));
        }, this);

        if (!preserveDropdown) {
            view.add([
                this.entityTextfield,
                me.input
            ]);
        }

        view.add(me.dataEntity);
    },

    onDropdownValidityChange: function (self, valid) {
        var view = this.getView();

        view.setValid(valid);
    },

    buildTitle: function (displayName, required, valid) {
        var title,
            invalidIcon;

        title = Ext.String.htmlEncode(displayName);

        if (required) {
            title = title + '<span class="un-dataentity-relation-required">*</span>';
        }

        if (!valid) {
            invalidIcon = '<span class="un-dataentity-relation-notification-title"><object data-ref="notification-icon" class="un-dataentity-relation-notification-icon" type="image/svg+xml" data="resources/icons/icon-notification-circle.svg"></object></span>';
            title = invalidIcon + title;
        }

        return title;
    },

    refreshTitle: function () {
        var view          = this.getView(),
            referenceMeta = view.getReferenceMeta(),
            required      = referenceMeta.get('required'),
            displayName   = referenceMeta.get('displayName'),
            valid         = view.getValid(),
            title;

        title = this.buildTitle(displayName, required, valid);
        view.setTitle(title);
    },

    isChanged: function () {
        return !this.input.isInitialValue();
    },

    checkDirty: function () {
        return Boolean(this.getCurrentEtalonId()) && this.dataEntity.getDataRecord().checkDirty();
    },

    checkValid: function () {
        var view = this.getView(),
            valid;

        valid =  this.input.validate();

        if (this.input.value) {
            valid =  this.dataEntity.isFieldsValid();
        }

        view.setValid(valid);

        return valid;
    },

    updateValid: function () {
        this.refreshTitle();
    },

    needDoSaving: function () {
        return this.input.getEtalonId();
    },

    saveReferenceRelations: function () {

        var me = this,
            promise;

        if (!me.getViewModel().get('dropped')) {
            return me.commitDoSave();
        }

        // есть удалённые данные, сначала удаляем
        promise = me.commitDoRemove()

        // теперь сохраняем новые данные
        .then(function () {
            return me.commitDoSave();
        });

        return promise;
    },

    /**
     * Получить запись измененной relationReference
     *
     * @return {*}
     */
    getRelationReferenceToUpdate: function () {
        var view = this.getView(),
            viewModel            = this.getViewModel(),
            referenceData        = viewModel.get('referenceData'),
            dataRecord           = view.getDataRecord(),
            etalonId = this.input.getEtalonId();

        if (!etalonId) {
            return null;
        }

        referenceData.set('etalonIdTo', etalonId);
        referenceData.set('validFrom',  dataRecord.get('validFrom'));
        referenceData.set('validTo',    dataRecord.get('validTo'));

        return referenceData;
    },

    /**
     * Получить запись удаленной relationReference
     *
     * @return {Unidata.model.data.RelationReferenceDelete}
     */
    getRelationReferenceToDelete: function () {
        var viewModel = this.getViewModel(),
            droppedReferenceData = viewModel.get('droppedReferenceData'),
            relationReferenceDelete;

        if (droppedReferenceData) {
            relationReferenceDelete = Ext.create('Unidata.model.data.RelationReferenceDelete', {
                etalonId: droppedReferenceData.get('etalonId'),
                relName: droppedReferenceData.get('relName')
            });
        }

        return relationReferenceDelete;
    },

    commitDoSave: function () {

        var me                   = this,
            view                 = me.getView(),
            dataRecord           = view.getDataRecord(),
            viewModel            = me.getViewModel(),
            referenceData        = viewModel.get('referenceData'),
            RelationReferenceAPI = Unidata.util.api.RelationReference,
            result;

        if (!me.needDoSaving()) {
            return;
        }

        referenceData.set('etalonIdTo', me.input.getEtalonId());
        referenceData.set('validFrom',  dataRecord.get('validFrom'));
        referenceData.set('validTo',    dataRecord.get('validTo'));

        result = RelationReferenceAPI.save(referenceData, dataRecord.get('etalonId'));

        // тут ещё нужно сделать обработку ошибок

        return result;
    },

    commitDoRemove: function () {
        var me = this,
            viewModel = me.getViewModel(),
            referenceData = viewModel.get('droppedReferenceData'),
            RelationReferenceAPI = Unidata.util.api.RelationReference,
            result;

        result = RelationReferenceAPI.remove(referenceData);

        result.then(function () {
            viewModel.set({
                dropped: false,
                droppedReferenceData: null
            });
        });

        return result;
    },

    search: function (text) {

        var result = [];

        if (this.input.getValue().replace(/\s+/g, ' ').toLowerCase().indexOf(text) !== -1) {
            result.push(this.getView());
        }

        result.push.apply(result, this.dataEntity.search(text));

        return result;
    },

    removeReference: function () {
        var viewModel     = this.getViewModel(),
            referenceData = viewModel.get('referenceData');

        if (!referenceData.phantom) {
            viewModel.set({
                dropped: true,
                droppedReferenceData: viewModel.get('referenceData')
            });
        }

        this.createNewRelation({
            preserveDropdown: true
        });
        this.getView().fireEvent('valueremoved');
    },

    getCurrentEtalonId: function () {
        return this.input.getEtalonId();
    },

    /**
     * Возвращает etalonId связанной записи. Методы добавлены для QA отдела. Используются в автотестах
     *
     * добавлены по задаче UN-3928
     * @returns {*}
     */
    getRelationEtalonId: function () {
        var view = this.getView(),
            referenceData = view.getReferenceData(),
            etalonId      = null;

        if (referenceData) {
            etalonId = referenceData.get('etalonId');
        }

        return etalonId;
    }
});
