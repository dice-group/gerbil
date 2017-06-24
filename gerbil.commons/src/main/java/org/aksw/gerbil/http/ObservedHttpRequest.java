/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.http;

import org.apache.http.client.methods.HttpUriRequest;

public class ObservedHttpRequest {

    public HttpUriRequest request;
    public HttpRequestEmitter emitter;

    public ObservedHttpRequest(HttpUriRequest request, HttpRequestEmitter emitter) {
        this.request = request;
        this.emitter = emitter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((emitter == null) ? 0 : emitter.hashCode());
        result = prime * result + ((request == null) ? 0 : request.hashCode());
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
        ObservedHttpRequest other = (ObservedHttpRequest) obj;
        if (emitter == null) {
            if (other.emitter != null)
                return false;
        } else if (!emitter.equals(other.emitter))
            return false;
        if (request == null) {
            if (other.request != null)
                return false;
        } else if (!request.equals(other.request))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ObservedHttpRequest [request=");
        builder.append(request);
        builder.append(", emitter=");
        builder.append(emitter);
        builder.append("]");
        return builder.toString();
    }

}
