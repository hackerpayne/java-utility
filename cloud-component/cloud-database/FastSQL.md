# 轻量级数据库访问框架FastSQL

[![Maven central](https://maven-badges.herokuapp.com/maven-central/top.fastsql/fastsql/badge.svg)](https://maven-badges.herokuapp.com/maven-central/top.fastsql/fastsql)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

![logo](logo_s.jpg)

# 目录

- [1 简介](#1-%E7%AE%80%E4%BB%8B)
- [2 入门](#2-%E5%85%A5%E9%97%A8)
- [3 SQLFactory的配置](#3-sqlfactory-%E9%85%8D%E7%BD%AE)
- [4 SQL类作为sql语句构建器](#4-sql%E7%B1%BB%E4%BD%9C%E4%B8%BAsql%E8%AF%AD%E5%8F%A5%E6%9E%84%E5%BB%BA%E5%99%A8%E4%BD%BF%E7%94%A8)
- [5 SQL类的执行sql功能](#5-sql%E7%B1%BB%E7%9A%84%E6%89%A7%E8%A1%8Csql%E5%8A%9F%E8%83%BD)
- [6 BaseDAO](#6-basedao)
- [7 通用工具](#7-%E9%80%9A%E7%94%A8%E5%B7%A5%E5%85%B7)
- [8 配置项](#8-%E9%85%8D%E7%BD%AE%E9%A1%B9)
- [9 其他](#9-%E5%85%B6%E4%BB%96)
- [10 更新日志](#10-更新日志)

# 1 简介

FastSQL一个基于spring-jdbc的简单ORM框架，它支持sql构建、sql执行、命名参数绑定、查询结果自动映射和通用DAO。结合了Hibernate/JPA快速开发和Mybatis高效执行的优点。

FastSQL可以完全满足你控制欲，可以用Java代码清晰又方便地写出sql语句并执行。

# 2 入门

## 2.1 安装

要使用 FastSQL， 只需将 fastsql-1.2.1.jar 文件置于 classpath 中即可。

如果使用 Maven 来构建项目，则需将下面的 dependency 代码置于 pom.xml 文件中：

```xml
<dependency>
    <groupId>top.fastsql</groupId>
    <artifactId>fastsql</artifactId>
    <version>1.2.1</version>
</dependency>
```

如果使用 Gradle 来构建项目，则需将下面的代码置于 build.gradle 文件的 dependencies 代码块中：

```groovy
compile 'top.fastsql:fastsql:1.2.1'
```

## 2.2 构建 SQLFactory
你可以直接从 Java 程序构建一个 SQLFactory ，如果使用SQL的执行功能，至少需要设置 DataSource 。
```java
//新建一个DataSource（这里使用了Spring-Jdbc的SimpleDriverDataSource）
DataSource dataSource = new SimpleDriverDataSource([传入url,username等]);

SQLFactory sqlFactory = new SQLFactory();
sqlFactory.setDataSource(dataSource);
```

## 2.3 从 SQLFactory 中获取 SQL

既然有了 SQLFactory ，我们就可以从中获得 SQL 的实例了。SQL类完全包含了面向数据库执行 sql 命令所需的所有方法。
你可以通过 SQL 实例来构建并直接执行 SQL 语句。例如：
```java
SQL sql = sqlFactory.createSQL();
Student student = sql.SELECT("*").FROM("student").WHERE("id=101").queryOne(Student.class);
```

## 2.4 作用域（Scope）和生命周期

### SQLFactory

SQLFactory 一旦被创建就应该在应用的运行期间一直存在，没有任何理由对它进行清除或重建。
使用 SQLFactory 的最佳实践是在应用运行期间不要重复创建多次，多次重建 SQLFactory 被视为一种代码“坏味道（bad smell）”。
因此 SQLFactory 的最佳作用域是应用的作用域。有很多方法可以做到，最简单的就是使用单例模式或者静态单例模式
（如果在Spring环境中，利用Spring容器的功能，你完全可以把它设置为一个单例bean）。

### SQL

SQL 实例是有状态的 ，不是线程安全的，是不能被共享的。即使在同一个线程中每执行sql语句一次，都需要重新构建一个 SQL 实例。
绝对不能将 SQL 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。


# 3 SQLFactory 配置

新建SQLFactory

```java
SQLFactory sqlFactory = new SQLFactory();
```

指定DataSource

```java
//新建任意类型一个DataSource，如SimpleDriverDataSource（Spring内部提供的）
//  或者其他支持连接池的DataSource
DataSource dataSource =  ... ;

//设置数据源
sqlFactory.setDataSource(dataSource);
```

设置数据源类型

```java
sqlFactory.setDataSourceType(DataSourceType.POSTGRESQL);//默认
//支持 DataSourceType.POSTGRESQL、 DataSourceType.MY_SQL、DataSourceType.ORACLE
```

设置其他内容

>撰写中


# 4 使用SQL类构建sql语句

Java程序员面对的最痛苦的事情之一就是在Java代码中嵌入SQL语句。`SQL`类可以简化你构建sql语句的过程。

## 4.1 基本查询

SELECT方法可以传入一个可变参数，以便选择多列。(FastSQL中建议SQL关键字全部采用大写)
```java
sqlFactory.createSQL().SELECT("name", "age").FROM("student").WHERE("age>10").build();
//==> SELECT name,age FROM student WHERE age>10
sqlFactory.createSQL().SELECT("name", "age").FROM("student").WHERE("name='小红'").build();
//==> SELECT name,age FROM student WHERE name='小红'
```
`WHERE()`关键字生成`WHERE 1=1`,如下
```java
SQL sql = sqlFactory.createSQL().SELECT("name", "age").FROM("student").WHERE();
if (true){
  sql.AND("age > 10");
}
if (false){
  sql.AND("age < 8");
}

//生成sql=>SELECT name,age  FROM student  WHERE 1 = 1  AND age > 10
```

## 4.2 使用操作符方法

FastSQL提供了一些操作符方便SQL的构建，比如：

```java
sqlFactory.createSQL()
    .SELECT("name", "age")
    .FROM("student")
    .WHERE("age").lt("10")
    .AND("name").eq("'小明'")
    .build();
//生成sql=> SELECT name,age FROM student WHERE age < 10 AND name = '小明'
```

如下：

| 方法             | 说明                                                  |
| :--------------- | :---------------------------------------------------- |
| eq(String)       | 生成 = ，并追加参数（equal的缩写）                    |
| gt(String)       | 生成 > ，并追加参数（是greater than的缩写）           |
| gtEq(String)     | 生成 >= ，并追加参数（是greater than or equal的缩写） |
| lt(String)       | 生成 < ，并追加参数（是less than的缩写 ）             |
| ltEq(String)     | 生成 <= ，并追加参数（是less than or equal的缩写）    |
| nEq(String)      | 生成 != ，并追加参数（是not equal的缩写  ）           |
| LIKE(String)     | 生成 LIKE ，并追加参数，                              |
| NOT_LIKE(String) | 生成 NOT LIKE ，并追加参数                            |
| IS_NULL()        | 生成 IS NULL                                          |
| IS_NOT_NULL()    | 生成 IS NOT NULL                                      |
| eq()             | 生成 =                                                |
| gt()             | 生成 >                                                |
| gtEq()           | 生成 >=                                               |
| lt()             | 生成 <                                                |
| ltEq()           | 生成 <=                                               |
| nEq()            | 生成 !=                                               |
| LIKE()           | 生成 LIKE                                             |
| NOT_LIKE()       | 生成 NOT LIKE                                         |

### byType(Object)

这些方法仅仅是字符串连接：`eq("1")`生成` = 1` ，`eq("'1'")`会生成` = '1'`。byType(Object)方法可以根据类型生成你想要的sql字符串

```java
sqlFactory.createSQL()
        .SELECT("name", "age")
        .FROM("student")
        .WHERE("age").lt().byType(10)
        .AND("name").eq().byType("小明")
        .build();
//==>SELECT name,age FROM student WHERE age < 10 AND name = '小明'
```

| 方法             | 说明                                                            |
| :--------------- | :-------------------------------------------------------------- |
| byType(Object)   | 根据类型生成相应字符串 ，如 byType(1)生成1 ，byType("1")生成'1' |
| eqByType(Object) | 使用 = 连接根据类型生成相应的字符串                             |

## 4.3 使用连接查询/排序

查询不及格的成绩

```java
sqlFactory.createSQL().SELECT("s.name","c.subject_name","c.score_value")
        .FROM("score c")
        .LEFT_JOIN_ON("student s", "s.id=c.student_id")
        .WHERE("c.score_value<60")
        .ORDER_BY("c.score_value")
        .build();
/*
生成sql =>
SELECT s.name, c.subject,c.score_value
FROM score c
LEFT OUTER JOIN student s ON (s.id = c.student_id)
WHERE c.score_value < 60
ORDER BY c.score_value
*/
```

## 4.4 分组查询

查询每个学生总分数

```java
sqlFactory.createSQL().SELECT("s.name", "sum(c.score_value) total_score")
        .FROM("score c")
        .LEFT_JOIN_ON("student s", "s.id=c.student_id")
        .GROUP_BY("s.name")
        .build()
/*
生成sql==>

SELECT s.name, sum(c.score_value) total_score
FROM score c
LEFT OUTER JOIN student s ON (s.id = c.student_id)
GROUP BY s.name
*/
```

## 4.5 IN语句

由于Jdbc规范不支持IN参数绑定，FastSQL提供了几种IN语句直接拼接的方式：

```java
//1.使用字符串
sqlFactory.createSQL().SELECT("*")
   .FROM("student")
   .WHERE("name").IN("('小明','小红')")
   .build();

//2.使用集合（List,Set等）
sqlFactory.createSQL().SELECT("*")
   .FROM("student")
   .WHERE("name").IN(Lists.newArrayList("小明","小红"))
   .build();

//3.使用数组
sqlFactory.createSQL().SELECT("*")
   .FROM("student")
   .WHERE("name").IN(new Object[]{"小明","小红"})//
   .build();

//生成sql==> SELECT *  FROM student  WHERE name  IN ('小明','小红')
```

## 4.6 使用$_$()方法进行子查询

查询大于平均分的成绩（可以使用 $_$()方法）

```java
sqlFactory.createSQL().SELECT("*")
   .FROM("score")
   .WHERE("score_value >")
   .$_$(
         sqlFactory.createSQL().SELECT("avg(score_value)").FROM("score")
    )
   .build();
//生成sql==>
//SELECT *  FROM score
//WHERE score_value >  ( SELECT avg(score_value)  FROM score  )
```

带有IN的子查询

```java
sqlFactory.createSQL().SELECT("*")
    .FROM("score")
    .WHERE()
    .AND("score")
    .IN().$_$(
         sqlFactory.createSQL().SELECT("DISTINCT score_value").FROM("score")
    )
    .build();
//生成sql==> SELECT * FROM score WHERE 1 = 1 AND score IN (SELECT DISTINCT score_value FROM score)
```

## 4.7 AND和OR结合使用

如果查询年龄大于10岁，并且名字是小明或小红

```java
sqlFactory.createSQL().SELECT("*")
   .FROM("student")
   .WHERE("age>10")
   .AND("(name='小明' OR name='小红')")//手动添加括号
   .build();
//或者
sqlFactory.createSQL().SELECT("*")
   .FROM("student")
   .WHERE("age>10")
   .AND().$_$("name='小明' OR name='小红'")//$_$ 生成左右括号
   .build();
```

## 4.8 使用Lambda表达式简化构建动态sql

- `ifTrue(boolean bool, Consumer<SQL> sqlConsumer)`:如果第1个参数为true，则执行第二个参数（Lambda表达式）
- `ifNotEmpty(Collection<?> collection, Consumer<SQL> sqlConsumer)`:如果第1个参数长度大于0，则执行第二个参数（Lambda表达式）
- `ifPresent(Object object, Consumer<SQL> sqlConsumer)`:如果第1个参数存在（不等于null且不为""），则执行第二个参数（Lambda表达式）

```java
sqlFactory.createSQL()
    .SELECT("student")
    .WHERE("id=:id")
    .ifTrue(true, sql -> thisBuilder.AND("name=:name"))
    .ifNotEmpty(names, sql -> {
        System.out.println("ifNotEmpty?");
        thisBuilder.AND("name").IN(Lists.newArrayList("小明", "小红"));
    })
    .ifPresent("",sql -> {
        System.out.println("ifPresent?");
        //...处理其他流程语句...
    })
    .build();
```

输出：

```
ifNotEmpty?
SELECT student WHERE id=:id AND name=:name AND name  IN ('小明','小红')
```

## 4.9 分页功能

### 使用原生关键字进行分页

```java
sqlFactory.createSQL().SELECT("*").FROM("student").LIMIT(10).build();
sqlFactory.createSQL().SELECT("*").FROM("student").LIMIT(5, 10).build(); //mysql中的写法
sqlFactory.createSQL().SELECT("*").FROM("student").LIMIT(10).OFFSET(5).build(); //postgresql中的写法
```
生成如下SQL

```sql
SELECT * FROM student LIMIT 10
SELECT * FROM student LIMIT 5,10
SELECT * FROM student LIMIT 10 OFFSET 5
```

### 使用 `pageThis(int,int)` 分页方法进行分页

```
//
sqlFactory.setDataSourceType(DataSourceType.POSTGRESQL); //使用枚举指定数据源类型
sqlFactory.createSQL().SELECT("*").FROM("student").pageThis(1,10).build();
```
注意：如果不指定 dataSourceType，将会使用 postgresql 数据库类型进行分页;

### 使用 `countThis()` 生成获取数量语句

```java
//countThis
sqlFactory.createSQL().SELECT("*").FROM("student").countThis().buildAndPrintSQL();
```

## 4.10 构建插入insert/修改update/删除delete语句

### 插入

```java
//使用列
sqlFactory.createSQL().INSERT_INTO("student", "id", "name", "age")
                .VALUES("21", "'Lily'", "12").build();
//=>INSERT INTO student (id,name,age)  VALUES (21,'Lily',12)

//不使用列
sqlFactory.createSQL().INSERT_INTO("student").VALUES("21", "'Lily'", "12").build();
//=>INSERT INTO student VALUES (21,'Lily',12)
```

### 修改

SET(String...items) :SET关键字

```java
sqlFactory.createSQL().UPDATE("student").SET("name = 'Jack'","age = 9").WHERE("name = 'Mike'").build();
//=>  UPDATE student SET name = 'Jack',age = 9 WHERE name = 'Mike'
```

### 构建删除语句

```java
sqlFactory.createSQL().DELETE_FROM("student").WHERE("id=12").build();
//=>DELETE FROM student WHERE id=12
```

# 5 使用SQL类执行sql语句

##  5.1 创建SqlFactory

```java
//创建任意DataSource对象（这里使用了spring自带的数据源SimpleDriverDataSource）
DataSource dataSource = new SimpleDriverDataSource(
                new Driver(), "jdbc:postgresql://192.168.0.226:5432/picasso_dev2?stringtype=unspecified",
                "developer", "password");

//创建SqlFactory
SqlFactory sqlFactory = new SqlFactory();
sqlFactory.setDataSource(dataSource);
sqlFactory.setDataSourceType(DataSourceType.MY_SQL);
```

##  5.2 设置参数的方法

FastSQL支持多种传入命名参数的方法：

- `parameter(SqlParameterSource)` 支持传入SqlParameterSource类型的参数（为了兼容spring-jdbc）。
- `beanParameter(Object)`方法可以传入对象参数。
- `mapParameter(Map<String, Object>)`支持传入Map类型参数。
- `mapItemsParameter(Object...)`支持多个key-value形式的参数，比如`mapItemsParameter("id", 12345,"name","小明")`。
- `beanAndMapParameter(Object, Map<String, Object>)` 支持两种不同的参数组合，后一个map会覆盖前面bean中的相同名字的参数。
- `addMapParameterItem(String, Object)`可以为以上几种传参方法追加参数。

>注意：以上方法除了`addMapParameterItem`都**只能调用一次，调用多次会导致前面的传参被覆盖**。如需追加参数，请使用`addMapParameterItem`。

FastSQL也支持?占位符和可变参数：
- `varParameter(Object... vars)` 传入可变参数，**可以调用多次**，用来追加参数。

### 示例

使用beanParameter方法支持传入一个参数bean
```java
public class StudentDTO{
    private String name;
    private int age;
    //省略set和get方法
}
```
```java
StudentDTO dto =new StudentDTO();
dto.setName="小明";
dto.setAge=10;

sqlFactory.createSQL().SELECT("*")
    .FROM("student")
    .WHERE("name=:name")
    .AND("age>:age")
    .beanParameter(dto)  //设置一个DTO查询参数
    .queryList(StudVO.class);

```
使用beanParameter方法并追加参数
```java
Map<String,Object> param = new HashMap<>();
map.put("name","李%");

sqlFactory.createSQL()
    .SELECT("*")
    .FROM("student")
    .WHERE("name").LIKE(":name")
    .AND("age > :age")
    .beanParameter(param)  //设置一个map参数
    .addParameterMapItem("age",12) //追加
    .queryList(Student.class);

```

使用varParameter方法--支持?占位符和可变参数
```
SQL sql = sqlFactory.createSQL();
sql.INSERT_INTO("student", "id", "name", "age")
    .VALUES("?", "?", "?")
    .varParameter("123", "小明")
    .varParameter(12)
    .update();
```


##  5.3 查询方法

### 查询方法解析

- `T queryOne(Class<T> returnClassType)`查询单行结果封装为一个对象,参数可以是可以为String/Integer/Long/Short/BigDecimal/BigInteger/Float/Double/Boolean或者任意POJO的class。
- `Map<String, Object> queryMap()`查询单行结果封装为Map
- `List<T> queryList(Class<T> returnClassType)`查询多行结果封装为一个对象列表
- `List<Map<String, Object>> queryMapList()`查询多行结果封装为Map数组
- `List<Object[]> queryArrayList()` 查询结果封装为泛型为Object数组的列表
- `ResultPage<T> queryPage(int page, int perPage, Class<T> returnClassType)` 查询结果页


### 示例

StudentVO是查询视图类，包含name和age字段；StudentDTO是查询参数类，包含name字段。

```java
//queryList可以查询列表，可以是基本类型列表或对象列表
List<String> strings = sqlFactory.createSQL().SELECT("name")
                .FROM("student")
                .queryList(String.class); //这里执行查询列表并指定返回类型

List<StudVO> studVOList = sqlFactory.createSQL().SELECT("name", "age")
                            .FROM("student")
                            .WHERE("name=:name")
                            .beanParameter(new StudentDTO())  //设置一个DTO查询参数
                            .queryList(StudVO.class);     //查询一个对象列表

//queryOne可以查询一个值，可以是基本类型  或 对象
String name = sqlFactory.createSQL().SELECT("name")
                 .FROM("student")
                 .WHERE("id=:id")
                 .AND("name=:name")
                 .mapItemsParameter("id", 12345) //可以传入多个k-v值，，还可以调用parameterMap传入Map参数，
                 .addParameterMapItem("name", "Jack")// 使用addParameterMapItem追加k-v值
                 .queryOne(String.class);  //这里执行查询一个对象（基本类型）并指定返回类型

StudVO studVO = sqlFactory.createSQL().SELECT("name", "age")
                   .FROM("student")
                   .WHERE("name=:name")
                   .beanParameter(new StudentDTO())  //设置一个DTO
                   .queryOne(StudVO.class);     //查询一个对象

//queryPage查询分页
ResultPage<StudVO> studVOResultPage =sqlFactory.createSQL().SELECT("name", "age")
                                        .FROM("student")
                                        .queryPage(1, 10, StudVO.class);  //分页查询（第一页，每页10条记录）
//根据特定数据库进行分页查询
ResultPage<StudVO> studVOResultPage =sqlFactory.createSQL().SELECT("name", "age")
                                        .FROM("student")
                                        .queryPage(1, 10, StudVO.class, DbType.MY_SQL);
```
注意1：queryOne调用后，如果查询的值不存在是不会抛出EmptyResultDataAccessException，而是返回null，所以要用包装类型接收他的值而不是基本类型，并判断非空性

注意2：queryPage返回的是ResultPage对象


##  5.4 增删改操作

使用update方法
```java
//插入
sqlFactory.createSQL().INSERT_INTO("student", "id", "name", "age")
        .VALUES(":id", ":name", ":age")
        .mapItemsParameter("id", 678, "name", "Kiven", "age", 123)
        .update();

//修改
sqlFactory.createSQL().UPDATE("student")
        .SET("name",":name")
        .WHERE("id=678")
        .mapItemsParameter("id", 678, "name", "Rose", "age", 123)
        .update();
//删除
sqlFactory.createSQL().DELETE_FROM("student")
        .WHERE("id=:id")
        .mapItemsParameter("id", 678)
        .update();
```

##  获取数据库元信息
```java
//表名称
List<String> tableNames = sqlFactory.createSQL().getTableNames();
//列名称
List<String> columnNames = sqlFactory.createSQL().getColumnNames("student");
//列对象
List<ColumnMetaData> columnMetaDataList = sqlFactory.createSQL().getColumnMetaDataList("sys_dict");

```

##  5.5 事务管理

手动事务：FastSQL事务管理使用Spring的工具类`org.springframework.jdbc.datasource.DataSourceUtils`
```java
Connection connection = DataSourceUtils.getConnection(dataSource);//开启事务
connection.setAutoCommit(false);//关闭自动提交

sqlFactory.createSQL()
     .INSERT_INTO("sys_users", "id").VALUES(":id")
     .mapItemsParameter("id", 456)
     .update();

sqlFactory.createSQL()
    .INSERT_INTO("sys_users", "id").VALUES(":id")
    .mapItemsParameter("id", 234)
    .update();

//connection.rollback(); //回滚

connection.commit();//提交事务
```


# 6 BaseDAO

##  6.1 数据准备

### Entity实体类

注解如下

1. @Table 非必需，如果不写表名称将会被解析为student（根据类名的下划线形式）
2. @Id 必须存在，对应表的主键
3. @Entity 非必需，标识一个实体，为了兼容JPA标准建议加上

```java
@Entity
@Table(name="student")
@Data  //use lombok
public class Student {
    @Id
    private Integer id;
    private String name;
    private Integer age;
    private LocalDate birthday;
    private String homeAddress;
}

```

新建DAO层数据访问类, 并继承BaseDAO类，会自动继承BaseDAO中的方法(详见第2部分）

### DAO类在Spring环境中的使用

DAO层：

```java
@Repository
public class StudentDAO extends BaseDAO<Student,Integer> {

}
```
如果设置了SQLFactory作为一个Spring的@Bean ,将会自动注入。在Service中，你就可以使用这个DAO：
```java
@Service
public class StudentService {
    @Autowire
    private StudentDAO studentDAO;

    @Transactional //如果需要事务--org.springframework.transaction.annotation.Transactional
    public void test1(){
        studentDAO.XXX();//调用任意方法
    }

}
```

### DAO类在非Spring环境中
```java
public class StudentDAO extends BaseDAO<Student,String> {

}
```

```java
public class Test  {
    public static void main(String[] args) {
        SQLFactory sqlFactory = ...

        StudentDAO studentDAO= new StudentDAO();
        studentDAO.setSqlFactory(sqlFactory);//手动注入
        //执行操作
        studentDAO.XXX();
    }
}
```

##  6.2 基本使用方法 CRUD
CRUD 是四种数据操作的简称：C 表示创建，R 表示读取，U 表示更新，D 表示删除。BaseDAO 自动创建了处理数据表中数据的方法。

### 数据插入

方法 ` int insert(E entity) `，插入对象中的值到数据库，null值在生成的sql语句中会设置为NULL
```java
Student student = new Student();
student.setId(2);
student.setName("小丽");
student.setBirthday(LocalDate.now());//这里使用jdk8时间类型
student.setHomeAddress("");

studentDao.insert(student);//获取保存成功的id

//等价如下SQL语句（注意：age被设置为null）
//INSERT INTO student(id,name,age,birthday,home_address) VALUES (2,'小丽',NULL,'2017-07-11','')

```

方法 ` int insertSelective(E entity)  `，插入对象中非null的值到数据库

```java
Student student = new Student();
student.setId(3);
student.setName("小丽");
student.setBirthday(new Date());
student.setHomeAddress("");
studentDao.insertSelective(student);

//等价如下SQL语句（注意：没有对age进行保存，在数据库层面age将会保存为该表设置的默认值，如果没有设置默认值，将会被保存为null ）
//===>INSERT INTO student(id,name,birthday,home_address)  VALUES  (3,'小丽','2017-07-11','')
```


### 数据修改

方法   `int update(E entity) ` ,根据对象进行更新（null字段在数据库中将会被设置为null），对象中@id字段不能为空
```java
//待更新
```

方法   `int updateSelective(E entity) `,根据对象进行更新（只更新实体中非null字段），对象中@id字段不能为空
```java
//待更新
```

方法   `int updateByColumn(E entity, String... columns) `,根据id更新可变参数columns列，对象中@id字段不能为空

```java
Student student = studentDAO.selectOneById(44);
student.setAge(19);
studentDAO.updateByColumn(student,"age");

//===>UPDATE student SET age=? WHERE id=?
```

### 数据删除

方法 `int deleteOneById(String id) ` 根据id删除数据
```java
int num = studentDao.deleteOneById(2);//获取删除的行数量
//===>DELETE FROM student WHERE id=2
```

方法 `int deleteAll()`,删除某个表所有行

```java
int number = studentDao.deleteAll();//获取删除的行数量
// ===>DELETE FROM student
```

方法 `int[]  deleteInBatch(List<String> ids)` ,根据id列表批量删除数据(所有删除语句将会一次性提交到数据库)

```java
List<String> ids = new ArrayList<>();
ids.add(1);
ids.add(2);
studentDao.deleteInBatch(ids);//返回成功删除的数量
```
方法` int deleteWhere(String sqlCondition, Object... values)`，根据条件删除

### 单条数据查询

方法     `E selectOneById(String id)`

通过id查询一个对象

```java
Student student = studentDao.selectOneById(4);//查询id为12345678的数据，并封装到Student类中
```
方法     `E selectOneWhere(String sqlCondition, Object... values)`,通过语句查询（返回多条数据将会抛出运行时异常,为了防止sql语句在service层滥用，可变参数最多支持三个）

```java
Student student = studentDao.selectOneWhere("name=? AND home_address=?", "小明", "成都");
```

方法     `E selectOneWhere(String sqlCondition, SqlParameterSource parameterSource)` 查询一条数据

### 多条数据查询

方法     `List<E> selectWhere(String sqlCondition, Object... values)`,用法与selectOneWhere()相同，可以返回一条或多条数据，可变参数最多支持三个

```java
List<Student> studentList  =  studentDao.selectWhere("name=?", "小明");
List<Student> studentList  =  studentDao.selectWhere("ORDER BY age");
List<Student> studentList  =  studentDao.selectWhere("home_address IS NULL ORDER BY age DESC");
//...
```

方法     `List<E> selectAll()` 查询所有

```java
List<Student> allStudents  =  studentDao.selectAll();
```

方法     ` List<E> selectWhere(String sqlCondition, SqlParameterSource parameterSource)`可以返回一条或多条数据

### 分页查询

方法     `ResultPage<E> selectPageWhere(String sqlCondition, int pageNumber, int perPage, Object... values)`

方法     ` ResultPage<E> selectPageWhere(String sqlCondition, int pageNumber, int perPage, SqlParameterSource parameterSource)` 

方法     `ResultPage<E> selectPage(int pageNumber, int perPage)`

### 其他查询

方法     `int countWhere(String sqlCondition, Object... values)`,通过条件查询数量

```java
int countWhere = studentDao.countWhere("age >= 20"); //查找年龄大于等于20的学生
int countWhere = studentDao.countWhere("age > ?" , 10); //查找年龄大于10的学生
```

方法     ` int countWhere(String sqlCondition, SqlParameterSource parameterSource)`,通过条件查询数量
```java
@Repository
public class BizPhotoDAO extends ApplicationBaseDAO<BizPhotoPO, String> {
    public int countByName() {
        return countWhere("photo_name=:name", new MapSqlParameterSource().addValue("name", "物品照片"));
    }
}
```

方法   `int count()` 查询表总数量

##  6.3 定制你的ApplicationBaseDAO

建议在你的程序中实现ApplicationBaseDAO，可以

1. 定制一些通用方法
2. 设置多数据库支持
3. 设置BaseDAO中的触发器

```java
public abstract class ApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
  //添加方法等
}

////我们的StudentDAO此时应该继承ApplicationBaseDAO
@Repository
public class StudentDAO extends ApplicationBaseDAO<Student,Integer> {

}
```

###  定制通用方法

如下，增加了一个名为logicDelete的逻辑删除方法，将会作用于继承于它的每个DAO
```java
public abstract class ApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
  //...其他方法

  public void logicDelete(ID id) {
      //每个表都有一个defunct，1表示已（逻辑）删除
      namedParameterJdbcTemplate.getJdbcOperations().update("UPDATE " + this.tableName + " SET defunct = 1");
  }

  //...其他方法
}
```
上面的logicDelete方法使用了tableName这个变量，BaseDAO中的部分可用变量为

```
Class<E> entityClass; //DAO对应的实体类
Class<ID> idClass;  //标识为@Id的主键类型

Logger log; //日志，可以在实现类中直接使用

String className; //实体类名
String tableName; //表名

Field idField;  //@Id对应的字段引用
String idColumnName; //表主键列名

namedParameterJdbcTemplate //jdbc模板

//...待更新

```

### 设置多数据源支持

```java
public abstract class OracleApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
      //重写setSqlFactory方法
      @Autowired
      @Qualifier("sqlFactory1")//===>根据名称注入
      @Override
      protected void setSqlFactory(SQLFactory sqlFactory) {
          super.setSqlFactory(sqlFactory);
      }
}
public abstract class MySqlApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
      //重写setSqlFactory
      @Autowired
      @Qualifier("sqlFactory2")//===>根据名称注入
      @Override
      protected void setSqlFactory(SQLFactory sqlFactory) {
          super.setSqlFactory(sqlFactory);
       }}
}
```

### 设置BaseDAO中的拦截器

```java
public abstract class ApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
    public ApplicationBaseDAO() {
         // 1.设置触发器开关
         this.useBeforeInsert = true; //在插入前执行
         this.useBeforeUpdate = true; //在更新前执行
    }

    //2.重写触发器相关方法
    @Override
    protected void beforeInsert(E object) {
        EntityRefelectUtils.setFieldValue(object, "createdAt", LocalDateTime.now());
        EntityRefelectUtils.setFieldValue(object, "updatedAt", LocalDateTime.now());//在插入数据时自动写入createdAt,updatedAt
    }

    @Override
    protected void beforeUpdate(E object) {
        EntityRefelectUtils.setFieldValue(object, "updatedAt", LocalDateTime.now());//在更新数据时自动更新updatedAt
    }
}
```

对应关系如下:

count 参数表示执行成功的条数

| 启用            | 需重写的方法                      | 作用于                                                        |
| :-------------- | :-------------------------------- | :------------------------------------------------------------ |
| useBeforeInsert | beforeInsert(E entity)            | insertSelective(..)/insert(..)执行插入之前                    |
| useAfterInsert  | afterInsert(E entity,int count)   | insertSelective(..)/insert(..)执行插入之后                    |
| useBeforeUpdate | beforeUpdate(E entity)            | updateSelective(..)/update(..)执行更新之前                    |
| useAfterUpdate  | afterUpdate(E entity,int count)   | updateSelective(..)/update(..)/updateByColumn(..)执行更新之后 |
| useBeforeDelete | beforeDelete(ID id)               | deleteOneById(..)执行删除之前                                 |
| useAfterDelete  | void afterDelete(ID id,int count) | deleteOneById(..)执行删除之后                                 |

##  6.4 SQL构建器在BaseDAO中的使用

BaseDAO整合了SQL构建器，在继承BaseDAO的类中你可以你可以直接调用 `getSQL()` 来获取一个SQL的实例：

```java
@Repository
public class StudentDAO extends ApplicationBaseDAO<Student, String> {
    /**
     * 查询姓李的同学列表
     */
    public List<Student> queryListByName() {
        return getSQL().SELECT("*").FROM(this.tableName)
                       .WHERE("name").LIKE("'李%'")
                       .queryList(Student.class);//查询列表
    }
    
    /**
    * 根据旧名字修改学生姓名
    */
    public int updateName(String oldName,String newName) {
        return getSQL().UPDATE(this.tableName).SET("name = '"+newName+"'").WHERE("name").eqByType(oldName).update();
    }
}
```

# 7 通用工具

## 7.1 获取sql的IN列表

`FastSQLUtils.getInClause(Collection<?> collection)`,会根据Collection的类型自动判断使用什么样的分隔符:

```java
FastSQLUtils.getInClause(Lists.newArrayList(1, 23, 4, 15));
//生成=>(1,23,4,15)

FastSQLUtils.getInClause(Lists.newArrayList("dog", "people", "food", "apple")); 
//生成=> ('dog','people','food','apple')
```

说明：IN功能已经整合到SQL构建器的IN方法

## 7.2 获取LIKE通配符

```
FastSqlUtils.bothWildcard("李"); // => %李%
FastSqlUtils.leftWildcard("李");  // => %李
FastSqlUtils.rightWildcard("李"); // => 李%
```

# 8 配置项

显示sql日志,需要调节相应的类日志级别：
org.springframework.jdbc.core.JdbcTemplate 日志级别调整为 debug 会显示SQL语句
org.springframework.jdbc.core.StatementCreatorUtils 日志级别调整为 trace 会显示绑定参数过程

下面是Springboot中的配置：

```properties
#显示sql
logging.level.org.springframework.jdbc.core.JdbcTemplate=debug
#显示绑定的参数
logging.level.org.springframework.jdbc.core.StatementCreatorUtils=trace
```

# 9 其他

* [项目主页](http://fastsql.top)
* [版本下载](https://oss.sonatype.org/content/repositories/releases/top/fastsql/fastsql/)

# 10 更新日志

## 1.2.1

1.重写获取总条数的sql，修复了分页查询效率低的问题。

