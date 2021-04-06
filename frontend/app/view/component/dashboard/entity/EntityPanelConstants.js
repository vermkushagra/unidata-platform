/**
 * @author Aleksandr Bavin
 * @date 2017-06-23
 */
Ext.define('Unidata.view.component.dashboard.entity.EntityPanelConstants', {

    alternateClassName: [
        'Unidata.EntityPanelConstants'
    ],

    singleton: true,

    TYPE: {
        TOTAL: 'TOTAL',
        NEW: 'NEW',
        UPDATED: 'UPDATED',
        MERGED: 'MERGED',
        ERRORS: 'ERRORS',
        DUPLICATES: 'DUPLICATES'
        // CLUSTERS: 'CLUSTERS'
    },

    typeNameMap: {
        TOTAL: Unidata.i18n.t('dashboard>total'),
        NEW: Unidata.i18n.t('glossary:new'),
        UPDATED: Unidata.i18n.t('dashboard>updated'),
        DUPLICATES: Unidata.i18n.t('glossary:duplicates'),
        // CLUSTERS: Unidata.i18n.t('dashboard>duplicateGroups'),
        MERGED: Unidata.i18n.t('dashboard>merged'),
        ERRORS: Unidata.i18n.t('dashboard>withErrors')
    },

    typeColorMap: {
        TOTAL: '#967ADC', // #AC92EC
        NEW: '#8CC152', // #A0D468
        UPDATED: '#37BC9B', // #48CFAD
        MERGED: '#F6BB42', // #FFCE54
        ERRORS: '#E9573F', // #FC6E51
        DUPLICATES: '#3BAFDA' // #4FC1E9
        // CLUSTERS: '#4A89DC' // #5D9CEC
    },

    ERROR_SEVERITY: {
        CRITICAL: 'CRITICAL',
        HIGH: 'HIGH',
        NORMAL: 'NORMAL',
        LOW: 'LOW'
    },

    severityColorMap: {
        CRITICAL: '#FF0000',
        HIGH: '#FF6600',
        NORMAL: '#FF9933',
        LOW: '#FFCC99'
    },

    severityChartIndexMap: {
        CRITICAL: 0,
        HIGH: 1,
        NORMAL: 2,
        LOW: 3
    },

    errorSeverityNameMap: {
        CRITICAL: Unidata.i18n.t('dashboard>critical'),
        HIGH: Unidata.i18n.t('dashboard>high'),
        NORMAL: Unidata.i18n.t('dashboard>normal'),
        LOW: Unidata.i18n.t('dashboard>low')
    }

});
