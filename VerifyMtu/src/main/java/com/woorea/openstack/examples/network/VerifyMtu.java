package com.woorea.openstack.examples.network;

import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.quantum.Quantum;
import com.woorea.openstack.quantum.model.Network;
import com.woorea.openstack.quantum.model.Networks;

import com.woorea.openstack.keystone.v3.Keystone;
import com.woorea.openstack.keystone.v3.model.Authentication;
import com.woorea.openstack.keystone.v3.model.Authentication.Identity;
import com.woorea.openstack.keystone.v3.model.Token;

import com.woorea.openstack.base.client.OpenStackResponse;

public class VerifyMtu {

	private static final String KEYSTONE_AUTH_URL = "http://192.168.1.62:35357/v3";
//	private static final String KEYSTONE_AUTH_URL = "https://fedora-27-gui:35357/v2.0";
	private static final String KEYSTONE_USERNAME = "demo";
	private static final String KEYSTONE_DOMAIN = "Default";
	private static final String KEYSTONE_PASSWORD = "";
//	private static final String ENDPOINT_URL = "https://fedora-27-gui:9696/v2.0";
	private static final String ENDPOINT_URL = "http://192.168.1.62:9696/v2.0";


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Keystone keystone = new Keystone(KEYSTONE_AUTH_URL);

		Authentication auth = new Authentication();
		auth.setIdentity(Identity.password(KEYSTONE_DOMAIN, KEYSTONE_USERNAME, KEYSTONE_PASSWORD));

		OpenStackResponse response = keystone.tokens().authenticate(auth).request();

		String tokenId = response.header("X-Subject-Token");
		System.out.println("token: " + tokenId);

		Quantum quantum = new Quantum(ENDPOINT_URL);
		quantum.setTokenProvider(new OpenStackSimpleTokenProvider(tokenId));


		Networks networks = quantum.networks().list().execute();
		for (Network network : networks) {
			System.out.println(network);
			System.out.println(network.getMtu());
		}

		createNetwork(quantum);
		createNetwork2(quantum);
	}

	private static void createNetwork(Quantum quantum) {
		Network network = new Network();
		network.setName("MtuTestNet1");
		System.out.println(quantum.networks().create(network).execute());
	}

	private static void createNetwork2(Quantum quantum) {
		Network network = new Network();
		network.setName("MtuTestNet2");
		network.setMtu(1234);
		System.out.println(quantum.networks().create(network).execute());
	}
}
