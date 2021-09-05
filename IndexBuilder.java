//package indexing;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class IndexBuilder implements IIndexBuilder {
    
    /**
     * <parseFeed> Parse each document/rss feed in the list and return a Map of
     * each document and all the words in it. (punctuation and special
     * characters removed)
     * 
     * @param feeds a List of rss feeds to parse
     * @return a Map of each documents (identified by its url) and the list of
     *         words in it.
     */
    @Override
    public Map<String, List<String>> parseFeed(List<String> feeds) {
        Map<String, List<String>> urlWords = new HashMap<>();
        for (String feed : feeds) {
            try {
                Document doc = Jsoup.connect(feed).get();
                Elements links = doc.getElementsByTag("link");
                for (Element link : links) {
                    String linkText = link.text();
                    Document html = Jsoup.connect(linkText).get();
                    Elements contents = html.getElementsByTag("body");
                    List<String> wordsList = new ArrayList<>();
                    for (Element content : contents) {
                        String contentText = content.text();
                        String validContent = contentText.replaceAll("[^a-zA-Z ]", "")
                                .toLowerCase();
                        String[] words = validContent.split(" ");
                        wordsList.addAll(Arrays.asList(words));
                    }
                    urlWords.put(linkText, wordsList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return urlWords;
    }
        
    
    /**
     * @param docs a map computed by {@parseFeed}
     * @return the forward index: a map of all documents and their 
     *         tags/keywords. the key is the document, the value is a 
     *         map of a tag term and its TFIDF value. 
     *         The values (Map<String, Double>) are sorted
     *         by lexicographic order on the key (tag term)
     *  
     */
    @Override
    public Map<String, Map<String, Double>> buildIndex(Map<String, List<String>> docs) {
        
        Map<String, Map<String, Integer>> docsTerms = new HashMap<>();
        Map<String, Integer> termsDocs = new HashMap<>();
        for (String doc : docs.keySet()) {
            Map<String, Integer> termCount = new HashMap<>();
            for (String term : docs.get(doc)) {
                termCount.put(term, termCount.getOrDefault(term, 0) + 1);
                if (termCount.get(term) == 1) {
                    termsDocs.put(term, termsDocs.getOrDefault(term, 0) + 1);
                }
            }
            docsTerms.put(doc, termCount);
        }
        
        Map<String, Map<String, Double>> forwardIndex = new HashMap<>();
        for (String doc : docsTerms.keySet()) {
            Map<String, Integer> termCount = docsTerms.get(doc);
            Map<String, Double> tagValue = new HashMap<>();
            for (String term : termCount.keySet()) {
                double tf = ((double)termCount.get(term)) / ((double)docs.get(doc).size());
                double idf = Math.log((double)(docs.size()) / ((double)termsDocs.get(term)));
                double tfidf = tf * idf;
                tagValue.put(term, tfidf);
            }
            Map<String, Double> sorted = new TreeMap<>(tagValue);
            forwardIndex.put(doc, sorted);
        }
        return forwardIndex;
    }
    
    /**
     * Compare two entry objects in descending order by value.
     * @return comparator Object
     */
    public static Comparator<Entry<String, Double>> reversedTFIDFValue() {
        return new Comparator<Entry<String, Double>>() {
            @Override
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }   
        };
    }
    
    /**
     * Build an inverted index consisting of a map of each tag term and a Collection (Java)
     * of Entry objects mapping a document with the TFIDF value of the term 
     * (for that document)
     * The Java collection (value) is sorted by reverse tag term TFIDF value 
     * (the document in which a term has the
     * highest TFIDF should be listed first).
     * 
     * 
     * @param index the index computed by {@buildIndex}
     * @return inverted index - a sorted Map of the documents in which term is a keyword
     */
    @Override
    public Map<?, ?> buildInvertedIndex(Map<String, Map<String, Double>> index) {
        Map<String, Collection<Entry<String, Double>>> invertedIndex = new HashMap<>();
        for (String doc : index.keySet()) {
            Map<String, Double> tagValue = index.get(doc);
            for (String term : tagValue.keySet()) {
                Collection<Entry<String, Double>> collection = 
                        new ArrayList<Entry<String, Double>>();
                collection = invertedIndex.getOrDefault(term, collection);
                collection.add(new AbstractMap.SimpleEntry<String, Double>(doc, 
                        tagValue.get(term)));
                invertedIndex.put(term, collection);
            }
        }
        for (String term : invertedIndex.keySet()) {
            Collection<Entry<String, Double>> collection = invertedIndex.get(term);
            Collections.sort((List<Entry<String, Double>>)collection, reversedTFIDFValue());
            invertedIndex.put(term, collection);
        }
        return invertedIndex;
    }
    
    /**
     * Compare two entry objects in descending order by value.
     * @return comparator Object
     */
    public static Comparator<Entry<String, List<String>>> buildHomePageComparator() {
        return new Comparator<Entry<String, List<String>>>() {
            @Override
            public int compare(Entry<String, List<String>> o1, Entry<String, List<String>> o2) {
                if (o2.getValue().size() == o1.getValue().size()) {
                    return o2.getKey().compareTo(o1.getKey());
                }
                return o2.getValue().size() - o1.getValue().size();
            }
        };
    }
    /**
     * @param invertedIndex
     * @return a sorted collection of terms and articles Entries are sorted by
     *         number of articles. If two terms have the same number of 
     *         articles, then they should be sorted by reverse lexicographic order.
     *         The Entry class is the Java abstract data type
     *         implementation of a tuple
     *         https://docs.oracle.com/javase/9/docs/api/java/util/Map.Entry.html
     *         One useful implementation class of Entry is
     *         AbstractMap.SimpleEntry
     *         https://docs.oracle.com/javase/9/docs/api/java/util/AbstractMap.SimpleEntry.html
     */
    @Override
    public Collection<Entry<String, List<String>>> buildHomePage(Map<?, ?> invertedIndex) {
        Collection<Entry<String, List<String>>> homePage = new ArrayList<>();
        for (Object termObj : invertedIndex.keySet()) {
            String term = (String) termObj;
            if (!STOPWORDS.contains(term)) {
                List<String> articles = new ArrayList<>();
                @SuppressWarnings("unchecked")
                Collection<Entry<String, Double>> collection = 
                        (Collection<Entry<String, Double>>) invertedIndex.get(term);
                for (Entry<String, Double> entry : collection) {
                    articles.add(entry.getKey());
                }
                homePage.add(new AbstractMap.SimpleEntry<String, List<String>>(term, articles));
            }
        }
        Collections.sort((List<Entry<String, List<String>>>)homePage, buildHomePageComparator());
        return homePage;
    }

    /**
     * Create a file containing all the words in the inverted index. Each word
     * should occupy a line Words should be written in lexicographic order
     * assign a weight of 0 to each word. The method must store the words into a 
     * file named autocomplete.txt
     * 
     * @param homepage the collection used to generate the homepage (buildHomePage)
     * @return A collection containing all the words written into the file 
     * sorted by lexicographic order
     */
    @Override
    public Collection<?> createAutocompleteFile(Collection<Entry<String, List<String>>> homepage) {
        Collection<String> file = new ArrayList<>();
        for (Entry<String, List<String>> entry : homepage) {
            file.add(entry.getKey());
        }
        Collections.sort((List<String>)file);
        File f = new File("autocomplete.txt");
        try {
            FileWriter fw = new FileWriter(f);
            int numOfWords = homepage.size();
            fw.write(numOfWords + "\n");
            for (String word : file) {
                fw.write("\t" + 0 + " " + word + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    
    /**
     * @param queryTerm
     * @param invertedIndex
     * @return
     */
    @Override
    public List<String> searchArticles(String queryTerm, Map<?, ?> invertedIndex) {
        List<String> articles = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Collection<Entry<String, Double>> collection = 
                (Collection<Entry<String, Double>>) invertedIndex.get(queryTerm);
        if (collection != null) {
            for (Entry<String, Double> entry : collection) {
                articles.add(entry.getKey());
            }
        } else {
            return null;
        }
        return articles;
    }

}
