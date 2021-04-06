/**
 *
 * @author Igor Redkin
 */
Ext.define('Unidata.util.Session', {
    singleton: true,

    autoUpdateSessionInterval: null,
    autoUpdateSessionCount: 0,

    sessionInterval: null,
    screen: null,

    config: {
        tokenTTL: 0
    },

    init: function () {
        this.screen = Ext.create('widget.lock', {
            renderTo: Ext.getBody(),
            hidden: true
        });
    },

    /**
     * Завершить сессию после n секунд (метод добавлен для принудительного завершения сессии из консоли браузера)
     *
     * @param seconds
     */
    expireSessionAfter: function (seconds) {
        var now = Number(new Date());

        localStorage.setItem('ud-session', now);
        localStorage.setItem('ud-tokenttl', +seconds);
    },

    /**
     * Установить время жизни сессии в секундах
     *
     * @param tokenttl
     */
    setTokenTTL: function (tokenttl) {
        var now = Number(new Date());

        localStorage.setItem('ud-session', now);
        localStorage.setItem('ud-tokenttl', +tokenttl);

        if (tokenttl) {
            this.start();
        }
    },

    /**
     * Возвращает время жизни сесси в секундах
     *
     * @returns {number}
     */
    getTokenTTL: function () {
        return Number(localStorage.getItem('ud-tokenttl'));
    },

    /**
     * Возвращает timestamp последнего обновления сессии
     *
     * @returns {number}
     */
    getSession: function () {
        return Number(localStorage.getItem('ud-session'));
    },

    /**
     * Возвращает истину если сессия не истекла иначе возвращает ложь
     *
     * @returns {boolean}
     */
    sessionAlive: function () {
        var now = Number(new Date()),
            session = this.getSession(),
            ttl = this.getTokenTTL();

        if (session + 1000 * ttl > now) {
            return true;
        }

        return false;
    },

    /**
     * Обновить timestamp ctccbb
     */
    updateSession: function () {
        var now;

        if (!this.getSession()) { // не продлеваем сессию, если её нет
            return;
        }

        now = Number(new Date());

        localStorage.setItem('ud-session', now);
    },

    /**
     * Затирает данные по сессии
     */
    clearSessionData: function () {
        localStorage.setItem('ud-session', null);
        localStorage.setItem('ud-tokenttl', null);
    },

    /**
     * Обработчик переодической проверки сессии
     */
    tickSessionHandler: function () {
        var me = this,
            application = Unidata.getApplication();

        if (!me.sessionAlive()) {
            me.stop();

            me.clearSessionData();
            me.screen.show();

            Unidata.Config.setToken(null);

            // сессия пользователя истекла
            application.fireEvent('sessionexpire');
        }
    },

    /**
     * Запуск сессии
     */
    start: function () {
        this.screen.hide();

        clearInterval(this.sessionInterval);

        this.sessionInterval = setInterval(this.tickSessionHandler.bind(this), 1000);
    },

    /**
     * Остановка сессии
     */
    stop: function () {
        clearInterval(this.sessionInterval);
    },

    /**
     * Скрывает связанный экран
     */
    hideScreen: function () {
        if (this.screen) {
            this.screen.hide();
        }
    },

    /**
     * Обработчик переодического продления сессии
     */
    tickAutoUpdateSessionHandler: function () {
        this.updateSession();
    },

    /**
     * Запускает автоматическое продление сессии
     */
    autoUpdateSessionStart: function () {
        this.autoUpdateSessionCount++;

        if (this.autoUpdateSessionInterval) {
            return;
        }

        this.updateSession();

        this.autoUpdateSessionInterval = setInterval(this.tickAutoUpdateSessionHandler.bind(this), this.getTokenTTL() / 2 * 1000);
    },

    /**
     * Останавливает автоматическое продление сессии
     */
    autoUpdateSessionEnd: function () {
        this.autoUpdateSessionCount--;

        if (this.autoUpdateSessionCount < 0) {
            this.autoUpdateSessionCount = 0;
        }

        if (!this.autoUpdateSessionCount) {
            clearInterval(this.autoUpdateSessionInterval);

            this.autoUpdateSessionInterval = null;
        }
    }
});
