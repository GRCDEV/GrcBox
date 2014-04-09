package grc.upv.es.andropi.common;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface AccountsResource {
	@Get ("txt")
	public String represent();

	@Post ("txt")
	public String add(String account);
}
