/**
 * Панель сводной информации по классифицированным реестрам и их записям (controller)
 *
 * @author Sergey Shishigin
 * @date 2016-10-07
 */
Ext.define('Unidata.view.classifierviewer.entitystat.ClassifierEntityStatController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.classifierviewer.entitystat',

    loadAndShowClassifierEntities: function () {
        var view             = this.getView(),
            classifierName,
            classifierNode = view.getClassifierNode(),
            classifierNodeId = null,
            classifier       = view.getClassifier(),
            promise,
            cfg;

        if (classifier) {
            classifierName = classifier.get('name');
        }

        if (classifierNode) {
            classifierNodeId = classifierNode.get('id');
        }

        classifierNodeId = classifierNodeId !== 'root' ? classifierNodeId : null;

        cfg = {
            classifierName: classifierName,
            classifierNodeId: classifierNodeId
        };

        promise = Unidata.util.api.Classifier.getClassifierEntityStats(cfg);
        promise
            .then(this.onGetClassifierEntitiesNodeFulfilled.bind(this),
                this.onGetClassifierEntitiesNodeRejected.bind(this))
            .done();
    },

    onGetClassifierEntitiesNodeFulfilled: function (classifierEntityStats) {
        this.showClassifierEntityPanels(classifierEntityStats);
    },

    showClassifierEntityPanels: function (classifierEntityStats) {
        var view = this.getView(),
            classifierEntityPanels;

        classifierEntityStats = Ext.Array.filter(classifierEntityStats, this.filterClassifierEntityStatsByPermission, this);
        classifierEntityPanels = Ext.Array.map(classifierEntityStats, this.buildClassifierEntityPanel, this);

        if (classifierEntityPanels.length > 0) {
            // Создаем панели и присваиваем вычисленные reference (необходимо для автотестов)
            Ext.Array.each(classifierEntityPanels, function (classifierEntityPanel) {
                var reference,
                    entityStat;

                entityStat = classifierEntityPanel.getEntityStat();
                reference = this.buildClassifierEntityPanelReference(entityStat);
                classifierEntityPanel = view.add(classifierEntityPanel);
                classifierEntityPanel.setReference(reference);
            }, this);
        } else {
            this.addEmptyPanel();
        }
    },

    addEmptyPanel: function () {
        var view = this.getView();

        // TODO: Использовать нормальный компонент
        view.add({
            xtype: 'container',
            //cls: 'un-card un-card-empty',
            html: Unidata.i18n.t('classifier>classifiedRecordsNotExists')
        });
    },

    buildClassifierEntityPanel: function (entityStat) {
        var panel,
            view = this.getView(),
            classifierNode = view.getClassifierNode();

        panel = Ext.create('Unidata.view.classifierviewer.entitystat.entitypanel.ClassifierEntityPanel', {
            entityStat: entityStat,
            classifierNode: classifierNode
        });

        return panel;
    },

    /**
     * Строим уникальный reference для панелей с таблицами записей (необходимо для сценариев автотестов)
     * @param {Unidata.model.classifier.ClassifierEntityStat} entityStat
     * @returns {String}
     */
    buildClassifierEntityPanelReference: function (entityStat) {
        var tpl = 'panel{0}{1}',
            reference,
            name,
            type;

        name = entityStat.get('name');
        type = entityStat.get('type');

        reference = Ext.String.format(tpl,
                                    Ext.String.capitalize(name.toLowerCase()),
                                    Ext.String.capitalize(type.toLowerCase()));

        return reference;
    },

    onGetClassifierEntitiesNodeRejected: function () {
        //TODO: implement me
    },

    filterClassifierEntityStatsByCount: function (classifierEntityStat) {
        var count = 0;

        if (!classifierEntityStat) {
            return false;
        }

        count = classifierEntityStat.get('count');

        return count > 0;
    },

    filterClassifierEntityStatsByPermission: function (classifierEntityStat) {
        var hasPermission = false,
            entityName = classifierEntityStat.get('name');

        hasPermission = Unidata.Config.userHasRight(entityName, 'read');

        return hasPermission;
    }
});
