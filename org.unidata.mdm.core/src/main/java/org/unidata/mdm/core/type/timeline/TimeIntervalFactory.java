package org.unidata.mdm.core.type.timeline;

import java.util.Collection;
import java.util.Date;

import org.unidata.mdm.core.type.calculables.Calculable;
import org.unidata.mdm.core.type.calculables.CalculableHolder;

/**
 * @author Mikhail Mikhailov
 * The factory interface, responsible for creation of new interval instancies.
 */
@FunctionalInterface
public interface TimeIntervalFactory<C extends Calculable> {
    /**
     * full constructor method to implement.
     * @return new time interval
     */
    TimeInterval<C> newInstance(Date validFrom, Date validTo, Collection<CalculableHolder<C>> contributors);
}
