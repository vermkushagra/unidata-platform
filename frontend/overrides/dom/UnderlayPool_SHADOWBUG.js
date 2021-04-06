/**
 * Ошибочное использование тени
 *
 * Смотри баг UN-2452
 *
 * Обсуждение ошибки на форуме sencha
 *
 * https://www.sencha.com/forum/showthread.php?298885-Ext.dom.Element.setStyle-Cannot-read-property-style-of-null
 */

Ext.define('Ext.overrides.dom.UnderlayPool_SHADOWBUG', {
    override: 'Ext.dom.UnderlayPool',

    /**
     * Override to check if el is destroyed
     */

    /**
     * КОММЕНТАРИЙ АВТОРА
     *
     * We ran into a similar issue in our application using 5.1.1. I unfortunately don't have a working test case
     * either since I don't know exactly what part of the application causes it. I do know though that for us it is
     * directly related to Ext.useShims. When set to false the issue does not come up. If I had to guess it is because
     * of a timing issue which is why it seems to be difficult to reproduce.
     *
     * It seems to have to do with Ext.dom.UnderlayPool. In the case when the error happens the UnderlayPool
     * cache contains an el that is already destroyed (isDestroyed is true and dom is undefined).
     * When the checkOut method is called the destroyed el is returned and when el.dom.style is accessed it
     * causes the error. When the error came up the cache did contain a second el that was not destroyed.
     * I don't know if that second el is the correct el or if a new el should be created. I'm currently testing
     * with the below override and it seems to fix the issue. I don't know what kind of side effects this may have
     * though so use at your own risk. It's possible that instead of shifting again the if statement that checks if
     * el exists should also check if that el is destroyed which would then create a new el.
     */
    checkOut: function () {
        var el = this.cache.shift();

        // If el is destroyed shift again
        if (el && el.isDestroyed) {
            el = this.cache.shift();
        }

        if (!el) {
            el = Ext.Element.create(this.elementConfig);
            el.setVisibilityMode(2);

            el.dom.setAttribute('data-sticky', true);
        }

        return el;
    }
});
