package grc.upv.es.andropi.server;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class RootServerResource extends ServerResource{

	public RootServerResource() {
		setNegotiated(false);
		setExisting(false);
	}

	@Override
	protected void doInit()throws ResourceException {
		System.out.println("The root resource was initialized.");
	}

	@Override
	protected void doCatch(Throwable throwable) {
		System.out.println("An exception was thrown in the root resource.");
	}

	@Override
	protected void doRelease()throws ResourceException {
		System.out.println("The root resource was released.\n");
	}

	@Override
	protected Representation get()throws ResourceException {
		System.out.println("The GET method of root resource was invoked.");
		return new StringRepresentation("This is the root resource");
	}

	@Override
	protected Representation options()throws ResourceException {
		System.out.println("The OPTIONS method of root resource was	invoked.");
		throw new RuntimeException("Not yet implemented");
	}
}
