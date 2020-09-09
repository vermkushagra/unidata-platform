/**
 * Оверрайд добавляет класс табам, на основе референса добавляемого в панельку элемента (для удобства тестирования)
 *
 * @author Aleksandr Bavin
 * @date 2017-07-19
 */
Ext.define('Unidata.overrides.tab.Bar', {

    override: 'Ext.tab.Bar',

    onAdd: function (tab) {
        var reference = tab.card ? tab.card.getReference() : null;

        if (reference) {
            tab.addCls('un-tab-' + reference);
            tab.setReference('untab' + reference);
        }

        this.callParent(arguments);
    }

});
