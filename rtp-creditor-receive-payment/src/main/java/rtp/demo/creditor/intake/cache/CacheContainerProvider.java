package rtp.demo.creditor.intake.cache;

import org.infinispan.commons.api.BasicCacheContainer;
import org.slf4j.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;

import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheContainerProvider {

	public CacheContainerProvider() {
		super();
	}

	private static final Logger LOG = LoggerFactory.getLogger(CacheContainerProvider.class);

	private RemoteCacheManager manager;

	private BasicCacheContainer basicManager;

	public RemoteCacheManager getCacheContainer() {
		if (manager == null) {
//			ConfigurationBuilder builder = new ConfigurationBuilder();
//			builder.addServer().host("172.30.126.95").port(Integer.parseInt("11333"));
//
//			builder.security().authentication().enable().serverName("jdg-server").saslMechanism("DIGEST-MD5")
//					.callbackHandler(new TestCallbackHandler("jdguser", "ApplicationRealm", "P@ssword1".toCharArray()));
//			

//			try {
//				ConfigurationBuilder clientBuilder = new ConfigurationBuilder();
//				clientBuilder.addServer().host("172.30.28.126").port(11333);
//				clientBuilder.security().authentication().enable().serverName("jdg-server").saslMechanism("DIGEST-MD5")
//						.callbackHandler(
//								new TestCallbackHandler("jdguser", "ApplicationRealm", "P@ssword1".toCharArray()));
//				clientBuilder.forceReturnValues(true);
//				manager = new RemoteCacheManager(clientBuilder.build());
//			} catch (Exception e) {
//				LOG.info("Exception");
//			}

			LOG.info("=== Using RemoteCacheManager (Hot Rod) ===");
		}
		return manager;
	}

	public BasicCacheContainer getBasicCacheContainer() {
		if (manager == null) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.addServer().host("rtp-demo-cache").port(11222);

			builder.security().authentication().enable().serverName("jdg-server").saslMechanism("DIGEST-MD5")
					.callbackHandler(new TestCallbackHandler("jdguser", "ApplicationRealm", "P@ssword1".toCharArray()));

			manager = new RemoteCacheManager(builder.build());
			LOG.info("=== Using RemoteCacheManager (Hot Rod) ===");
		}
		return manager;
	}

	public static class TestCallbackHandler implements CallbackHandler {
		final private String username;
		final private char[] password;
		final private String realm;

		public TestCallbackHandler(String username, String realm, char[] password) {
			this.username = username;
			this.password = password;
			this.realm = realm;
		}

		@Override
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (Callback callback : callbacks) {
				if (callback instanceof NameCallback) {
					NameCallback nameCallback = (NameCallback) callback;
					nameCallback.setName(username);
				} else if (callback instanceof PasswordCallback) {
					PasswordCallback passwordCallback = (PasswordCallback) callback;
					passwordCallback.setPassword(password);
				} else if (callback instanceof AuthorizeCallback) {
					AuthorizeCallback authorizeCallback = (AuthorizeCallback) callback;
					authorizeCallback.setAuthorized(
							authorizeCallback.getAuthenticationID().equals(authorizeCallback.getAuthorizationID()));
				} else if (callback instanceof RealmCallback) {
					RealmCallback realmCallback = (RealmCallback) callback;
					realmCallback.setText(realm);
				} else {
					throw new UnsupportedCallbackException(callback);
				}
			}
		}
	}

}
