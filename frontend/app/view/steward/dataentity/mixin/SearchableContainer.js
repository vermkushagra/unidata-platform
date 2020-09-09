Ext.define('Unidata.view.steward.dataentity.mixin.SearchableContainer', {

    extend: 'Ext.Mixin',

    requires: [
        'Unidata.view.steward.dataentity.mixin.SearchableComponent'
    ],

    search: function (text) {

        var result = [];

        filterInItems(this.items, text);

        function filterInItems (items, text) {

            if (!items) {
                return;
            }

            items.each(function (item) {

                switch (true) {
                    case Unidata.view.steward.dataentity.mixin.SearchableContainer.isSearchableContainer(item):
                        result.push.apply(result, item.search(text));
                        break;
                    case Unidata.view.steward.dataentity.mixin.SearchableComponent.isSearchableComponent(item):
                        if (item.search(text)) {
                            result.push(item);
                        }
                        break;
                    case item instanceof Ext.container.Container:
                        filterInItems(item.items, text);
                        break;
                }

            });

        }

        return result;

    }
}, function () {

    var key = 'isSearchableContainer' + Ext.timestamp();

    /**
     * Проверяет, применён ли mixin для контейнера
     * @param obj
     * @returns {*}
     */
    this.isSearchableContainer = function (obj) {
        return obj[key];
    };

    this.prototype[key] = true;

});
