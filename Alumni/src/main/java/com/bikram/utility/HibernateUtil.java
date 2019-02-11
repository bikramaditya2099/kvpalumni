package com.bikram.utility;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

private static SessionFactory factory;
	
	static{
		initialize();
	}

	private static void initialize() {
		factory=new Configuration().configure().buildSessionFactory();
	}
	
	public static Session getNewSession()
	{
		if(factory==null)
			initialize();
		return factory.openSession();
	}
}
