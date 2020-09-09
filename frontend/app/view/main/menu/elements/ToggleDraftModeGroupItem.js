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
            userDialog = Unidata.util.UserDialog,
            title = Unidata.i18n.t('common:confirmation'),
            yesHandler,
            deleteConfirmationText,
            dialogBody,
            viewConfig;

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
            // переключение ИЗ режима черновика производим только после разрешения пользователя
            yesHandler = function () {
                this.toggleDraftMode();
            };

            deleteConfirmationText = this.buildSwitchDraftModeConfirmationHtml();
            dialogBody = userDialog.buildDialogBodyHtml({
                iconHtml: Unidata.util.Icon.getLinearIcon('warning'),
                textHtml: deleteConfirmationText
            });

            viewConfig = {
                yesText: Unidata.i18n.t('admin.metamodel>draftModeToggleToPublicYesButtonText'),
                noText: Unidata.i18n.t('common:cancel_noun'),
                html: dialogBody,
                noBtnColor: 'transparent'
            };

            userDialog.showPrompt(title, null, yesHandler, this, null, null, null, null, viewConfig);
        }
    },

    /**
     * Возвращает html верстку для диалогового окна
     *
     * @returns {*}
     */
    buildSwitchDraftModeConfirmationHtml: function () {
        var tpl = new Ext.XTemplate('<div>{title} {paragraph}</div>'),
            html;

        html = tpl.apply({
            title: Unidata.util.UserDialog.buildInlineBoldHtml(Unidata.i18n.t('admin.metamodel>draftModeToggleToPublicPromptTitle')),
            paragraph: Unidata.util.UserDialog.buildBlockHtml(Unidata.i18n.t('admin.metamodel>draftModeToggleToPublicPromptText'))
        });

        return html;
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

    onElIconWrapBodyMouseLeave: function () {
        this.hideTooltip();
    }
});
