/**
 * Плагин управляемой загрузки stores и управляемого рендеринга
 *
 * Плагин подключается к view-компоненту, у которого имеется viewModel и stores
 * Плагин ожидает загрузки stores, имена которых перечислены в массиве storeNames.
 * После загрузки всех stores вызывается callback с callbackArgs,
 * а также осуществляется перерисовка parentContainer, если параметр render = true
 * Если parentContainerRef не указан, то перерисовывается компонент целиком
 *
 * @author Sergey Shishigin
 * 2015-10-28
 */

Ext.define('Unidata.plugin.component.ManagedStoreLoader', {
    extend: 'Ext.AbstractPlugin',

    alias: 'plugin.managedstoreloader',

    callback: null,             // callback is called, when all stores with storeNames are loaded
    callbackArgs: null,         // callback args
    callbackScope: null,        // callback scope
    storeNames: null,           // names of store, waited for loading
    everyStoreIsLoaded: false,  // flag of all store loading
    render: true,               // flag of necessary of rendering
    parentContainerRef: null,   // parent container reference

    init: function (cmp) {
        var me = this,
            viewModel = cmp.getViewModel();

        this.setCmp(cmp);
        this.callbackScope = this.callbackScope || cmp;
        this.storeNames = this.storeNames ? this.storeNames : Object.keys(viewModel.config.stores);

        this.storeNames.forEach(function (storeName) {
            viewModel.getStore(storeName).on('load', me.onStoreLoad, me);
        });
    },

    onStoreLoad: function () {
        var cmp = this.getCmp(),
            viewModel = cmp.getViewModel();

        this.everyStoreIsLoaded = Ext.Array.every(this.storeNames, function (storeName) {
            return viewModel.getStore(storeName).isLoaded();
        });

        if (this.everyStoreIsLoaded && this.callback) {
            this.callback.apply(this.callbackScope, this.callbackArgs);

            if (this.render) {
                if (this.parentContainerRef) {
                    cmp.lookupReference(this.parentContainerRef).updateLayout();
                } else {
                    cmp.updateLayout();
                }
            }
        }
    },

    isEveryStoreIsLoaded: function () {
        return this.everyStoreIsLoaded;
    }
});
