package org.aksw.gerbil.transfer.nif.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.TypedSpan;

public class TypedSpanImpl extends SpanImpl implements TypedSpan {

    protected Set<String> types;

    public TypedSpanImpl(int startPosition, int length, Set<String> types) {
        super(startPosition, length);
        this.types = types;
    }

    public TypedSpanImpl(int startPosition, int length, String... types) {
        super(startPosition, length);
        this.types = new HashSet<String>(Arrays.asList(types));
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
        builder.append(", a ");
        builder.append(Arrays.toString(types.toArray()));
        builder.append(')');
        return builder.toString();
    }
}
