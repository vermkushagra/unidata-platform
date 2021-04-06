/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.data.ContributorRO;
import com.unidata.mdm.backend.api.rest.dto.data.TimeIntervalRO;
import com.unidata.mdm.backend.api.rest.dto.data.TimelineRO;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.dto.ContributorDTO;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;

/**
 * @author Mikhail Mikhailov
 * Time line converter.
 */
public class TimelineToTimelineROConverter {

    /**
     * Constructor.
     */
    private TimelineToTimelineROConverter() {
        super();
    }

    /**
     * Converts a list of sodurce timeline objects to target.
     * @param source the source
     * @param target the target
     */
    public static void convert(List<TimelineDTO> source, List<TimelineRO> target) {
        for (int i = 0; source != null && i < source.size(); i++) {
            target.add(convert(source.get(i)));
        }
    }

    /**
     * Convert method.
     * @param source the source
     * @return target
     */
    public static TimelineRO convert(TimelineDTO source) {
        TimelineRO target = new TimelineRO();

        target.setEtalonId(source.getEtalonId());
        target.setTimeline(new ArrayList<TimeIntervalRO>());

        copyTimeIntervals(source.getIntervals(), target.getTimeline());

        return target;
    }

    /**
     * Converts time intervals.
     * @param source the source
     * @param target the target
     */
    private static void copyTimeIntervals(List<TimeIntervalDTO> source, List<TimeIntervalRO> target) {
        for (int i = 0; source != null && i < source.size(); i++) {
            TimeIntervalRO tit = new TimeIntervalRO();

            copyTimeInterval(source.get(i), tit);

            target.add(tit);
        }
    }

    /**
     * Copy individual interval.
     * @param source the source
     * @param target the target
     */
    private static void copyTimeInterval(TimeIntervalDTO source, TimeIntervalRO target) {

        target.setDateFrom(ConvertUtils.date2LocalDateTime(source.getValidFrom()));
        target.setDateTo(ConvertUtils.date2LocalDateTime(source.getValidTo()));
        target.setActive(source.isActive());
        target.setContributors(new ArrayList<ContributorRO>());

        copyContributors(source.getContributors(), target.getContributors());
    }

    /**
     * Copy contributors list.
     * @param source the source
     * @param target the target
     */
    private static void copyContributors(List<ContributorDTO> source, List<ContributorRO> target) {
        for (int i = 0; source != null && i < source.size(); i++) {

            ContributorRO co = new ContributorRO();

            co.setOriginId(source.get(i).getOriginId());
            co.setSourceSystem(source.get(i).getSourceSystem());
            co.setVersion(source.get(i).getRevision());
            co.setStatus(source.get(i).getStatus() == null ? null : source.get(i).getStatus().toString());
            co.setApproval(source.get(i).getApproval());
            co.setOwner(source.get(i).getOwner());

            target.add(co);
        }
    }
}
