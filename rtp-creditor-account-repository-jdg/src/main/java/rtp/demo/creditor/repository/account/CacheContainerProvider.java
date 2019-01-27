package rtp.demo.creditor.repository.account;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCacheContainer;

public class CacheContainerProvider {

	private static final String JDG_HOST = "rtp-demo-cache";
	private static final int JDG_PORT = 11222;
	private static final String JDG_SERVER = "jdg-server";
	private static final String SASL_MECH = "DIGEST-MD5";
	private static final String JDG_USER = "jdguser";
	private static final char[] JDG_PASSWORD = "P@ssword1".toCharArray();
	private static final String JDG_REALM = "ApplicationRealm";

	private BasicCacheContainer basicContainer;

	public BasicCacheContainer getBasicCacheContainer() {
		if (basicContainer == null) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.addServer().host(JDG_HOST).port(JDG_PORT);
			builder.security().authentication().serverName(JDG_SERVER).saslMechanism(SASL_MECH)
					.callbackHandler(new DemoCallbackHandler(JDG_USER, JDG_PASSWORD, JDG_REALM)).enable();

			basicContainer = new RemoteCacheManager(builder.build());
		}
		return basicContainer;
	}

	public static class DemoCallbackHandler implements CallbackHandler {
		final private String username;
		final private char[] password;
		final private String realm;

		public DemoCallbackHandler(String username, char[] password, String realm) {
			this.username = username;
			this.password = password;
			this.realm = realm;
		}

		@Override
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (Callback callback : callbacks) {
				if (callback instanceof NameCallback) {
					((NameCallback) callback).setName(username);
				} else if (callback instanceof PasswordCallback) {
					((PasswordCallback) callback).setPassword(password);
				} else if (callback instanceof RealmCallback) {
					((RealmCallback) callback).setText(realm);
				} else {
					throw new UnsupportedCallbackException(callback);
				}
			}

		}

	}

}