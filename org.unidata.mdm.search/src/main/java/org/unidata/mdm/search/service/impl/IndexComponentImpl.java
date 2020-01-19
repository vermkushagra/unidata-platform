/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.search.service.impl;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.exception.SearchApplicationException;
import org.unidata.mdm.search.exception.SearchExceptionIds;
import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.indexing.IndexingField;
import org.unidata.mdm.search.type.indexing.IndexingRecord;
import org.unidata.mdm.search.type.indexing.impl.AbstractValueIndexingField;
import org.unidata.mdm.search.type.indexing.impl.CompositeIndexingField;

/**
 * @author Mikhail Mikhailov on Oct 9, 2019
 * Data indexing component.
 */
@Component
public class IndexComponentImpl extends BaseAgentComponent {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexComponentImpl.class);
    /**
     * Type of bulk request.
     * @author Mikhail Mikhailov on Oct 9, 2019
     */
    enum BulkRequestType {
        DELETE_REQUEST,
        INDEX_REQUEST,
        UPDATE_REQUEST
    }
    /**
     * The bulk collector.
     * @author Mikhail Mikhailov on Oct 9, 2019
     */
    private class BulkCollector {

        private final Map<String, Map<BulkRequestType, Collection<? extends ActionRequest>>> bulk = new HashMap<>();

        public BulkCollector() {
            super();
        }

        @SuppressWarnings("unchecked")
        public void addDelete(String index, DeleteRequest request) {
            List<DeleteRequest> target = (List<DeleteRequest>) bulk
                    .computeIfAbsent(index, key -> new EnumMap<>(BulkRequestType.class))
                    .computeIfAbsent(BulkRequestType.DELETE_REQUEST, key -> new ArrayList<DeleteRequest>());

            target.add(request);
        }

        @SuppressWarnings("unchecked")
        public void addIndex(String index, IndexRequest request) {
            List<IndexRequest> target = (List<IndexRequest>) bulk
                    .computeIfAbsent(index, key -> new EnumMap<>(BulkRequestType.class))
                    .computeIfAbsent(BulkRequestType.INDEX_REQUEST, key -> new ArrayList<IndexRequest>());

            target.add(request);
        }

        @SuppressWarnings("unchecked")
        public void addUpdate(String index, UpdateRequest request) {
            List<UpdateRequest> target = (List<UpdateRequest>) bulk
                    .computeIfAbsent(index, key -> new EnumMap<>(BulkRequestType.class))
                    .computeIfAbsent(BulkRequestType.UPDATE_REQUEST, key -> new ArrayList<UpdateRequest>());

            target.add(request);
        }

        public Collection<String> getIndexes() {
            return bulk.keySet();
        }

        @SuppressWarnings("unchecked")
        public Collection<DeleteRequest> getDelete(String index) {
            Map<BulkRequestType, Collection<? extends ActionRequest>> slot = bulk.get(index);
            if (MapUtils.isNotEmpty(slot)) {
                return slot.get(BulkRequestType.DELETE_REQUEST) == null
                        ? Collections.emptyList()
                        : (List<DeleteRequest>) slot.get(BulkRequestType.DELETE_REQUEST);
            }
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        public Collection<IndexRequest> getIndex(String index) {
            Map<BulkRequestType, Collection<? extends ActionRequest>> slot = bulk.get(index);
            if (MapUtils.isNotEmpty(slot)) {
                return slot.get(BulkRequestType.INDEX_REQUEST) == null
                        ? Collections.emptyList()
                        : (List<IndexRequest>) slot.get(BulkRequestType.INDEX_REQUEST);
            }
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        public Collection<UpdateRequest> getUpdate(String index) {
            Map<BulkRequestType, Collection<? extends ActionRequest>> slot = bulk.get(index);
            if (MapUtils.isNotEmpty(slot)) {
                return slot.get(BulkRequestType.UPDATE_REQUEST) == null
                        ? Collections.emptyList()
                        : (List<UpdateRequest>) slot.get(BulkRequestType.UPDATE_REQUEST);
            }
            return Collections.emptyList();
        }
    }
    /**
     * Transport client to use.
     */
    @Autowired
    private Client client;
    /**
     * Fix UTC ZID here.
     */
    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    /**
     * Local date FMT.
     */
    private static final  DateTimeFormatter ELASTIC_ISO_LOCAL_DATE = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .toFormatter();
    /**
     * Local time FMT.
     */
    private static final  DateTimeFormatter ELASTIC_ISO_LOCAL_TIME = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .toFormatter();
    /**
     * Local dt FMT.
     */
    private static final  DateTimeFormatter ELASTIC_ISO_LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ELASTIC_ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(ELASTIC_ISO_LOCAL_TIME)
            .toFormatter();
    /**
     * Elastic native TS format. TS are indexed always at UTC. Thi is for instants.
     */
    private static final DateTimeFormatter ELASTIC_ISO_INSTANT
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    /**
     * Constructor.
     */
    public IndexComponentImpl() {
        super();
    }
    /**
     * Does indexing processing.
     * @param ctx the context to process
     * @return true, if successful, false otherwise
     */
    public void process(IndexRequestContext ctx) {

        BulkCollector collector = new BulkCollector();
        process(ctx, collector);
        bulk(ctx.isRefresh(), collector);
    }
    /**
     * Does indexing processing.
     * @param ctxs the contexts to process
     * @return true, if successful, false otherwise
     */
    public void process(Collection<IndexRequestContext> ctxs, boolean refresh) {

        if (CollectionUtils.isEmpty(ctxs)) {
            return;
        }

        BulkCollector collector = new BulkCollector();
        for (IndexRequestContext ctx : ctxs) {
            process(ctx, collector);
        }

        bulk(refresh, collector);
    }

    private void process(IndexRequestContext ctx, BulkCollector collector) {

        String targetIndexName =  constructIndexName(ctx);

        // 1. Deletes
        ctx.getDeletes().forEach((type, ids) ->
            ids.forEach(id -> {

                DeleteRequest dr = new DeleteRequest(targetIndexName, id.getSearchType().getName(), id.getIndexId());
                dr.parent(id.getRouting());
                dr.routing(id.getRouting());
                collector.addDelete(targetIndexName, dr);
            })
        );

        // 2. Updates
        ctx.getUpdates().forEach((type, payload) ->
            payload.forEach(i -> {

                try (XContentBuilder b = XContentFactory.jsonBuilder()) {

                    processRecord(i, b);

                    UpdateRequest ur = new UpdateRequest(targetIndexName, type.getName(), i.getIndexId().getIndexId());

                    ur.doc(b);
                    ur.parent(i.getIndexId().getRouting());
                    ur.routing(i.getIndexId().getRouting());

                    collector.addUpdate(targetIndexName, ur);
                } catch (IOException e) {
                    throwIOExceptionFailure(e);
                }
            })
        );

        // 3. Index
        ctx.getIndex().forEach((type, payload) ->
            payload.forEach(i -> {

                try (XContentBuilder b = XContentFactory.jsonBuilder()) {

                    processRecord(i, b);

                    boolean hasIndexId = Objects.nonNull(i.getIndexId());
                    final IndexRequest record = new IndexRequest(targetIndexName, type.getName(), hasIndexId ? i.getIndexId().getIndexId() : null);

                    record.source(b);
                    if (hasIndexId && Objects.nonNull(i.getIndexId().getRouting())) {
                        record.parent(i.getIndexId().getRouting());
                        record.routing(i.getIndexId().getRouting());
                    }

                    collector.addIndex(targetIndexName, record);
                } catch (IOException e) {
                    throwIOExceptionFailure(e);
                }
            })
        );
    }

    private void processRecord(IndexingRecord record, XContentBuilder builder) throws IOException {

        if (record.isEmpty()) {
            return;
        }

        builder.startObject();
        for (IndexingField i : record.getFields()) {
            if (i.getFieldType() == FieldType.COMPOSITE) {
                processCompositeField((CompositeIndexingField) i, builder);
            } else {
                processValueField((AbstractValueIndexingField<?, ?>) i, builder);
            }
        }
        builder.endObject();
    }

    private void processCompositeField(CompositeIndexingField field, XContentBuilder builder) throws IOException {

        if (field.isEmpty()) {
            return;
        }

        builder.startArray(field.getName());
        for (IndexingRecord record : field.getRecords()) {
            processRecord(record, builder);
        }
        builder.endArray();
    }

    private void processValueField(AbstractValueIndexingField<?, ?> avif, XContentBuilder builder) throws IOException {

        if (avif.isEmpty()) {
            return;
        }

        if (avif.isSingleton()) {
            processSingletonValueField(avif, builder);
        } else {
            processCollectionValueField(avif, builder);
        }
    }

    private void processSingletonValueField(AbstractValueIndexingField<?, ?> avif, XContentBuilder builder) throws IOException {

        Object obj = avif.getValue();
        switch (avif.getFieldType()) {
        case BOOLEAN:
            builder.field(avif.getName(), (Boolean) obj);
            break;
        case DATE:
            builder.field(avif.getName(), obj == null ? null : ELASTIC_ISO_LOCAL_DATE.format((LocalDate) obj));
            break;
        case TIME:
            builder.field(avif.getName(), obj == null ? null : ELASTIC_ISO_LOCAL_TIME.format((LocalTime) obj));
            break;
        case TIMESTAMP:
            builder.field(avif.getName(), obj == null ? null : ELASTIC_ISO_LOCAL_DATE_TIME.format((LocalDateTime) obj));
            break;
        case INSTANT:
            builder.field(avif.getName(), obj == null ? null : ELASTIC_ISO_INSTANT.format(OffsetDateTime.ofInstant((Instant) obj, UTC_ZONE_ID)));
            break;
        case NUMBER:
            builder.field(avif.getName(), (Double) obj);
            break;
        case INTEGER:
            builder.field(avif.getName(), (Long) obj);
            break;
        case STRING:
            String val = (String) obj;
            // Discard blank lines.
            if (StringUtils.isBlank(val)) {
                val = null;
            }
            builder.field(avif.getName(), val);
            break;
        default:
            break;
        }
    }

    private void processCollectionValueField(AbstractValueIndexingField<?, ?> avif, XContentBuilder builder) throws IOException {

        builder.startArray(avif.getName());
        for (Object obj : avif.getValues()) {
            switch (avif.getFieldType()) {
            case BOOLEAN:
                builder.value((Boolean) obj);
                break;
            case DATE:
                builder.value(obj == null ? null : ELASTIC_ISO_LOCAL_DATE.format((LocalDate) obj));
                break;
            case TIME:
                builder.value(obj == null ? null : ELASTIC_ISO_LOCAL_TIME.format((LocalTime) obj));
                break;
            case TIMESTAMP:
                builder.value(obj == null ? null : ELASTIC_ISO_LOCAL_DATE_TIME.format((LocalDateTime) obj));
                break;
            case INSTANT:
                builder.field(avif.getName(), obj == null ? null : ELASTIC_ISO_INSTANT.format(OffsetDateTime.ofInstant((Instant) obj, UTC_ZONE_ID)));
                break;
            case NUMBER:
                builder.value((Double) obj);
                break;
            case INTEGER:
                builder.value((Long) obj);
                break;
            case STRING:
                String val = (String) obj;
                // Discard blank lines.
                if (StringUtils.isBlank(val)) {
                    val = null;
                }
                builder.value(val);
                break;
            default:
                break;
            }
        }
        builder.endArray();
    }

    private void bulk(boolean refresh, BulkCollector collector) {

        for (String forIndex : collector.getIndexes()) {

            BulkRequestBuilder builder = client.prepareBulk().setRefreshPolicy(refresh
                    ? WriteRequest.RefreshPolicy.IMMEDIATE
                    : WriteRequest.RefreshPolicy.NONE);

            for (DeleteRequest dr : collector.getDelete(forIndex)) {
                builder.add(dr);
            }

            for (IndexRequest ir : collector.getIndex(forIndex)) {
                builder.add(ir);
            }

            for (UpdateRequest ur : collector.getUpdate(forIndex)) {
                builder.add(ur);
            }

            if (CollectionUtils.isEmpty(builder.request().requests())) {
                continue;
            }

            BulkResponse response = executeRequest(builder);
            if (response.hasFailures()) {
                final String message = response.buildFailureMessage();
                LOGGER.error("Error during indexing {}", message);
                throw new SearchApplicationException(message, SearchExceptionIds.EX_INDEXING_EXCEPTION, response);
            }
        }
    }

    private void throwIOExceptionFailure(IOException e) {
        final String message = "Document build failed. XContentBuilder threw an exception. {}.";
        LOGGER.warn(message, e);
        throw new SearchApplicationException(message, e,
                SearchExceptionIds.EX_SEARCH_DOCUMENT_BUILD_FAILED);
    }
}
