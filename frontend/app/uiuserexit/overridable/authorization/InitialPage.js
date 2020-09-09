/**
 * Точка расширения для конфигурирования начальной страницы пользователя
 *
 * @author Denis Makarov
 * @date 2018-05-24
 */

Ext.define('Unidata.uiuserexit.overridable.authorization.InitialPage', {
    singleton: true,

    consts: {
        REDIRECT_URL_FIELD: 'redirectUrl'
    },
    /**
     * @param authenticateData
     * @returns хэш страницы в формате #main?section=data
     */
    buildInitialPageHash: function (authenticateData) {
        var pageHash;

        pageHash = this.buildHashByUser(authenticateData);

        if (!pageHash) {
            pageHash = this.buildHashByRole(authenticateData);
        }

        return pageHash;
    },

    buildHashByUser: function (authenticateData) {
        var userProperties =  authenticateData.userInfo ? authenticateData.userInfo.properties : [],
            me = this,
            pageHash = '';

        Ext.Array.each(userProperties, function (roleProperty) {
            if (roleProperty.name === me.consts.REDIRECT_URL_FIELD) {
                pageHash = roleProperty.value;

                return false;
            }
        });

        return pageHash;
    },

    buildHashByRole: function (authenticateData) {
        var roles =  authenticateData.userInfo ? authenticateData.userInfo.rolesData : [],
            me = this,
            roleProperties,
            pageHash = '';

        Ext.Array.each(roles, function (role) {
            if (role.properties) {
                roleProperties = role.properties;

                Ext.Array.each(roleProperties, function (roleProperty) {
                    if (roleProperty.name === me.consts.REDIRECT_URL_FIELD) {
                        pageHash = roleProperty.value;

                        return false;
                    }
                });
            }
        });

        return pageHash;
    }

});
