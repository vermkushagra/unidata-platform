/**
 * @author Ivan Marshalkin
 * @date 2017-10-09
 */

Ext.define('Unidata.view.main.menu.elements.ToggleDraftModeGroupItem', {

    extend: 'Unidata.view.main.menu.elements.GroupItem',

    alias: 'widget.un.list.item.mainmenu.item.togglegroup',

    iconCls: 'icon-toggle-on',
    hideable: false,

    tooltipText: null,

    initComponent: function () {
        this.callParent(arguments);

        this.syncIconCls();
    },

    onComponentRender: function () {
        this.callParent(arguments);

        this.elIconWrap.on('click', this.onIconWrapClick, this);

        this.syncIconCls();
    },

    toggleDraftMode: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        DraftModeNotifier.setDraftMode(!DraftModeNotifier.getDraftMode());
        this.syncIconCls();
    },

    syncIconCls: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier,
            draftMode = DraftModeNotifier.getDraftMode(),
            iconCls;

        if (draftMode) {
            iconCls = 'icon-toggle-on';
            this.tooltipText = Unidata.i18n.t('admin.metamodel>draftModeOnToolTooltip');
        } else {
            iconCls = 'icon-toggle-off';
            this.tooltipText = Unidata.i18n.t('admin.metamodel>draftModeOffToolTooltip');
        }

        this.setIconCls(iconCls);
    },

    onIconWrapClick: function (e) {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier,
            draftMode = DraftModeNotifier.getDraftMode(),
            selectedMenuItem = Unidata.view.main.menu.elements.InnerItem.getSelectedMenuItem(),
            prompt;

        e.stopEvent();

        // переключаться можно только с экранов доступных в режиме редактирования черновика
        if (!selectedMenuItem || !selectedMenuItem.allowedDraftMode) {
            Unidata.showWarning(Unidata.i18n.t('admin.metamodel>draftModeDisallowedAtCurrentScreen'));

            return;
        }

        if (!draftMode) {
            // в режим черновика переключаемся без запроса
            this.toggleDraftMode();
        } else {
            // переключение ИЗ режима черновика проивзводим только после разрешения пользователя
            prompt = Ext.create('Ext.window.MessageBox', {
                header: true
            });

            prompt.show({
                title: Unidata.i18n.t('common:confirmation'),
                message: Unidata.i18n.t('admin.metamodel>draftModeToggleToPublicPromptText'),
                scope: this,
                defaultFocus: 3,
                buttons: Ext.MessageBox.YESNO,
                buttonText: {
                    yes: Unidata.i18n.t('common:yes'),
                    no: Unidata.i18n.t('common:no')
                },
                fn: function (btn) {
                    if (btn === 'yes') {
                        this.toggleDraftMode();
                    }
                }
            });
        }
    },

    initToolTip: function () {
        this.elIconWrap.dom.addEventListener('mouseenter', this.onElIconWrapMouseEnter.bind(this));
        this.elIconWrap.dom.addEventListener('mouseleave', this.onElIconWrapBodyMouseLeave.bind(this));

        this.elIconWrap.on('click', this.hideTooltip, this);
    },

    onElIconWrapMouseEnter: function (event) {
        var tooltipCfg;

        // если текст умещается то тултип не отображаем
        if (this.elTextVisible()) {
            return false;
        }

        tooltipCfg = {
            text: this.tooltipText
        };

        this.showTooltipDelayed(event, this.elIconWrap, tooltipCfg);
    },

    onElIconWrapBodyMouseLeave: function (event) {
        this.hideTooltip();
    }
});
