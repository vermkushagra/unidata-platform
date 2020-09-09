/**
 * Провайдер
 * Вспомогательный класс для добавления формул из ui user exit
 *
 * @author Ivan Marshalkin
 * @date 2017-07-06
 */

Ext.define('Unidata.uiuserexit.viewmodel.FormulaProvider', {
    requires: [
        'Unidata.uiuserexit.viewmodel.FormulaBase'
    ],

    statics: {
        /**
         * Провайдит формулы кастомера для viewModel
         *
         * @param viewModel - viewModel для которой доопределяем формулы
         * @param formulaType - тип (место использования)
         * @param opts - объект содержащий дополнительные параметры
         */
        provideActiveUiUserExit: function (viewModel, formulaType, opts) {
            var viewModelFormulas = viewModel.getFormulas(),
                formulaClasses,
                createdFormulas;

            // ативные формулы кастомера
            formulaClasses = Unidata.uiuserexit.viewmodel.FormulaBase.getAllActiveCustomerFormulaClassesByType(formulaType);

            // оставляем только те которые могут быть применены для данного контекста
            formulaClasses = Ext.Array.filter(formulaClasses, function (formulaClass) {
                return formulaClass.isProvideable(opts) === true;
            });

            formulaClasses = Ext.Array.sort(formulaClasses, function (a, b) {
                var orderA = Ext.coalesceDefined(a.order, 0),
                    orderB = Ext.coalesceDefined(b.order, 0);

                if (orderA < orderB) {
                    return -1;
                } else if (orderA > orderB) {
                    return 1;
                }

                return 0;
            });

            // обрабатываем для использвоания в методе viewModel.setFormulas()
            createdFormulas = Unidata.uiuserexit.viewmodel.FormulaBase.getFormulaJsonFromClasses(formulaClasses);

            if (!Ext.Object.isEmpty(createdFormulas)) {
                // добавляем формулы к viewModel
                viewModelFormulas = Ext.apply(viewModelFormulas, createdFormulas);

                // ВНИМАНИЕ: если делать setFormulas(formulas) в цикле то все по тихому ломается
                // поэтому добавляем формулы только один раз
                viewModel.setFormulas(viewModelFormulas);
            }
        }
    }
});
