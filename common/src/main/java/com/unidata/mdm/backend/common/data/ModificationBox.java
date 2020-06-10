package com.unidata.mdm.backend.common.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.ValidityRange;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;

/**
 * @author Mikhail Mikhailov
 * Modification box for record periods.
 * Internally, the data for a source system + ext id is organized as follows:
 * -----------------------------------
 * |          modX (at tail)         |
 * -----------------------------------
 * |          mod2                   |
 * -----------------------------------
 * |          mod1                   |
 * -----------------------------------
 * |          base (if any at head)  |
 * -----------------------------------
 */
public class ModificationBox implements ValidityRange {
    /**
     * Modifications.
     */
    private final Map<String, Deque<CalculableHolder<DataRecord>>> box = new HashMap<>();
    /**
     * From boundary.
     */
    private Date validFrom;
    /**
     * To boundary.
     */
    private Date validTo;
    /**
     * Modification cause (starting point).
     */
    private DataRecord cause;
    /**
     * Current state (current etalon record).
     */
    private DataRecord etalonState;
    /**
     * Current state (current origin record).
     */
    private DataRecord originState;
    /**
     * Constructor.
     */
    private ModificationBox() {
        super();
    }
    /**
     *
     * @param input
     * @return
     */
    public static ModificationBox of(List<CalculableHolder<DataRecord>> input) {

        ModificationBox result = new ModificationBox();
        if (CollectionUtils.isNotEmpty(input)) {
            for (CalculableHolder<DataRecord> ch : input) {
                result.box.computeIfAbsent(toBoxKey(ch), key -> new ArrayDeque<>()).addFirst(ch);
            }
        }

        return result;
    }
    /**
     *
     * @param input
     * @return
     */
    public static ModificationBox of(List<CalculableHolder<DataRecord>> input, OriginRecord cause) {
        CalculableHolder<DataRecord> start = CalculableHolder.of(cause);
        ModificationBox result = of(input);
        result.cause = cause;
        result.push(start);
        return result;
    }
    /**
     * Creates box key for {@link RecordIdentityContext}.
     * @param ctx the context
     * @return key
     */
    public static String toBoxKey(RecordIdentityContext ctx) {
        RecordKeys keys = ctx.keys();
        String sourceSystem = keys == null ? ctx.getSourceSystem() : keys.getOriginKey().getSourceSystem();
        String externalId = keys == null ? ctx.getExternalId() : keys.getOriginKey().getExternalId();
        return String.join("|", sourceSystem, externalId);
    }
    /**
     * Creates box key for {@link RecordIdentityContext}.
     * @param key the origin key
     * @return key
     */
    public static String toBoxKey(OriginKey key) {
        return String.join("|", key.getSourceSystem(), key.getExternalId());
    }
    /**
     * Creates box key.
     * @param ch the holder
     * @return key
     */
    public static String toBoxKey(CalculableHolder<DataRecord> ch) {
        return String.join("|", ch.getSourceSystem(), ch.getExternalId());
    }
    /**
     * Creates box key for source system and external id strings.
     * @param sourceSystem the source system string
     * @param externalId the external id string
     * @return key string
     */
    public static String toBoxKey(String sourceSystem, String externalId) {
        return String.join("|", sourceSystem, externalId);
    }
    /**
     * From boundary.
     * @return from boundary
     */
    @Override
    public Date getValidFrom() {
        return validFrom;
    }
    /**
     * To boundary.
     * @return to boundary
     */
    @Override
    public Date getValidTo() {
        return validTo;
    }
    /**
     * Gets the calculation base for this box.
     * The map is not modifyable.
     */
    public List<CalculableHolder<DataRecord>> toCalculationBase() {

        List<CalculableHolder<DataRecord>> result = new ArrayList<>(box.size());
        Iterator<Entry<String, Deque<CalculableHolder<DataRecord>>>> it = box.entrySet().iterator();
        while (it.hasNext()) {

            Entry<String, Deque<CalculableHolder<DataRecord>>> entry = it.next();
            CalculableHolder<DataRecord> ch = entry.getValue().peekFirst();
            if (Objects.nonNull(ch) && ch.getRevision() > 0) {
                result.add(ch);
            }
        }

        return result;
    }
    /**
     * Gets the calculation modifications of this box.
     * The map is not modifyable.
     */
    public Map<String, List<CalculableHolder<DataRecord>>> toModifications() {
        return box.entrySet().stream().sequential()
                .flatMap(entry -> entry.getValue().stream().sequential())
                .filter(holder -> holder.getRevision() == 0)
                .collect(Collectors.groupingBy(ModificationBox::toBoxKey,
                         Collectors.mapping(holder -> holder, Collectors.toList())));
    }
    /**
     * Returns collection of top most claculables for all source systems.
     * @return collection
     */
    public List<CalculableHolder<DataRecord>> toCalculables() {

        List<CalculableHolder<DataRecord>> result = new ArrayList<>(box.size());
        Iterator<Entry<String, Deque<CalculableHolder<DataRecord>>>> it = box.entrySet().iterator();
        while (it.hasNext()) {

            Entry<String, Deque<CalculableHolder<DataRecord>>> entry = it.next();
            CalculableHolder<DataRecord> ch = entry.getValue().peekLast();
            if (Objects.nonNull(ch)) {
                result.add(ch);
            }
        }

        return result;
    }
    /**
     * Tells whether this box has modifications (is dirty).
     * @return true, if so, false otherwise
     */
    public boolean isDirty() {

        Iterator<Entry<String, Deque<CalculableHolder<DataRecord>>>> it = box.entrySet().iterator();
        while (it.hasNext()) {

            Entry<String, Deque<CalculableHolder<DataRecord>>> entry = it.next();
            CalculableHolder<DataRecord> ch = entry.getValue().peekLast();
            if (Objects.nonNull(ch) && ch.getRevision() == 0) {
                return true;
            }
        }

        return false;
    }
    /**
     * Pushes a calculable to the source system stack.
     * @param record calculable
     */
    public void push(CalculableHolder<DataRecord> record) {
        box.computeIfAbsent(toBoxKey(record), key -> new ArrayDeque<>(2))
           .addLast(record);
    }
    /**
     * Peeks the last added calculable by the source system.
     * @param boxKey the source system name
     * @return calculable or null
     */
    public CalculableHolder<DataRecord> peek(String boxKey) {
        Deque<CalculableHolder<DataRecord>> stack = box.get(boxKey);
        return stack != null ? stack.peekLast() : null;
    }
    /**
     * Gets the number of versions by source system and external id.
     * @param boxKey the source system name
     * @return number of versions
     */
    public int count(String boxKey) {
        Deque<CalculableHolder<DataRecord>> stack = box.get(boxKey);
        return stack != null ? stack.size() : 0;
    }
    /**
     * Resets versions to calculation base by source system and external id.
     * @param boxKey the source system name
     * @return number of versions
     */
    public List<CalculableHolder<DataRecord>> reset(String boxKey) {

        Deque<CalculableHolder<DataRecord>> stack = box.get(boxKey);
        List<CalculableHolder<DataRecord>> result = new ArrayList<>(stack != null ? stack.size() : 0);
        if (stack != null) {
            for (Iterator<CalculableHolder<DataRecord>> i = stack.iterator(); i.hasNext(); ) {

                CalculableHolder<DataRecord> ch = i.next();
                if (ch.getRevision() > 0) {
                    continue;
                }

                i.remove();
                result.add(ch);
            }
        }

        return result;
    }
    /**
     * Resets latest versions by given count, source system and external id.
     * @param boxKey the source system name
     * @return number of versions
     */
    public List<CalculableHolder<DataRecord>> resetBy(String boxKey, int count) {

        Deque<CalculableHolder<DataRecord>> stack = box.get(boxKey);
        List<CalculableHolder<DataRecord>> result = new ArrayList<>(stack != null ? stack.size() : 0);
        if (stack != null && count <= stack.size()) {

            int i = 0;
            int offset = stack.size() - count;
            for (Iterator<CalculableHolder<DataRecord>> ci = stack.iterator(); ci.hasNext(); ) {

                CalculableHolder<DataRecord> ch = ci.next();
                if (i++ < offset) {
                    continue;
                }

                ci.remove();
                result.add(ch);
            }
        }

        return result;
    }
    /**
     * Pops the last added calculable by the source system.
     * @param boxKey the source system name
     * @return calculable or null
     */
    public CalculableHolder<DataRecord> pop(String boxKey) {
        Deque<CalculableHolder<DataRecord>> stack = box.get(boxKey);
        return stack != null ? stack.pollLast() : null;
    }
    /**
     * Gets the modification cause.
     * @return initial record
     */
    @SuppressWarnings("unchecked")
    public<T extends DataRecord> T cause() {
        return (T) cause;
    }
    /**
     * Sets the current etalon state.
     * @param state the state to set
     */
    public void etalonState(DataRecord state) {
        this.etalonState = state;
    }
    /**
     * Gets the current etalon state.
     * @return current state
     */
    @SuppressWarnings("unchecked")
    public<T extends DataRecord> T etalonState() {
        return (T) etalonState;
    }
    /**
     * Sets the origin state.
     * @param state the state to set
     */
    public void originState(DataRecord state) {
        this.originState = state;
    }
    /**
     * Gets the current origin state.
     * @return current state
     */
    @SuppressWarnings("unchecked")
    public<T extends DataRecord> T originState() {
        return (T) originState;
    }
}
