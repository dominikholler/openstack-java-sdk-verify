package com.woorea.openstack.examples.network;

import com.woorea.openstack.base.client.OpenStackResponse;
import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.keystone.v3.Keystone;
import com.woorea.openstack.keystone.v3.model.Authentication;
import com.woorea.openstack.keystone.v3.model.Authentication.Identity;
import com.woorea.openstack.keystone.v3.model.Token;
import com.woorea.openstack.quantum.Quantum;
import com.woorea.openstack.quantum.model.Network;
import com.woorea.openstack.quantum.model.Networks;

import java.util.List;
import java.util.Objects;


public class VerifyMtu {

	private static final String OS_USERNAME = "admin";
	private static final String OS_PASSWORD = "ZoSie2tI";
	private static final String OS_AUTH_URL = "http://142.93.103.226:5000/v3";

	private static final String OS_PROJECT_NAME = "admin";
	private static final String OS_USER_DOMAIN_NAME = "Default";
	private static final String OS_PROJECT_DOMAIN_NAME = "Default";

	private static final String NETWORK_SERVICE_TYPE = "network";
	private static final String NETWORK_SERVICE_REGION = null;
	private static final String NETWORK_SERVICE_INTERFACE = "public";
	private static final String NETWORK_SERVICE_VERSION = "/v2.0";


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Keystone keystone = new Keystone(OS_AUTH_URL);

		Authentication auth = new Authentication();
		auth.setIdentity(Identity.password(OS_USER_DOMAIN_NAME, OS_USERNAME, OS_PASSWORD));
		auth.setScope(Authentication.Scope.project(OS_PROJECT_DOMAIN_NAME, OS_PROJECT_NAME));

		OpenStackResponse response = keystone.tokens().authenticate(auth).request();

		String tokenId = response.header("X-Subject-Token");
		System.out.println("token: " + tokenId);


		Token token = response.getEntity(Token.class);
		String endpointUrl = findEndpointURL(token.getCatalog(), NETWORK_SERVICE_TYPE, NETWORK_SERVICE_REGION,
				NETWORK_SERVICE_INTERFACE) +  NETWORK_SERVICE_VERSION;
		Quantum quantum = new Quantum(endpointUrl);
		quantum.setTokenProvider(new OpenStackSimpleTokenProvider(tokenId));


		Networks networks = quantum.networks().list().execute();
		for (Network network : networks) {
			System.out.println(network);
			System.out.println(network.getMtu());
		}

		createNetwork(quantum);
		createNetwork2(quantum);
	}

	public static String findEndpointURL(List<Token.Service> serviceCatalog, String type, String region, String facing) {
		for(Token.Service service : serviceCatalog) {
			if(type.equals(service.getType())) {
				for(Token.Service.Endpoint endpoint : service.getEndpoints()) {
					if(region == null || region.equals(endpoint.getRegion())) {
						if(facing.equals(endpoint.getInterface())) {
							return endpoint.getUrl();
						}
					}
				}
			}
		}
		throw new RuntimeException("endpoint url not found");
	}

	private static void createNetwork(Quantum quantum) {
		Network network = new Network();
		network.setName("MtuTestNet10");
		createAndDestroy(quantum, network);
	}

	private static void createNetwork2(Quantum quantum) {
		Network network = new Network();
		network.setName("MtuTestNet20");
		network.setMtu(1234);
		network.setPortSecurityEnabled(false);
		createAndDestroy(quantum, network);
	}

	private static boolean equals(Object request, Object result) {
		return request == null || Objects.equals(request, result);
	}

	private static boolean validate(Network request, Network result) {
		return equals(request.getName(), result.getName()) &&
				equals(request.getMtu(), result.getMtu()) &&
				equals(request.getPortSecurityEnabled(), result.getPortSecurityEnabled());
	}

	private static void createAndDestroy(Quantum quantum, Network network) {
		Network result = quantum.networks().create(network).execute();
		System.out.println(String.format("%s: %s",
				validate(network, result),
				result));
		System.out.println();
		quantum.networks().delete(result.getId()).execute();
	}
}
