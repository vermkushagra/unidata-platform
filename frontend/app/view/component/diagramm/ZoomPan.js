/**
 * Класс для диаграммы, реализующий её зуммирование и таскание мышкой
 *
 * @property jsPlumbCont
 * @property jsPlumbContWrapper
 * @property elToolbar
 */
Ext.define('Unidata.view.component.diagramm.ZoomPan', {

    extend: 'Ext.Component',

    baseCls: 'unidata-diagramm',

    childEls: [
        'elToolbar',
        'jsPlumbContWrapper',
        'jsPlumbCont'
    ],

    renderTpl: [
        '<div id="{id}-elToolbar" class="{baseCls}-toolbar" data-ref="elToolbar"></div>',
        '<div id="{id}-jsPlumbContWrapper" class="{baseCls}-cont-wrapper" data-ref="jsPlumbContWrapper">',
            '<div id="{id}-jsPlumbCont" class="{baseCls}-cont" data-ref="jsPlumbCont"></div>',
        '</div>'
    ],

    config: {
        localStorageNamespace: ''
    },

    applyLocalStorageNamespace: function (localStorageNamespace) {
        var namespace = 'diagram';

        if (!Ext.isEmpty(localStorageNamespace)) {
            namespace += '-' + localStorageNamespace;
        }

        return namespace;
    },

    onRender: function () {

        var me = this,
            el;

        me.callParent(arguments);

        el = me.el;

        el.on({
            scope: me,
            wheel: me.onMouseWheel,
            mousedown: me.onMouseDown
        });

        el.addCls([
            'diagramm-background'
        ]);

        me.jsPlumbScroller = Ext.scroll.Scroller.create({
            element: me.jsPlumbContWrapper
        });

        me.zoomDefaultButton = Ext.create('Ext.Button', {
            text: Unidata.i18n.t('admin.scheme>defaultScale'),
            renderTo: me.elToolbar,
            listeners: {
                click: me.onClickZoomDefault,
                scope: me
            }
        });

        me.config.width = el.getWidth();

        me.jsPlumbInstance = jsPlumb.getInstance({
            DragOptions: {
                cursor: 'pointer',
                zIndex: 2000
            },
            Container: me.jsPlumbCont.dom
        });

        me.elToolbar.setStyle({
            right: (Ext.getScrollbarSize().width + 5) + 'px'
        });

    },

    /**
     * @param {Ext.event.Event} e
     */
    updatePanPosition: function (e) {

        var startXY = this.panningStartXY,
            xy = e.getXY(),
            deltaX = startXY[0] - xy[0],
            deltaY = startXY[1] - xy[1],
            startScrollPosition = this.panningStartScrollPosition,
            newScrollPositionX = startScrollPosition.x + deltaX,
            newScrollPositionY = startScrollPosition.y + deltaY,
            scroller = this.jsPlumbScroller,
            maxScrollPosition = scroller.getMaxPosition();

        if (newScrollPositionX < 0) {
            newScrollPositionX = 0;
        }

        if (newScrollPositionY < 0) {
            newScrollPositionY = 0;
        }

        if (newScrollPositionX > maxScrollPosition.x) {
            newScrollPositionX = maxScrollPosition.x;
        }

        if (newScrollPositionY > maxScrollPosition.y) {
            newScrollPositionY = maxScrollPosition.y;
        }

        scroller.scrollTo(newScrollPositionX, newScrollPositionY);

    },

    onClickZoomDefault: function () {
        this.setZoom(1);
    },

    /**
     * @param {Ext.event.Event} e
     */
    onMouseWheel: function (e) {

        this.setZoom(this.jsPlumbInstance.getZoom() + e.getWheelDelta() * 0.1);

        e.preventDefault();

    },

    /**
     * @param {Ext.event.Event} e
     */
    onMouseDown: function (e) {

        var doc,
            elXY,
            elWidth,
            elHeight,
            mouseXY,
            scrollbarSize,
            jsPlumbContWrapper = this.jsPlumbContWrapper;

        if (e.target !== jsPlumbContWrapper.dom) {
            return;
        }

        mouseXY = e.getXY();
        scrollbarSize = Ext.getScrollbarSize();

        elXY = jsPlumbContWrapper.getXY();
        elWidth = jsPlumbContWrapper.getWidth();
        elHeight = jsPlumbContWrapper.getHeight();

        // не обрабатываем событие, когда пользователь скруллит с помощью скруллбара.
        // по-хорошему надо перевёрстывать, но я уже пытался и это приводит к ещё более
        // страшным ошибкам, так что такой фикс - самое безобидное решение в настоящий момент

        // попали на вертикальный скруллбар
        if (mouseXY[0] > elWidth + elXY[0] - scrollbarSize.width) {
            return;
        }

        // попали на горизонтальный скруллбар
        if (mouseXY[1] > elHeight + elXY[1] - scrollbarSize.height) {
            return;
        }

        this.panningStartXY = mouseXY;
        this.panningStartScrollPosition = this.jsPlumbScroller.getPosition();

        doc = Ext.getDoc();
        doc.on({
            scope:     this,
            mousemove: this.onMouseMove,
            mouseup:   this.onMouseUp
        });

        Ext.getBody().unselectable();

        this.el.addCls('unidata-diagramm__panning');

    },

    /**
     * @param {Ext.event.Event} e
     */
    onMouseUp: function (e) {

        var doc;

        this.updatePanPosition(e);

        doc = Ext.getDoc();
        doc.un({
            scope:     this,
            mousemove: this.onMouseMove,
            mouseup:   this.onMouseUp
        });

        Ext.getBody().selectable();

        this.el.removeCls('unidata-diagramm__panning');

    },

    onMouseMove: function (e) {

        this.updatePanPosition(e);

        e.preventDefault();
    },

    setZoom: function (zoom) {

        var prefixes = [
                'webkit',
                'khtml',
                'o',
                'ms'
            ],
            i,
            style = {},
            transform,
            transformOrigin = '0 0 0';

        if (!this.jsPlumbCont) {
            return;
        }

        if (zoom > 2) {
            zoom = 2;
        }

        if (zoom < 0.25) {
            zoom = 0.25;
        }

        transform = 'scale(' + zoom + ')';

        for (i = 0; i < prefixes.length; i++) {
            style[prefixes[i] + 'Transform'] = transform;
            style[prefixes[i] + 'TransformOrigin'] = transformOrigin;
        }

        style.transform = transform;
        style.transformOrigin = transformOrigin;

        this.jsPlumbCont.setStyle(style);
        this.jsPlumbInstance.setZoom(zoom, true);

        Unidata.LocalStorage.setCurrentUserValue(this.getLocalStorageNamespace(), 'un-diagram-zoom', zoom);
    },

    getZoom: function () {
        var zoom = 1,
            storedZoom = Unidata.LocalStorage.getCurrentUserValue(this.getLocalStorageNamespace(), 'un-diagram-zoom');

        if (!this.jsPlumbInstance) {
            return storedZoom || zoom;
        }

        return storedZoom || this.jsPlumbInstance.getZoom();
    }

});
