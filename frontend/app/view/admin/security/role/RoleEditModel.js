/**
 * @author Ivan Marshalkin
 * @date 2017-02-03
 */
Ext.define('Unidata.view.admin.security.role.RoleEditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.security.roleedit',

    data: {
        currentRole: null,
        readOnly: true,
        nameEditable: false,
        catalogStoreLoaded: false,
        securedResourceStoreLoaded: false,
        securityLabelsStoreLoaded: false,
        propertiesIsVisible: true
    },

    stores: {
        securedResourceStore: {
            type: 'tree',
            model: 'Unidata.model.user.SecuredResourceTree',
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: Unidata.Config.getMainUrl() + 'internal/security/role/get-all-secured-resources/',
                limitParam: '',
                startParam: '',
                pageParam: '',
                reader: {
                    type: 'json',
                    rootProperty: 'content',
                    transform: {
                        // что бы ничего не подргужалось, дочерние элементы должны находиться в rootProperty (content)
                        fn: function (data) {

                            function childrenToContent (item) {
                                item.leaf = item.children ? false : true;
                                item.content = item.children ? item.children : [];
                                delete item.children;

                                Ext.Array.each(item.content, childrenToContent);
                            }

                            Ext.Array.each(data.content, childrenToContent);

                            return data.content;
                        }
                    }
                }
            },
            listeners: {
                load: 'onSecuredResourcesStoreLoad'
            },
            root: {
                id: null
            }
        },

        catalogStore: {
            type: 'tree',
            model: 'Unidata.model.entity.Catalog',
            autoLoad: true,
            proxy: {
                type: 'un.entity.catalog',
                onlyCatalog: false,
                filterEmptyGroups: true
            },
            root: {
                id: null
            },
            listeners: {
                load: 'onCatalogStoreLoad'
            }
        },

        securityLabels: {
            model: 'Unidata.model.user.SecurityLabelRole',
            autoLoad: true,
            proxy: {
                type: 'rest',
                url: Unidata.Config.getMainUrl() + 'internal/security/role/get-all-security-labels',
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            },
            listeners: {
                load: 'onSecurityLabelsStoreLoad'
            }
        },

        treeGroupedStore: {
            type: 'tree',
            autoLoad: false,
            root: {
                id: null,
                displayName: 'Rootnode',
                expanded: false
            }
        }
    },

    formulas: {
        allDataLoaded: {
            bind: {
                catalogStoreLoaded: '{catalogStoreLoaded}',
                securedResourceStoreLoaded: '{securedResourceStoreLoaded}',
                securityLabelsStoreLoaded: '{securityLabelsStoreLoaded}',
                deep: true
            },
            get: function (getter) {
                var loaded = false;

                if (getter && getter.catalogStoreLoaded && getter.securedResourceStoreLoaded && getter.securityLabelsStoreLoaded) {
                    loaded = true;
                }

                return loaded;
            }
        },

        title: {
            bind: {
                role: '{currentRole}',
                deep: true
            },
            get: function (getter) {
                var title = '',
                    role;

                if (getter && getter.role) {
                    role = getter.role;

                    title = role.phantom ? Unidata.i18n.t('admin.security>newRole') : Unidata.i18n.t('admin.metamodel>edit');
                }

                return title;
            }
        }
    }
});
