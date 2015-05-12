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
        builder.append(uri);
        builder.append(", a ");
        builder.append(Arrays.toString(types.toArray()));
        builder.append(')');
        return builder.toString();
    }

}
