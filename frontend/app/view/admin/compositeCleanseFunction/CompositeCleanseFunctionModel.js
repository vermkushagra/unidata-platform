Ext.define('Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunctionModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.compositeCleanseFunction',

    data: {
        cleanseFunctionName: '',
        currentRecord: null
    },
    stores: {
        cleanseGroupsStore: {
            model: 'cleansefunction.Group',
            autoLoad: false,
            // при изменениях не забывать что это дублируется в двух местах
            // Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunctionModel
            // и
            // Unidata.view.admin.cleanseFunction.CleanseFunctionModel
            proxy: {
                type: 'rest',
                url: Unidata.Config.getMainUrl() + 'internal/meta/cleanse-functions',
                reader: {
                    type: 'json',
                    transform: function (data) {
                        // сортируем функции по алфавиту

                        Ext.Array.each(data.groups, function (group) {
                            group.functions = Ext.Array.sort(group.functions, function (f1, f2) {
                                var name1 = f1.name.toLowerCase(),
                                    name2 = f2.name.toLowerCase();

                                if (name1 === name2) {
                                    return 0;
                                }

                                return (name1 > name2) ? 1 : -1;
                            });

                            Ext.Array.each(group.functions, function (fn, index) {
                                fn.order = index + 1;
                            });
                        });

                        return data;
                    }
                }
            }
        }
    },

    formulas: {
        cleanseGroups: {
            bind: {
                bindTo: '{cleanseGroupsStore}',
                deep: true
            },
            get: function (record) {
                return record && record.first();
            }
        }
    }
});
