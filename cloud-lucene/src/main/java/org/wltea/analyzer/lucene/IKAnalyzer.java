
package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.IOUtils;

import java.io.Reader;
import java.io.StringReader;


/**
 * IK分词器，Lucene Analyzer接口实现
 * 兼容Lucene 6.5.0版本 暴走抹茶 2017.3.28
 */
public final class IKAnalyzer extends Analyzer {

    private boolean useSmart;

    public boolean useSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    /**
     * IK分词器Lucene  Analyzer接口实现类
     * <p>
     * 默认细粒度切分算法
     */
    public IKAnalyzer() {
        this(false);
    }

    /**
     * IK分词器Lucene Analyzer接口实现类
     *
     * @param useSmart 当为true时，分词器进行智能切分
     */
    public IKAnalyzer(boolean useSmart) {
        super();
        this.useSmart = useSmart;
    }

    /**
     * @param fieldName
     * @return
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Reader reader = null;
        try {
            reader = new StringReader(fieldName);
            IKTokenizer it = new IKTokenizer(reader);
            return new TokenStreamComponents(it);
        } finally {
            IOUtils.closeWhileHandlingException(reader);
        }
    }

}
