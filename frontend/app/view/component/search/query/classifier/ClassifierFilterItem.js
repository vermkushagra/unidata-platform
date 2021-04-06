/**
 * Панель оборачивающая компонент для выбора узла классификатора + панель фильтрации атрибутов
 *
 * @author Ivan Marshalkin
 * @date 2016-12-01
 */

Ext.define('Unidata.view.component.search.query.classifier.ClassifierFilterItem', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.search.query.classifier.ClassifierFilterItemController',
        'Unidata.view.component.search.query.classifier.ClassifierFilterItemModel',

        'Unidata.view.component.ClassifierNodePicker'
    ],

    alias: 'widget.component.search.query.classifieritem',

    controller: 'component.search.query.classifieritem',
    viewModel: {
        type: 'component.search.query.classifieritem'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-classifier-filter-item',

    referenceHolder: true,
    eventBusHolder: true,

    methodMapper: [
        {
            method: 'getFilters'
        },
        {
            method: 'isEmptyFilter'
        },
        {
            method: 'setAllowedClassifiers'
        },
        {
            method: 'getClassifierNode'
        },
        {
            method: 'excludeField'
        },
        {
            method: 'setDisabled'
        }
    ],

    classifierNodePicker: null,
    classifierFilterPanel: null,

    config: {
        allowedEntities: null
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    onDestroy: function () {
        var me = this;

        me.classifierNodePicker  = null;
        me.classifierFilterPanel = null;

        me.callParent(arguments);
    },

    initComponentReference: function () {
        var me = this;

        me.classifierNodePicker  = me.lookupReference('classifierNodePicker');
        me.classifierFilterPanel = me.lookupReference('classifierFilterPanel');
    },

    items: [],

    initItems: function () {
        this.callParent(arguments);

        this.add([
            {
                xtype: 'un.classifiernodepicker',
                ui: 'un-field-default',
                publishes: ['value'],
                cls: 'un-classifier-nodepicker',
                reference: 'classifierNodePicker'
            },
            {
                xtype: 'component.search.classifierattribute.classifierfilterpanel',
                hidden: true,
                bind: {
                    hidden: '{!classifierNodePicker.value}'
                },
                listeners: {
                    collapse: function () {
                        this.lookupReference('classifierFilterMargin').hide();
                    },
                    expand: function () {
                        this.lookupReference('classifierFilterMargin').show();
                    },
                    scope: this
                },
                reference: 'classifierFilterPanel'
            },
            {
                xtype: 'container',
                cls: 'un-classifier-margin',
                reference: 'classifierFilterMargin',
                hidden: true
            }
        ]);
    }

});
