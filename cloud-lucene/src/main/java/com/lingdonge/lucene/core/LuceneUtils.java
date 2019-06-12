package com.lingdonge.lucene.core;

import com.google.common.collect.Lists;
import com.lingdonge.core.file.FileUtil;
import com.lingdonge.core.util.StringUtils;
import com.lingdonge.core.util.Utils;
import com.lingdonge.lucene.entity.HighlighterParam;
import com.lingdonge.lucene.entity.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lucene操作类
 */
@Slf4j
public class LuceneUtils {

    private volatile static LuceneUtils singleton;

    private final Lock writerLock = new ReentrantLock();

    private volatile IndexWriter writer;

    private volatile IndexReader reader;

    private volatile IndexSearcher searcher;

    public void setWriter(IndexWriter writer) {
        this.writer = writer;
    }

    public void setReader(IndexReader reader) {
        this.reader = reader;
    }

    public void setSearcher(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public String getIndexStoreFolder() {
        return indexStoreFolder;
    }

    public void setIndexStoreFolder(String indexStoreFolder) {
        this.indexStoreFolder = indexStoreFolder;
    }

    /**
     * 默认使用IK分词器
     */
    private Analyzer analyzer = new IKAnalyzer();

    /**
     * 实现单例模式
     *
     * @return
     */
    public static LuceneUtils getInstance() {
        if (null == singleton) {
            synchronized (LuceneUtils.class) {
                if (null == singleton) {
                    singleton = new LuceneUtils();
                }
            }
        }
        return singleton;
    }


    /**
     * 索引保存位置
     */
    private String indexStoreFolder = FileUtil.file(Utils.CurrentDir, "data", "lucene").getAbsolutePath();

    /**
     * 默认
     */
    public LuceneUtils() {
    }

    /**
     * 指定索引保存目录
     *
     * @param indexStoreFolder
     */
    public LuceneUtils(String indexStoreFolder) {
        this.indexStoreFolder = indexStoreFolder;
    }

    /**
     * 指定索引保存目录和分词器
     *
     * @param indexStoreFolder
     * @param analyzer
     */
    public LuceneUtils(String indexStoreFolder, Analyzer analyzer) {
        this.indexStoreFolder = indexStoreFolder;
        this.analyzer = analyzer;
    }

    /**
     * 打开索引目录
     *
     * @param luceneDir
     * @return
     * @throws IOException
     */
    public FSDirectory openFSDirectory(String luceneDir) {
        FSDirectory directory = null;
        try {
            directory = FSDirectory.open(Paths.get(luceneDir));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return directory;
    }

    /**
     * 关闭索引目录并销毁
     *
     * @param directory
     * @throws IOException
     */
    public void closeDirectory(Directory directory) throws IOException {
        if (null != directory) {
            directory.close();
            directory = null;
        }
    }

    public IndexWriter getWriter() {

        if (null == writer)
            writer = getWriter(this.indexStoreFolder, analyzer);
        return writer;
    }

    /**
     * 默认删除以前的索引
     *
     * @param analyzer
     * @return
     */
    public IndexWriter getWriter(String indexStoreFolder, Analyzer analyzer) {
        return getWriter(indexStoreFolder, analyzer, false);
    }

    /**
     * 创建写入器
     *
     * @param analyzer
     * @return
     */
    public IndexWriter getWriter(String indexStoreFolder, Analyzer analyzer, Boolean append) {

        try {
            writerLock.lock();

            if (writer == null) {
                // Directory directory = new RAMDirectory();//放到内存里面
                Directory directory = openFSDirectory(indexStoreFolder);// 存储到文件里面

                // 利用分词工具创建 IndexWriter
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                config.setOpenMode(append ? IndexWriterConfig.OpenMode.CREATE_OR_APPEND : IndexWriterConfig.OpenMode.CREATE);//创建，会覆盖旧的数据

                writer = new IndexWriter(directory, config);

                if (!append) {
                    writer.deleteAll();//删除所有以前建立的索引
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            writerLock.unlock();
        }

        return writer;
    }


    /**
     * 关闭IndexWriter
     *
     * @param writer
     */
    public void closeIndexWriter(IndexWriter writer) {
        if (null != writer) {
            try {
                writer.close();
                writer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     */
    public IndexReader getReader() {
        if (null == reader)
            reader = getReader(this.indexStoreFolder);
        return reader;
    }

    /**
     * 获取IndexReader对象(默认不启用NETReader)
     *
     * @param dir
     * @return
     */
    public IndexReader getReader(Directory dir) {
        return getReader(dir, false);
    }

    /**
     * 创建索引阅读器
     *
     * @param directoryPath 索引目录
     * @return
     * @throws IOException 可能会抛出IO异常
     */
    public IndexReader getReader(String directoryPath) {
//        return DirectoryReader.open(FSDirectory.open(Paths.get(directoryPath, new String[0])));
        return getReader(openFSDirectory(directoryPath), false);
    }

    /**
     * 获取IndexReader对象
     *
     * @param dir
     * @param enableNRTReader 是否开启NRTReader
     * @return
     */
    public IndexReader getReader(Directory dir, boolean enableNRTReader) {
        if (null == dir) {
            throw new IllegalArgumentException("Directory can not be null.");
        }
        try {
            if (null == reader) {
                reader = DirectoryReader.open(dir);
            } else {
                if (enableNRTReader && reader instanceof DirectoryReader) {
                    //开启近实时Reader,能立即看到动态添加/删除的索引变化
                    reader = DirectoryReader.openIfChanged((DirectoryReader) reader);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

    /**
     * 关闭IndexReader
     *
     * @param reader
     */
    public void closeIndexReader(IndexReader reader) {
        if (null != reader) {
            try {
                reader.close();
                reader = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取索引器
     *
     * @return
     */
    public IndexSearcher getSearcher() {

        if (null == searcher) {
            searcher = getSearcher(this.indexStoreFolder);
        }
        return searcher;
    }

    /**
     * 获取IndexSearcher对象(不支持多线程查询)
     *
     * @param reader IndexReader对象实例
     * @return
     */
    public IndexSearcher getSearcher(IndexReader reader) {
        if (null == searcher) {
            searcher = new IndexSearcher(reader);
        }
        return searcher;
    }

    /**
     * 如果目录有变化，获取最新的目录进行读取
     *
     * @return
     */
    public IndexSearcher getSearcher(String indexStoreFolder) {
        try {

            if (reader == null) {
//                reader = IndexReader.open(directory);
                reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexStoreFolder)));
            }

            if (reader == null) {

//                IndexReader tr = IndexReader.openIfChanged(reader);//旧版
                IndexReader tr = DirectoryReader.openIfChanged((DirectoryReader) reader);
                if (tr != null) {
                    reader.close();
                    reader = tr;
                }
            }

            if (null == searcher) {
                searcher = new IndexSearcher(reader);
            }
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searcher;
    }

    /**
     * 创建QueryParser对象
     *
     * @param field
     * @param analyzer
     * @return
     */
    public QueryParser createQueryParser(String field, Analyzer analyzer) {
        return new QueryParser(field, analyzer);
    }


    /**
     * 关闭IndexReader和IndexWriter
     *
     * @param reader
     * @param writer
     */
    public void closeAll(IndexReader reader, IndexWriter writer) {
        closeIndexReader(reader);
        closeIndexWriter(writer);
    }

    /**
     * 对大量的关键词进行索引
     *
     * @param listKeywords
     */
    public void createKeywords(List<String> listKeywords) {

        IndexWriter writer = null;
        try {
            writer = getWriter();

            Document doc = null;
            for (String keyword : listKeywords) {
                if (StringUtils.isEmpty(keyword)) {
                    continue;
                }

                //创建新的Document
                doc = new Document();
                doc.add(new TextField("keyword", keyword.trim(), Field.Store.YES));

                //把Document加入 IndexWriter
                writer.addDocument(doc);
            }

            writer.commit();//提交
            writer.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            IOUtils.closeWhileHandlingException(writer);
        }

    }


    /**
     * 更新索引文档
     *
     * @param writer
     * @param term
     * @param document
     */
    public void updateDocument(IndexWriter writer, Term term, Document document) {
        try {
            writer.updateDocument(term, document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加索引文档
     *
     * @param writer
     */
    public void updateDocument(IndexWriter writer, Document document) {
        updateDocument(writer, null, document);
    }

    /**
     * 根据ID进行更新数据
     *
     * @param doc
     * @param id
     */
    public void updateDocument(Document doc, Integer id) {
        updateDocument(doc, new Term("id", id.toString()));
    }

    /**
     * 根据Term进行数据更新
     *
     * @param doc
     * @param term
     */
    public void updateDocument(Document doc, Term term) {

        try {
            IndexWriter indexWriter = getWriter();

            indexWriter.updateDocument(term, doc);

            indexWriter.commit();
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 更新索引文档
     *
     * @param writer
     * @param document
     */
    public void updateDocument(IndexWriter writer, String field, String value, Document document) {
        updateDocument(writer, new Term(field, value), document);
    }


    /**
     * 精确查询
     *
     * @param field
     * @param name
     * @param num
     */
    public void searchByTerm(String field, String name, int num) {
        try {
            IndexSearcher searcher = getSearcher();
            Query query = new TermQuery(new Term(field, name));
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了:" + tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") + "---->" +
                        doc.get("name") + "[" + doc.get("email") + "]-->" + doc.get("id") + "," +
                        doc.get("attach") + "," + doc.get("date"));
            }
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 范围查询
     *
     * @param field 要搜索的字段
     * @param start 开始位置
     * @param end   结束位置
     * @param num   搜索数量
     */
    public void searchByTermRange(String field, String start, String end, int num) {
        try {
            IndexSearcher searcher = getSearcher();
            Query query = new TermRangeQuery(field, new BytesRef(start), new BytesRef(end), true, true);
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了:" + tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") + "---->" +
                        doc.get("name") + "[" + doc.get("email") + "]-->" + doc.get("id") + "," +
                        doc.get("attach") + "," + doc.get("date"));
            }
//            searcher.close();
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 字符串范围查询
     *
     * @param field
     * @param lowerTerm
     * @param upperTerm
     * @param includeLower
     * @param includeUpper
     * @return
     */
    public static TermRangeQuery newStringRange(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper) {
        BytesRef lower = lowerTerm == null ? null : new BytesRef(lowerTerm);
        BytesRef upper = upperTerm == null ? null : new BytesRef(upperTerm);
        return new TermRangeQuery(field, lower, upper, includeLower, includeUpper);
    }

    /**
     * 数字范围查询
     *
     * @param
     * @return
     * @author
     */
    public void searchByNumricRange(String field, int start, int end, int num) {
        try {
            IndexSearcher searcher = getSearcher();

            // 6.x之后的用法
            Query query = IntPoint.newRangeQuery(field, start, end);

//            Query query = NumericRangeQuery.newIntRange(field, start, end, true, true);
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了:" + tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") + "---->" +
                        doc.get("name") + "[" + doc.get("email") + "]-->" + doc.get("id") + "," +
                        doc.get("attach") + "," + doc.get("date"));
            }
//            searcher.close();
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 前缀查询
     *
     * @param
     * @return
     * @author
     */
    public void searchByPrefix(String field, String value, int num) {
        try {
            IndexSearcher searcher = getSearcher();
            Query query = new PrefixQuery(new Term(field, value));
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了:" + tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") + "---->" +
                        doc.get("name") + "[" + doc.get("email") + "]-->" + doc.get("id") + "," +
                        doc.get("attach") + "," + doc.get("date"));
            }
//            searcher.close();
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通配符查询
     *
     * @param
     * @return
     * @author
     */
    public void searchByWildcard(String field, String value, int num) {
        try {
            IndexSearcher searcher = getSearcher();
            //在传入的value中可以使用通配符:?和*,?表示匹配一个字符，*表示匹配任意多个字符
            Query query = new WildcardQuery(new Term(field, value));
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了:" + tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") + "---->" +
                        doc.get("name") + "[" + doc.get("email") + "]-->" + doc.get("id") + "," +
                        doc.get("attach") + "," + doc.get("date"));
            }
//            searcher.close();
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接子查询
     * BooleanQuery可以连接多个子查询
     * Occur.MUST表示必须出现
     * Occur.SHOULD表示可以出现
     * Occur.MUSE_NOT表示不能出现
     *
     * @param num
     */
    public void searchByBoolean(int num) {
        try {
            IndexSearcher searcher = getSearcher();

            // 6.X之后的写法
            // 包括美国，不包括日本的记录
            Query query1 = new TermQuery(new Term("title", "美国"));
            Query query2 = new TermQuery(new Term("content", "日本"));
            BooleanClause bc1 = new BooleanClause(query1, BooleanClause.Occur.MUST);
            BooleanClause bc2 = new BooleanClause(query2, BooleanClause.Occur.MUST_NOT);
            BooleanQuery boolQuery = new BooleanQuery.Builder().add(bc1).add(bc2).build();

            System.out.println(boolQuery.toString());

            // 6.X之前的写法
//            BooleanQuery boolQuery = new BooleanQuery();
//            boolQuery.add(new TermQuery(new Term("name", "zhangsan")), BooleanClause.Occur.MUST_NOT);
//            boolQuery.add(new TermQuery(new Term("content", "game")), BooleanClause.Occur.SHOULD);
//
            TopDocs tds = searcher.search(boolQuery, num);
            System.out.println("一共查询了:" + tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") + "---->" +
                        doc.get("name") + "[" + doc.get("email") + "]-->" + doc.get("id") + "," +
                        doc.get("attach") + "," + doc.get("date"));
            }
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据短语和距离查询
     * http://lucene.apache.org/core/6_5_0/core/org/apache/lucene/search/PhraseQuery.html
     *
     * @param
     * @return
     * @author
     */
    public void searchByPhrase(int num) {
        try {
            IndexSearcher searcher = getSearcher();

            // 6.X之后的用法
            PhraseQuery.Builder builder = new PhraseQuery.Builder();
            builder.add(new Term("body", "one"), 4);
            builder.add(new Term("body", "two"), 5);
            PhraseQuery query = builder.build();

            // 6.X以前用法
//            PhraseQuery query = new PhraseQuery();
//            query.setSlop(3);
//            query.add(new Term("content", "pingpeng"));
//            //第一个Term
//            query.add(new Term("content", "i"));

            //产生距离之后的第二个Term
//          query.add(new Term("content","football"));
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了:" + tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") + "---->" +
                        doc.get("name") + "[" + doc.get("email") + "]-->" + doc.get("id") + "," +
                        doc.get("attach") + "," + doc.get("date"));
            }
//            searcher.close();
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 搜索数据
     *
     * @param searchString
     * @param analyzer
     */
    public void indexSearch(String searchString, Analyzer analyzer) {

        IndexSearcher searcher = null;

        ScoreDoc[] hits = null;
        try {

            //根据索引位置简历 IndexSearch
            searcher = getSearcher();

            // 解析用户的输入
            QueryParser parser = new QueryParser("fieldname", analyzer);// 要检索的字段和使用的分词器
            Query query = parser.parse(searchString);// 把搜索词扔进来分词准备进行搜索

//            //建立搜索单元,searchType 代表要搜索的Field,searchKey代表关键字
//            Term t = new Term("url","");
//            //由Term产生 Query
//            Query q = new TermQuery(t);

//            //获取一个 <document,frequency>的枚举对象 TermDocs
//            TermDocs docs = isearcher.getIndexReader().totalTermFreq(t);
//            while(docs.next()){
//                System.out.print("find" + docs.freq() + "matches in");
//                System.out.println(searcher.getIndexReader().document(docs.doc()).getField("filename").stringValue());
//
//            }

            TopDocs topDocs = searcher.search(query, 10);
            System.out.println("查找到的文档总共有：" + topDocs.totalHits);

            // 根据TopDocs获取ScoreDoc对象
            hits = topDocs.scoreDocs;

            // 遍历并打印所有相关数据
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = searcher.doc(hits[i].doc);
                String body = hitDoc.get("body");
                String path = hitDoc.get("path");
//            assertEquals("This is the text to be indexed.",hitDoc.get("fieldname"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeWhileHandlingException(reader);
        }
    }


    /**
     * 搜索索引里面的关键词信息
     *
     * @param searchString
     * @param analyzer
     */
    public List<String> queryKeywords(String searchString, Analyzer analyzer) {

        IndexSearcher searcher = null;

        List<String> listResults = Lists.newArrayList();
        try {

            //根据索引位置简历 IndexSearch
            searcher = getSearcher();

            // 解析用户的输入
            QueryParser parser = new QueryParser("keyword", analyzer);// 要检索的字段和使用的分词器
            Query query = parser.parse(searchString);// 把搜索词扔进来分词准备进行搜索
            ScoreDoc[] hits = searcher.search(query, 10).scoreDocs;

            // 遍历并打印所有相关数据
            String keywords;
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = searcher.doc(hits[i].doc);
                keywords = hitDoc.get("keyword");
                listResults.add(keywords);
            }
            return listResults;
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            IOUtils.closeWhileHandlingException(reader);
            return listResults;
        }
    }

    /**
     * 返回指定数量的结果
     *
     * @param query
     * @param searchNum
     * @return
     * @throws IOException
     */
    public List<Document> query(Query query, Integer searchNum) throws IOException {
        IndexSearcher searcher = getSearcher();
        return query(searcher, query, searchNum);
    }

    /**
     * 根据指定的Query检索并返回搜索出来的文档
     * 通过短语查询，用得最多，以上都可以实现，具体的query实现不同的功能
     *
     * @param query
     * @param searchNum
     * @return
     * @throws IOException
     */
    public List<Document> query(IndexSearcher searcher, Query query, Integer searchNum) throws IOException {
//        IndexSearcher searcher = getSearcher();
        TopDocs topDocs = searcher.search(query, searchNum > 0 ? searchNum : Integer.MAX_VALUE);
        List<Document> docList = new ArrayList<Document>();
        ScoreDoc[] docs = topDocs.scoreDocs;
        int length = docs.length;
        if (length <= 0) {
            return Collections.emptyList();
        }

        try {
            for (int i = 0; i < length; i++) {
                Document doc = searcher.doc(docs[i].doc);
                docList.add(doc);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

//        for (ScoreDoc scoreDoc : docs) {
//            int docID = scoreDoc.doc;
//            Document document = searcher.doc(docID);
//            docList.add(document);
//        }
        searcher.getIndexReader().close();
        return docList;
    }

    /**
     * 返回索引文档的总数[注意：请自己手动关闭IndexReader]
     *
     * @param reader
     * @return
     */
    public int getIndexTotalCount(IndexReader reader) {
        return reader.numDocs();
    }

    /**
     * 返回索引文档中最大文档ID[注意：请自己手动关闭IndexReader]
     *
     * @param reader
     * @return
     */
    public int getMaxDocId(IndexReader reader) {
        return reader.maxDoc();
    }

    /**
     * 返回已经删除尚未提交的文档总数[注意：请自己手动关闭IndexReader]
     *
     * @param reader
     * @return
     */
    public int getDeletedDocNum(IndexReader reader) {
        return getMaxDocId(reader) - getIndexTotalCount(reader);
    }

    /**
     * 根据docId查询索引文档
     *
     * @param reader       IndexReader对象
     * @param docID        documentId
     * @param fieldsToLoad 需要返回的field
     * @return
     */
    public Document findDocumentByDocId(IndexReader reader, int docID, Set<String> fieldsToLoad) {
        try {
            return reader.document(docID, fieldsToLoad);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 根据docId查询索引文档
     *
     * @param reader IndexReader对象
     * @param docID  documentId
     * @return
     */
    public Document findDocumentByDocId(IndexReader reader, int docID) {
        return findDocumentByDocId(reader, docID, null);
    }

    /**
     * @param query            索引查询对象
     * @param prefix           高亮前缀字符串
     * @param stuffix          高亮后缀字符串
     * @param fragmenterLength 摘要最大长度
     * @return
     * @Title: createHighlighter
     * @Description: 创建高亮器
     */
    public Highlighter createHighlighter(Query query, String prefix, String stuffix, int fragmenterLength) {
        Formatter formatter = new SimpleHTMLFormatter((prefix == null || prefix.trim().length() == 0) ?
                "<font color=\"red\">" : prefix, (stuffix == null || stuffix.trim().length() == 0) ? "</font>" : stuffix);
        Scorer fragmentScorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, fragmentScorer);
        Fragmenter fragmenter = new SimpleFragmenter(fragmenterLength <= 0 ? 50 : fragmenterLength);
        highlighter.setTextFragmenter(fragmenter);
        return highlighter;
    }

    /**
     * @param document    索引文档对象
     * @param highlighter 高亮器
     * @param analyzer    索引分词器
     * @param field       高亮字段
     * @return
     * @throws IOException
     * @throws InvalidTokenOffsetsException
     * @Title: highlight
     * @Description: 生成高亮文本
     */
    public String highlight(Document document, Highlighter highlighter, Analyzer analyzer, String field) throws IOException {
        List<IndexableField> list = document.getFields();
        for (IndexableField fieldable : list) {
            String fieldValue = fieldable.stringValue();
            if (fieldable.name().equals(field)) {
                try {
                    fieldValue = highlighter.getBestFragment(analyzer, field, fieldValue);
                } catch (InvalidTokenOffsetsException e) {
                    fieldValue = fieldable.stringValue();
                }
                return (fieldValue == null || fieldValue.trim().length() == 0) ? fieldable.stringValue() : fieldValue;
            }
        }
        return null;
    }

    /**
     * @param query
     * @return
     * @throws IOException
     * @Title: searchTotalRecord
     * @Description: 获取符合条件的总记录数
     */
    public int searchTotalRecord(IndexSearcher search, Query query) {
        ScoreDoc[] docs = null;
        try {
            TopDocs topDocs = search.search(query, Integer.MAX_VALUE);
            if (topDocs == null || topDocs.scoreDocs == null || topDocs.scoreDocs.length == 0) {
                return 0;
            }
            docs = topDocs.scoreDocs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docs.length;
    }

    /**
     * @param searcher
     * @param query
     * @param page
     * @throws IOException
     * @Title: pageQuery
     * @Description: Lucene分页查询
     */
    public void pageQuery(IndexSearcher searcher, Directory directory, Query query, Page<Document> page) {
        int totalRecord = searchTotalRecord(searcher, query);
        //设置总记录数
        page.setTotalRecord(totalRecord);
        TopDocs topDocs = null;
        try {
            topDocs = searcher.searchAfter(page.getAfterDoc(), query, page.getPageSize());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Document> docList = new ArrayList<Document>();
        ScoreDoc[] docs = topDocs.scoreDocs;
        int index = 0;
        for (ScoreDoc scoreDoc : docs) {
            int docID = scoreDoc.doc;
            Document document = null;
            try {
                document = searcher.doc(docID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (index == docs.length - 1) {
                page.setAfterDoc(scoreDoc);
                page.setAfterDocId(docID);
            }
            docList.add(document);
            index++;
        }
        page.setItems(docList);
        closeIndexReader(searcher.getIndexReader());
    }

    /**
     * @param query
     * @param page
     * @param highlighterParam
     * @param writerConfig
     * @throws IOException
     */
    public void pageQuery(Query query, Page<Document> page, HighlighterParam highlighterParam, IndexWriterConfig writerConfig) throws IOException {
        pageQuery(searcher, openFSDirectory(indexStoreFolder), query, page, highlighterParam, writer);
    }

    /**
     * @param searcher
     * @param directory
     * @param query
     * @param page
     * @param highlighterParam
     * @throws IOException
     * @Title: pageQuery
     * @Description: 分页查询[如果设置了高亮, 则会更新索引文档]
     */
    public void pageQuery(IndexSearcher searcher, Directory directory, Query query, Page<Document> page, HighlighterParam highlighterParam, IndexWriter writer) throws IOException {
        //若未设置高亮
        if (null == highlighterParam || !highlighterParam.isHighlight()) {
            pageQuery(searcher, directory, query, page);
        } else {
            int totalRecord = searchTotalRecord(searcher, query);
            System.out.println("totalRecord:" + totalRecord);
            //设置总记录数
            page.setTotalRecord(totalRecord);
            TopDocs topDocs = searcher.searchAfter(page.getAfterDoc(), query, page.getPageSize());
            List<Document> docList = new ArrayList<Document>();
            ScoreDoc[] docs = topDocs.scoreDocs;
            int index = 0;
            for (ScoreDoc scoreDoc : docs) {
                int docID = scoreDoc.doc;
                Document document = searcher.doc(docID);
                String content = document.get(highlighterParam.getFieldName());
                if (null != content && content.trim().length() > 0) {
                    //创建高亮器
                    Highlighter highlighter = createHighlighter(query,
                            highlighterParam.getPrefix(), highlighterParam.getStuffix(),
                            highlighterParam.getFragmenterLength());
                    String text = highlight(document, highlighter, analyzer, highlighterParam.getFieldName());
                    //若高亮后跟原始文本不相同，表示高亮成功
                    if (!text.equals(content)) {
                        Document tempdocument = new Document();
                        List<IndexableField> indexableFieldList = document.getFields();
                        if (null != indexableFieldList && indexableFieldList.size() > 0) {
                            for (IndexableField field : indexableFieldList) {
                                if (field.name().equals(highlighterParam.getFieldName())) {
                                    tempdocument.add(new TextField(field.name(), text, Field.Store.YES));
                                } else {
                                    tempdocument.add(field);
                                }
                            }
                        }
                        updateDocument(writer, new Term(highlighterParam.getFieldName(), content), tempdocument);
                        document = tempdocument;
                    }
                }
                if (index == docs.length - 1) {
                    page.setAfterDoc(scoreDoc);
                    page.setAfterDocId(docID);
                }
                docList.add(document);
                index++;
            }
            page.setItems(docList);
        }
        closeIndexReader(searcher.getIndexReader());
        closeIndexWriter(writer);
    }


    /**
     * 对目录下面的文件建立索引
     *
     * @param folderToIndex
     */
    public void indexFolders(String folderToIndex) {
        try {

            IndexWriter writer = getWriter();

            File filesDir = new File(folderToIndex);

            //取得 要建立 索引的文件数组
            File[] files = filesDir.listFiles();

            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (fileName.substring(fileName.lastIndexOf(".")).equals(".txt")) {

                    //创建新的Document
                    Document doc = new Document();

                    //为文件名创建一个 Field
                    Field field = new Field("filename", files[i].getName(), TextField.TYPE_STORED);
                    doc.add(field);

                    field = new Field("content", FileUtils.readFileToString(files[i], "utf-8"), TextField.TYPE_STORED);
                    doc.add(field);

                    //把Document加入 IndexWriter
                    writer.addDocument(doc);
                }
            }
            writer.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    /**
     * 高亮显示
     * 使用时：String value = toHighlighter(query,document,"content",analyzer);
     *
     * @param query
     * @param doc
     * @param field
     * @param analyzer
     * @return
     */
    public String toHighlighter(Query query, Document doc, String field, Analyzer analyzer) {
        try {
            SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("<font color=\"red\">", "</font>");
            Highlighter highlighter = new Highlighter(simpleHtmlFormatter, new QueryScorer(query));
            TokenStream tokenStream1 = analyzer.tokenStream("text", new StringReader(doc.get(field)));
            String highlighterStr = highlighter.getBestFragment(tokenStream1, doc.get(field));
            return highlighterStr == null ? doc.get(field) : highlighterStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据商品ID删除索引文件
     *
     * @param id
     */
    public void deleteDocument(String field, int id) {
        BytesRef bytes = new BytesRef(id);
        Term term = new Term(field, bytes);
        deleteByTerm(term);
    }


    /**
     * 根据传入的文档ID进行删除操作
     *
     * @param id
     */
    public void deleteDocument(int id) {

        IndexWriter indexWriter = null;

        try {
            indexWriter = getWriter();

            // 6.x之后的用法
            Query query = IntPoint.newRangeQuery("id", id, id);

            // 6.x以前的用法
//            Query query = NumericRangeQuery.newIntRange("ID",id-1, id+1, false, false);
            indexWriter.deleteDocuments(query);
            indexWriter.commit();
            indexWriter.close();

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    /**
     * 根据Term删除数据
     *
     * @param term
     */
    public void deleteByTerm(Term term) {
//        Analyzer analyzer = AnalyzerUtils.getAnalyzer(AnalyzerType.IKAnalyzer);

        IndexWriter indexWriter = null;
        try {

            indexWriter = getWriter();
            indexWriter.deleteDocuments(term);
            indexWriter.commit();//提交
            indexWriter.close();//关闭

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除索引[注意：请自己关闭IndexWriter对象]
     *
     * @param writer
     * @param field
     * @param value
     */
    public void deleteIndex(IndexWriter writer, String field, String value) {
        try {
            writer.deleteDocuments(new Term[]{new Term(field, value)});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除索引[注意：请自己关闭IndexWriter对象]
     *
     * @param writer
     * @param query
     */
    public void deleteIndex(IndexWriter writer, Query query) {
        try {
            writer.deleteDocuments(query);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量删除索引[注意：请自己关闭IndexWriter对象]
     *
     * @param writer
     * @param terms
     */
    public void deleteIndexs(IndexWriter writer, Term[] terms) {
        try {
            writer.deleteDocuments(terms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量删除索引[注意：请自己关闭IndexWriter对象]
     *
     * @param writer
     * @param querys
     */
    public void deleteIndexs(IndexWriter writer, Query[] querys) {
        try {
            writer.deleteDocuments(querys);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除所有索引文档
     *
     * @param writer
     */
    public void deleteAllIndex(IndexWriter writer) {
        try {
            writer.deleteAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
