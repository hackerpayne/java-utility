package com.lindonge.core.file.fileThreads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Observable;

/**
 *
 * 读取文件
 */
public class ReadFile extends Observable {

    private int bufSize = 1024;

    // 换行符
    private byte key = "\n".getBytes()[0];

    // 当前行数
    private long lineNum = 0;

    // 文件编码,默认为gb2312
    private String encode = "gb2312";

    // 具体业务逻辑监听器
    private ReaderFileListener readerListener;

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public void setReaderListener(ReaderFileListener readerListener) {
        this.readerListener = readerListener;
    }

    /**
     * 获取准确开始位置
     *
     * @param file
     * @param position
     * @return
     * @throws Exception
     */
    public long getStartNum(File file, long position) throws Exception {
        long startNum = position;
        FileChannel fcin = new RandomAccessFile(file, "r").getChannel();
        fcin.position(position);
        try {
            int cache = 1024;
            ByteBuffer rBuffer = ByteBuffer.allocate(cache);
            // 每次读取的内容
            byte[] bs = new byte[cache];
            // 缓存
            byte[] tempBs = new byte[0];
            String line = "";
            while (fcin.read(rBuffer) != -1) {
                int rSize = rBuffer.position();
                rBuffer.rewind();
                rBuffer.get(bs);
                rBuffer.clear();
                byte[] newStrByte = bs;
                // 如果发现有上次未读完的缓存,则将它加到当前读取的内容前面
                if (null != tempBs) {
                    int tL = tempBs.length;
                    newStrByte = new byte[rSize + tL];
                    System.arraycopy(tempBs, 0, newStrByte, 0, tL);
                    System.arraycopy(bs, 0, newStrByte, tL, rSize);
                }
                // 获取开始位置之后的第一个换行符
                int endIndex = indexOf(newStrByte, 0);
                if (endIndex != -1) {
                    return startNum + endIndex;
                }
                tempBs = substring(newStrByte, 0, newStrByte.length);
                startNum += 1024;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fcin.close();
        }
        return position;
    }

    /**
     * 从设置的开始位置读取文件，一直到结束为止。如果 end设置为负数,刚读取到文件末尾
     *
     * @param fullPath
     * @param start
     * @param end
     * @throws Exception
     */
    public void readFileByLine(String fullPath, long start, long end) throws Exception {
        File fin = new File(fullPath);
        if (fin.exists()) {
            FileChannel fcin = new RandomAccessFile(fin, "r").getChannel();
            fcin.position(start);
            try {
                ByteBuffer rBuffer = ByteBuffer.allocate(bufSize);
                // 每次读取的内容
                byte[] bs = new byte[bufSize];
                // 缓存
                byte[] tempBs = new byte[0];
                String line = "";
                // 当前读取文件位置
                long nowCur = start;
                while (fcin.read(rBuffer) != -1) {
                    nowCur += bufSize;

                    int rSize = rBuffer.position();
                    rBuffer.rewind();
                    rBuffer.get(bs);
                    rBuffer.clear();
                    byte[] newStrByte = bs;
                    // 如果发现有上次未读完的缓存,则将它加到当前读取的内容前面
                    if (null != tempBs) {
                        int tL = tempBs.length;
                        newStrByte = new byte[rSize + tL];
                        System.arraycopy(tempBs, 0, newStrByte, 0, tL);
                        System.arraycopy(bs, 0, newStrByte, tL, rSize);
                    }
                    // 是否已经读到最后一位
                    boolean isEnd = false;
                    // 如果当前读取的位数已经比设置的结束位置大的时候，将读取的内容截取到设置的结束位置
                    if (end > 0 && nowCur > end) {
                        // 缓存长度 - 当前已经读取位数 - 最后位数
                        int l = newStrByte.length - (int) (nowCur - end);
                        newStrByte = substring(newStrByte, 0, l);
                        isEnd = true;
                    }
                    int fromIndex = 0;
                    int endIndex = 0;
                    // 每次读一行内容，以 key（默认为\n） 作为结束符
                    while ((endIndex = indexOf(newStrByte, fromIndex)) != -1) {
                        byte[] bLine = substring(newStrByte, fromIndex, endIndex);
                        line = new String(bLine, 0, bLine.length, encode);
                        lineNum++;
                        // 输出一行内容，处理方式由调用方提供
                        readerListener.outLine(line.trim(), lineNum, false);
                        fromIndex = endIndex + 1;
                    }
                    // 将未读取完成的内容放到缓存中
                    tempBs = substring(newStrByte, fromIndex, newStrByte.length);
                    if (isEnd) {
                        break;
                    }
                }
                // 将剩下的最后内容作为一行，输出，并指明这是最后一行
                String lineStr = new String(tempBs, 0, tempBs.length, encode);
                readerListener.outLine(lineStr.trim(), lineNum, true);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fcin.close();
            }

        } else {
            throw new FileNotFoundException("没有找到文件：" + fullPath);
        }
        // 通知观察者,当前工作已经完成
        setChanged();
        notifyObservers(start + "-" + end);
    }

    /**
     * 查找一个byte[]从指定位置之后的一个换行符位置
     *
     * @param src
     * @param fromIndex
     * @return
     * @throws Exception
     */
    private int indexOf(byte[] src, int fromIndex) throws Exception {

        for (int i = fromIndex; i < src.length; i++) {
            if (src[i] == key) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 从指定开始位置读取一个byte[]直到指定结束位置为止生成一个全新的byte[]
     *
     * @param src
     * @param fromIndex
     * @param endIndex
     * @return
     * @throws Exception
     */
    private byte[] substring(byte[] src, int fromIndex, int endIndex) throws Exception {
        int size = endIndex - fromIndex;
        byte[] ret = new byte[size];
        System.arraycopy(src, fromIndex, ret, 0, size);
        return ret;
    }

}