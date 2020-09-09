/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2017-05-10
 */

Ext.define('Unidata.view.steward.relation.m2m.carousel.M2mCarouselController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.relation.m2mcarousel',

    /**
     * Обновляет состояние "только для чтения"
     *
     * @param readOnly
     */
    updateReadOnly: function (readOnly) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            carousel = view.carousel,
            items;

        viewModel.set('readOnly', readOnly);

        if (carousel) {
            items = carousel.getCarouselItems();
        }

        if (!items) {
            return;
        }

        items.each(function (item) {
            item.setReadOnly(readOnly);
        });
    },

    /**
     * Отображает связи
     */
    displayRelations: function () {
        var me = this,
            view = this.getView(),
            dataRecord = view.getDataRecord(),
            metaRecord = view.getMetaRecord(),
            dataRelation = view.getDataRelation(),
            metaRelation = view.getMetaRelation(),
            readOnly = view.getReadOnly(),
            carousel = view.carousel,
            expandedCarouselItems = [];

        // список развернутых рекордов
        carousel.getCarouselItems().each(function (carouselItem) {
            if (!carouselItem.collapsed) {
                expandedCarouselItems.push(carouselItem.getDataRelation().getId());
            }
        });

        carousel.removeAllCarouselItems();

        Ext.Array.each(dataRelation, function (dataRelationItem) {
            var panel,
                panelCfg;

            // восстанавливаем состояние развенуто / схлопнуто
            panelCfg = {
                collapsed: !Ext.Array.contains(expandedCarouselItems, dataRelationItem.getId())
            };

            panel = me.createRecordPanel(metaRecord, dataRecord, metaRelation, dataRelationItem, readOnly, panelCfg);
            panel.displayRelationRecord();

            carousel.addCarouselItem(panel);
        });
    },

    /**
     * Создание новой связи
     *
     * @param metaRecord
     * @param dataRecord
     * @param metaRelation
     * @param dataRelation
     * @param readOnly
     * @returns {Unidata.view.steward.relation.m2m.edit.M2mRecord|*}
     */
    createRecordPanel: function (metaRecord, dataRecord, metaRelation, dataRelation, readOnly, panelCfg) {
        var view = this.getView(),
            saveAtomic = view.getSaveAtomic(),
            panel,
            cfg;

        panelCfg = Ext.isObject(panelCfg) ? panelCfg : {};

        cfg = {
            metaRecord: metaRecord,
            dataRecord: dataRecord,
            metaRelation: metaRelation,
            dataRelation: dataRelation,
            readOnly: readOnly,
            flex: 1,
            saveAtomic: saveAtomic
        };

        cfg = Ext.apply(cfg, panelCfg);

        panel = Ext.create('Unidata.view.steward.relation.m2m.edit.M2mRecord', cfg);

        // настраиваем обработчики событий
        panel.on('beforeremovem2m', this.onBeforeRemoveM2m, this);
        panel.on('removem2m', this.onRemoveM2m, this);
        panel.on('changerelto', this.onChangeRelationToM2m, this);
        panel.on('replacedatarelation', this.onReplaceDataRelationM2m, this);
        panel.on('m2mrecordvaliditychange', this.onM2mRecordValidityChange, this);

        view.relayEvents(panel, [
            'm2mdirtychange',
            'timeintervalcheckstart',
            'timeintervalcheckstop'
        ]);

        return panel;
    },

    /**
     * Обработчик события beforeremovem2m. В данном обработчике можно отменить удаление записи-связи
     *
     * @param panel
     */
    onBeforeRemoveM2m: function () {
    },

    /**
     * Обработчик события removem2m
     *
     * @param panel
     */
    onRemoveM2m: function (panel, dataRelationRecord) {
        var view = this.getView(),
            dataRelation = view.getDataRelation(),
            carousel = view.carousel;

        // удаляем элемент из массива
        Ext.Array.remove(dataRelation, dataRelationRecord);

        carousel.removeCarouselItem(panel);

        view.fireEvent('changerelationcount');
        view.fireEvent('removem2m', view, dataRelationRecord);
    },

    /**
     * Обработчик события changerelto (смена связанной записи)
     *
     * @param panel
     */
    onChangeRelationToM2m: function (panel, etalonId) {
        var view = this.getView(),
            carousel = view.carousel,
            canChange = true,
            items;

        if (carousel) {
            items = carousel.getCarouselItems();

            // кодовое значение можно указыть только для одной записи
            items.each(function (item) {
                if (item !== panel && item.pickerField.getEtalonId() === etalonId) {
                    canChange = false;
                }
            });
        }

        return canChange;
    },

    /**
     * Обработчик подмены отображаемого dataRelation
     *
     * @param newDataRelation
     * @param oldDataRelation
     */
    onReplaceDataRelationM2m: function (newDataRelation, oldDataRelation) {
        var view = this.getView(),
            dataRelation = view.getDataRelation();

        // удаляем элемент из массива
        Ext.Array.replace(dataRelation, Ext.Array.indexOf(dataRelation, oldDataRelation), 1, [newDataRelation]);
    },

    onM2mRecordValidityChange: function () {
        this.checkPanelsValid();
    },

    /**
     * Создает новую запись связи многие ко многим
     */
    createRelationTo: function (collapsed) {
        var view = this.getView(),
            dataRecord = view.getDataRecord(),
            metaRecord = view.getMetaRecord(),
            dataRelation = view.getDataRelation(),
            metaRelation = view.getMetaRelation(),
            readOnly = view.getReadOnly(),
            carousel = view.carousel,
            newRelationReference,
            panel,
            panelOpts;

        collapsed = collapsed ? true : false;

        panelOpts = {
            collapsed: collapsed
        };

        newRelationReference = Ext.create('Unidata.model.data.RelationReference', {
            relName: metaRelation.get('name')
        });

        dataRelation.push(newRelationReference);

        panel = this.createRecordPanel(metaRecord, dataRecord, metaRelation, newRelationReference, readOnly, panelOpts);
        panel.displayRelationRecord();

        carousel.addCarouselItem(panel);
        carousel.moveLast();

        view.fireEvent('changerelationcount');
    },

    getRelationReferences: function () {
        return this.getView().getDataRelation();
    },

    checkPanelsValid: function () {
        var view = this.getView(),
            carousel = view.carousel,
            valid = true,
            items;

        if (carousel) {
            items = carousel.getCarouselItems();

            // кодовое значение можно указыть только для одной записи
            valid = Ext.Array.reduce(items.getRange(), function (previous, item) {
                return item.checkValid() && previous;
            }, true);
        }

        this.fireEvent('m2mvaliditychange', valid);

        return valid;
    }
});
