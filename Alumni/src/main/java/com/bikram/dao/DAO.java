package com.bikram.dao;

import com.bikram.dao.beans.RoleBean;
import com.bikram.dao.beans.UserBean;

public interface DAO {
void insertRole(RoleBean bean);
void createUser(UserBean bean);
RoleBean getRoleById(int id);
}
