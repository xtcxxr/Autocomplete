import java.util.Comparator;

/**
 * @author ericfouh
 */
public interface ITerm
    extends Comparable<ITerm> {

    /**
     * Compares the two terms in descending order by weight.
     * 
     * @return comparator Object
     */
    public static Comparator<ITerm> byReverseWeightOrder() {
        return new Comparator<ITerm>() {

            public int compare(ITerm o1, ITerm o2) {
                Term t1 = (Term) o1;
                Term t2 = (Term) o2;
                return (int)(t2.getWeight() - t1.getWeight());
            } 
        };

    }
        
    /**
     * Compares the two terms in lexicographic order but using only the first r
     * characters of each query.
     * 
     * @param r
     * @return comparator Object
     */
    public static Comparator<ITerm> byPrefixOrder(int r) {
        if (r < 0) {
            throw new IllegalArgumentException();
        } else {
            return new Comparator<ITerm>() {
                public int compare(ITerm o1, ITerm o2) {
                    Term t1 = (Term) o1;
                    Term t2 = (Term) o2;
                    
                    int r1 = t1.getTerm().length();
                    int r2 = t2.getTerm().length();
                    
                    if (r1 > r) {
                        r1 = r;
                    }
                    if (r2 > r) {
                        r2 = r;
                    }
                    
                    String t1Prefix = t1.getTerm().substring(0,r1);
                    String t2Prefix = t2.getTerm().substring(0,r2);
                    return t1Prefix.compareTo(t2Prefix);
                    
                }

            };
        }

    }

    // Compares the two terms in lexicographic order by query.
    public int compareTo(ITerm that);


    // Returns a string representation of this term in the following format:
    // the weight, followed by a tab, followed by the query.
    public String toString();

}
