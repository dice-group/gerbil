package org.aksw.gerbil.annotator.impl.sw;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.http.HttpManagement;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class RDFWebServiceSystemTest {

	private static final int FAST_SERVER_PORT = 12312;
	private static final String FAST_HTTP_SERVER_ADDRESS = "http://localhost:" + FAST_SERVER_PORT;
	protected RDFServiceBasedMock fastServerContainer;
	protected Server fastServer;
	protected Connection fastConnection;
	private static final long MAX_WAITING_TIME = 2000;
	private static final long CHECK_INERVAL = 1000;

	@BeforeClass
	public static void setHttpConfig() {
		HttpManagement mngmt = HttpManagement.getInstance();
		mngmt.setMaxWaitingTime(MAX_WAITING_TIME);
		mngmt.setCheckInterval(CHECK_INERVAL);
	}

	@Before
	public void startServer() throws IOException {
		fastServerContainer = new RDFServiceBasedMock();
		fastServer = new ContainerServer(fastServerContainer);
		fastConnection = new SocketConnection(fastServer);
		SocketAddress address1 = new InetSocketAddress(FAST_SERVER_PORT);
		fastConnection.connect(address1);

	}

	@Test
	public void test() throws GerbilException {
		RDFWebServiceSystem system = new RDFWebServiceSystem("test", FAST_HTTP_SERVER_ADDRESS);
		Model model = ModelFactory.createDefaultModel();
		model.addLiteral(ResourceFactory.createResource("http://test.com/stmt1"),
				ResourceFactory.createProperty("http://conf.st/p1"), 1);
		model.addLiteral(ResourceFactory.createResource("http://test.com/stmt1"),
				ResourceFactory.createProperty("http://conf.st/p2"), 0);
		model.addLiteral(ResourceFactory.createResource("http://test.com/stmt2"),
				ResourceFactory.createProperty("http://conf.st/p1"), 1);

		Model recv = system.performTask1(model).get(0);
		Model intersect = model.intersection(recv);
		Model diff1 = model.difference(recv);
		Model diff2 = recv.difference(model);

		assertEquals(2, intersect.size());
		assertEquals(1, diff1.size());
		assertEquals(1, diff2.size());

		Statement stmt = diff1.listStatements().next();
		assertEquals("http://test.com/stmt1", stmt.getSubject().toString());
		assertEquals("http://conf.st/p2", stmt.getPredicate().toString());
		assertEquals(0, stmt.getInt());

		stmt = diff2.listStatements().next();
		assertEquals("http://test.com/stmt1", stmt.getSubject().toString());
		assertEquals("http://conf.st/p2", stmt.getPredicate().toString());
		assertEquals(1, stmt.getInt());
	}

	@After
	public void stopServer() throws IOException {
		fastConnection.close();
		fastServer.stop();
	}

}
