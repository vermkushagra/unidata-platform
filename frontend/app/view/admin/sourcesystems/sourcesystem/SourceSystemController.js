Ext.define('Unidata.view.admin.sourcesystems.sourcesystem.SourceSystemController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.sourcesystems.sourcesystem',

    onSaveRecord: function () {
        var me = this,
            view = this.getView(),
            draftMode = view.getDraftMode(),
            viewModel = this.getViewModel(),
            record = viewModel.get('currentRecord'),
            weight = record.get('weight'),
            checkWeightUnique = Unidata.Config.getCheckSourceSystemWeightUnique();

        if (!record.isValid()) {
            Unidata.showError(Unidata.i18n.t('admin.sourcesystems>incorrectFields'));

            return;
        }

        if (checkWeightUnique && !this.weightUnique()) {
            Unidata.showError(Unidata.i18n.t('admin.sourcesystems>weightUsedInAnotherSourceSystem'));

            return;
        }

        if (!Ext.isNumber(weight) || weight > 100) {
            Unidata.showError(Unidata.i18n.t('admin.sourcesystems>incorrectWeight'));

            return;
        }

        record.setId(record.get('name'));

        if (record.modified !== null && record.modified.name) {
            record.setId(record.modified.name);
        }

        record.save({
            params: {
                draft: draftMode
            },
            success: function (r) {
                var view        = this.getView(),
                    entityPanel = view.lookupReference('sourceSystemMainPanel').lookupReference('entityPanel');

                if (entityPanel) {
                    entityPanel.setTitle(r.get('name'));
                }

                me.fireViewEvent('recordsave', this.getView());

                this.showMessage(Unidata.i18n.t('admin.common>dataSaveSuccess'));
            },
            scope: this
        });
    },

    weightUnique: function () {
        var view             = this.getView(),
            viewModel        = this.getViewModel(),
            sourceSystemList = view.sourceSystemList,
            record           = viewModel.get('currentRecord'),
            weight           = record.get('weight'),
            weightUnique     = true;

        // ноль не проверяем на дублирование
        if (weight === 0) {
            return true;
        }

        sourceSystemList.each(function (sourceSystem) {
            if (sourceSystem.get('name') !== record.get('name') && sourceSystem.get('weight') === weight) {
                weightUnique = false;
            }
        });

        return weightUnique;
    },

    onDeleteConfirmClick: function (btn) {
        var record = this.getViewModel().get('currentRecord'),
            msgBox = Ext.window.MessageBox.create({});

        record.setId(record.data.name);

        if (record.modified !== null && record.modified.name) {
            record.setId(record.modified.name);
        }

        msgBox.show({
            title: Unidata.i18n.t('admin.sourcesystems>removeSourceSystem'),
            message: Unidata.i18n.t('admin.sourcesystems>confirmRemoveSourceSystem'),
            buttons: Ext.MessageBox.YESNO,
            buttonText: {
                yes: Unidata.i18n.t('common:yes'),
                no: Unidata.i18n.t('common:no')
            },
            scope: this,
            animateTarget: btn,
            defaultFocus: 3,
            fn: function (btn) {
                if (btn === 'yes') {
                    this.deleteRecord();
                }
            }
        });
    },

    deleteRecord: function () {
        var record = this.getViewModel().get('currentRecord'),
            me = this;

        function clean () {
            me.fireViewEvent('recorddelete', this.getView());
            this.getView().close();
        }

        if (!record.phantom) {
            record.erase({
                success: function (r) {
                    clean.call(this, r);
                },
                scope: this
            });
        } else {
            clean.call(this, record);
        }

    },

    onAfterRender: function () {
        var name, icon;

        name = this.getViewModel().get('currentRecord').get('name');
        icon = Unidata.model.sourcesystem.SourceSystem.createTypeIcon();

        this.getView().lookupReference('sourceSystemMainPanel').add(
            {
                xtype: 'component.dashboard.entity',
                width: '70%',
                title: icon + ' ' + name,
                reference: 'entityPanel',
                hiddenStats: ['duplicatesCount', 'clustersCount', 'errorsCount', 'mergedCount'],
                viewModel: {
                    data: {
                        paramName: 'sourceSystemName',
                        paramDisplayName: 'sourceSystemDisplayName',
                        displayButtons: false,
                        paramValue: name,
                        paramDisplayValue: name,
                        typeIcon: icon
                    }
                },
                bind: {
                    hidden: '{isPhantom}'
                }
            }
        );

    },

    updateReadOnly: function (readOnly) {
        var view = this.getView(),
            viewModel = this.getViewModel();

        if (view.isDestroyed || view.destroying) {
            return;
        }

        viewModel.set('readOnly', readOnly);
    },

    updateDraftMode: function (draftMode) {
        var view = this.getView(),
            viewModel = this.getViewModel();

        if (view.isDestroyed || view.destroying) {
            return;
        }

        viewModel.set('draftMode', draftMode);
    }

});
