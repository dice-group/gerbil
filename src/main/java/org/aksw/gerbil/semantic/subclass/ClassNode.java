package org.aksw.gerbil.semantic.subclass;

import java.util.Set;

/**
 * A node inside a {@link ClassSet} containing a {@link Set} of URIs
 * representing the class inside the hierarchy.
 * 
 * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
 * 
 */
public interface ClassNode {

    // private List<ClassNode> parents;
    // private List<ClassNode> children;
    // private Set<String> uris;
    //
    // public ClassNode(String uri) {
    // // parents = new ArrayList<ClassNode>(3);
    // // children = new ArrayList<ClassNode>(3);
    // uris = new HashSet<String>(3);
    // uris.add(uri);
    // }
    //
    // public ClassNode(Set<String> uris, List<ClassNode> children) {
    // // parents = new ArrayList<ClassNode>(3);
    // // this.children = children;
    // this.uris = uris;
    // }

    public Set<String> getUris();

    public void setUris(Set<String> uris);

    public void addUri(String uri);

    // public List<ClassNode> getParents() {
    // return parents;
    // }
    //
    // public void setParents(List<ClassNode> parents) {
    // this.parents = parents;
    // }
    //
    // public void addParent(ClassNode parent) {
    // this.parents.add(parent);
    // }
    //
    // public void removeParent(ClassNode parent) {
    // this.parents.remove(parent);
    // }
    //
    // public List<ClassNode> getChildren() {
    // return children;
    // }
    //
    // public void setChildren(List<ClassNode> children) {
    // this.children = children;
    // }
    //
    // public void addChild(ClassNode child) {
    // this.children.add(child);
    // }
    //
    // public void removeChild(ClassNode child) {
    // this.children.remove(child);
    // }
    //
    // public Set<String> getUris() {
    // return uris;
    // }
    //
    // public void setUris(Set<String> uris) {
    // this.uris = uris;
    // }
    //
    // public void addUri(String uri) {
    // this.uris.add(uri);
    // }

    // @Override
    // public int hashCode() {
    // final int prime = 31;
    // int result = 1;
    // result = prime * result + ((uris == null) ? 0 : uris.hashCode());
    // return result;
    // }

    // @Override
    // public boolean equals(Object obj) {
    // if (this == obj)
    // return true;
    // if (obj == null)
    // return false;
    // if (getClass() != obj.getClass())
    // return false;
    // ClassNode other = (ClassNode) obj;
    // if (uris == null) {
    // if (other.uris != null)
    // return false;
    // } else if (!uris.equals(other.uris))
    // return false;
    // return true;
    // }
}
