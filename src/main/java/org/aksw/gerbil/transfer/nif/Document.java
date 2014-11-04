package org.aksw.gerbil.transfer.nif;

import java.util.List;

public interface Document {

    // FIXME add language

    public String getDocumentURI();

    public void setDocumentURI(String uri);

    public String getText();

    public void setText(String text);

    public List<Marking> getMarkings();

    public void setMarkings(List<Marking> markings);

    public void addMarking(Marking marking);

    public <T extends Marking> List<T> getMarkings(Class<T> clazz);
}
