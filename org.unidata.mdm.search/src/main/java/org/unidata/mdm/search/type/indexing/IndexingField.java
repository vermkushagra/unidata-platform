package org.unidata.mdm.search.type.indexing;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.unidata.mdm.search.type.IndexField;
import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.indexing.impl.BooleanIndexingField;
import org.unidata.mdm.search.type.indexing.impl.CompositeIndexingField;
import org.unidata.mdm.search.type.indexing.impl.DateIndexingField;
import org.unidata.mdm.search.type.indexing.impl.DoubleIndexingField;
import org.unidata.mdm.search.type.indexing.impl.InstantIndexingField;
import org.unidata.mdm.search.type.indexing.impl.LongIndexingField;
import org.unidata.mdm.search.type.indexing.impl.StringIndexingField;
import org.unidata.mdm.search.type.indexing.impl.TimeIndexingField;
import org.unidata.mdm.search.type.indexing.impl.TimestampIndexingField;

/**
 * @author Mikhail Mikhailov on Oct 9, 2019
 * Marker interface for indexing sub-hierarchie.
 */
public interface IndexingField extends IndexField {

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, String value) {
        return new StringIndexingField(name)
                .withIndexType(type)
                .withValue(value);
    }

    static IndexingField ofStrings(@Nonnull IndexType type, @Nonnull String name, Collection<String> value) {
        return new StringIndexingField(name)
                .withIndexType(type)
                .withValues(value);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, String value, Function<String, ?> f) {
        return new StringIndexingField(name)
                .withIndexType(type)
                .withValue(value)
                .withTransform(f);
    }

    static IndexingField ofStrings(@Nonnull IndexType type, @Nonnull String name, Collection<String> value, Function<String, ?> f) {
        return new StringIndexingField(name)
                .withIndexType(type)
                .withValues(value)
                .withTransform(f);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, LocalDate value) {
        return new DateIndexingField(name)
                .withIndexType(type)
                .withValue(value);
    }

    static IndexingField ofDates(@Nonnull IndexType type, @Nonnull String name, Collection<LocalDate> value) {
        return new DateIndexingField(name)
                .withIndexType(type)
                .withValues(value);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, LocalDate value, Function<LocalDate, ?> f) {
        return new DateIndexingField(name)
                .withIndexType(type)
                .withValue(value)
                .withTransform(f);
    }

    static IndexingField ofDates(@Nonnull IndexType type, @Nonnull String name, Collection<LocalDate> value, Function<LocalDate, ?> f) {
        return new DateIndexingField(name)
                .withIndexType(type)
                .withValues(value)
                .withTransform(f);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, LocalTime value) {
        return new TimeIndexingField(name)
                .withIndexType(type)
                .withValue(value);
    }

    static IndexingField ofTimes(@Nonnull IndexType type, @Nonnull String name, Collection<LocalTime> value) {
        return new TimeIndexingField(name)
                .withIndexType(type)
                .withValues(value);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, LocalTime value, Function<LocalTime, ?> f) {
        return new TimeIndexingField(name)
                .withIndexType(type)
                .withValue(value)
                .withTransform(f);
    }

    static IndexingField ofTimes(@Nonnull IndexType type, @Nonnull String name, Collection<LocalTime> value, Function<LocalTime, ?> f) {
        return new TimeIndexingField(name)
                .withIndexType(type)
                .withValues(value)
                .withTransform(f);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, LocalDateTime value) {
        return new TimestampIndexingField(name)
                .withIndexType(type)
                .withValue(value);
    }

    static IndexingField ofTimestamps(@Nonnull IndexType type, @Nonnull String name, Collection<LocalDateTime> value) {
        return new TimestampIndexingField(name)
                .withIndexType(type)
                .withValues(value);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, LocalDateTime value, Function<LocalDateTime, ?> f) {
        return new TimestampIndexingField(name)
                .withIndexType(type)
                .withValue(value)
                .withTransform(f);
    }

    static IndexingField ofTimestamps(@Nonnull IndexType type, @Nonnull String name, Collection<LocalDateTime> value, Function<LocalDateTime, ?> f) {
        return new TimestampIndexingField(name)
                .withIndexType(type)
                .withValues(value)
                .withTransform(f);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, Date value) {
        return new InstantIndexingField(name)
                .withIndexType(type)
                .withValue(value == null ? null : value.toInstant());
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, Instant value) {
        return new InstantIndexingField(name)
                .withIndexType(type)
                .withValue(value);
    }

    static IndexingField ofOldInstants(@Nonnull IndexType type, @Nonnull String name, Collection<Date> value) {
        return new InstantIndexingField(name)
                .withIndexType(type)
                .withValues(value == null ? null : value.stream().filter(Objects::nonNull).map(Date::toInstant).collect(Collectors.toList()));
    }

    static IndexingField ofInstants(@Nonnull IndexType type, @Nonnull String name, Collection<Instant> value) {
        return new InstantIndexingField(name)
                .withIndexType(type)
                .withValues(value);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, Instant value, Function<Instant, ?> f) {
        return new InstantIndexingField(name)
                .withIndexType(type)
                .withValue(value)
                .withTransform(f);
    }

    static IndexingField ofInstants(@Nonnull IndexType type, @Nonnull String name, Collection<Instant> value, Function<Instant, ?> f) {
        return new InstantIndexingField(name)
                .withIndexType(type)
                .withValues(value)
                .withTransform(f);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, Long value) {
        return new LongIndexingField(name)
                .withIndexType(type)
                .withValue(value);
    }

    static IndexingField ofIntegers(@Nonnull IndexType type, @Nonnull String name, Collection<Long> value) {
        return new LongIndexingField(name)
                .withIndexType(type)
                .withValues(value);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, Double value) {
        return new DoubleIndexingField(name)
                .withIndexType(type)
                .withValue(value);
    }

    static IndexingField ofNumbers(@Nonnull IndexType type, @Nonnull String name, Collection<Double> value) {
        return new DoubleIndexingField(name)
                .withIndexType(type)
                .withValues(value);
    }

    static IndexingField of(@Nonnull IndexType type, @Nonnull String name, Boolean value) {
        return new BooleanIndexingField(name)
                .withIndexType(type)
                .withValue(value);
    }

    static IndexingField ofBooleans(@Nonnull IndexType type, @Nonnull String name, Collection<Boolean> value) {
        return new BooleanIndexingField(name)
                .withIndexType(type)
                .withValues(value);
    }

    static IndexingField ofRecords(@Nonnull IndexType type, @Nonnull String name, Collection<IndexingRecord> value) {
        return new CompositeIndexingField(name)
                .withIndexType(type)
                .withRecords(value);
    }
}
