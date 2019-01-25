package org.example.repository.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static final String DATABASE_URL = System.getenv("DATABASE_URL");
	private static final String DATABASE_USER = System.getenv("DATABASE_USER");
	private static final String DATABASE_PASS = System.getenv("DATABASE_PASS");

	public static Session getHibernateSession() {

		Configuration config = new Configuration();
		config.configure("hibernate.cfg.xml");

		if (DATABASE_URL != null) {
			config.setProperty("hibernate.connection.url", DATABASE_URL);
		}
		if (DATABASE_USER != null) {
			config.setProperty("hibernate.connection.username", DATABASE_USER);
		}
		if (DATABASE_PASS != null) {
			config.setProperty("hibernate.connection.password", DATABASE_PASS);
		}

		final SessionFactory sf = config.buildSessionFactory();
		final Session session = sf.openSession();
		return session;
	}

}