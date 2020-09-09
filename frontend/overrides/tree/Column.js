/**
 * Оверрайд колонки tree дерева
 *
 * @author Ivan Marshalkin
 * @date 2017-05-24
 */
// смотри
// https://www.sencha.com/forum/showthread.php?13913-Security-XSS-attacks-for-Extjs-Applications-critical-warning/page20
//
// Ext.define('Ext.overrides.tree.Column', {
//     override: 'Ext.tree.Column',
//
//     setupRenderer: function (type) {
//         //to ignore parent (Ext.grid.column.Column) xss attack protector.
//         var me = this,
//             origAllowMarkup = me.allowMarkup;
//
//         me.allowMarkup = true;
//         me.callParent([type]);
//         me.allowMarkup = origAllowMarkup;
//     },
//
//     initComponent: function () {
//         var me = this;
//
//         me.callParent();
//
//         me.setupXssProtectorRenderer();
//     },
//
//     setupXssProtectorRenderer: function () {
//         var me = this;
//
//         if (!me.allowMarkup) {
//
//             var olCompositeCleanseFunctiondRenderer = me.innerRenderer;
//
//             me.innerRenderer = function (value) {
//                 if (oldRenderer) {
//                     value = oldRenderer.apply(this, arguments);
//                 }
//
//                 if (value) {
//                     value = Ext.String.htmlEncode(value);
//                 }
//
//                 return value;
//             };
//         }
//     }
// });
