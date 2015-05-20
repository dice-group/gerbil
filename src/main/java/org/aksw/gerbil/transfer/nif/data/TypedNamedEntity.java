package org.aksw.gerbil.transfer.nif.data;

import java.util.Arrays;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.TypedSpan;

public class TypedNamedEntity extends NamedEntity implements TypedSpan {

    protected Set<String> types;

    public TypedNamedEntity(int startPosition, int length, String uri, Set<String> types) {
        super(startPosition, length, uri);
        this.types = types;
    }

    public TypedNamedEntity(int startPosition, int length, Set<String> uris, Set<String> types) {
        super(startPosition, length, uris);
        this.types = types;
    }

    public Set<String> getTypes() {
        return types;
    }

    public void setTypes(Set<String> types) {
        this.types = types;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        builder.append(startPosition);
        builder.append(", ");
        builder.append(length);
        builder.append(", ");
        builder.append(Arrays.toString(uris.toArray()));
        builder.append(", a ");
        builder.append(Arrays.toString(types.toArray()));
        builder.append(')');
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((types == null) ? 0 : types.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TypedNamedEntity other = (TypedNamedEntity) obj;
        if (types == null) {
            if (other.types != null)
                return false;
        } else if (!types.equals(other.types))
            return false;
        return true;
    }

}
