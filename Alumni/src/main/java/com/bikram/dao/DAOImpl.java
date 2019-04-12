package com.bikram.dao;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.bikram.beans.LoginBean;
import com.bikram.dao.beans.AttendantBean;
import com.bikram.dao.beans.EventBean;
import com.bikram.dao.beans.RoleBean;
import com.bikram.dao.beans.UserBean;
import com.bikram.exception.ApplicationEnum;
import com.bikram.exception.KvpalException;
import com.bikram.utility.BadgeGenerator;
import com.bikram.utility.EmailBodyGenerator;
import com.bikram.utility.EmailClient;
import com.bikram.utility.EmailEnum;
import com.bikram.utility.Encryption;
import com.bikram.utility.EventIdGenerator;
import com.bikram.utility.HibernateUtil;
import com.bikram.utility.TokenManager;
import com.google.zxing.WriterException;
@Service
public class DAOImpl implements DAO{
	@Autowired
	private Encryption encrypt;
	@Autowired
	private TokenManager manager;
	@Autowired
	private BadgeGenerator generator;
	@Autowired
	private EmailClient emailclient;
	@Autowired
	private EmailBodyGenerator emailBodyGenerator;
	@Autowired
	private EventIdGenerator eventIdGenerator;
	private static final String SUBJECT="no reply";
	@Override
	public void insertRole(RoleBean bean) {
		
		Session session=HibernateUtil.getNewSession();
		Transaction transaction=session.beginTransaction();
		session.save(bean);
		transaction.commit();
		session.close();
		
	}

	@Override
	public void createUser(UserBean bean) throws KvpalException {
		Session session=HibernateUtil.getNewSession();
		Criteria criteria=session.createCriteria(UserBean.class);
		criteria.add(Restrictions.or(Restrictions.eq("email", bean.getEmail()),Restrictions.eq("mobile", bean.getMobile())));
		if(criteria.list().size()<=0){
		Transaction transaction=session.beginTransaction();
		UUID uuid=UUID.randomUUID();
		bean.setUid(uuid.toString());
		session.save(bean);
		transaction.commit();
		session.close();
		if(bean.getRole().getId()!=1)
		emailclient.send(bean.getEmail(),SUBJECT, emailBodyGenerator.generateEmailBody(bean.getFirstName(), ApplicationEnum.REG_SUCCESS.getMessage()+" and "+ApplicationEnum.PENDING_USER.getMessage()));
		throw new KvpalException(ApplicationEnum.REG_SUCCESS);
		}else
			throw new KvpalException(ApplicationEnum.USER_ALREADY_EXISTS);
		
		
	}

	@Override
	public RoleBean getRoleById(int id) {
		Session session=HibernateUtil.getNewSession();
		RoleBean bean=(RoleBean) session.get(RoleBean.class,id);
		return bean;
	}

	@Override
	public String checkLogin(LoginBean bean) throws KvpalException {
		Session session=HibernateUtil.getNewSession();
	
			UserBean userBean=getUserByEmail(bean.getUserName());
			if(bean.getPassword().equals(encrypt.decrypt(userBean.getPassword()))){
			userBean.setSso(manager.generateToken(bean.getUserName(),userBean.getPassword()));
			userBean.setLastLogin(new Date());
			Transaction transaction=session.beginTransaction();
			session.update(userBean);
			transaction.commit();
			throw new KvpalException(ApplicationEnum.LOGIN_SUCCESS,  userBean.getSso());
			
		}
		else{
			throw new KvpalException(ApplicationEnum.LOGIN_FAILED);
		}
		
	}

	@Override
	public UserBean getUserByEmail(String email) throws KvpalException {
		Session session=HibernateUtil.getNewSession();
		Criteria criteria=session.createCriteria(UserBean.class);
		criteria.add(Restrictions.eq("email", email));
		List<UserBean> list=criteria.list();
		if(list.size()>0){
			UserBean userBean=list.get(0);
			return userBean;
		}
		else{
			throw new KvpalException(ApplicationEnum.USER_NOT_EXISTS);
		}
	}

	@Override
	public void updateUser(UserBean bean) {
		Session session=HibernateUtil.getNewSession();
		Transaction transaction=session.beginTransaction();
		session.update(bean);
		transaction.commit();
		session.close();
	}

	@Override
	public UserBean getUserBySSO(String sso) throws KvpalException {
		manager.getUserNameAndPasswordFromToken(sso);
		UserBean userBean=getUserByEmail(manager.getUserName());
		if(!userBean.getSso().equals(sso)){
			throw new KvpalException(ApplicationEnum.TOKEN_EXPIRED);
		}
		if(encrypt.decrypt(manager.getPassword()).equals(encrypt.decrypt(userBean.getPassword()))){
			return userBean;
		}
		else
			throw new KvpalException(ApplicationEnum.LOGIN_FAILED);
	}
	@Override
	public UserBean getUserDetail(String sso) throws KvpalException {
		manager.getUserNameAndPasswordFromToken(sso);
		UserBean userBean=getUserByEmail(manager.getUserName());
		if(!userBean.getSso().equals(sso)){
			throw new KvpalException(ApplicationEnum.TOKEN_EXPIRED);
		}
		if(encrypt.decrypt(manager.getPassword()).equals(encrypt.decrypt(userBean.getPassword()))){
			if(!userBean.getIsActive()){
				throw new KvpalException(ApplicationEnum.PENDING_USER);
			}
			throw new KvpalException(ApplicationEnum.USER_FETCHED, userBean);
		}
		else
			throw new KvpalException(ApplicationEnum.LOGIN_FAILED);
	}
	@Override
	public void logoutUser(String sso) throws KvpalException {
		
		manager.getUserNameAndPasswordFromToken(sso);
		UserBean userBean=getUserByEmail(manager.getUserName());
		if(!userBean.getSso().equals(sso)){
			throw new KvpalException(ApplicationEnum.TOKEN_EXPIRED);
		}
		if(encrypt.decrypt(manager.getPassword()).equals(encrypt.decrypt(userBean.getPassword()))){
			Session session=HibernateUtil.getNewSession();
			Transaction transaction=session.beginTransaction();
			userBean.setSso("");
			session.update(userBean);
			transaction.commit();
			session.close();
			throw new KvpalException(ApplicationEnum.LOGOUT_SUCCESS);
		}
		else
			throw new KvpalException(ApplicationEnum.UNABLE_LOGOUT);
	}

	@Override
	public MultipartFile createBadge(String sso,HttpServletRequest request) throws KvpalException {
		manager.getUserNameAndPasswordFromToken(sso);
		UserBean userBean=getUserByEmail(manager.getUserName());
		if(encrypt.decrypt(manager.getPassword()).equals(encrypt.decrypt(userBean.getPassword()))){
			return generator.createImage(userBean, request);
			/*BufferedImage image=generator.createImage(userBean, request);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", os);
			
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			return is;*/
		}
		else{
			throw new KvpalException(ApplicationEnum.LOGIN_FAILED);
		}
		
	}

	@Override
	public List<UserBean> getAllUserDetail(String sso) throws KvpalException {
		manager.getUserNameAndPasswordFromToken(sso);
		UserBean userBean=getUserByEmail(manager.getUserName());
		if(!userBean.getSso().equals(sso)){
			throw new KvpalException(ApplicationEnum.TOKEN_EXPIRED);
		}
		if(encrypt.decrypt(manager.getPassword()).equals(encrypt.decrypt(userBean.getPassword()))){
			if(userBean.getRole().getId()!=1)
				throw new KvpalException(ApplicationEnum.NOT_ADMIN);
			else{
				Session session=HibernateUtil.getNewSession();
				Criteria criteria=session.createCriteria(UserBean.class);
				throw new KvpalException(ApplicationEnum.USER_FETCHED, criteria.list());
				
			}
			
		}
		else
			throw new KvpalException(ApplicationEnum.LOGIN_FAILED);
	}

	@Override
	public void setUserStatus(String sso, int userId) throws KvpalException {
		manager.getUserNameAndPasswordFromToken(sso);
		UserBean userBean=getUserByEmail(manager.getUserName());
		if(!userBean.getSso().equals(sso)){
			throw new KvpalException(ApplicationEnum.TOKEN_EXPIRED);
		}
		if(encrypt.decrypt(manager.getPassword()).equals(encrypt.decrypt(userBean.getPassword()))){
			if(userBean.getRole().getId()!=1)
				throw new KvpalException(ApplicationEnum.NOT_ADMIN);
			else{
				Session session=HibernateUtil.getNewSession();
				UserBean bean=(UserBean) session.get(UserBean.class, userId);
				if(bean==null)
					throw new KvpalException(ApplicationEnum.USER_NOT_EXISTS);
				bean.setIsActive(!bean.getIsActive());
				Transaction transaction=session.beginTransaction();
				session.update(bean);
				transaction.commit();
				session.close();
				if(bean.getIsActive())
				emailclient.send(bean.getEmail(),SUBJECT, emailBodyGenerator.generateEmailBody(bean.getFirstName(),EmailEnum.VERIFIED.getValue()));
				throw new KvpalException(ApplicationEnum.USER_STATUS_SET_SUCCESSFULLY);
				
			}
			
		}
		else
			throw new KvpalException(ApplicationEnum.LOGIN_FAILED);
		
	}

	@Override
	public long createEvent(String sso,String name) throws KvpalException {
		manager.getUserNameAndPasswordFromToken(sso);
		UserBean userBean=getUserByEmail(manager.getUserName());
		if(!userBean.getSso().equals(sso)){
			throw new KvpalException(ApplicationEnum.TOKEN_EXPIRED);
		}
		if(encrypt.decrypt(manager.getPassword()).equals(encrypt.decrypt(userBean.getPassword()))){
			if(userBean.getRole().getId()!=1)
				throw new KvpalException(ApplicationEnum.NOT_ADMIN);
			else{
				Session session=HibernateUtil.getNewSession();
				long eventId=eventIdGenerator.generate();
				EventBean bean=new EventBean();
				bean.setId(eventId);
				bean.setName(name);
				bean.setDate(new Date());
				Transaction transaction=session.beginTransaction();
				session.save(bean);
				transaction.commit();
				throw new KvpalException(ApplicationEnum.EVENT_CREATED_SUCCESSFULLY, bean.getId());
				
			}
			
		}
		else
			throw new KvpalException(ApplicationEnum.LOGIN_FAILED);
	}

	@Override
	public void pushAttendant(String sso, String uuid, long eventId) throws KvpalException {
		manager.getUserNameAndPasswordFromToken(sso);
		UserBean userBean=getUserByEmail(manager.getUserName());
		if(!userBean.getSso().equals(sso)){
			throw new KvpalException(ApplicationEnum.TOKEN_EXPIRED);
		}
		if(encrypt.decrypt(manager.getPassword()).equals(encrypt.decrypt(userBean.getPassword()))){
			if(userBean.getRole().getId()!=1)
				throw new KvpalException(ApplicationEnum.NOT_ADMIN);
			else{
				Session session=HibernateUtil.getNewSession();
				UserBean attendant=getUserByUUID(uuid);
				EventBean eventBean=getEventById(eventId);
				if(attendant!=null && eventBean!=null){
					AttendantBean bean=new AttendantBean();
					bean.setAddress(attendant.getAddress());
					bean.setBatch(attendant.getBatch());
					bean.setEmail(attendant.getEmail());
					bean.setFirstName(attendant.getFirstName());
					bean.setLastName(attendant.getLastName());
					bean.setGender(attendant.getGender());
					bean.setMobile(attendant.getMobile());
					bean.setUid(attendant.getUid());
					bean.setEvent(eventBean);
					bean.setEventDate(new Date());
					Transaction transaction=session.beginTransaction();
					session.save(bean);
					transaction.commit();
					session.close();
					throw new KvpalException(ApplicationEnum.ATTENDANT_ENTERED_SUCCESSFULLY);		
				}
				
				
			}
			
		}
		else
			throw new KvpalException(ApplicationEnum.LOGIN_FAILED);
		
	}

	@Override
	public UserBean getUserByUUID(String uuid) {
		Session session=HibernateUtil.getNewSession();
		Criteria criteria=session.createCriteria(UserBean.class);
		criteria.add(Restrictions.eq("uid", uuid));
		List<UserBean> userBeans=criteria.list();
		if(userBeans.size()>0)
			return userBeans.get(0);
		else
		return null;
	}
	
	public EventBean getEventById(long eventId){
		Session session=HibernateUtil.getNewSession();
		EventBean bean=(EventBean) session.get(EventBean.class, eventId);
		return bean;
	}
	

}
