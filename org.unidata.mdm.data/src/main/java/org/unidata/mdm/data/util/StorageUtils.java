package org.unidata.mdm.data.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.core.type.keys.ExternalId;
import org.unidata.mdm.data.configuration.DataConfiguration;
import org.unidata.mdm.data.service.DataStorageService;
import org.unidata.mdm.data.type.storage.DataNode;

/**
 * @author Mikhail Mikhailov
 * The code is taken from PostgreSQL C sources and adopted to Java.
 * The purpose is to calculate integer partition/shard number for record UUID exactly in the same way,
 * as PostgreSQL does it for hash partitioning.
 */
public class StorageUtils {
    /**
     * PG fixed seed value.
     */
    private static final long PARTITION_SEED = 0x7a5b22367996dcfdL;
    /**
     * Unsigned 32 bit integer mask.
     */
    private static final long UINT32_MASK = 0xffffffffL;
    /**
     * Unsigned char mask.
     */
    private static final short UCHAR8_MASK = 0xff;
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageUtils.class);
    /**
     * Storage service instance.
     */
    private static DataStorageService storageService;
    /**
     * Constructor.
     */
    private StorageUtils() {
        super();
    }

    public static void init() {
        try {
            storageService = DataConfiguration.getBean(DataStorageService.class);
        } catch (Exception exc) {
            LOGGER.warn("Meta model service bean GET. Exception caught.", exc);
        }
    }
    /**
     * Gets current number of shards.
     * @return number of shards
     */
    public static int numberOfShards() {
        return storageService.getShardsCount();
    }
    /**
     * Gets current number of nodes.
     * @return number of nodes
     */
    public static int numberOfNodes() {
        return storageService.getNodesCount();
    }
    /**
     * Calculates the node number for a UUID value.
     * @param val the value
     * @return node number
     */
    public static DataNode node(UUID val) {
        int node = node(shard(val));
        return storageService.getCluster().getNodes()[node];
    }
    /**
     * Calculates the node number for an ExternalId value.
     * @param val the value
     * @return node number
     */
    public static DataNode node(ExternalId val) {
        int node = node(shard(val));
        return storageService.getCluster().getNodes()[node];
    }
    /**
     * Returns the node number for the given shard.
     * @param shard the shard number
     * @return the node number
     */
    public static int node(int shard) {
        // 1. Temporary. Simple stuff here.
        // Shard index is zero based
        return (shard + 1) % numberOfNodes();
    }
    /**
     * Calculates external key's shard number.
     * @param id the external id
     * @return shard number
     */
    public static int shard(ExternalId id)  {
        return shard(id.compact());
    }
    /**
     * Calculates external key's shard number.
     * @param key the external id
     * @param entityName the entity name
     * @param sourceSystem the source system
     * @return shard number
     */
    public static int shard(String key, String entityName, String sourceSystem)  {
        return shard(ExternalId.compact(key, entityName, sourceSystem));
    }
    /**
     * Calculates compacted external key's shard number.
     * @param key the compacted external id
     * @return shard number
     */
    public static int shard(String key) {

        // 1. Only one shard - standard and possibly enterprise. Nothing to calculate.
        // Shard index is zero based
        if (numberOfShards() == 1) {
            return 0;
        }

        return shard(new PartitionNumberCalculationContext(key));
    }
    /**
     * Calculates partition number for UUID (i. e. etalon id).
     * @param value the value
     * @return shard number
     */
    public static int shard(UUID value) {

        // Only one shard - standard and possibly enterprise. Nothing to calculate.
        // Shard index is zero based
        if (numberOfShards() == 1) {
            return 0;
        }

        return shard(new PartitionNumberCalculationContext(value));
    }
    /**
     * Calculates partition number for UUID (i. e. etalon id).
     * @param value the value
     * @return shard number
     */
    private static int shard(PartitionNumberCalculationContext ctx) {

        // Mix always first, since seed is always used.
        mix(ctx);

        int l = ctx.k.length;
        int i = 0;
        while (l >= 12) {

            ctx.a = uia(ctx.a, ctx.uint32(i));
            ctx.b = uia(ctx.b, ctx.uint32(i + 4));
            ctx.c = uia(ctx.c, ctx.uint32(i + 8));

            mix(ctx);

            l -= 12;
            i += 12;
        }

        remaining(ctx, l, i);
        finish(ctx);

        long result = combine((ctx.b << 32) | ctx.c);

        // 2. Take remainder. Guava reminder is significantly faster, than Long.remainderUnsigned().
        return (int) remainder(result, numberOfShards());
    }
    /**
     * Calculate the rest of the bytes into the context.
     * @param ctx the context
     * @param remaining the number of remaining bytes
     * @param position the position in the byte buffer
     */
    private static void remaining(PartitionNumberCalculationContext ctx, int remaining, int position) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            remainingBE(ctx, remaining, position);
        } else {
            remainingLE(ctx, remaining, position);
        }
    }
    /**
     * LE variant.
     * @param ctx the context
     * @param remaining remaining bytes to process
     * @param i current position
     */
    private static void remainingLE(PartitionNumberCalculationContext ctx, int remaining, int i) {

        switch (remaining)
        {
            case 11:
                ctx.c = uia(ctx.c, (ctx.uchar8(i + 10) << 24));
                /* fall through */
            case 10:
                ctx.c = uia(ctx.c, (ctx.uchar8(i + 9) << 16));
                /* fall through */
            case 9:
                ctx.c = uia(ctx.c, (ctx.uchar8(i + 8) << 8));
                /* fall through */
            case 8:
                /* the lowest byte of c is reserved for the length */
                ctx.b = uia(ctx.b, ctx.uchar8(i + 7) << 24);
                /* fall through */
            case 7:
                ctx.b = uia(ctx.b, (ctx.uchar8(i + 6) << 16));
                /* fall through */
            case 6:
                ctx.b = uia(ctx.b, (ctx.uchar8(i + 5) << 8));
                /* fall through */
            case 5:
                ctx.b = uia(ctx.b, ctx.uchar8(i + 4));
                /* fall through */
            case 4:
                ctx.a = uia(ctx.a, ctx.uchar8(i + 3) << 24);
                /* fall through */
            case 3:
                ctx.a = uia(ctx.a, (ctx.uchar8(i + 2) << 16));
                /* fall through */
            case 2:
                ctx.a = uia(ctx.a, (ctx.uchar8(i + 1) << 8));
                /* fall through */
            case 1:
                ctx.a = uia(ctx.a, ctx.uchar8(i));
                /* case 0: nothing left to add */
                break;
            default:
                break;
        }
    }
    /**
     * BE variant.
     * @param ctx the context
     * @param remaining remaining bytes to process
     * @param i current position
     */
    private static void remainingBE(PartitionNumberCalculationContext ctx, int remaining, int i) {

        switch (remaining)
        {
            case 11:
                ctx.c = uia(ctx.c, (ctx.uchar8(i + 10) << 8));
                /* fall through */
            case 10:
                ctx.c = uia(ctx.c, (ctx.uchar8(i + 9) << 16));
                /* fall through */
            case 9:
                ctx.c = uia(ctx.c, (ctx.uchar8(i + 8) << 24));
                /* fall through */
            case 8:
                /* the lowest byte of c is reserved for the length */
                ctx.b = uia(ctx.b, ctx.uchar8(i + 7));
                /* fall through */
            case 7:
                ctx.b = uia(ctx.b, (ctx.uchar8(i + 6) << 8));
                /* fall through */
            case 6:
                ctx.b = uia(ctx.b, (ctx.uchar8(i + 5) << 16));
                /* fall through */
            case 5:
                ctx.b = uia(ctx.b, (ctx.uchar8(i + 4) << 24));
                /* fall through */
            case 4:
                ctx.a = uia(ctx.a, ctx.uchar8(i + 3));
                /* fall through */
            case 3:
                ctx.a = uia(ctx.a, (ctx.uchar8(i + 2) << 8));
                /* fall through */
            case 2:
                ctx.a = uia(ctx.a, (ctx.uchar8(i + 1) << 16));
                /* fall through */
            case 1:
                ctx.a = uia(ctx.a, (ctx.uchar8(i) << 24));
                /* case 0: nothing left to add */
                break;
            default:
                break;
        }
    }
    /* Rotate a uint32 value left by k bits - note multiple evaluation! */
    private static long rotate(long x, long k) {
        return ((x << k) | (x >> (32 - k))) & UINT32_MASK;
    }
    /**
     * Final -- final mixing of 3 32-bit values (a,b,c) into c
     *
     * Pairs of (a,b,c) values differing in only a few bits will usually
     * produce values of c that look totally different.  This was tested for
     * * pairs that differed by one bit, by two bits, in any combination
     *   of top bits of (a,b,c), or in any combination of bottom bits of
     *   (a,b,c).
     * * "differ" is defined as +, -, ^, or ~^.  For + and -, I transformed
     *   the output delta to a Gray code (a^(a>>1)) so a string of 1's (as
     *   is commonly produced by subtraction) look like a single 1-bit
     *   difference.
     * * the base values were pseudorandom, all zero but one bit set, or
     *   all zero plus a counter that starts at zero.
     *
     * The use of separate functions for mix() and final() allow for a
     * substantial performance increase since final() does not need to
     * do well in reverse, but is does need to affect all output bits.
     * mix(), on the other hand, does not need to affect all output
     * bits (affecting 32 bits is enough).  The original hash function had
     * a single mixing operation that had to satisfy both sets of requirements
     * and was slower as a result.
     *----------
     */
    private static void finish(PartitionNumberCalculationContext ctx) {
        ctx.c ^= ctx.b; ctx.c = uis(ctx.c, rotate(ctx.b, 14));
        ctx.a ^= ctx.c; ctx.a = uis(ctx.a, rotate(ctx.c, 11));
        ctx.b ^= ctx.a; ctx.b = uis(ctx.b, rotate(ctx.a, 25));
        ctx.c ^= ctx.b; ctx.c = uis(ctx.c, rotate(ctx.b, 16));
        ctx.a ^= ctx.c; ctx.a = uis(ctx.a, rotate(ctx.c, 4));
        ctx.b ^= ctx.a; ctx.b = uis(ctx.b, rotate(ctx.a, 14));
        ctx.c ^= ctx.b; ctx.c = uis(ctx.c, rotate(ctx.b, 24));
    }

    /**
     * Mix -- mix 3 32-bit values reversibly.
     *
     * This is reversible, so any information in (a,b,c) before mix() is
     * still in (a,b,c) after mix().
     *
     * If four pairs of (a,b,c) inputs are run through mix(), or through
     * mix() in reverse, there are at least 32 bits of the output that
     * are sometimes the same for one pair and different for another pair.
     * This was tested for:
     * * pairs that differed by one bit, by two bits, in any combination
     *   of top bits of (a,b,c), or in any combination of bottom bits of
     *   (a,b,c).
     * * "differ" is defined as +, -, ^, or ~^.  For + and -, I transformed
     *   the output delta to a Gray code (a^(a>>1)) so a string of 1's (as
     *   is commonly produced by subtraction) look like a single 1-bit
     *   difference.
     * * the base values were pseudorandom, all zero but one bit set, or
     *   all zero plus a counter that starts at zero.
     *
     * This does not achieve avalanche.  There are input bits of (a,b,c)
     * that fail to affect some output bits of (a,b,c), especially of a.  The
     * most thoroughly mixed value is c, but it doesn't really even achieve
     * avalanche in c.
     *
     * This allows some parallelism.  Read-after-writes are good at doubling
     * the number of bits affected, so the goal of mixing pulls in the opposite
     * direction from the goal of parallelism.  I did what I could.  Rotates
     * seem to cost as much as shifts on every machine I could lay my hands on,
     * and rotates are much kinder to the top and bottom bits, so I used rotates.
     *----------
     */
    private static void mix(PartitionNumberCalculationContext ctx) {
        ctx.a = uis(ctx.a, ctx.c);  ctx.a ^= rotate(ctx.c, 4);  ctx.c = uia(ctx.c, ctx.b);
        ctx.b = uis(ctx.b, ctx.a);  ctx.b ^= rotate(ctx.a, 6);  ctx.a = uia(ctx.a, ctx.c);
        ctx.c = uis(ctx.c, ctx.b);  ctx.c ^= rotate(ctx.b, 8);  ctx.b = uia(ctx.b, ctx.a);
        ctx.a = uis(ctx.a, ctx.c);  ctx.a ^= rotate(ctx.c,16);  ctx.c = uia(ctx.c, ctx.b);
        ctx.b = uis(ctx.b, ctx.a);  ctx.b ^= rotate(ctx.a,19);  ctx.a = uia(ctx.a, ctx.c);
        ctx.c = uis(ctx.c, ctx.b);  ctx.c ^= rotate(ctx.b, 4);  ctx.b = uia(ctx.b, ctx.a);
    }

    /**
     * 0x49a0f4dd15e5a8e3 is 64bit random data.
     * Original:
     * a ^= b + UINT64CONST(0x49a0f4dd15e5a8e3) + (a << 54) + (a >> 7);
     * return a;
     * Since no other fields are participating in shards calculation omit the mix.
     */
    private static long combine(long value) {
        return value + 0x49a0f4dd15e5a8e3L;
    }
    /**
     * Unsigned Int Add:
     * This ugly method tries to mimic C unsigned int overflow approach known as reminder rotation.
     * @param first the value to increment
     * @param second the value to increment by
     * @return result
     */
    private static long uia(long first, long second) {
        return (first + second) & UINT32_MASK;
    }
    /**
     * Unsigned Int Subtract:
     * Same as above for unsigned int underflow.
     * @param first the value to decrement
     * @param second the value to decrement by
     * @return result
     */
    private static long uis(long first, long second) {
        return (first - second) & UINT32_MASK;
    }
    /**
     * Taken from Guava.
     * Copyright (C) 2011 The Guava Authors
     *
     * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
     * in compliance with the License. You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software distributed under the License
     * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
     * or implied. See the License for the specific language governing permissions and limitations under
     * the License.
     *
     * Returns dividend % divisor, where the dividend and divisor are treated as unsigned 64-bit
     * quantities.
     *
     * <p><b>Java 8 users:</b> use {@link Long#remainderUnsigned(long, long)} instead.
     *
     * @param dividend the dividend (numerator)
     * @param divisor the divisor (denominator)
     * @throws ArithmeticException if divisor is 0
     * @since 11.0
     */
    private static long remainder(long dividend, long divisor) {

        if (divisor < 0) { // i.e., divisor >= 2^63:
            if (Long.compareUnsigned(dividend, divisor) < 0) {
                return dividend; // dividend < divisor
            } else {
                return dividend - divisor; // dividend >= divisor
            }
        }

        // Optimization - use signed modulus if dividend < 2^63
        if (dividend >= 0) {
            return dividend % divisor;
        }

        /*
         * Otherwise, approximate the quotient, check, and correct if necessary. Our approximation is
         * guaranteed to be either exact or one less than the correct value. This follows from the fact
         * that floor(floor(x)/i) == floor(x/i) for any real x and integer i != 0. The proof is not
         * quite trivial.
         */
        long quotient = ((dividend >>> 1) / divisor) << 1;
        long rem = dividend - quotient * divisor;
        return rem - (Long.compareUnsigned(rem, divisor) >= 0 ? divisor : 0);
    }
    /**
     * @author Mikhail Mikhailov
     * Calculation state.
     */
    private static class PartitionNumberCalculationContext {
        /**
         * Calc state 'a'.
         */
        long a;
        /**
         * Calc state 'b'.
         */
        long b;
        /**
         * Calc state 'c'.
         */
        long c;
        /**
         * The bytes.
         */
        byte[] k;
        /**
         * PG sources say:
         *
         * In essence, the seed is treated as part of the data being hashed,
         * but for simplicity, we pretend that it's padded with four bytes of
         * zeroes so that the seed constitutes a 12-byte chunk.
         *
         * @param length
         */
        private void init() {
            a = b = c = (0x9e3779b9L + this.k.length + 3923095L);
            a = uia(a, (PARTITION_SEED >> 32)) ;
            b = uia(b , PARTITION_SEED);
        }
        /**
         * Ext keys constructor.
         * @param value compacted ext. key.
         */
        public PartitionNumberCalculationContext(String value) {
            super();
            this.k = value.getBytes(StandardCharsets.UTF_8);

            init();
        }

        /**
         * UUID constructor.
         */
        public PartitionNumberCalculationContext(UUID value) {

            super();
            this.k = new byte[16];

            // Java stores UUID parts in two longs using its own layout.
            // PG stores it as bytes in the same order as they appear in string notation (uchar array).
            long msb = value.getMostSignificantBits();

            if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
                k[0] = (byte) (msb & UCHAR8_MASK);
                k[1] = (byte) ((msb >> 8) & UCHAR8_MASK);
                k[2] = (byte) ((msb >> 16) & UCHAR8_MASK);
                k[3] = (byte) ((msb >> 24) & UCHAR8_MASK);
                k[4] = (byte) ((msb >> 32) & UCHAR8_MASK);
                k[5] = (byte) ((msb >> 40) & UCHAR8_MASK);
                k[6] = (byte) ((msb >> 48) & UCHAR8_MASK);
                k[7] = (byte) ((msb >> 56) & UCHAR8_MASK);
            } else {
                k[0] = (byte) ((msb >> 56) & UCHAR8_MASK);
                k[1] = (byte) ((msb >> 48) & UCHAR8_MASK);
                k[2] = (byte) ((msb >> 40) & UCHAR8_MASK);
                k[3] = (byte) ((msb >> 32) & UCHAR8_MASK);
                k[4] = (byte) ((msb >> 24) & UCHAR8_MASK);
                k[5] = (byte) ((msb >> 16) & UCHAR8_MASK);
                k[6] = (byte) ((msb >> 8) & UCHAR8_MASK);
                k[7] = (byte) (msb & UCHAR8_MASK);
            }

            long lsb = value.getLeastSignificantBits();
            if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
                k[8] = (byte) (lsb & UCHAR8_MASK);
                k[9] = (byte) ((lsb >> 8) & UCHAR8_MASK);
                k[10] = (byte) ((lsb >> 16) & UCHAR8_MASK);
                k[11] = (byte) ((lsb >> 24) & UCHAR8_MASK);
                k[12] = (byte) ((lsb >> 32) & UCHAR8_MASK);
                k[13] = (byte) ((lsb >> 40) & UCHAR8_MASK);
                k[14] = (byte) ((lsb >> 48) & UCHAR8_MASK);
                k[15] = (byte) ((lsb >> 56) & UCHAR8_MASK);
            } else {
                k[8] = (byte) ((lsb >> 56) & UCHAR8_MASK);
                k[9] = (byte) ((lsb >> 48) & UCHAR8_MASK);
                k[10] = (byte) ((lsb >> 40) & UCHAR8_MASK);
                k[11] = (byte) ((lsb >> 32) & UCHAR8_MASK);
                k[12] = (byte) ((lsb >> 24) & UCHAR8_MASK);
                k[13] = (byte) ((lsb >> 16) & UCHAR8_MASK);
                k[14] = (byte) ((lsb >> 8) & UCHAR8_MASK);
                k[15] = (byte) (lsb & UCHAR8_MASK);
            }

            init();
        }
        /**
         * Gets uint32 bit mask from the given byte offset similar to {@link ByteBuffer#getInt(int)}.
         * @param offset the byte offset
         * @return uint32 mask
         */
        public long uint32(int offset) {
            if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
                return  ((this.k[offset + 3] & UCHAR8_MASK) +
                        ((this.k[offset + 2] & UCHAR8_MASK) << 8) +
                        ((this.k[offset + 1] & UCHAR8_MASK) << 16) +
                        ((this.k[offset] & UCHAR8_MASK) << 24)) & UINT32_MASK;
            else
                return  ((this.k[offset] & UCHAR8_MASK) +
                        ((this.k[offset + 1] & UCHAR8_MASK) << 8) +
                        ((this.k[offset + 2] & UCHAR8_MASK) << 16) +
                        ((this.k[offset + 3] & UCHAR8_MASK) << 24)) & UINT32_MASK;
        }
        /**
         * Gets uchar8 bit mask from the given byte offset similar to {@link ByteBuffer#getInt(int)}.
         * @param offset the byte offset
         * @return uchar8 mask
         */
        public long uchar8(int offset) {
            return this.k[offset] & UCHAR8_MASK;
        }
    }
}
