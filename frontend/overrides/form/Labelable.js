/**
 * Оверрайд колонки tree дерева
 *
 * @author Ivan Marshalkin
 * @date 2017-05-24
 */
// смотри
// https://www.sencha.com/forum/showthread.php?13913-Security-XSS-attacks-for-Extjs-Applications-critical-warning/page20
//
// Ext.define('Ext.overrides.form.Labelable', {
//     override: 'Ext.form.Labelable',
//
//     initComponent: function () {
//         var me = this;
//         // me.origInnerTpl = me.getInnerTpl;
//         // me.getInnerTpl = function (displayField) {
//         //     var tpl = me.origInnerTpl(displayField);
//         //     if (tpl) tpl = tpl.replace(/\{(.*?)\}/g, '{$1:htmlEncode}');
//         //     return tpl;
//         //
//         //
//         // }
//         me.callParent();
//     },
// });

// Ext.define('Override.form.Labelable', {
//     override: 'Ext.form.Labelable',
//
//
//     setActiveErrors: function (errors) {
//         if (Ext.isArray(errors)) {
//             Ext.each(errors, function (msg, i) {
//                 errors[i] = Ext.String.htmlEncode(msg);
//             });
//         } else if (Ext.isString(errors)) {
//             errors = Ext.String.htmlEncode(errors);
//         }
//
//
//
//
//         this.callParent([errors]);
//     },
//
//
//     renderActiveError: function () {
//         this.callParent();
//         if (this.errorEl) this.errorEl.allowMarkup = true;//for quicktip
//     },
// });
