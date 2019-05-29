package com.lingdonge.http.jsoupxpath.model;

import com.lingdonge.http.jsoupxpath.core.XpathEvaluator;
import com.lingdonge.http.jsoupxpath.exception.NoSuchAxisException;
import com.lingdonge.http.jsoupxpath.exception.NoSuchFunctionException;
import com.lingdonge.http.jsoupxpath.exception.XpathSyntaxErrorException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;


/**
 * @author github.com/zhegexiaohuozi [seimimaster@gmail.com]
 */
public class JXDocument {
    private Elements elements;
    private XpathEvaluator xpathEva = new XpathEvaluator();
    public JXDocument(Document doc){
        elements = doc.children();
    }
    public JXDocument(String html){
        elements = Jsoup.parse(html).children();
    }
    public JXDocument(Elements els){
        elements = els;
    }
    
    public List<Object> sel(String xpath) throws XpathSyntaxErrorException {
        List<Object> res = new LinkedList<Object>();
        try {
             List<JXNode> jns = xpathEva.xpathParser(xpath,elements);
             for (JXNode j:jns){
                 if (j.isText()){
                     res.add(j.getTextVal());
                 }else {
                     res.add(j.getElement());
                 }
             }
        } catch (Exception e){
            String msg = "please check the jsoupxpath syntax";
            if (e instanceof NoSuchAxisException ||e instanceof NoSuchFunctionException){
                msg = e.getMessage();
            }
            throw new XpathSyntaxErrorException(msg);
        }
        return res;
    }

    public List<JXNode> selN(String xpath) throws XpathSyntaxErrorException{
        try {
            return xpathEva.xpathParser(xpath,elements);
        }catch (Exception e){
            String msg = "please check the jsoupxpath syntax";
            if (e instanceof NoSuchAxisException||e instanceof NoSuchFunctionException){
                msg = e.getMessage();
            }
            throw new XpathSyntaxErrorException(msg);
        }
    }
    
    public Object selOne(String xpath) throws XpathSyntaxErrorException {
    	JXNode jxNode = selNOne(xpath);
    	if(jxNode != null) {
    		if (jxNode.isText()){
                return jxNode.getTextVal();
            }else {
                return jxNode.getElement();
            }
    	}
    	return null;
    }
    
    public JXNode selNOne(String xpath) throws XpathSyntaxErrorException {
    	List<JXNode> jxNodeList = selN(xpath);
    	if(jxNodeList != null && jxNodeList.size() > 0) {
    		return jxNodeList.get(0);
    	}
    	return null;
    }
}
