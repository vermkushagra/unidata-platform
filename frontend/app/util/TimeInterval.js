/**
 * Утилитный класс периодов актуальности
 *
 * @author Sergey Shishigin
 * @date 2016-11-07
 */

Ext.define('Unidata.util.TimeInterval', {
    singleton: true,

    /**
     * Временные интервалы пересекающиеся с отрезком [dateFrom, dateTo]
     *
     * @param dateFrom {Date|null} Левая граница отрезка
     * @param dateTo {Date|null} Правая граница отрезка
     * @param timeIntervals {Array[Unidata.model.data.TimeInterval]} Массив временных интервалов
     * @returns {Array[Unidata.model.data.TimeInterval]} Массив временных интервалов, пересекающих [dateFrom, dateTo]
     */
    findIntersectedTimeIntervals: function (dateFrom, dateTo, timeIntervals) {
        var found = [],
            interval1 = {},
            interval2 = {};

        if (dateFrom > dateTo) {
            return found;
        }

        interval1.from = dateFrom;
        interval1.to = this.applyTimeToDateTo(dateTo);

        Ext.Array.forEach(timeIntervals, function (timeInterval) {
            interval2.from = timeInterval.get('dateFrom');
            interval2.to   = timeInterval.get('dateTo');

            if (this.isDatesIntersectTimeInterval(interval1, interval2)) {
                found.push(timeInterval);
            }
        }, this);

        return found;
    },

    /**
     * Метод определяет находится ли отрезок [dateFrom, dateTo] целиком внутри timeInterval
     *
     * @param dateFrom {Date|null} Левая граница отрезка
     * @param dateTo {Date|null} Правая граница отрезка
     * @param timeInterval {Unidata.model.data.TimeInterval} Временной интервал
     * @returns {boolean}
     */
    isDatesInsideTimeInterval: function (dateFrom, dateTo, timeInterval) {
        var result,
            tiDateFrom,
            tiDateTo;

        if (dateFrom >= dateTo || !timeInterval) {
            return false;
        }

        tiDateFrom = timeInterval.get('dateFrom');
        tiDateTo   = timeInterval.get('dateTo');

        if (tiDateFrom === null && tiDateTo === null) {
            result = true;
        } else if (tiDateFrom === null) {
            result = dateTo <= tiDateTo;
        } else if (tiDateTo === null) {
            result = dateFrom >= tiDateFrom;
        } else {
            result = dateFrom >= tiDateFrom && dateTo <= tiDateTo;
        }

        return result;
    },

    /**
     * Проверяет является пересекает ли отрезок [dateFrom1, dateTo1] временной интервал timeInterval
     *
     * Случай 1:
     * отрезок [dateFrom1, dateTo1] внутри timeInterval с границами (-Inf, +Inf)
     *
     *       ---
     * -Inf ----- +Inf
     *
     * Случай 2
     * отрезок [dateFrom1, dateTo1] пересекает timeInterval с границами (-Inf, tiDateTo]
     *
     *          ---
     * -Inf -----
     *
     *       ---
     * -Inf -----
     *
     *      ---
     * -Inf -----
     *
     *      -------
     * -Inf -----
     *
     * Случай 3
     * отрезок [dateFrom1, dateTo1] пересекает timeInterval с границами [tiDateFrom, +Inf)
     *
     *        ---
     *       ----- + Inf
     *
     *      ---
     *       ----- + Inf
     *
     *     -------
     *       ----- + Inf
     *
     *        ---
     *      ----- + Inf
     *
     * Случай 4
     * отрезок [dateFrom1, dateTo1] пересекает timeInterval с границами [tiDateFrom, tiDateTo]
     *
     *        ---
     *       -----
     *
     *     ---
     *       -----
     *
     *           ---
     *       -----
     *
     *        ---
     *      -----
     *
     *      ---
     *      -----
     *
     * @param interval1 {Object}
     * @param interval2 {Object}
     *
     * interval
     * from - left border {Date|null}
     * to - right border {Date|null}
     *
     * @returns {boolean}
     */
    isDatesIntersectTimeInterval: function (interval1, interval2) {
        var result,
            dateFrom1 = interval1.from,
            dateTo1 = interval1.to,
            dateFrom2 = interval2.from,
            dateTo2 = interval2.to;

        if (dateFrom1 > dateTo1 || dateFrom2 > dateTo2) {
            return false;
        }

        // если отрезок [dateFrom1, dateTo1] совпадает с [dateFrom2, dateTo2], то нет пересечения
        if (Ext.Date.isEqual(dateFrom1, dateFrom2) && Ext.Date.isEqual(dateTo1, dateTo2)) {
            return false;
        }

        if (dateFrom2 === null && dateTo2 === null) {
            // Случай 1:
            // отрезок (dateFrom1, dateTo1) внутри timeInterval с границами (-Inf, +Inf)
            result = (dateFrom1 !== null || dateTo1 !== null);
        } else if (dateFrom2 === null) {
            // Случай 2
            // отрезок (dateFrom1, dateTo1) пересекает timeInterval с границами (-Inf, dateTo2)
            result = (dateFrom1 !== null && dateFrom1 < dateTo2) || dateFrom1 === null;
        } else if (dateTo2 === null) {
            // Случай 3
            // отрезок [dateFrom1, dateTo1] пересекает timeInterval с границами [dateFrom2, +Inf)
            result = (dateTo1 !== null && dateTo1 > dateFrom2) || dateTo1 === null;
        } else {
            // Случай 4
            // отрезок [dateFrom1, dateTo1] пересекает timeInterval с границами [dateFrom2, dateTo2]
            result = (dateFrom1 > dateFrom2 && dateFrom1 < dateTo2) ||
                     (dateTo1 > dateFrom2 && dateTo1 < dateTo2);
        }

        return result;
    },

    /**
     * Проставить время для dateTo
     *
     * @param date
     * @returns {*}
     */
    applyTimeToDateTo: function (date) {

        if (Ext.isDate(date)) {
            date.setHours(23);
            date.setMinutes(59);
            date.setSeconds(59);
            date.setMilliseconds(999);
        }

        return date;
    },

    /**
     * Получить список имен sourceSystem на основе информации о timeIntervals
     *
     * @param timeIntervals {Unidata.model.data.TimeInterval[]}
     * @return sourceSystems {String[]} Список имен sourceSystem
     */
    pluckSourceSystems: function (timeIntervals) {
        var sourceSystems = [];

        Ext.Array.each(timeIntervals, function (timeInterval) {
            var contributors = timeInterval.contributors();

            contributors.each(function (contributor) {
                sourceSystems.push(contributor.get('sourceSystem'));
            });
        });

        sourceSystems = Ext.Array.unique(sourceSystems);

        return sourceSystems;
    }
});
