import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Autocomplete implements IAutocomplete {
    private Node root;
    private int k;
    
    public Autocomplete() {
        this.root = new Node("",0);
    }
    
    /**
     * Adds a new word with its associated weight to the Trie
     * 
     * @param word the word to be added to the Trie
     * @param weight the weight of the word
     */
    @Override
    public void addWord(String word, long weight) {
        Node node = root;
        if (word != null && weight >= 0) {
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                if (!Character.isLetter(c)) {
                    return;
                }
            }
        } else { 
            return;
        }
        String lowerWord = word.toLowerCase();
        for (int i = 0; i < lowerWord.length(); i++) { 
            char c = lowerWord.charAt(i);
            int index = c - 'a';
            if (node.getReferences()[index] == null) {
                node.setReferences(index, new Node());
            }
            node.setPrefixes(node.getPrefixes() + 1);
            node = node.getReferences()[index];
        }
        node.setTerm(new Term(lowerWord, weight));
        node.setPrefixes(node.getPrefixes() + 1);
        node.setWords(1);
    }

    /**
     * Initializes the Trie
     *
     * @param filename the file to read all the autocomplete data from each line
     *                 contains a word and its weight This method will call the
     *                 addWord method
     * @param k the maximum number of suggestions that should be displayed 
     * @return the root of the Trie You might find the readLine() method in
     *         BufferedReader useful in this situation as it will allow you to
     *         read a file one line at a time.
     */
    @Override
    public Node buildTrie(String filename, int k) {
        this.k = k;
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String[] strings = line.trim().split("\t");
                if (strings.length == 2) {
                    long weight = Long.parseLong(strings[0]);
                    addWord(strings[1], weight);
                }
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }
    
    /**
     * @return k the the maximum number of suggestions that should be displayed 
     */
    @Override
    public int numberSuggestions() {
        return k;
    }
    
    /**
     * @param prefix
     * @return the root of the subTrie corresponding to the last character of
     *         the prefix.
     */
    @Override
    public Node getSubTrie(String prefix) {  
        if (prefix != null) {
            for (int i = 0; i < prefix.length(); i++) {
                char c = prefix.charAt(i);
                if (!Character.isLetter(c)) {
                    return null;
                }
            }
        } else {
            return null;
        }
        String lowerPrefix = prefix.toLowerCase();
        Node node = root;
        for (int i = 0; i < lowerPrefix.length(); i++) {
            char c = lowerPrefix.charAt(i);
            int index = c - 'a';
            if (node.getReferences()[index] == null) {
                return null;
            }
            node = node.getReferences()[index];   
        }
        return node;   
    }

    /**
     * @param prefix
     * @return the number of words that start with prefix.
     */
    @Override
    public int countPrefixes(String prefix) {
        Node node = getSubTrie(prefix);
        if (node != null) {
            return node.getPrefixes();
        }
        return 0;
    }

    /**
     * This method should not throw an exception
     * @param prefix
     * @return a List containing all the ITerm objects with query starting with
     *         prefix. Return an empty list if there are no ITerm object starting
     *         with prefix.
     */
    @Override
    public List<ITerm> getSuggestions(String prefix) {
        List<ITerm> suggestions = new ArrayList<>();
        Node node = getSubTrie(prefix);
        depthFirstSearch(node, suggestions);
        Collections.sort(suggestions);
        return suggestions;
    }
    
    /**
     * A recursive helper function for getSuggestions()
     * @param node
     * @param list
     */
    private void depthFirstSearch(Node node, List<ITerm> list) {
        if (node == null) {
            return;
        } else {
            if (node.isLeaf()) {
                Term suggestion = node.getTerm();
                Term toAdd = new Term(suggestion.getTerm(), suggestion.getWeight());
                list.add(toAdd);
            }
        }
        for (int i = 0; i < 26; i++) {
            depthFirstSearch(node.getReferences()[i], list);
        }
        
    }

}
