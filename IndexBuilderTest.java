//package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

//import indexing.IndexBuilder;

/**
 * @author xiranxu
 */
public class IndexBuilderTest {
    String rssTest = "http://cit594.ericfouh.com/sample_rss_feed.xml";
    String html1 = "http://cit594.ericfouh.com/page1.html";
    String html2 = "http://cit594.ericfouh.com/page2.html";
    String html3 = "http://cit594.ericfouh.com/page3.html";
    String html4 = "http://cit594.ericfouh.com/page4.html";
    String html5 = "http://cit594.ericfouh.com/page5.html";
    Map<String, List<String>> docs;
    IndexBuilder ib = new IndexBuilder();
    List<String> rss = new ArrayList<>();

    @Before
    public void setUp() {
        rss.add(rssTest);
        docs = ib.parseFeed(rss);
    }
    
    @Test
    public void testParseFeed() {
        assertEquals(5, docs.size());
        assertTrue(docs.containsKey(html1));
        assertTrue(docs.containsKey(html2));
        assertTrue(docs.containsKey(html3));
        assertTrue(docs.containsKey(html4));
        assertTrue(docs.containsKey(html5));
        assertEquals(10, docs.get(html1).size());
        assertEquals(55, docs.get(html2).size());
        assertEquals(33, docs.get(html3).size());
        assertEquals(22, docs.get(html4).size());
        assertEquals(18, docs.get(html5).size());  
    }
    
    @Test
    public void testBuildIndex() {
        Map<String, Map<String, Double>> forwardIndex = ib.buildIndex(docs);
        assertEquals(8,forwardIndex.get(html1).size());
        assertEquals(21,forwardIndex.get(html4).size());
        assertEquals(0.1021, forwardIndex.get(html1).get("data"), 0.0001);
        assertEquals(0.0585, forwardIndex.get(html2).get("search"), 0.0001);
        assertEquals(0.04877, forwardIndex.get(html3).get("implement"), 0.00001);
        assertEquals(0.0731, forwardIndex.get(html4).get("mallarme"), 0.0001);
        assertEquals(0.0894, forwardIndex.get(html5).get("categorization"), 0.0001);
        
    }
    
    @Test
    public void testBuildInvertedIndex() {
        Map<String, Map<String, Double>> forwardIndex = ib.buildIndex(docs);
        @SuppressWarnings("unchecked")
        Map<String, Collection<Entry<String, Double>>> invertedIndex = 
                (Map<String, Collection<Entry<String, Double>>>)ib.buildInvertedIndex(forwardIndex);
        assertEquals(3, invertedIndex.get("data").size());
        assertEquals(2, invertedIndex.get("structures").size());
        assertEquals(1, invertedIndex.get("about").size());
        assertEquals(html1, (((ArrayList<Entry<String, Double>>) 
                invertedIndex.get("structures")).get(0)).getKey());
        assertEquals(html2, (((ArrayList<Entry<String, Double>>) 
                invertedIndex.get("structures")).get(1)).getKey());
        assertEquals(html1, (((ArrayList<Entry<String, Double>>) 
                invertedIndex.get("queues")).get(0)).getKey());
    }
    
    @Test
    public void testBuildHomePage() {
        Map<String, Map<String, Double>> forwardIndex = ib.buildIndex(docs);
        @SuppressWarnings("unchecked")
        Map<String, Collection<Entry<String, Double>>> invertedIndex = 
                (Map<String, Collection<Entry<String, Double>>>)ib.buildInvertedIndex(forwardIndex);
        ArrayList<Entry<String, List<String>>> homepage = (
                ArrayList<Entry<String, List<String>>>) ib.buildHomePage(invertedIndex);
        assertEquals(57, homepage.size());
        assertEquals("data", homepage.get(0).getKey());
        assertEquals("trees", homepage.get(1).getKey()); 
        assertEquals("structures", homepage.get(2).getKey());       
    }
    
    @Test
    public void testCreateAutocompleteFile() {
        Map<String, Map<String, Double>> forwardIndex = ib.buildIndex(docs);
        @SuppressWarnings("unchecked")
        Map<String, Collection<Entry<String, Double>>> invertedIndex = 
                (Map<String, Collection<Entry<String, Double>>>)ib.buildInvertedIndex(forwardIndex);
        ArrayList<Entry<String, List<String>>> homepage = (
                ArrayList<Entry<String, List<String>>>) ib.buildHomePage(invertedIndex);
        @SuppressWarnings("unchecked")
        ArrayList<String> file = (ArrayList<String>) ib.createAutocompleteFile(homepage);
        assertEquals(homepage.size(), file.size());
    }
    
    @Test
    public void testSearchArticles() {
        Map<String, Map<String, Double>> forwardIndex = ib.buildIndex(docs);
        @SuppressWarnings("unchecked")
        Map<String, Collection<Entry<String, Double>>> invertedIndex = 
                (Map<String, Collection<Entry<String, Double>>>)ib.buildInvertedIndex(forwardIndex);
        List<String> articles = ib.searchArticles("structures", invertedIndex);
        assertEquals(2, articles.size());
        assertEquals(html1, articles.get(0));
        assertEquals(html2, articles.get(1));
        
        List<String> articlesNull = ib.searchArticles("homework", invertedIndex);
        assertNull(articlesNull);
    }

}
