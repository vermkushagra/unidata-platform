/**
 *
 */
package com.unidata.mdm.backend.exchange.chain.csv;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext.UpsertRequestContextBuilder;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.impl.OriginRecordImpl;
import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.chain.BaseTransformImportChainMember;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.ExchangeFieldTransformer;
import com.unidata.mdm.backend.exchange.def.ExchangeTemporalFieldTransformer;
import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.exchange.def.csv.CsvExchangeEntity;
import com.unidata.mdm.backend.exchange.def.csv.CsvExchangeField;
import com.unidata.mdm.backend.exchange.def.csv.CsvNaturalKey;
import com.unidata.mdm.backend.exchange.def.csv.CsvRelatesToRelation;
import org.apache.commons.lang.StringUtils;


/**
 * @author Mikhail Mikhailov
 * CSV base transform chain member.
 */
public abstract class CsvBaseTransformChainMember
    extends BaseTransformImportChainMember {

    /**
     * Constructor.
     */
    public CsvBaseTransformChainMember() {
        super();
    }

    /**
     * Loads lines from CSV input source.
     * @param ctx the context
     * @param dee the entity definition
     * @return set of lines
     */
    protected List<List<String>> loadResultSet(ExchangeContext ctx, CsvExchangeEntity dee) {

        Charset charset = null;
        if (dee.getCharset() != null) {
            TRANSFORM_CHAIN_MEMBER_LOGGER.info("Setting import charset for entity '{}'. Using {}.", dee.getName(), dee.getCharset());
            charset = Charset.forName(dee.getCharset());
        } else {
            TRANSFORM_CHAIN_MEMBER_LOGGER.info("Import charset NOT set for entity '{}'. Using default UTF-8", dee.getName());
            charset = StandardCharsets.UTF_8;
        }

        String separator = null;
        if (dee.getSeparator() != null) {
            TRANSFORM_CHAIN_MEMBER_LOGGER.info("Setting separator for entity '{}'. Using {}.", dee.getName(), dee.getSeparator());
            separator = dee.getSeparator();
        } else {
            TRANSFORM_CHAIN_MEMBER_LOGGER.info("Import separator NOT set for entity '{}'. Using default (comma ',')", dee.getName());
            separator = ",";
        }

        try (BufferedReader reader
                = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(dee.getResource()), charset))) {

            List<List<String>> lines = new ArrayList<List<String>>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(separator);
                lines.add(Arrays.asList(split));
            }
            return lines;
        } catch (FileNotFoundException e) {
            TRANSFORM_CHAIN_MEMBER_LOGGER.error("Source not found or unavailable", e);
        } catch (IOException e) {
            TRANSFORM_CHAIN_MEMBER_LOGGER.error("Source not readable", e);
        }

        return null;
    }

    /**
     * Loads lines from CSV input source.
     * @param ctx the context
     * @param dee the entity definition
     * @param type object type
     * @return set of lines
     */
    protected List<List<String>> loadResultSet(ExchangeContext ctx, CsvRelatesToRelation dee) {

        Charset charset = null;
        if (dee.getCharset() != null) {
            TRANSFORM_CHAIN_MEMBER_LOGGER.info("Setting import charset for entity '{}'. Using {}.", dee.getRelation(), dee.getCharset());
            charset = Charset.forName(dee.getCharset());
        } else {
            TRANSFORM_CHAIN_MEMBER_LOGGER.info("Import charset NOT set for entity '{}'. Using default UTF-8", dee.getRelation());
            charset = StandardCharsets.UTF_8;
        }

        String separator = null;
        if (dee.getSeparator() != null) {
            TRANSFORM_CHAIN_MEMBER_LOGGER.info("Setting separator for entity '{}'. Using {}.", dee.getRelation(), dee.getSeparator());
            separator = dee.getSeparator();
        } else {
            TRANSFORM_CHAIN_MEMBER_LOGGER.info("Import separator NOT set for entity '{}'. Using default (comma ',')", dee.getRelation());
            separator = ",";
        }

        try (BufferedReader reader
                = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(dee.getResource()), charset))) {

            List<List<String>> lines = new ArrayList<List<String>>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(separator);
                lines.add(Arrays.asList(split));
            }
            return lines;
        } catch (FileNotFoundException e) {
            TRANSFORM_CHAIN_MEMBER_LOGGER.error("Source not found or unavailable", e);
        } catch (IOException e) {
            TRANSFORM_CHAIN_MEMBER_LOGGER.error("Source not readable", e);
        }

        return null;
    }

    /**
     * Imports a row and creates a new entity.
     * @param fields the fields
     * @param exchangeEntity exchange settings for the entity
     * @param attrs attributes cache
     * @return upsert request context with a new record
     */
    protected UpsertRequestContext importEntity(List<String> fields, ExchangeEntity exchangeEntity, Map<String, AttributeInfoHolder> attrs) {

        // 1. Create origin key. Drop import if it is impossible
        final OriginKey key = importOriginKey(fields,
                (CsvNaturalKey) exchangeEntity.getNaturalKey(),
                exchangeEntity.getName(),
                exchangeEntity.getSourceSystem());
        if (key == null) {
            TRANSFORM_CHAIN_MEMBER_LOGGER.warn("Record cannot be imported [{}].", fields);
            return null;
        }

        // 2. Import possibly defined ranges
        final Date from = importRangeFrom(fields, exchangeEntity.getVersionRange());
        final Date to = importRangeTo(fields, exchangeEntity.getVersionRange());

        // 3. Proceed with fields.
        OriginRecord record = new OriginRecordImpl(new SerializableDataRecord());

        for (ExchangeField f : exchangeEntity.getFields()) {
            // 2.1. Get value
            Integer index = ((CsvExchangeField) f).getIndex();
            String value = index != null
                    ? fields.size() > index ? fields.get(index) : null
                    : f.getValue() != null ? f.getValue().toString() : null;
            if (value == null || value.isEmpty()) {
                continue;
            }

            // 2.2. Possibly transform
            List<ExchangeFieldTransformer> tf = f.getTransformations();
            if (tf != null) {
                value = applyTransformation(value, tf);
                if (value == null) {
                    continue;
                }
            }

            // 2.3. Resolve and set
            setAttribute(record, f, attrs, f.getName(), value, 0);
        }

        return new UpsertRequestContextBuilder()
                .record(record)
                .validFrom(from)
                .validTo(to)
                .originKey(key)
                .sourceSystem(exchangeEntity.getSourceSystem())
                .entityName(exchangeEntity.getName())
                .skipCleanse(exchangeEntity.isSkipCleanse())
                .build();
    }

    /**
     * Imports origin key.
     * @param fields the record fields
     * @param naturalKey the entity description
     * @param entityName the entity name
     * @param sourceSystem the source system
     * @return key or null
     */
    protected OriginKey importOriginKey(List<String> fields, CsvNaturalKey naturalKey, String entityName, String sourceSystem) {
        OriginKey key = null;
        while (naturalKey != null) {

            String joinWith = naturalKey.getJoinWith();
            List<Integer> indices = naturalKey.getIndices();
            if (indices == null || indices.isEmpty()) {
                break;
            }

            StringBuilder keyBuilder  = new StringBuilder();
            for (int i = 0; i < indices.size(); i++) {
                Integer keyIndex = indices.get(i);
                if (keyIndex >= fields.size() || fields.get(keyIndex) == null) {
                    break;
                }

                keyBuilder
                    .append(fields.get(keyIndex).trim().replaceAll("[^a-zA-Z0-9]*", ""))
                    .append(joinWith != null && i < indices.size() - 1 ? joinWith : "");
            }

            if (keyBuilder.length() == 0) {
                break;
            }

            key = OriginKey.builder()
                .externalId(keyBuilder.toString().trim())
                .entityName(entityName)
                .sourceSystem(sourceSystem)
                .build();

            break;
        }

        return key;
    }

    /**
     * Imports from date for a possibly given range.
     * Null means, the start date is not defined, what is pretty normal.
     * @param fields the fields
     * @param range the entity
     * @return date or null
     */
    protected Date importRangeFrom(List<String> fields, VersionRange range) {

        Date from = null;
        if (range != null
         && range.getValidFrom() != null) {

            CsvExchangeField fromField = (CsvExchangeField) range.getValidFrom();
            Integer index = fromField.getIndex();
            String value = index != null
                    ? fields.size() > index ? fields.get(index) : null
                    : fromField.getValue() != null ? fromField.getValue().toString() : null;

            if (StringUtils.isBlank(value)) {
                return null;
            }

            // 2.2. Possibly transform
            List<ExchangeFieldTransformer> tf = fromField.getTransformations();
            if (tf != null) {
                value = applyTransformation(value, tf);
                if (StringUtils.isBlank(value)) {
                    return null;
                }
            }

            from = ExchangeTemporalFieldTransformer.ISO801TZMillisStringToDate(value);
            if (from != null && range.isNormalizeFrom()) {
                Calendar clndr = Calendar.getInstance();
                clndr.setTime(from);
                clndr.set(Calendar.HOUR, 0);
                clndr.set(Calendar.MINUTE, 0);
                clndr.set(Calendar.SECOND, 0);
                clndr.set(Calendar.MILLISECOND, 0);
                clndr.setTimeZone(TimeZone.getDefault());

                from = clndr.getTime();
            }
        }

        return from;
    }

    /**
     * Imports to date for a possibly given range.
     * Null means, the to date is not defined, what is pretty normal.
     * @param fields the fields
     * @param range the entity
     * @return date or null
     */
    protected Date importRangeTo(List<String> fields, VersionRange range) {

        Date from = null;
        if (range != null
         && range.getValidTo() != null) {

            CsvExchangeField toField = (CsvExchangeField) range.getValidTo();
            Integer index = toField.getIndex();
            String value = index != null
                   ? fields.size() > index ? fields.get(index) : null
                   : toField.getValue() != null ? toField.getValue().toString() : null;

            if (StringUtils.isBlank(value)) {
                return null;
            }

            // 2.2. Possibly transform
            List<ExchangeFieldTransformer> tf = toField.getTransformations();
            if (tf != null) {
                value = applyTransformation(value, tf);
                if (StringUtils.isBlank(value)) {
                    return null;
                }
            }

            from = ExchangeTemporalFieldTransformer.ISO801TZMillisStringToDate(value);
            if (from != null && range.isNormalizeTo()) {
                Calendar clndr = Calendar.getInstance();
                clndr.setTime(from);
                clndr.set(Calendar.HOUR, 23);
                clndr.set(Calendar.MINUTE, 59);
                clndr.set(Calendar.SECOND, 59);
                clndr.set(Calendar.MILLISECOND, 999);
                clndr.setTimeZone(TimeZone.getDefault());

                from = clndr.getTime();
            }
        }

        return from;
    }


}
