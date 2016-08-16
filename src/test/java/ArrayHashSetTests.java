import com.margo.set.arrayset.HashSet;

import java.util.Collection;
import java.util.Set;

public class ArrayHashSetTests extends SetTests {

    public Set<String> buildSet() {
        return new HashSet<String>();
    }

    public Set<String> buildSet(Collection<String> collection) {
        return new HashSet<String>(collection);
    }
}
