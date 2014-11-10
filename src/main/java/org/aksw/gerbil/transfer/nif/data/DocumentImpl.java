/**
 * The MIT License (MIT)
 *
 * Copyright (C) ${year} Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.transfer.nif.data;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;

public class DocumentImpl implements Document {

    private String uri;
    private String text;
    private List<Marking> markings;

    public DocumentImpl() {
        markings = new ArrayList<Marking>();
    }

    public DocumentImpl(String text) {
        this.text = text;
        markings = new ArrayList<Marking>();
    }

    public DocumentImpl(String text, String uri) {
        this.text = text;
        this.uri = uri;
        markings = new ArrayList<Marking>();
    }

    public DocumentImpl(String text, List<Marking> markings) {
        this.text = text;
        this.markings = markings;
    }

    public DocumentImpl(String text, String uri, List<Marking> markings) {
        this.text = text;
        this.uri = uri;
        this.markings = markings;
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
    public List<Marking> getMarkings() {
        return markings;
    }

    @Override
    public void setMarkings(List<Marking> markings) {
        this.markings = markings;
    }

    @Override
    public void addMarking(Marking marking) {
        markings.add(marking);
    }

    @Override
    public String getDocumentURI() {
        return uri;
    }

    @Override
    public void setDocumentURI(String uri) {
        this.uri = uri;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Marking> List<T> getMarkings(Class<T> clazz) {
        List<T> markingsWithClass = new ArrayList<T>();
        for (Marking marking : markings) {
            if (clazz.isInstance(marking)) {
                markingsWithClass.add((T) marking);
            }
        }
        return markingsWithClass;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((markings == null) ? 0 : markings.hashCode());
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
        DocumentImpl other = (DocumentImpl) obj;
        if (markings == null) {
            if (other.markings != null)
                return false;
        } else if (!markings.equals(other.markings))
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AnnotatedDocumentImpl [uri=");
        builder.append(uri);
        builder.append(", text=");
        builder.append(text);
        builder.append(", markings=");
        builder.append(markings);
        builder.append("]");
        return builder.toString();
    }

}
