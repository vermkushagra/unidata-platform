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

        referenceData = new Unidata.model.data.RelationsTo({
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
            hideCreateTrigger = false,
            preserveDropdown;

        cfg = cfg || {};
        preserveDropdown = cfg.preserveDropdown;
        preserveDropdown = Ext.isBoolean(preserveDropdown) ? preserveDropdown : false;

        if (!preserveDropdown) {
            view.remove(this.entityTextfield);
            view.remove(me.input);
        }
        view.remove(me.dataEntity);

        // скрываем тригер создания новой связанной записи
        // для новой записи кнопка должна быть активной смотри UN-2217
        if (readOnly || (dataRecord.get('status') !== 'ACTIVE' && !dataRecord.phantom)) {
            hideCreateTrigger = true;
        }

        // значение null в этом случае необходимо чтоб ddpickerfield не делал запросы на сервер
        // см UN-2219
        if (Ext.isEmpty(etalonIdTo)) {
            etalonIdTo = null;
        }

        view.setTitle(Ext.String.htmlEncode(referenceMeta.get('displayName')));

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

        if (!preserveDropdown) {
            me.input = Ext.widget({
                xtype: 'dropdownpickerfield',
                entityName: referenceMeta.get('toEntity'),
                entityType: 'entity',
                displayAttributes: referenceMeta.get('toEntityDefaultDisplayAttributes'),
                fieldLabel: Unidata.i18n.t('glossary:relation'),
                useAttributeNameForDisplay: referenceMeta.get('useAttributeNameForDisplay'),
                labelSeparator: '',
                labelWidth: 110,
                value: etalonIdTo,
                codeValue: etalonIdTo,
                openLookupRecordHidden: false,
                addLookupRecordHidden: hideCreateTrigger,
                readOnly: readOnly,
                cls: 'un-relation-record-attribute un-dataentity-attribute-input un-dataentity-attribute-input-lookup'
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

    isChanged: function () {
        return !this.input.isInitialValue();
    },

    checkDirty: function () {
        return Boolean(this.getCurrentEtalonId()) && this.dataEntity.getDataRecord().checkDirty();
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
