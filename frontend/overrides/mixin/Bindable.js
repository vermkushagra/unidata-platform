/**
 * Исправление ошибки удаления биндинга при уничтожении компонента.
 *
 * смотри подробнее
 * https://www.sencha.com/forum/showthread.php?308430-Bug-in-binding-in-view-component-when-destroyed
 * https://www.sencha.com/forum/archive/index.php/t-308430.html
 * https://www.sencha.com/forum/showthread.php?299783-Override-a-mixin-in-Extjs-5
 *
 * @author Ivan Marshalkin
 * @date 2017-10-17
 *
 */

/*
   // тест кейс для воспроизведения / проверки
   Ext.define('Unidata.BindingBugTestModel', {
       extend: 'Ext.app.ViewModel',

       alias: 'viewmodel.bindingbugtestmodel',

       data: {
           dataRecord: {
               title: 'тайтл из дефолтного объекта'
           }
       },

       formulas: {
           testFormula: {
               bind: {
                   bindTo: '{dataRecord}',
                   deep: true
               },
               get: function (dataRecord) {
                   if (dataRecord) {
                       return dataRecord.title;
                   }

                   return 'нед датарекорда';
               }
           }
       }
   });

   var wnd = Ext.create('Ext.window.Window', {
       viewModel: {
           type: 'bindingbugtestmodel'
       },

       width: 300,

       bind: {
           title: '{testFormula}'
       }
   });

   wnd.setBind({
       title: null
   });


   wnd.show();

   setTimeout(function () {
       wnd.destroy();
   }, 3000);
*/

Ext.define('Ext.overrides.mixin.Bindable', {
    override: 'Ext.mixin.Bindable',

    privates: {
        removeBindings: function () {
            var bindings = this.bind,
                key, binding;

            if (bindings && typeof bindings !== 'string') {
                for (key in bindings) {
                    binding = bindings[key];

                    if (binding) {
                        binding.destroy();
                        binding._config = binding.getTemplateScope = null; // jscs:ignore
                    }
                }
            }
            this.bind = null;
        }
    }
}, function () {
    var mixinId = this.prototype.mixinId;

    // обходим все классы т.к. миксены добавляются при объявлении класса
    Ext.Object.each(Ext.ClassManager.classes, function (name, cls) {
        if (cls.prototype && cls.prototype.mixins && cls.prototype.mixins.hasOwnProperty(mixinId)) {
            // оверрайд полностью повторяет оверайд Ext.mixin.Bindable
            Ext.override(cls, {
                privates: {
                    removeBindings: function () {
                        var bindings = this.bind,
                            key, binding;

                        if (bindings && typeof bindings !== 'string') {
                            for (key in bindings) {
                                binding = bindings[key];

                                if (binding) {
                                    binding.destroy();
                                    binding._config = binding.getTemplateScope = null; // jscs:ignore
                                }
                            }
                        }
                        this.bind = null;
                    }
                }
            });
        }
    }, this);
});
