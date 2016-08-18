import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public abstract class SetTests {

    public abstract Set<String> buildSet();
    public abstract Set<String> buildSet(Collection<String> collection);


    private static void checkSets(String[] expected, Set<String> actual) {
        Assert.assertEquals(expected.length, actual.size());

        for (int i = 0; i < expected.length; i++) {
            Assert.assertTrue(actual.contains(expected[i]));
        }
    }

    @Test
    public void shouldCreateEmptySet() {
        Set<String> set = buildSet();

        Assert.assertEquals(0, set.size());
    }

    @Test
    public void shouldInitializeSetFromCollection() {
        String[] expected = new String[]{"one", "two"};
        Set<String> actual = buildSet(Arrays.asList("one", "two"));

        checkSets(expected, actual);
    }

    @Test
    public void shouldAddElement() {
        String[] expected = new String[]{"one"};
        Set<String> actual = buildSet();
        actual.add("one");

        checkSets(expected, actual);
    }

    @Test
    public void shouldRemoveElement() {
        String[] expected = new String[]{"one"};
        Set<String> actual = buildSet(Arrays.asList("one", "two"));

        actual.remove("two");

        checkSets(expected, actual);
    }

    @Test
    public void shouldAddAllSpecifiedElements() {
        String[] expected = new String[]{"two", "three"};
        Set<String> actual = buildSet();

        actual.addAll(Arrays.asList("two", "three"));

        checkSets(expected, actual);
    }

    @Test
    public void shouldRemoveAllSpecifiedElements() {
        String[] expected = new String[]{"three"};
        Set<String> actual = buildSet(Arrays.asList("one", "two", "three"));

        actual.removeAll(Arrays.asList("one", "two"));

        checkSets(expected, actual);
    }

    @Test
    public void shouldRetainAllSpecifiedElements() {
        String[] expected = new String[]{"one", "two"};
        Set<String> actual = buildSet(Arrays.asList("one", "two", "three"));

        actual.retainAll(Arrays.asList("one", "two"));

        checkSets(expected, actual);
    }

    @Test
    public void shouldClear() {
        Set<String> set = buildSet(Arrays.asList("one", "two", "three"));
        set.clear();

        Assert.assertTrue(set.size() == 0);
    }

    @Test
    public void shouldReturnIsEmptyTrue() {
        Set<String> set = buildSet();

        Assert.assertTrue(set.isEmpty());

        set.addAll(Arrays.asList("one", "two", "three"));

        Assert.assertFalse(set.isEmpty());

        set.clear();

        Assert.assertTrue(set.isEmpty());
    }

    @Test
    public void shouldTwoEqualSetsBeEqual() {
        Set<String> set1 = buildSet(Arrays.asList("one", "two", "three"));
        Set<String> set2 = buildSet(Arrays.asList("one", "two", "three"));

        Assert.assertTrue(set1.equals(set2));
    }

    @Test
    public void shouldTwoEqualSetsHaveSameHashCode() {
        Set<String> set1 = buildSet(Arrays.asList("one", "two", "three"));
        Set<String> set2 = buildSet(Arrays.asList("one", "two", "three"));

        Assert.assertTrue(set1.hashCode() == set2.hashCode());
    }
}
