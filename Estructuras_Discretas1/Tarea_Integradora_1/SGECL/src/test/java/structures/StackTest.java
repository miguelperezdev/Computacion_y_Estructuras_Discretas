package structures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StackTest {

    @Test
    public void testEmptyStack() {
        Stack<Integer> stack = new Stack<>();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.getSize());
        assertNull(stack.peek());
        assertNull(stack.top());
        assertNull(stack.pop());
    }

    @Test
    public void testPushAndPeek() {
        Stack<String> stack = new Stack<>();
        stack.push("first");

        assertFalse(stack.isEmpty());
        assertEquals(1, stack.getSize());
        assertEquals("first", stack.peek());
        assertEquals("first", stack.top());

        stack.push("second");
        assertEquals("second", stack.peek());
        assertEquals(2, stack.getSize());
    }

    @Test
    public void testPop() {
        Stack<Integer> stack = new Stack<>();
        stack.push(10);
        stack.push(20);
        stack.push(30);

        assertEquals(30, stack.pop());
        assertEquals(2, stack.getSize());

        assertEquals(20, stack.pop());
        assertEquals(1, stack.getSize());

        assertEquals(10, stack.pop());
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.getSize());
    }

    @Test
    public void testPopEmptyStack() {
        Stack<String> stack = new Stack<>();
        assertNull(stack.pop());
    }

    @Test
    public void testPeekEmptyStack() {
        Stack<Double> stack = new Stack<>();
        assertNull(stack.peek());
    }

    @Test
    public void testTopEmptyStack() {
        Stack<Character> stack = new Stack<>();
        assertNull(stack.top());
    }

    @Test
    public void testMultipleOperations() {
        Stack<String> stack = new Stack<>();
        stack.push("a");
        stack.push("b");
        stack.pop();
        stack.push("c");
        stack.push("d");

        assertEquals(3, stack.getSize());
        assertEquals("d", stack.pop());
        assertEquals("c", stack.pop());
        assertEquals("a", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    public void testSetTop() {
        Stack<Integer> stack = new Stack<>();
        Node<Integer> newNode = new Node<>(42);

        stack.setTop(newNode);
        stack.setSize(1);

        assertEquals(42, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    public void testStackSizeAfterOperations() {
        Stack<String> stack = new Stack<>();
        assertEquals(0, stack.getSize());

        stack.push("one");
        assertEquals(1, stack.getSize());

        stack.push("two");
        assertEquals(2, stack.getSize());

        stack.pop();
        assertEquals(1, stack.getSize());

        stack.pop();
        assertEquals(0, stack.getSize());
    }

    @Test
    public void testPeekDoesNotRemove() {
        Stack<Integer> stack = new Stack<>();
        stack.push(100);

        assertEquals(100, stack.peek());
        assertEquals(1, stack.getSize());
        assertEquals(100, stack.top());
        assertEquals(1, stack.getSize());
    }

    @Test
    public void testTopEqualsPeek() {
        Stack<String> stack = new Stack<>();
        stack.push("test");

        assertEquals(stack.peek(), stack.top());
        stack.pop();
        assertNull(stack.peek());
        assertNull(stack.top());
    }

    @Test
    public void testPushNull() {
        Stack<Object> stack = new Stack<>();
        stack.push(null);

        assertFalse(stack.isEmpty());
        assertEquals(1, stack.getSize());
        assertNull(stack.peek());
        assertNull(stack.pop());
    }
}