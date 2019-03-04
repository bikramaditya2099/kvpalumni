package com.bikram.dao;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.bikram.beans.LoginBean;
import com.bikram.dao.beans.RoleBean;
import com.bikram.dao.beans.UserBean;
import com.bikram.exception.KvpalException;

public interface DAO {
String checkLogin(LoginBean bean) throws KvpalException;	
void insertRole(RoleBean bean);
void createUser(UserBean bean)throws KvpalException;
void updateUser(UserBean bean);
void logoutUser(String token)throws KvpalException ;
UserBean getUserBySSO(String sso) throws KvpalException;
UserBean getUserDetail(String sso) throws KvpalException;
List<UserBean> getAllUserDetail(String sso) throws KvpalException;
RoleBean getRoleById(int id);
UserBean getUserByEmail(String email)throws KvpalException;
MultipartFile createBadge(String sso,HttpServletRequest request)throws KvpalException;
void setUserStatus(String sso,int userId)throws KvpalException ;
}
