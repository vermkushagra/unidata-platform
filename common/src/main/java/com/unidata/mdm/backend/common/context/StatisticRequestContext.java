package com.unidata.mdm.backend.common.context;

import java.util.Date;

import com.unidata.mdm.backend.common.statistic.GranularityType;

/**
 * The Class StatisticRequest.
 */
public class StatisticRequestContext extends CommonRequestContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -3236657078399233166L;

    /** The start date. */
    private final Date startDate;

    /** The end date. */
    private final Date endDate;

    /** The granularity. */
    private final GranularityType granularity;

    /** The entity name. */
    private final String entityName;

    /** The source system name. */
    private final String sourceSystem;

    private final boolean forLastDate;

    /**
     * Constructor.
     * @param b
     */
    private StatisticRequestContext(StatisticRequestContextBuilder b) {
        super();
        this.endDate = b.endDate;
        this.startDate = b.startDate;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.granularity = b.granularity;
        this.forLastDate = b.forLastDate;
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Gets the granularity.
     *
     * @return the granularity
     */
    public GranularityType getGranularity() {
        return granularity;
    }

    /**
     * Gets the entity name.
     *
     * @return the entity name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Gets the source system name.
     *
     * @return the source system name
     */
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * Get for last date flag.
     * @return flag value
     */
    public boolean isForLastDate() {
        return forLastDate;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((entityName == null) ? 0 : entityName.hashCode());
        result = prime * result + ((granularity == null) ? 0 : granularity.hashCode());
        result = prime * result + ((sourceSystem == null) ? 0 : sourceSystem.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + (forLastDate ? 3 : 5);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        StatisticRequestContext other = (StatisticRequestContext) obj;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (entityName == null) {
            if (other.entityName != null)
                return false;
        } else if (!entityName.equals(other.entityName))
            return false;
        if (granularity != other.granularity)
            return false;
        if (sourceSystem == null) {
            if (other.sourceSystem != null)
                return false;
        } else if (!sourceSystem.equals(other.sourceSystem))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if(forLastDate != other.forLastDate){
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StatisticRequest [startDate = ");
        builder.append(startDate);
        builder.append(", endDate = ");
        builder.append(endDate);
        builder.append(", granularity = ");
        builder.append(granularity);
        builder.append(", entityName = ");
        builder.append(entityName);
        builder.append(", sourceSystem = ");
        builder.append(sourceSystem);
        builder.append(", forLastDate = ");
        builder.append(forLastDate);
        builder.append("]");
        return builder.toString();
    }

    public static StatisticRequestContextBuilder builder() {
        return new StatisticRequestContextBuilder();
    }


    /**
     * @author Mikhail Mikhailov
     * Request builder.
     */
    public static class StatisticRequestContextBuilder {

        /** The start date. */
        private Date startDate;

        /** The end date. */
        private Date endDate;

        /** The granularity. */
        private GranularityType granularity;

        /** The entity name. */
        private String entityName;

        /** The source system name. */
        private String sourceSystem;
        /**
         * for last date flag
         */
        private boolean forLastDate;

        private StatisticRequestContextBuilder() {
            super();
        }

        /**
         * @param startDate the startDate to set
         */
        public StatisticRequestContextBuilder startDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * @param endDate the endDate to set
         */
        public StatisticRequestContextBuilder endDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        /**
         * @param granularity the granularity to set
         */
        public StatisticRequestContextBuilder granularity(GranularityType granularity) {
            this.granularity = granularity;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public StatisticRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public StatisticRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param forLastDate value for flag "forLastDate"
         */
        public StatisticRequestContextBuilder forLastDate(boolean forLastDate) {
            this.forLastDate = forLastDate;
            return this;
        }
        /**
         * Builds request.
         * @return request
         */
        public StatisticRequestContext build() {
            return new StatisticRequestContext(this);
        }
    }
}
