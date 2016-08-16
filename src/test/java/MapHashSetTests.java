import com.margo.set.mapset.HashSet;

import java.util.Collection;
import java.util.Set;

public class MapHashSetTests extends SetTests{

    public Set<String> buildSet() {
        return new HashSet<String>();
    }

    public Set<String> buildSet(Collection<String> collection) {
        return new HashSet<String>(collection);
    }
}
