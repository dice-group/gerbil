package org.aksw.gerbil.http;

import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

public class SimpleRedirectStrategy extends LaxRedirectStrategy {

    @Override
    public boolean isRedirected(final HttpRequest request, final HttpResponse response, final HttpContext context)
            throws ProtocolException {
        Args.notNull(request, "HTTP request");
        Args.notNull(response, "HTTP response");

        final int statusCode = response.getStatusLine().getStatusCode();
        final String method = request.getRequestLine().getMethod();
        final Header locationHeader = response.getFirstHeader("location");
        switch (statusCode) {
        case HttpStatus.SC_MOVED_TEMPORARILY:
            return isRedirectable(method) && locationHeader != null;
        case HttpStatus.SC_MOVED_PERMANENTLY:
        case HttpStatus.SC_TEMPORARY_REDIRECT:
        case 308: // Ensure that HTTP 308 is redirected as well
            return isRedirectable(method);
        case HttpStatus.SC_SEE_OTHER:
            return true;
        default:
            return false;
        } // end of switch
    }

    @Override
    public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response, final HttpContext context)
            throws ProtocolException {
        final URI uri = getLocationURI(request, response, context);
        return RequestBuilder.copy(request).setUri(uri).build();
    }

}
