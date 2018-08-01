package com.woorea.openstack.examples.network;

import com.woorea.openstack.base.client.HttpMethod;
import com.woorea.openstack.base.client.OpenStackClient;
import com.woorea.openstack.base.client.OpenStackRequest;
import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Authentication;
import com.woorea.openstack.quantum.Quantum;
import com.woorea.openstack.quantum.model.Network;
import com.woorea.openstack.quantum.model.Networks;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;


@JsonRootName(value = "auth")
class ApiKey extends Authentication {
	private ApiKey.ApiKeyCredentials apiKeyCredentials = new ApiKey.ApiKeyCredentials();

	public ApiKey() {
	}

	public ApiKey(String username, String apiKey) {
		this.apiKeyCredentials.setUsername(username);
		this.apiKeyCredentials.setApiKey(apiKey);
	}

	@JsonProperty("RAX-KSKEY:apiKeyCredentials")
	public ApiKey.ApiKeyCredentials getApiKeyCredentials() {
		return this.apiKeyCredentials;
	}

	@JsonProperty("RAX-KSKEY:apiKeyCredentials")
	public void setApiKeyCredentials(ApiKey.ApiKeyCredentials apiKeyCredentials) {
		this.apiKeyCredentials = apiKeyCredentials;
	}

	public static final class ApiKeyCredentials {
		private String username;
		private String apiKey;

		public ApiKeyCredentials() {
		}

		public String getUsername() {
			return this.username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getApiKey() {
			return this.apiKey;
		}

		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
		}
	}
}

class QuantumChecker {

	private static final String KEYSTONE_AUTH_URL = "https://identity.api.rackspacecloud.com/v2.0";
	private static final String KEYSTONE_USERNAME = "dominikholler";
	private static final String KEYSTONE_PASSWORD = "";
	private static final String ENDPOINT_URL = "https://lon.networks.api.rackspacecloud.com/v2.0";

	static void check() {

		Keystone keystone = new Keystone(KEYSTONE_AUTH_URL);
		Access access = keystone.tokens().authenticate(
				new ApiKey(KEYSTONE_USERNAME, KEYSTONE_PASSWORD))
				.execute();

		Quantum quantum = new Quantum(ENDPOINT_URL);
		quantum.setTokenProvider(new OpenStackSimpleTokenProvider(access.getToken().getId()));

		Networks networks = quantum.networks().list().execute();
		for (Network network : networks) {
			System.out.println(network);
		}
	}
}

class RawRequestChecker {

	private static final String ENDPOINT_URL = "http://glance.ovirt.org:9292/v1";

	static void check() {
		OpenStackClient client = new OpenStackClient(ENDPOINT_URL);
		client.execute(new OpenStackRequest<>(client, HttpMethod.GET, "", null, null));
		client.execute(new OpenStackRequest<>(client, HttpMethod.GET, "/", null, null));
	}
}

public class CompatibilityChecker {
	public static void main(String args[]) {
		RawRequestChecker.check();
		System.out.println("RawRequestChecker succeeded.");
		QuantumChecker.check();
		System.out.println("QuantumChecker succeeded.");
	}
}
