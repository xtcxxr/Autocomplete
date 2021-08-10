import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TermTest {

    @Test
    public void test1() {
        Term t1 = new Term("phone", 12);
        Term t2 = new Term("computer", 50);
        
        t1.setTerm("iPhone");
        t2.setWeight(100);
        
        assertEquals("iPhone", t1.getTerm());
        assertEquals(12, t1.getWeight());
        assertEquals("12\tiPhone", t1.toString());
        
        assertEquals("computer", t2.getTerm());
        assertEquals(100, t2.getWeight());
        assertEquals("100\tcomputer", t2.toString());
        
    }
    
    @Test
    public void test2() {
        Term t1 = new Term("phone", 120);
        Term t2 = new Term("computer", 50);
        
        List<Term> list = new ArrayList<>();
        list.add(t1);
        list.add(t2);
        Collections.sort(list, ITerm.byPrefixOrder(1));
        assertEquals("computer", list.get(0).getTerm());
        Collections.sort(list, ITerm.byReverseWeightOrder());
        assertEquals("phone", list.get(0).getTerm());
        
    }

}
