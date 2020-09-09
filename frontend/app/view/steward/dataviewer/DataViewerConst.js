/**
 * Константы используемые вьювером
 *
 * @author Ivan Marshalkin
 * @date 2016-03-25
 */

Ext.define('Unidata.view.steward.dataviewer.DataViewerConst', {
    singleton: true,

    //примерные константы статусов вьювера (просьба пока не использовать до понимания когда и в каком объеме использовать)
    VIEWER_STATUS_DONE: 'done',                               // готов к взоимодействию с пользователем
    VIEWER_STATUS_LOADING: 'loading',                         // загружает данные
    VIEWER_STATUS_ORIGINLOADING: 'originloading',             // загружает данные
    VIEWER_STATUS_HISTORYLOADING: 'historyloading',           // загружает данные
    VIEWER_STATUS_SAVE: 'save',                               // сохраняет
    VIEWER_STATUS_APPROVE: 'approve',                         // в процессе подтверждения не согласованных изменений
    VIEWER_STATUS_DECLINE: 'decline',                         // в процессе отклонения не согласованных изменений
    VIEWER_STATUS_DATACARDLOCKED: 'datacardlocked',           // карточка с данными заблокирована
    VIEWER_STATUS_DATACARDUNLOCKED: 'datacardunlocked',       // карточка с данными разблокирована
    VIEWER_STATUS_RESTORE: 'restore',                         // карточка с данными в процессе восстановления
    VIEWER_STATUS_FAILED: 'failed',                           // карточка с данными в процессе восстановления

    // константы вкладок
    DATA_CARD: 'data',                                        // данные записи
    HISTORY_CARD: 'history',                                  // история записи
    BACKREL_CARD: 'backrelation',                             // обратные ссылки
    ORIGIN_CARD: 'origin',                                    // исходные записи

    // константы статусов записи
    ETALON_STATUS_ACTIVE: 'ACTIVE',                           // активен
    ETALON_STATUS_INACTIVE: 'INACTIVE',                       // неактивен
    ETALON_STATUS_RESTORE: 'RESTORE',                         // на восстановлении

    // константы состояния признака записи согласовано / на согласовании
    ETALON_APPROVAL_APPROVED: 'APPROVED',                     // согласовано
    ETALON_APPROVAL_PENDING: 'PENDING'                        // на согласовании
});
