package org.aksw.gerbil.semantic.subclass;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.SetUtils;

/**
 * A node inside a {@link ClassSet} containing a {@link Set} of URIs
 * representing the class inside the hierarchy.
 * 
 * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
 * 
 */
public class SimpleClassNode implements ClassNode {

    // private List<SimpleClassNode> children;
    protected Set<String> uris;

    public SimpleClassNode(String uri) {
        // children = new ArrayList<SimpleClassNode>(3);
        uris = new HashSet<String>(3);
        uris.add(uri);
    }

    public SimpleClassNode(Set<String> uris/* , List<SimpleClassNode> children */) {
        // this.children = children;
        this.uris = uris;
    }

    // public List<SimpleClassNode> getChildren() {
    // return children;
    // }
    //
    // public void setChildren(List<SimpleClassNode> children) {
    // this.children = children;
    // }
    //
    // public void addChild(SimpleClassNode child) {
    // this.children.add(child);
    // }

    public Set<String> getUris() {
        return uris;
    }

    public void setUris(Set<String> uris) {
        this.uris = uris;
    }

    public void addUri(String uri) {
        this.uris.add(uri);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uris == null) ? 0 : uris.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SimpleClassNode))
            return false;
        SimpleClassNode other = (SimpleClassNode) obj;
        if (uris == null) {
            if (other.uris != null)
                return false;
        } else if (!SetUtils.isEqualSet(uris, other.uris))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(uris=" + Arrays.toString(uris.toArray(new String[uris.size()])) + ")";
    }
}
