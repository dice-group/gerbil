package org.aksw.gerbil.transfer.nif.data;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.AnnotatedDocument;
import org.aksw.gerbil.transfer.nif.Annotation;

public class AnnotatedDocumentImpl implements AnnotatedDocument {

    private String uri;
    private String text;
    private List<Annotation> annotations;

    public AnnotatedDocumentImpl() {
        annotations = new ArrayList<Annotation>();
    }

    public AnnotatedDocumentImpl(String text) {
        this.text = text;
        annotations = new ArrayList<Annotation>();
    }

    public AnnotatedDocumentImpl(String text, String uri) {
        this.text = text;
        this.uri = uri;
        annotations = new ArrayList<Annotation>();
    }

    public AnnotatedDocumentImpl(String text, List<Annotation> annotations) {
        this.text = text;
        this.annotations = annotations;
    }

    public AnnotatedDocumentImpl(String text, String uri, List<Annotation> annotations) {
        this.text = text;
        this.uri = uri;
        this.annotations = annotations;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    @Override
    public String getDocumentURI() {
        return uri;
    }

    @Override
    public void setDocumentURI(String uri) {
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnnotatedDocumentImpl other = (AnnotatedDocumentImpl) obj;
        if (annotations == null) {
            if (other.annotations != null)
                return false;
        } else if (!annotations.equals(other.annotations))
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }

}
