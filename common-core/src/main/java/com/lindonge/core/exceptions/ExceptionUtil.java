package com.lindonge.core.exceptions;


import com.lindonge.core.file.FastByteArrayOutputStream;
import com.lindonge.core.util.JudgeUtil;
import com.lindonge.core.util.StringUtils;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常工具类
 * 
 * @author Looly
 *
 */
public final class ExceptionUtil {
	
	private ExceptionUtil(){};

	/**
	 * 获得完整消息，包括异常名
	 * 
	 * @param e 异常
	 * @return 完整消息
	 */
	public static String getMessage(Throwable e) {
		return StringUtils.format("{}: {}", e.getClass().getSimpleName(), e.getMessage());
	}
	
	/**
	 * 使用运行时异常包装编译异常
	 * @param throwable 异常
	 * @return 运行时异常
	 */
	public static RuntimeException wrapRuntime(Throwable throwable){
		if(throwable instanceof RuntimeException){
			return (RuntimeException) throwable;
		}else{
			return new RuntimeException(throwable);
		}
	}

	/**
	 * 剥离反射引发的InvocationTargetException、UndeclaredThrowableException中间异常，返回业务本身的异常
	 * @param wrapped 包装的异常
	 * @return 剥离后的异常
	 */
	public static Throwable unwrap(Throwable wrapped) {
		Throwable unwrapped = wrapped;
		while (true) {
			if (unwrapped instanceof InvocationTargetException) {
				unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
			} else if (unwrapped instanceof UndeclaredThrowableException) {
				unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
			} else {
				return unwrapped;
			}
		}
	}
	
	/**
	 * 获取当前栈信息
	 * 
	 * @return 当前栈信息
	 */
	public static StackTraceElement[] getStackElements() {
//		return (new Throwable()).getStackTrace();
		return Thread.currentThread().getStackTrace();
	}
	
	
	
	/**
	 * 堆栈转为单行完整字符串
	 * @param throwable 异常对象
	 * @return 堆栈转为的字符串
	 */
	public static String stacktraceToOneLineString(Throwable throwable){
		return stacktraceToOneLineString(throwable, 3000);
	}
	
	/**
	 * 堆栈转为单行完整字符串
	 * @param throwable 异常对象
	 * @param limit 限制最大长度
	 * @return 堆栈转为的字符串
	 */
	public static String stacktraceToOneLineString(Throwable throwable, int limit){
		Map<Character, String> replaceCharToStrMap = new HashMap<>();
		replaceCharToStrMap.put(StringUtils.C_CR, StringUtils.SPACE);
		replaceCharToStrMap.put(StringUtils.C_LF, StringUtils.SPACE);
		replaceCharToStrMap.put(StringUtils.C_TAB, StringUtils.SPACE);
		
		return stacktraceToString(throwable, limit, replaceCharToStrMap);
	}
	
	/**
	 * 堆栈转为完整字符串
	 * @param throwable 异常对象
	 * @return 堆栈转为的字符串
	 */
	public static String stacktraceToString(Throwable throwable){
		return stacktraceToString(throwable, 3000);
	}
	
	/**
	 * 堆栈转为完整字符串
	 * @param throwable 异常对象
	 * @param limit 限制最大长度
	 * @return 堆栈转为的字符串
	 */
	public static String stacktraceToString(Throwable throwable, int limit){
		return stacktraceToString(throwable, limit, null);
	}
	
	/**
	 * 堆栈转为完整字符串
	 * @param throwable 异常对象
	 * @param limit 限制最大长度
	 * @param replaceCharToStrMap 替换字符为指定字符串
	 * @return 堆栈转为的字符串
	 */
	public static String stacktraceToString(Throwable throwable, int limit, Map<Character, String> replaceCharToStrMap){
		final FastByteArrayOutputStream baos = new FastByteArrayOutputStream();
		throwable.printStackTrace(new PrintStream(baos));
		String exceptionStr = baos.toString();
		int length = exceptionStr.length();
		if(limit > 0 && limit < length){
			length = limit;
		}
		
		if(JudgeUtil.isNotEmpty(replaceCharToStrMap)){
			final StringBuilder sb = StringUtils.builder();
			char c;
			String value;
			for(int i = 0; i < length; i++){
				c = exceptionStr.charAt(i);
				value = replaceCharToStrMap.get(c);
				if(null != value){
					sb.append(value);
				}else{
					sb.append(c);
				}
			}
			return sb.toString();
		}else{
			return exceptionStr;
		}
	}
}
