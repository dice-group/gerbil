package org.aksw.gerbil.dataset.impl.msnbc;

import java.util.Arrays;
import java.util.HashSet;

import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public class MSNBC_NamedEntity extends NamedEntity {

    private static final int NOT_SET_SENTINEL = -1;

    protected String surfaceForm = null;

    public MSNBC_NamedEntity() {
        super(NOT_SET_SENTINEL, NOT_SET_SENTINEL, new HashSet<String>());
    }

    public String getSurfaceForm() {
        return surfaceForm;
    }

    public void setSurfaceForm(String surfaceForm) {
        this.surfaceForm = surfaceForm;
    }

    public boolean isComplete() {
        return (this.startPosition != NOT_SET_SENTINEL) && (this.length != NOT_SET_SENTINEL) && (!this.uris.isEmpty())
                && (this.surfaceForm != null);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        builder.append(startPosition);
        builder.append(", ");
        builder.append(length);
        builder.append(", \"");
        builder.append(surfaceForm);
        builder.append("\", ");
        builder.append(Arrays.toString(uris.toArray()));
        builder.append(')');
        return builder.toString();
    }

    public NamedEntity toNamedEntity() {
        return new NamedEntity(startPosition, length, uris);
    }
}
