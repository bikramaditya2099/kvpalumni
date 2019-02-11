package com.bikram.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.bikram.dao.beans.RoleBean;
import com.bikram.dao.beans.UserBean;
import com.bikram.utility.HibernateUtil;

public class DAOImpl implements DAO{

	@Override
	public void insertRole(RoleBean bean) {
		
		Session session=HibernateUtil.getNewSession();
		Transaction transaction=session.beginTransaction();
		session.save(bean);
		transaction.commit();
		session.close();
		
	}

	@Override
	public void createUser(UserBean bean) {
		Session session=HibernateUtil.getNewSession();
		Transaction transaction=session.beginTransaction();
		session.save(bean);
		transaction.commit();
		session.close();
		
	}

	@Override
	public RoleBean getRoleById(int id) {
		Session session=HibernateUtil.getNewSession();
		RoleBean bean=(RoleBean) session.get(RoleBean.class,id);
		return bean;
	}

}
