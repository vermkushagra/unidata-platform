/**
 * Кнопки в виде иконок LinearIcons
 *
 * @author: Sergey Shishigin
 */

Ext.define('Unidata.view.component.button.FontButton', {
    extend: 'Ext.button.Button',

    xtype: 'un.fontbutton',

    scale: 'medium',

    ui: 'un-fontbutton',

    iconTpl:
    '<span id="{id}-btnIconEl" data-ref="btnIconEl" role="presentation" unselectable="on" class="{baseIconCls} ' +
        '{baseIconCls}-{ui} {iconCls} {glyphCls}{childElCls}" style="' +
        '<tpl if="iconUrl">background-image:url({iconUrl});</tpl>' +
        '<tpl if="glyph && glyphFontFamily">font-family:{glyphFontFamily};</tpl>">' +
        '<tpl if="glyph">&#{glyph};</tpl><tpl if="iconCls || iconUrl"></tpl>' + // убран nbsp
    '</span>',

    shadow: false

});
