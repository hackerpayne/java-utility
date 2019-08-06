package com.lingdonge.core.algorithm;

import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import com.google.common.primitives.UnsignedBytes;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 牺牲吞吐效率（性能瓶颈在IO）来换取强一致性和无大小限制的BloomFilter，直接在硬盘上随机读写，不载入内存
 *
 * Guava版本 21.0
 *
 *
 * Guava中BloomFilter序列化格式：
 * +-----------------------------------------------------------------------+
 * | strategyOrdinal | numHashFunctions | dataLength |      BitArray       |
 * |     1 byte      |      1 byte      |    1 int   |  8*dataLength bytes |
 * +-----------------------------------------------------------------------+
 * 采用的numHashFunctions个Hash函数实现：
 * 首先使用murmurHash3对Object生成一个128位的值，取低64位值为combinedHash
 * 1.mask combinedHash的符号位后对BitArray.Size进行取余后的值就是新的置true的位置
 * 2.combinedHash更新为combinedHash += 高64位的值
 * 按上2步骤循环numHashFunctions次
 *
 * Created by shaoxiong on 17-7-1.
 */
public class DirectDiskUrlFilter {

    private Funnel funnel;
    private File filterFile;
    private RandomAccessFile raf;
    private int numHashFunctions;
    private int dataLength;
    private long bitsSize;
    private Bits bits;

    /* 1 byte + 1 byte + 1 int */
    static final int OPERATION_OFFSET = 6;

    /**
     *
     * @param filterPath 原Guava序列化存储的文件路径
     * @param funnel     原Guava BloomFilter使用的Funnel
     * @throws IOException
     */
    public DirectDiskUrlFilter(String filterPath, Funnel<CharSequence> funnel) throws IOException {
        filterFile = new File(filterPath);
        raf = new RandomAccessFile(filterFile, "rw");
        /* jump strategyOrdinal value */
        raf.readByte();
        numHashFunctions = UnsignedBytes.toInt(raf.readByte());
        dataLength = raf.readInt();
        bitsSize = (long)dataLength * 64L;
        bits = new Bits();
        this.funnel = funnel;
    }

    public boolean put(String url) throws IOException {
        byte[] bytes = Hashing.murmur3_128().hashObject(url, funnel).asBytes();
        long hash1 = this.lowerEight(bytes);
        long hash2 = this.upperEight(bytes);
        boolean bitsChanged = false;
        long combinedHash = hash1;

        for(int i = 0; i < numHashFunctions; ++i) {
            /* mask combinedHash with 0x7FFFFFFFFFFFFFFF */
            bitsChanged |= bits.set((combinedHash & 9223372036854775807L) % bitsSize);
            combinedHash += hash2;
        }

        return bitsChanged;
    }

    public boolean mightContain(String url) throws IOException {
        byte[] bytes = Hashing.murmur3_128().hashObject(url, funnel).asBytes();
        long hash1 = this.lowerEight(bytes);
        long hash2 = this.upperEight(bytes);
        long combinedHash = hash1;

        for(int i = 0; i < numHashFunctions; ++i) {
            if(!bits.get((combinedHash & 9223372036854775807L) % bitsSize)) {
                return false;
            }

            combinedHash += hash2;
        }

        return true;
    }

    /**
     * 使用完毕要记得关闭IO流
     *
     * @throws IOException
     */
    public void close() throws IOException {
        raf.close();
    }

    private long lowerEight(byte[] bytes) {
        return Longs.fromBytes(bytes[7], bytes[6], bytes[5], bytes[4], bytes[3], bytes[2], bytes[1], bytes[0]);
    }

    private long upperEight(byte[] bytes) {
        return Longs.fromBytes(bytes[15], bytes[14], bytes[13], bytes[12], bytes[11], bytes[10], bytes[9], bytes[8]);
    }

    /**
     * ((index >>> 6) << 3) 与 index >>> 3 的效果完全是不一样的，请特别注意
     * 由于存储的时候 1 byte = 8 bits，所以这里重写了定位（取余）的算法
     */
    final class Bits {

        boolean set(long index) throws IOException {
            long blockValue = this.getBlockValue(index);
            if(blockValue != -1) {
                raf.seek(OPERATION_OFFSET + ((index >>> 6) << 3));
                raf.writeLong(blockValue | (1L << (int)index));
                return true;
            } else {
                return false;
            }
        }

        boolean get(long index) throws IOException {
            raf.seek(OPERATION_OFFSET + ((index >>> 6) << 3));
            return (raf.readLong() & (1L << (int)index)) != 0L;
        }

        long getBlockValue(long index) throws IOException {
            raf.seek(OPERATION_OFFSET + ((index >>> 6) << 3));
            long blockValue = raf.readLong();
            if((blockValue & (1L << (int)index)) == 0L){
                return blockValue;
            }
            return -1;
        }
    }
}
