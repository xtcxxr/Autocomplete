import static org.junit.Assert.*;

import org.junit.Test;

public class NodeTest {

    @Test
    public void test() {
        Node root = new Node();
        assertNull(root.getTerm());
        
        Node node1 = new Node("phone", 100);
        Node node2 = new Node("computer", 100);
        node1.setWords(1);
        node1.setPrefixes(1);
        node1.setReferences(0, node2);
        assertEquals("phone", node1.getTerm().getTerm());
        assertEquals(100, node1.getTerm().getWeight());
        assertEquals(1, node1.getPrefixes());
        assertEquals(1, node1.getWords());
        assertNotNull(node1.getReferences()[0]);
        assertTrue(node1.isLeaf());
        assertEquals("100\tphone", node1.toString());
    }

}
