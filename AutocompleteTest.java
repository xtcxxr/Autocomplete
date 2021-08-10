import static org.junit.Assert.*;

import org.junit.Test;

public class AutocompleteTest {

    @Test
    public void test1() {
        Autocomplete ac = new Autocomplete();
        ac.buildTrie("/autograder/submission/pokemon.txt", 7);
//        ac.buildTrie("src/pokemon.txt", 7);
        assertEquals(7, ac.numberSuggestions());
        assertEquals(3, ac.countPrefixes("az"));
        assertEquals(5, ac.countPrefixes("kr"));
        assertEquals(2, ac.getSuggestions("kri").size());
        assertEquals(3, ac.getSuggestions("ab").size());
    }
    
    @Test
    public void test2() {
        Autocomplete ac = new Autocomplete();
        ac.buildTrie("/autograder/submission/wiktionary.txt", 7);
//        ac.buildTrie("src/wiktionary.txt", 7);
        assertEquals(7, ac.numberSuggestions());
        assertEquals(4, ac.countPrefixes("nam"));
        assertEquals(16, ac.countPrefixes("fin"));
        assertEquals(6, ac.getSuggestions("kne").size());
        assertEquals(5, ac.getSuggestions("soo").size());
    }
}
