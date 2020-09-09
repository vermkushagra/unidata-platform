/**
 * Класс реализует представление одной исходной записи для карточки "Исходные записи"
 *
 * @author Sergey Shishigin
 * @date 2016-03-28
 */

Ext.define('Unidata.view.steward.dataviewer.card.origin.OriginItem', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.AttributeViewMode'
    ],

    alias: 'widget.steward.dataviewer.originitem',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        originId: null,
        detachAllowed: false,
        metaRecord: null,
        originDataRecord: null
    },

    scrollable: true,

    originId: null, // хринит originId записи для нужд QA отдела

    dataEntity: null,

    referenceHolder: true,

    dataEntity: null,
    systemAttributeEntity: null,
    detachOriginButton: null,

    collapsible: false,
    header: false,

    cls: 'un-compare',
    ui: 'un-card',

    items: [
        {
            xtype: 'container',
            margin: '0 0 10 0',
            layout: {
                type: 'hbox',
                align: 'stretch',
                pack: 'end'
            },
            items: [
                {
                    xtype: 'button',
                    reference: 'detachOriginButton',
                    ui: 'un-toolbar-admin',
                    scale: 'small',
                    iconCls: 'icon-undo2',
                    hidden: true
                }
            ]
        },
        {
            xtype: 'dataentity',
            reference: 'dataEntity',
            attributeViewMode: Unidata.AttributeViewMode.COMPARE,
            useCarousel: false,
            readOnly: true,
            preventMarkField: true,
            showEmptyClassifierAttributeTablet: true,
            showClassifierAttributeGroup: true,
            flex: 1,
            noWrapTitle: true
        },
        {
            xtype: 'dataentity',
            reference: 'systemAttributeEntity',
            attributeViewMode: Unidata.AttributeViewMode.COMPARE,
            useCarousel: false,
            readOnly: true,
            flex: 1,
            depth: 1,
            noWrapTitle: true
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.dataEntity = this.lookupReference('dataEntity');

        this.dataEntity = this.lookupReference('dataEntity');
        this.systemAttributeEntity = this.lookupReference('systemAttributeEntity');
        this.detachOriginButton = this.lookupReference('detachOriginButton');

        this.initComponentEvent();
    },

    initComponentEvent: function () {
        this.detachOriginButton.on('click', this.onDetachOriginButtonClick, this);
        this.on('afterrender', this.onComponentAfterRender, this);
    },

    onDestroy: function () {
        this.dataEntity = null;
        this.systemAttributeEntity = null;
        this.detachOriginButton = null;

        this.callParent(arguments);
    },

    onComponentAfterRender: function () {
        var metaRecord = this.getMetaRecord(),
            userHasRightsToDetach;

        userHasRightsToDetach = Unidata.Config.userHasRights(metaRecord.get('name'), ['create', 'update']);

        if (this.getDetachAllowed() && userHasRightsToDetach) {
            this.detachOriginButton.setVisible(true);
        }
    },

    onDetachOriginButtonClick: function () {
        var dataRecord = this.getOriginDataRecord(),
            title = Unidata.i18n.t('dataviewer>originCard>detachOriginWndTitle'),
            msg = Unidata.i18n.t('dataviewer>originCard>detachOriginWndMsg');

        Unidata.showPrompt(title, msg, function () {
            this.fireEvent('detachorigin', this, dataRecord.get('originId'));
        }, this);
    },

    updateOriginDataRecord: function () {
        this.originId = this.getOriginId();
    },

    /**
     * Возвращает originId записи. Методы добавлены для QA отдела. Используются в автотестах
     *
     * добавлены по задаче UN-3928
     * @returns {*}
     */
    getOriginId: function () {
        var dataRecord = this.getOriginDataRecord(),
            originId = null;

        if (dataRecord) {
            originId = dataRecord.get('originId');
        }

        return originId;
    }

});
