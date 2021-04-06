/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.Objects;

import com.unidata.mdm.backend.api.rest.dto.meta.PeriodBoundaryDefinition;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.PeriodBoundaryDef;

/**
 * @author Mikhail Mikhailov
 * Validity period boundary converter.
 */
public class PeriodBoundaryConverter {

    /**
     * Constructor.
     */
    private PeriodBoundaryConverter() {
        super();
    }

    /**
     * The 'To' converting method.
     * @param source the source
     * @return REST target
     */
    public static PeriodBoundaryDefinition to(PeriodBoundaryDef source) {

        if (Objects.isNull(source)) {
            return null;
        }

        PeriodBoundaryDefinition target = new PeriodBoundaryDefinition();
        target.setEnd(JaxbUtils.xmlGregorianCalendarToDate(source.getEnd()));
        target.setStart(JaxbUtils.xmlGregorianCalendarToDate(source.getStart()));

        return target;
    }

    /**
     * The 'From' converting method.
     * @param source the source
     * @return XML target
     */
    public static PeriodBoundaryDef from(PeriodBoundaryDefinition source) {

        if (Objects.isNull(source)) {
            return null;
        }

        PeriodBoundaryDef target = JaxbUtils.getMetaObjectFactory().createPeriodBoundaryDef();
        target.setEnd(JaxbUtils.dateToXMGregorianCalendar(source.getEnd()));
        target.setStart(JaxbUtils.dateToXMGregorianCalendar(source.getStart()));

        return target;
    }
}
