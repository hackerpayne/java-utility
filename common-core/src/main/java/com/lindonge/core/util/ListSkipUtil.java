package com.lindonge.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListSkipUtil {
	private Logger logger = LoggerFactory.getLogger(getClass());
	// 按每max_number个一组分割
	public static List<String> ListSkip(Integer max_number, String data,int type) {
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
		System.out.println(sts);
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

	public static void main(String[] args) {
		ListSkip(12,"1,2,3,4,5,6,7,8,9,12,12,23,4,5,5",0);
		ListSkip(12,"1,2,3,4,5,6,7,8,9,12,12,23,4,5,5",1);
	}
}
