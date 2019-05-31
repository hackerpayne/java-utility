package com.lingdonge.db.db;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.lingdonge.core.bean.common.ModelDBItem;
import com.lingdonge.core.bean.base.ModelPair;
import com.lingdonge.core.util.StringUtils;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by kyle on 17/5/24.
 */
public class DBUtils {

    public static void main(String[] args) {

        String sql = "insert into yixin_cpl (mobile, keywords,name,city,amount,usefor,result) values (?, ?,?,?,?) ON DUPLICATE KEY UPDATE title= ? ,body=?,file=?,pubtime=?";

//        Map<String, String> mapKeys = Maps.newConcurrentMap();
//        mapKeys.put("mobile", "1233423423");
//        mapKeys.put("keywords", "testste");

//        List<Triplet<String, String, Boolean>> listTuples = Lists.newArrayList();
//        listTuples.add(new Triplet("mobile", "手机号", false));
//        listTuples.add(new Triplet("keywords", "关键词", true));
//        listTuples.add(new Triplet("haha", "数量", true));
//        listTuples.add(new Triplet("created_at", DateUtil.getNowTime(), false));
//        listTuples.add(new Triplet("updated_at", DateUtil.getNowTime(), true));

//        List<ModelTuple> tuiple = Lists.newArrayList();
//        tuiple.add(new ModelTuple("mobile","1233423423",false));
//        tuiple.add(new ModelTuple("mobile","1233423423",false));

//        String keyStr = Joiner.on(",").join(mapKeys.keySet());
////        String valueStr=Joiner.on("=").join(mapKeys.values());
//        System.out.println("所有Key值列表：");
//        System.out.println();
//        System.out.println();
////

//        KeyValue<String, Object[]> pair = parseSQL("1688", listTuples);

//        System.out.println(pair.getKey());
//        System.out.println(Joiner.on("===").join(pair.getValue()));

    }

    /**
     * 生成链接字符串
     *
     * @param server
     * @param dbName
     * @return
     */
    public static String getConnectionUrl(String server, String dbName) {
        return getConnectionUrl(server, dbName, 3306);
    }

    /**
     * 生成带端口的链接字符串
     *
     * @param server
     * @param dbname
     * @param port
     * @return
     */
    public static String getConnectionUrl(String server, String dbname, Integer port) {
        return MessageFormat.format("jdbc:mysql://{0}:{2}/{1}?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false", server, dbname, Integer.toString(port));
    }

    /**
     * @param tableName
     * @param columns
     * @return
     */
    public static String parseSQLText(String tableName, List<String> columns) {
        return parseSQLText(tableName, columns, false);
    }

    /**
     * 根据表和字段列表，生成SQL语句
     *
     * @param tableName
     * @param columns
     * @param update
     * @return
     */
    public static String parseSQLText(String tableName, List<String> columns, Boolean update) {

        List<String> listCommas = Lists.newArrayList();
        List<String> listKeys = Lists.newArrayList();
        List<String> listValues = Lists.newArrayList();

        for (String column : columns) {

            if (StringUtils.isEmpty(column)) {
                continue;//值为空的不要处理，看数据库的默认值
            }

            listCommas.add("?");
            listKeys.add(column);

            if (update) {
                listValues.add(column + " = ?");
            }
        }

        String keyStr = Joiner.on(",").join(listKeys);
        String commaStr = Joiner.on(",").join(listCommas);
        String valueStr = "";
        if (listValues.size() > 0)
            valueStr = Joiner.on(",").join(listValues);

        String sqlText;
        if (StringUtils.isEmpty(valueStr)) {
            sqlText = MessageFormat.format("insert into `{0}` ({1}) values ({2})", tableName, keyStr, commaStr);
        } else {
            sqlText = MessageFormat.format("insert into `{0}` ({1}) values ({2}) ON DUPLICATE KEY UPDATE {3}", tableName, keyStr, commaStr, valueStr);

        }

        return sqlText;

    }

    /**
     * 解析SQL
     * List<ModelDBItem> listItems = Lists.newArrayList();
     * ModelPair<Object[]> pair = DBUtils.parseSQL("1688", listItems);
     *
     * @param tableName
     * @param listItems
     * @return
     */
    public static ModelPair<Object[]> parseSQL(String tableName, List<ModelDBItem> listItems) {

        List<String> listCommas = Lists.newArrayList();
        List<String> listKeys = Lists.newArrayList();
        List<String> listValues = Lists.newArrayList();

        List<String> listObjsAdd = Lists.newArrayList();
        List<String> listObjsAppend = Lists.newArrayList();

        for (ModelDBItem item : listItems) {

            if (null == item.getValue()) {
                continue;//值为空的不要处理，看数据库的默认值
            }

            listCommas.add("?");
            listKeys.add(item.getKey());

            listObjsAdd.add(item.getValue().toString());

            if (item.isUpdate()) {
                listValues.add(item.getKey() + " = ?");
                listObjsAppend.add(item.getValue().toString());
            }
        }

        String keyStr = Joiner.on(",").join(listKeys);
        String commaStr = Joiner.on(",").join(listCommas);
        String valueStr = "";
        if (listValues.size() > 0)
            valueStr = Joiner.on(",").join(listValues);

        String sqlText;
        if (StringUtils.isEmpty(valueStr)) {
            sqlText = MessageFormat.format("insert into `{0}` ({1}) values ({2})", tableName, keyStr, commaStr);
        } else {
            sqlText = MessageFormat.format("insert into `{0}` ({1}) values ({2}) ON DUPLICATE KEY UPDATE {3}", tableName, keyStr, commaStr, valueStr);

        }

        listObjsAdd.addAll(listObjsAppend);

        return new ModelPair<>(sqlText, listObjsAdd.toArray());

    }

//    /**
//     * 解析数据，生成SQL语句，返回SQL语句以及参数列表
//     *
//     * @param tableName  要保存的数据表名
//     * @param listTuples
//     * @return
//     */
//    public static KeyValue<String, Object[]> parseSQL2(String tableName, List<Triplet<String, String, Boolean>> listTuples) {
//
//        List<String> listCommas = Lists.newArrayList();
//        List<String> listKeys = Lists.newArrayList();
//        List<String> listValues = Lists.newArrayList();
//
//        List<String> listObjsAdd = Lists.newArrayList();
//        List<String> listObjsAppend = Lists.newArrayList();
//
//        for (Triplet<String, String, Boolean> item : listTuples) {
//            listCommas.add("?");
//            listKeys.add(item.getValue0());
//
//            listObjsAdd.add(item.getValue1());
//
//            if (item.getValue2()) {
//                listValues.add(item.getValue0() + " = ?");
//                listObjsAppend.add(item.getValue1());
//            }
//        }
//
//        String keyStr = Joiner.on(",").join(listKeys);
//        String commaStr = Joiner.on(",").join(listCommas);
//        String valueStr = Joiner.on(",").join(listValues);
//
//        String sqlText;
//        if (StringUtils.isEmpty(valueStr)) {
//            sqlText = MessageFormat.format("insert into `{0}` ({1}) values ({2})", tableName, keyStr, commaStr);
//        } else {
//            sqlText = MessageFormat.format("insert into `{0}` ({1}) values ({2}) ON DUPLICATE KEY UPDATE {3}", tableName, keyStr, commaStr, valueStr);
//
//        }
//
//        listObjsAdd.addAll(listObjsAppend);
//
//        return KeyValue.with(sqlText, listObjsAdd.toArray());
//
//    }


}
