/**
 * Представление полноразмерного изображения с затемненным фоном
 *
 * @author Sergey Shishigin
 * @date 2017-09-25
 */

Ext.define('Unidata.view.component.image.ImageWindow', {
    extend: 'Ext.window.Window',

    referenceHolder: true,

    cls: 'un-imagewindow',

    layout: {
        type: 'fit',
        align: 'stretch'
    },

    modal: false,
    closable: true,
    resizable: false,
    header: false,
    shadow: false,

    config: {
        src: ''
    },

    padding: 0,

    initItems: function () {
        var html;

        this.callParent(arguments);

        html = this.buildImageHtml();

        this.add(
            {
                xtype: 'container',
                reference: 'imageArea',
                cls: 'un-image-container',
                flex: 1,
                html: html
            }
        );
    },

    buildImageHtml: function () {
        var src = this.getSrc(),
            tpl = '<img src="{0}">',
            html;

        html = Ext.String.format(tpl, src);

        return html;
    },

    listeners: {
        beforeshow: function () {
            this.resizeWindow();
        },
        show: function () {
            Ext.get(window).on('click', this.onDocumentClick, this);
        }
    },

    resizeWindow: function () {
        var size = Ext.getBody().getViewSize();

        this.setHeight(size.height);
        this.setWidth(size.width);

        this.setPosition(0, 0);
    },

    onResizeWindow: function () {
        this.resizeWindow();
    },

    onDocumentClick: function () {
        this.close();
    },

    initEvents: function () {
        this.callParent(arguments);

        Ext.fly(window).on('resize', this.onResizeWindow, this);
    },

    onDestroy: function () {
        Ext.fly(window).un('resize', this.onResizeWindow, this);
        Ext.get(window).on('click', this.onDocumentClick, this);

        this.callParent(arguments);
    }
});
