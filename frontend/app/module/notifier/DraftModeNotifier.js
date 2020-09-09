/**
 * Оповещатель переключения режима черновик / опубликованная версия метамодели
 *
 * @author Ivan Marshalkin
 * @date 2017-10-04
 */

Ext.define('Unidata.module.notifier.DraftModeNotifier', {
    extend: 'Unidata.module.notifier.Notifier',

    inheritableStatics: {
        eventBus: new Ext.util.Observable(),

        getMe: function () {
            return Unidata.module.notifier.DraftModeNotifier;
        }
    },

    statics: {
        types: {
            UNKNOWN: 1,
            DRAFTMODECHANGE: 2,
            APPLYDRAFT: 3,
            REMOVEDRAFT: 4
        },

        unknownEventName: 'unknown',
        changeDraftModeEventName: 'change',
        applyEventName: 'apply',
        removeEventName: 'remove',

        draftMode: false,

        setDraftMode: function (draftMode, eventData) {
            var me = Unidata.module.notifier.DraftModeNotifier;

            me.draftMode = draftMode;

            me.notify(me.types.DRAFTMODECHANGE, me.draftMode, eventData);
        },

        toggleDraftMode: function (eventData) {
            var me = Unidata.module.notifier.DraftModeNotifier;

            me.setDraftMode(!me.draftMode, eventData);
        },

        getDraftMode: function () {
            return Unidata.module.notifier.DraftModeNotifier.draftMode;
        },

        getEventNameByType: function (type) {
            var me = Unidata.module.notifier.DraftModeNotifier,
                eventName = me.types.unknownEventName;

            switch (type) {
                case me.types.DRAFTMODECHANGE:
                    eventName = me.changeDraftModeEventName;
                    break;
                case me.types.APPLYDRAFT:
                    eventName = me.applyEventName;
                    break;
                case me.types.REMOVEDRAFT:
                    eventName = me.removeEventName;
                    break;
            }

            return eventName;
        }
    }
});
