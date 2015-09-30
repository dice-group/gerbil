/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.transfer.nif.data;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;

public class DocumentImpl implements Document {

    protected String uri;
    protected String text;
    protected List<Marking> markings;

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
