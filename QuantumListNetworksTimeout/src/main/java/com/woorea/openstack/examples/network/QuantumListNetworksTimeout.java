package com.woorea.openstack.examples.network;

import com.woorea.openstack.connector.RESTEasyConnector;
import com.woorea.openstack.keystone.model.Authentication;

import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.quantum.Quantum;
import com.woorea.openstack.quantum.model.Network;
import com.woorea.openstack.quantum.model.Networks;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;


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


public class QuantumListNetworksTimeout {

	static private class CustomizedRESTEasyConnector extends RESTEasyConnector {

		private static final int tenSeconds = 10000;
		private static final int connectionTimeout = tenSeconds;
		private static final int socketTimeout = tenSeconds;

		@Override
		protected ClientExecutor createClientExecutor() {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
			HttpConnectionParams.setSoTimeout(params, socketTimeout);
			return new ApacheHttpClient4Executor(httpClient);
		}
	}

	private static final String KEYSTONE_AUTH_URL = "https://identity.api.rackspacecloud.com/v2.0";
	private static final String KEYSTONE_USERNAME = "dominikholler";
	private static final String KEYSTONE_PASSWORD = "58eecc5790f14c3d87ac302143dbb337";
	private static final String ENDPOINT_URL = "https://lon.networks.api.rackspacecloud.com/v2.0";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Keystone keystone = new Keystone(KEYSTONE_AUTH_URL);

		Access access = keystone.tokens().authenticate(
				new ApiKey(KEYSTONE_USERNAME, KEYSTONE_PASSWORD))
				.execute();

		Quantum quantum = new Quantum(ENDPOINT_URL, new CustomizedRESTEasyConnector());
		quantum.setTokenProvider(new OpenStackSimpleTokenProvider(access.getToken().getId()));

		Networks networks = quantum.networks().list().execute();
		for (Network network : networks) {
			System.out.println(network);
			System.out.println(network.getMtu());
		}
	}
}
