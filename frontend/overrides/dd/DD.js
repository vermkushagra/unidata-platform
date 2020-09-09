/**
 * @author Aleksandr Bavin
 * @date 2017-10-11
 */
Ext.define('Unidata.overrides.dd.DD', {

    override: 'Ext.dd.DD',

    /**
     * Слегка модифицирован оригинальный метод,
     * что бы разрешить перетаскивание за пределы левой верхней и нижней границы родительского контейнера
     *
     * @param el
     * @param iPageX
     * @param iPageY
     * @returns {*|Number}
     */
    alignElWithMouse: function (el, iPageX, iPageY) {
        var oCoord = this.getTargetCoord(iPageX, iPageY),
            fly = el.dom ? el : Ext.fly(el, '_dd'),
            elSize = fly.getSize(),
            EL = Ext.Element,
            vpSize,
            aCoord,
            newLeft,
            newTop;

        if (!this.deltaSetXY) {
            vpSize = this.cachedViewportSize = {width: EL.getDocumentWidth(), height: EL.getDocumentHeight()};
            aCoord = [
                // Math.max(0, Math.min(oCoord.x, vpSize.width - elSize.width)),
                // Math.max(0, Math.min(oCoord.y, vpSize.height - elSize.height))
                oCoord.x,
                oCoord.y
            ];
            fly.setXY(aCoord);
            newLeft = this.getLocalX(fly);
            newTop  = fly.getLocalY();
            this.deltaSetXY = [newLeft - oCoord.x, newTop - oCoord.y];
        } else {
            vpSize = this.cachedViewportSize;
            this.setLocalXY(
                fly,
                // Math.max(0, Math.min(oCoord.x + this.deltaSetXY[0], vpSize.width - elSize.width)),
                // Math.max(0, Math.min(oCoord.y + this.deltaSetXY[1], vpSize.height - elSize.height))
                oCoord.x + this.deltaSetXY[0],
                oCoord.y + this.deltaSetXY[1]
            );
        }

        this.cachePosition(oCoord.x, oCoord.y);
        this.autoScroll(oCoord.x, oCoord.y, el.offsetHeight, el.offsetWidth);

        return oCoord;
    }

});
