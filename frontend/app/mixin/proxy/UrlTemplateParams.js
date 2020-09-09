/**
 * Миксин для прокси, который позволяет использовать url как темплейт
 * и подставлять в них значения
 *
 * @author Aleksandr Bavin
 * @date 2016-08-16
 */
Ext.define('Unidata.mixin.proxy.UrlTemplateParams', {

    extend: 'Ext.Mixin',

    baseUrl: '',

    config: {
        urlParams: {}
    },

    setUrlParam: function (name, value) {
        this.urlParams[name] = value;
    },

    setUrlParams: function (params, merge) {
        if (merge) {
            this.urlParams = Ext.Object.merge(this.urlParams, params);
        } else {
            this.urlParams = params;
        }
    },

    privates: {

        afterClassMixedIn: function (targetClass) {
            targetClass.addMember('buildUrl', function () {
                var url = this.callParent(arguments),
                    template = new Ext.XTemplate(url);

                return this.baseUrl + template.apply(this.urlParams);
            });
        }
    }

});
