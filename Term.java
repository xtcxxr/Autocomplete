
public class Term implements ITerm {
    
    private String query;
    private long weight;
    
    /**
     * Create a new Term object.
     * @param query
     * @param weight
     */
    public Term(String query, long weight) {
        if (query == null || weight < 0) {
            throw new IllegalArgumentException();
        } else {
            this.query = query;
            this.weight = weight;
        }
        
    }

    /**
     * @return the query
     */
    public String getTerm() {
        return query;
    }

    /**
     * Changes the query
     * 
     * @param term the new query
     */
    public void setTerm(String term) {
        this.query = term;
    }
    
    /**
     * @return the weight
     */
    public long getWeight() {
        return weight;
    }

    /**
     * Changes the weight
     * 
     * @param term the new weight
     */
    public void setWeight(long weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(ITerm that) {
        return this.query.compareTo(((Term)that).query);
    }
    
    @Override
    public String toString() {
        return weight + "\t" + query;
        
    }



}
