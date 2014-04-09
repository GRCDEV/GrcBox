package grc.upv.es.andropi.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import grc.upv.es.andropi.common.AccountsResource;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class AccountsServerResource extends ServerResource implements AccountsResource {

	private static final List<String> accounts = new CopyOnWriteArrayList<String>();

	public static List<String> getAccounts(){
		return accounts;
	}

	@Override
	@Get("txt")
	public String represent() {
		StringBuilder result = new StringBuilder();
		for (String account : getAccounts()) {
			result.append((account == null) ? "" : account).append('\n');
		}
		return result.toString();
	}

	@Override
	@Post("txt")
	public String add(String account) {
		getAccounts().add(account);
		return Integer.toString(getAccounts().indexOf(
				account));
	}
}
