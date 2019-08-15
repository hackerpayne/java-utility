package com.lingdonge.core.collection;

import com.lingdonge.core.util.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合相关工具类，包括数组
 */
public class CollectionUtil {

    /**
     * 删除列表中的指定元素
     *
     * @param list        需要处理的列表
     * @param removeItems 待删除的元素
     * @return
     */
    public static List<String> removeItems(List<String> list, String... removeItems) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();

            for (String item : removeItems) {
                if (next.equals(item)) {
                    iterator.remove();
                }
            }
        }
        return list;
    }

    /**
     * 把List拆分成多个List子集,主要通过偏移量来实现的
     *
     * @param source 源List
     * @param n
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {

        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n;  // (先计算出余数)
        int number = source.size() / n;  // 然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    /**
     * List转Array
     *
     * @param list List
     * @return Array
     */
    public static String[] toArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    /**
     * 按每max_number个一组分割
     *
     * @param max_number
     * @param data
     * @param type
     * @return
     */
    public static List<String> listSkip(Integer max_number, String data, int type) {
        List<String> sts = new ArrayList<String>();
        List<String> list = Arrays.asList(data.split(",")).stream().map(s -> String.valueOf(type == 0 ? DigestUtils.md5Hex(s.trim()).toUpperCase() : s.trim()))
                .collect(Collectors.toList());
        int limit = countStep(max_number, list.size());

        // 方法一：使用流遍历操作
        List<List<String>> mglist = new ArrayList<>();
        Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
            mglist.add(list.stream().skip(i * max_number).limit(max_number).collect(Collectors.toList()));
        });
        for (List<String> mg : mglist) {
            sts.add(String.join(",", mg.stream().map(Object::toString).collect(Collectors.toList())));
        }
        return sts;
        // 方法二：获取分割后的集合
//		List<List<Integer>> splitList = Stream.iterate(0, n -> n + 1).limit(limit).parallel()
//				.map(a -> list.stream().skip(a * MAX_NUMBER).limit(MAX_NUMBER).parallel().collect(Collectors.toList()))
//				.collect(Collectors.toList());
//
//		System.out.println(splitList);
    }

    /**
     * 计算切分次数
     */
    private static Integer countStep(Integer max_number, Integer size) {
        return (size + max_number - 1) / max_number;
    }


    /**
     * 取数组里面，随机位置的索引，需要传入的是数组的长度:arr.length
     * 亦可用于返回0-length之间值的方法以
     *
     * @param length
     * @return
     */
    public static int getRandomIndex(int length) {
        return (int) (Math.random() * length);
    }

    /**
     * 获取List随机结果一个
     *
     * @param listArray
     * @param <T>
     * @return
     */
    public static <T> T getRandomItem(List<T> listArray) {
        int index = (int) (Math.random() * listArray.size());
        return listArray.get(index);
    }

    /**
     * 为数组每一项添加开头和结尾字符串
     *
     * @param listArray
     * @param head
     * @param foot
     * @return
     */
    public static List<String> addStr(List<String> listArray, String head, String foot) {
        return addStr(listArray, head, foot, true);
    }

    /**
     * 在数组每一项的头尾增加字符串
     *
     * @param listArray
     * @param head
     * @param foot
     * @param ignoreEmpty
     * @return
     */
    public static List<String> addStr(List<String> listArray, String head, String foot, boolean ignoreEmpty) {
        for (int i = 0; i < listArray.size(); i++) {
            if (ignoreEmpty && StringUtils.isEmpty(listArray.get(i))) {
                continue;
            }
            if (StringUtils.isNotEmpty(head)) {
                listArray.set(i, head + listArray.get(i));
            }
            if (StringUtils.isNotEmpty(foot)) {
                listArray.set(i, listArray.get(i) + foot);
            }
        }
        return listArray;
    }


}