package grc.upv.es.andropi.server;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import grc.upv.es.andropi.common.*;

public class AccountServerResource extends ServerResource implements AccountResource {

	private int accountId;

	@Override
	protected void doInit()throws ResourceException {
		this.accountId = Integer.parseInt(getAttribute("accountId"));
	}

	@Override
	@Get("txt")
	public String represent() {
		return AccountsServerResource.getAccounts().get(this.accountId);
	}

	@Override
	@Post("txt")
	public void store(String account) {
		AccountsServerResource.getAccounts().set(
				this.accountId, account);
	}

	@Override
	@Delete
	public void remove() {
		AccountsServerResource.getAccounts().remove(this.accountId);		
	}
}
