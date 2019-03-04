package com.bikram.utility;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.bikram.beans.AppConfigManager;
import com.bikram.dao.DAO;
import com.bikram.dao.DAOImpl;
import com.bikram.dao.beans.RoleBean;
import com.bikram.dao.beans.UserBean;
import com.bikram.exception.KvpalException;

@Component
@ConfigurationProperties
public class CreateDefaults {

	@Autowired
	AppConfigManager manager;
	@Autowired
	Encryption encryption;

	public void CreateRoles() {
		Session session = HibernateUtil.getNewSession();
		DAO dao = new DAOImpl();
		String defaultRoles = manager.getRoles();
		String[] rolesAndDescription = defaultRoles.split(",");
		for (String entry : rolesAndDescription) {
			String[] eachEntry = entry.split("\\|");
			System.out.println(eachEntry[0] + "- " + eachEntry[1]);
			Criteria criteria = session.createCriteria(RoleBean.class);
			criteria.add(Restrictions.eq("role", eachEntry[0]));
			if (criteria.list().size() <= 0) {
				RoleBean bean = new RoleBean();
				bean.setRole(eachEntry[0]);
				bean.setDescription(eachEntry[1]);
				dao.insertRole(bean);
			}

		}

		session.close();

	}
	
	public void CreateAdminUser() {
		Session session=HibernateUtil.getNewSession();
		Criteria criteria=session.createCriteria(UserBean.class);
		criteria.add(Restrictions.eq("email", "kvpal@gmail.com"));
		if(criteria.list().size()<=0){
		DAO dao=new DAOImpl();
		RoleBean role=dao.getRoleById(1);
		UserBean bean=new UserBean();
		bean.setAddress("KV Puri");
		bean.setAlumniBadge("test");
		bean.setRole(role);
		bean.setEmail("kvpal@gmail.com");
		bean.setFirstName("Admin");
		bean.setLastName("User");
		bean.setGender("male");
		bean.setImageLocation("test");
		bean.setIsActive(true);
		bean.setMobile("8971271732");
		bean.setPassout_year(2000);
		bean.setPassword(encryption.encrypt("test@123")[0]);
		bean.setRegistration_date(new Date());
		try {
			dao.createUser(bean);
		} catch (KvpalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
}
