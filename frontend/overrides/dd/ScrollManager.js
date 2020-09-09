/**
 * Фикс-хак для корректного перемещения перетаскиваемого элемента, при скролле
 *
 * @author Aleksandr Bavin
 * @date 2017-11-07
 */
Ext.define('Unidata.overrides.dd.ScrollManager', {

    override: 'Ext.dd.ScrollManager',

    fixDraggablePos: function (distance) {
        var dir = this.proc.dir,
            dragCurrent = this.ddmInstance.dragCurrent,
            dragCurrentEl = dragCurrent[0],
            newDelta = [0, 0];

        if (!dragCurrentEl) {
            return;
        }

        switch (dir) {
            case 'up':
                newDelta[1] -= distance;
                break;
            case 'down':
                newDelta[1] += distance;
                break;
            case 'left':
                newDelta[0] -= distance;
                break;
            case 'right':
                newDelta[0] += distance;
                break;
        }

        dragCurrent.deltaSetXY[0] += newDelta[0];
        dragCurrent.deltaSetXY[1] += newDelta[1];
        dragCurrentEl.setLocalXY(
            newDelta[0] + dragCurrentEl.getLocalX(),
            newDelta[1] + dragCurrentEl.getLocalY()
        );
    }

}, function () {

    this.doScroll = Ext.bind(function () {
        var me = this;

        if (me.ddmInstance.dragCurrent) {
            var proc   = me.proc,
                procEl = proc.el,
                scrollComponent = proc.component,
                ddScrollConfig = proc.el.ddScrollConfig,
                distance = ddScrollConfig && ddScrollConfig.increment    ? ddScrollConfig.increment : me.increment,
                animate  = ddScrollConfig && 'animate' in ddScrollConfig ? ddScrollConfig.animate   : me.animate,
                afterScroll = function () {
                    me.triggerRefresh();
                };

            if (animate) {
                if (animate === true) {
                    animate = {
                        callback: afterScroll
                    };
                } else {
                    animate.callback = animate.callback ?
                        Ext.Function.createSequence(animate.callback, afterScroll) :
                        afterScroll;
                }
            }

            // If the element is the overflow element of a Component, and we are scrolling using CSS transform,
            // Then scroll using the correct method!
            if (scrollComponent) {

                // Left/right means increment has to be negated
                distance = distance * me.dirTrans[proc.dir];

                // Pass X or Y params depending upon dimension being scrolled
                if (proc.dir === 'up' || proc.dir === 'down') {
                    scrollComponent.scrollBy(0, distance, animate);
                } else {
                    scrollComponent.scrollBy(distance, 0, animate);
                }
            } else {
                procEl.scroll(proc.dir, distance, animate);
            }

            this.fixDraggablePos(Math.abs(distance)); // добавлен метод, модифицирующий позицию

            if (!animate) {
                afterScroll();
            }
        }
    }, this);

});
