package org.aksw.gerbil.annotator.impl.sw;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;

public class RDFServiceBasedMock implements Container {

	@Override
	public void handle(Request req, Response resp) {
		Model recv = ModelFactory.createDefaultModel();
		Model ret = ModelFactory.createDefaultModel();
		try {
			RDFDataMgr.read(recv,req.getInputStream(), Lang.TTL);
			for(Statement stmt : recv.listStatements().toList()) {
				ret.addLiteral(stmt.getSubject(), stmt.getPredicate(), 1);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringWriter writer = new StringWriter();
		RDFDataMgr.write(writer, ret, Lang.TTL);
		OutputStream out = null;
        try {
            byte data[] = writer.toString().getBytes("UTF-8");
            resp.setCode(Status.OK.code);
            resp.setValue("Content-Type", "text/turtle;charset=utf-8");
            resp.setContentLength(data.length);
            out = resp.getOutputStream();

            out.write(data);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
}
	}

}
