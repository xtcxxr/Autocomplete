

/**
 * ==== Attributes ====
 * - words: number of words
 * - term: the ITerm object
 * - prefixes: number of prefixes 
 * - references: Array of references to next/children Nodes
 * 
 * ==== Constructor ====
 * Node(String word, long weight)
 * 
 * @author Xiran Xu
 */
public class Node {
    private Term term;
    private int words;
    private int prefixes;
    private Node[] references;
    
    /**
     * Create a new Node object.
     */
    public Node() {
        this.term = null;
        this.words = 0;
        this.prefixes = 0;
        this.references = new Node[26];
    }
    
    /**
     * Create a new Node object.
     * @param query
     * @param weight
     */
    public Node(String query, long weight) {
        if (query == null || weight < 0) {
            throw new IllegalArgumentException();
        } else {
            this.term = new Term(query, weight);
            this.words = 0;
            this.prefixes = 0;
            this.references = new Node[26];
        }
        
    }
    
    /**
     * @return the prefixes
     */
    public int getPrefixes() {
        return prefixes;
    }
    
    /**
     * @param prefixes the prefixes to set
     */
    public void setPrefixes(int prefixes) {
        this.prefixes = prefixes;
    }
    
    public boolean isLeaf() {
        return words == 1;
    }
    
    /**
     * @return the term
     */
    public Term getTerm() {
        return term;
    }
    
    /**
     * @param term the term to set
     */
    public void setTerm(Term term) {
        this.term = term;
    }
    
    /**
     * @return the words
     */
    public int getWords() {
        return words;
    }
    
    /**
     * @param words the words to set
     */
    public void setWords(int words) {
        this.words = words;
    }
    
    /**
     * @return the references
     */
    public Node[] getReferences() {
        return references.clone();
    }
    
    /**
     * @param references the references to set
     */
    public void setReferences(int index, Node node) {
        references[index] = node;
    }
    
    @Override
    public String toString() {
        if (term != null) {
            return term.toString();
        }
        return null;
    }

}
