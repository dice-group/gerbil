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
package org.aksw.gerbil.annotator.http;

import java.io.IOException;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.http.AbstractHttpRequestEmitter;
import org.aksw.gerbil.utils.ClosePermitionGranter;
import org.apache.http.impl.client.CloseableHttpClient;

public abstract class AbstractHttpBasedAnnotator extends AbstractHttpRequestEmitter implements Annotator {

    protected ClosePermitionGranter granter;

    public AbstractHttpBasedAnnotator() {
        super();
    }

    public AbstractHttpBasedAnnotator(CloseableHttpClient client) {
        super(client);
    }

    public AbstractHttpBasedAnnotator(String name) {
        super(name);
    }

    public AbstractHttpBasedAnnotator(String name, CloseableHttpClient client) {
        super(name, client);
    }

    @Override
    public void setClosePermitionGranter(ClosePermitionGranter granter) {
        this.granter = granter;
    }

    @Override
    public final void close() throws IOException {
        if ((granter == null) || (granter.givePermissionToClose())) {
            performClose();
        }
    }

    protected void performClose() throws IOException {
        super.close();
    }
}
