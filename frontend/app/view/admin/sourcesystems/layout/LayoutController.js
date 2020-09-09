Ext.define('Unidata.view.admin.sourcesystems.layout.LayoutController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.sourcesystems.layout',

    control: {
        'admin\\.sourcesystems\\.resultset grid': {
            'itemclick': 'onItemResultClick'
        }
    },

    init: function () {
        this.recreateSourceSystemsInfo();
    },

    recreateSourceSystemsInfo: function () {
        var view = this.getView(),
            sourceSystemsInfo = Unidata.model.sourcesystem.SourceSystemsInfo.create(),
            draftMode = view.getDraftMode();

        this.getViewModel().set('sourceSystemsInfo', sourceSystemsInfo);
        this.getViewModel().get('sourceSystemsInfo').load({
            params: {
                draft: draftMode
            }
        });

        return this.getViewModel().get('sourceSystemsInfo');
    },

    onAddRecord: function (view, record) {
        this.createRecordTabFromRecord(record);
    },

    onItemResultClick: function (component, record) {
        this.createRecordTabFromRecord(record);
    },

    createRecordTabFromRecord: function (record) {
        var view = this.getView(),
            tabPanel,
            resultsetGrid,
            viewClassName,
            modelClassName;

        tabPanel       = view.recordshowTabPanel;
        resultsetGrid  = view.resultsetPanel.lookupReference('resultsetGrid');
        viewClassName  = 'Unidata.view.admin.sourcesystems.sourcesystem.SourceSystem';
        modelClassName = 'Unidata.model.sourcesystem.SourceSystem';

        this.createRecordTab(tabPanel, resultsetGrid, record.get('name'), viewClassName, modelClassName);
    },

    refreshResultset: function () {
        this.recreateSourceSystemsInfo();
    },

    createRecordTab: function (tabPanel, resultsetGrid, name, viewClassName, modelClassName) {
        var me = this,
            view = this.getView(),
            viewModel = this.getViewModel(),
            draftMode = view.getDraftMode(),
            tabComponent,
            record;

        function findRecordTab () {
            var tabComponent = tabPanel.items.findBy(function (item) {
                var record = item.getViewModel().get('currentRecord');

                return record !== null && record.get('name') === name;
            });

            return tabComponent;
        }

        function applyCurrentRecord () {
            tabComponent.getViewModel().set('currentRecord', record);
            tabComponent.getViewModel().set('adminSystemName', me.getViewModel().get('adminSystemName'));
            tabPanel.add(tabComponent);

            if (!record.phantom) {
                tabComponent.getViewModel().set('currentResultsetRecord', resultsetGrid.getSelection()[0]);
            }
        }

        if ((tabComponent = findRecordTab()) === null) {
            tabComponent = Ext.create(viewClassName, {
                draftMode: draftMode,
                readOnly: draftMode ? false : true,
                sourceSystemList: viewModel.get('resultsetStore'),
                listeners: {
                    recordsave: function () {
                        me.refreshResultset();
                    },
                    recorddelete: function () {
                        me.refreshResultset();
                    }
                }
            });
            record = Ext.create(modelClassName);

            if (name !== null && name !== '') {
                record.setId(name);
                record.load({
                    params: {
                        draft: draftMode
                    },
                    success: function () {
                        applyCurrentRecord();
                    }
                });
            } else {
                applyCurrentRecord();
            }
        } else {
            tabPanel.setActiveTab(tabComponent);
        }
    },

    updateDraftMode: function (draftMode) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            readOnly = draftMode ? false : true;

        viewModel.set('draftMode', draftMode);

        view.resultsetPanel.setReadOnly(readOnly);

        this.recreateSourceSystemsInfo();
    }
});
