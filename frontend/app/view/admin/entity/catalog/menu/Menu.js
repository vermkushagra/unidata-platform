/**
 * Меню для управления каталогом реестров/справочников
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-15
 */
Ext.define('Unidata.view.admin.entity.catalog.menu.Menu', {

    extend: 'Ext.menu.Menu',

    requires: [
        'Unidata.view.admin.entity.catalog.menu.MenuController',
        'Unidata.view.admin.entity.catalog.menu.MenuModel'
    ],

    alias: 'widget.admin.entity.catalog.menu',

    controller: 'admin.entity.catalog.menu',

    viewModel: {
        type: 'admin.entity.catalog.menu'
    },

    items: [
        {
            text: Unidata.i18n.t('common:add'),
            glyph: 'xf055@FontAwesome',
            handler: 'onClickAdd',
            bind: {
                disabled: '{!createAllowed}'
            }
        },
        {
            text: Unidata.i18n.t('admin.metamodel>edit'),
            glyph: 'xf044@FontAwesome',
            handler: 'onClickEdit',
            bind: {
                disabled: '{!editAllowed}'
            }
        },
        {
            text: Unidata.i18n.t('common:delete'),
            glyph: 'xf014@FontAwesome',
            handler: 'onClickRemove',
            bind: {
                disabled: '{!deleteAllowed}'
            }
        }
    ],

    /**
     * Сеттер текущего элемента каталога
     *
     * @param {Unidata.model.entity.Catalog} record
     */
    setRecord: function (record) {
        this.getController().setRecord(record);
    },

    /**
     * @param {Object} rights
     */
    updateRights: function (rights) {

        var viewModel = this.getViewModel(),
            rightsList = [
                'createAllowed',
                'editAllowed',
                'deleteAllowed'
            ],
            right,
            i;

        for (i = 0; i < rightsList.length; i++) {

            right = rightsList[i];

            if (rights.hasOwnProperty(right)) {
                viewModel.set(rightsList[i], rights[right]);
            }
        }

    }

});
