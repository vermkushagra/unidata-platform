/**
 * Список классификаторов (модель)
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.classifier.list.ClassifierListModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.classifier.list',

    formulas: {
        classifierExportDisabled: {
            bind: {
                bindTo: '{classifierGrid.selection}',
                deep: true
            },
            get: function (classifier) {
                var phantom = true,
                    disabled = true;

                if (classifier) {
                    phantom = classifier.phantom;
                }

                disabled = !classifier || phantom;

                disabled = Ext.coalesceDefined(disabled, true);

                return disabled;
            }
        }
    }
});
