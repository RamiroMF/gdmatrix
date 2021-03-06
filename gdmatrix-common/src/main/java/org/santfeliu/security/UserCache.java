/*
 * GDMatrix
 *
 * Copyright (C) 2020, Ajuntament de Sant Feliu de Llobregat
 *
 * This program is licensed and may be used, modified and redistributed under
 * the terms of the European Public License (EUPL), either version 1.1 or (at
 * your option) any later version as soon as they are approved by the European
 * Commission.
 *
 * Alternatively, you may redistribute and/or modify this program under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either  version 3 of the License, or (at your option)
 * any later version.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the licenses for the specific language governing permissions, limitations
 * and more details.
 *
 * You should have received a copy of the EUPL1.1 and the LGPLv3 licenses along
 * with this program; if not, you may find them at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * http://www.gnu.org/licenses/
 * and
 * https://www.gnu.org/licenses/lgpl.txt
 */
package org.santfeliu.security;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import javax.xml.ws.WebServiceContext;
import org.matrix.security.Role;
import org.matrix.security.RoleFilter;
import org.matrix.security.RoleInRole;
import org.matrix.security.RoleInRoleFilter;
import org.matrix.security.SecurityConstants;
import org.matrix.security.SecurityManagerPort;
import org.matrix.security.SecurityManagerService;
import org.matrix.security.UserFilter;
import org.matrix.security.UserInRole;
import org.matrix.security.UserInRoleFilter;
import org.matrix.util.WSDirectory;
import org.matrix.util.WSEndpoint;
import org.santfeliu.jmx.CacheMBean;
import org.santfeliu.jmx.JMXUtils;
import org.santfeliu.security.util.Credentials;
import org.santfeliu.security.util.SecurityUtils;
import org.santfeliu.util.MatrixConfig;

/**
 *
 * @author realor
 */
public class UserCache
{
  static final Logger LOGGER = Logger.getLogger("UserCache");
  static final UserCache instance = new UserCache();

  private final Map<String, User> users =
    Collections.synchronizedMap(new HashMap<>());
  private final Map<String, Set<String>> userInRoles =
    Collections.synchronizedMap(new HashMap<>());
  private final Map<String, Set<String>> roleInRoles =
    Collections.synchronizedMap(new HashMap<>());
  private long updateTime;
  private long lastPurgeMillis;
  private String lastUserChangeDateTime;
  private String lastRoleChangeDateTime;
  private String lastResetDate;

  private UserCache()
  {
    updateTime = 1000 * 60; // 60 seconds
    lastPurgeMillis = System.currentTimeMillis();
    lastUserChangeDateTime = toDateTime(lastPurgeMillis);
    lastRoleChangeDateTime = lastUserChangeDateTime;
    lastResetDate = toDate(lastPurgeMillis);
    JMXUtils.registerMBean("UserCache", getCacheMBean());
  }

  public static void setUpdateTime(long updateTime) // millis
  {
    instance.updateTime = updateTime;
  }

  public static long getUpdateTime()
  {
    return instance.updateTime;
  }

  public static User login(String userId, String password)
  {
    // synchronize with database
    instance.purge(false);

    SecurityManagerPort port = instance.getSecurityManagerPort();
    User user = new User(port.login(userId, password));
    user.setRoles(instance.explodeUserInRoles(userId));
    instance.users.put(userId, user);

    return user;
  }

  public static User loginCertificate(byte[] certData)
  {
    // synchronize with database
    instance.purge(false);

    SecurityManagerPort port = instance.getSecurityManagerPort();
    User user = new User(port.loginCertificate(certData));
    String userId = user.getUserId();
    user.setRoles(instance.explodeUserInRoles(userId));
    instance.users.put(userId, user);

    return user;
  }

  public static User getUser(String userId, String password)
  {
    // check for database synchronization
    instance.purge(true);

    // look for userId entry
    User user = instance.users.get(userId);
    if (user == null ||
      (!instance.validPassword(user.getPassword(), password)))
    {
      SecurityManagerPort port = instance.getSecurityManagerPort();
      user = new User(port.login(userId, password));
      user.setRoles(instance.explodeUserInRoles(userId));
      instance.users.put(userId, user);
    }

    return user;
  }

  public static User getUser(Credentials credentials)
  {
    return getUser(credentials.getUserId(), credentials.getPassword());
  }

  public static User getUser(WebServiceContext wsContext)
  {
    return getUser(SecurityUtils.getCredentials(wsContext));
  }

  public static Set<String> getUserInRoles(String userId)
  {
    return getUserInRoles(userId, true);
  }

  public static Set<String> getUserInRoles(String userId, boolean explode)
  {
    if (explode)
    {
      return instance.explodeUserInRoles(userId);
    }
    else
    {
      return new HashSet(instance.loadUserInRoles(userId));
    }
  }

  public static Set<String> getRoleInRoles(String roleId)
  {
    return getRoleInRoles(roleId, true);
  }
  
  public static Set<String> getRoleInRoles(String roleId, boolean explode)
  {
    if (explode)
    {
      HashSet<String> explodedRoleSet = new HashSet<>();
      instance.explodeRole(roleId, explodedRoleSet);      
      return explodedRoleSet;
    }
    else
    {
      return new HashSet(instance.loadRoleInRoles(roleId));
    }
  }

  public static void clear()
  {
    instance.users.clear();
    instance.userInRoles.clear();
    instance.roleInRoles.clear();
  }

  // ***** private methods *****

  private boolean validPassword(String realPsw, String enteredPsw)
  {
    return realPsw == null || realPsw.equals(enteredPsw);
  }

  private Set<String> explodeUserInRoles(String userId)
  {
    HashSet<String> explodedRoleSet = new HashSet<>();
    Set<String> roleSet = loadUserInRoles(userId);
    // explode user roles
    Iterator<String> iter = roleSet.iterator();
    while (iter.hasNext())
    {
      instance.explodeRole(iter.next(), explodedRoleSet);
    }
    return explodedRoleSet;
  }

  private void explodeRole(String roleId, Set<String> explodedRoleSet)
  {
    explodedRoleSet.add(roleId);
    Set<String> roleSet = loadRoleInRoles(roleId);
    for (String includedRoleId : roleSet)
    {
      if (!explodedRoleSet.contains(includedRoleId))
      {
        if (includedRoleId.startsWith(SecurityConstants.SELF_ROLE_PREFIX))
        {
          explodedRoleSet.add(includedRoleId);
        }
        else
        {
          explodeRole(includedRoleId, explodedRoleSet);
        }
      }
    }
  }

  private Set<String> loadUserInRoles(String userId)
  {
    Set<String> roleSet = userInRoles.get(userId);
    if (roleSet == null)
    {
      roleSet = new HashSet<>();
      SecurityManagerPort port = instance.getSecurityManagerPort();
      UserInRoleFilter filter = new UserInRoleFilter();
      filter.setUserId(userId);
      String strDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
      filter.setMinDate(strDate);
      filter.setMaxDate(strDate);
      List<UserInRole> list = port.findUserInRoles(filter);
      for (UserInRole userInRole : list)
      {
        roleSet.add(userInRole.getRoleId());
      }
      userInRoles.put(userId, roleSet);
    }
    return roleSet;
  }

  private Set<String> loadRoleInRoles(String roleId)
  {
    Set<String> roleSet = roleInRoles.get(roleId);
    if (roleSet == null)
    {
      roleSet = new HashSet<>();
      SecurityManagerPort port = instance.getSecurityManagerPort();
      RoleInRoleFilter filter = new RoleInRoleFilter();
      filter.setContainerRoleId(roleId);
      List<RoleInRole> list = port.findRoleInRoles(filter);
      for (RoleInRole roleInRole : list)
      {
        roleSet.add(roleInRole.getIncludedRoleId());
      }
      roleInRoles.put(roleId, roleSet);
    }
    return roleSet;
  }

  private void purge(boolean fast)
  {
    long now = System.currentTimeMillis();
    String strNow = toDate(now);

    if (reset(strNow)) return;

    synchronized (this)
    {
      if (fast && now - instance.lastPurgeMillis < instance.updateTime)
        return;
    }

    SecurityManagerPort port = instance.getSecurityManagerPort();

    // get users && userInRoles to purge
    UserFilter userFilter = new UserFilter();
    String date = toDateTime(toMillis(lastUserChangeDateTime) + 1000);
    userFilter.setStartDateTime(date);
    LOGGER.log(Level.INFO, "===> purge users: {0}", date);
    List<org.matrix.security.User> userList = port.findUsers(userFilter);

    // get roleInRoles to purge
    RoleFilter roleFilter = new RoleFilter();
    date = toDateTime(toMillis(lastRoleChangeDateTime) + 1000);
    roleFilter.setStartDateTime(date);
    LOGGER.log(Level.INFO, "===> purge roles: {0}", date);
    List<Role> roleList = port.findRoles(roleFilter);

    synchronized (this)
    {
      for (org.matrix.security.User user : userList)
      {
        LOGGER.log(Level.INFO, "===> remove user {0}", user.getUserId());
        users.remove(user.getUserId());
        userInRoles.remove(user.getUserId());
        if (lastUserChangeDateTime.compareTo(user.getChangeDateTime()) < 0)
        {
          lastUserChangeDateTime = user.getChangeDateTime();
        }
      }

      for (Role role : roleList)
      {
        LOGGER.log(Level.INFO, "===> remove role {0}", role.getRoleId());
        roleInRoles.remove(role.getRoleId());
        if (lastRoleChangeDateTime.compareTo(role.getChangeDateTime()) < 0)
        {
          lastRoleChangeDateTime = role.getChangeDateTime();
        }
      }
      lastPurgeMillis = now;
    }
  }

  private long toMillis(String sdate)
  {
    try
    {
      SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
      Date date = df.parse(sdate);
      return date.getTime();
    }
    catch (ParseException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private String toDateTime(long millis)
  {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
    return df.format(new Date(millis));
  }

  private String toDate(long millis)
  {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    return df.format(new Date(millis));
  }

  private synchronized boolean reset(String strNow)
  {
    if (strNow.compareTo(lastResetDate) > 0)
    {
      clear();
      lastResetDate = strNow;
      return true;
    }
    return false;
  }

  private SecurityManagerPort getSecurityManagerPort()
  {
    WSDirectory wsDirectory = WSDirectory.getInstance();
    WSEndpoint endpoint = wsDirectory.getEndpoint(SecurityManagerService.class);
    return endpoint.getPort(SecurityManagerPort.class,
      MatrixConfig.getProperty("adminCredentials.userId"),
      MatrixConfig.getProperty("adminCredentials.password"));
  }

  private UserCacheMBean getCacheMBean()
  {
    try
    {
      return new UserCacheMBean();
    }
    catch (NotCompliantMBeanException ex)
    {
      return null;
    }
  }

  public class UserCacheMBean extends StandardMBean implements CacheMBean
  {
    public UserCacheMBean() throws NotCompliantMBeanException
    {
      super(CacheMBean.class);
    }

    @Override
    public String getName()
    {
      return "UserCache";
    }

    @Override
    public long getMaxSize()
    {
      return Long.MAX_VALUE;
    }

    @Override
    public long getSize()
    {
      return users.size();
    }

    @Override
    public String getDetails()
    {
      return "usersSize=" + getSize() + "," +
        "userInRolesSize=" + userInRoles.size() + "," +
        "roleInRolesSize=" + roleInRoles.size();
    }

    @Override
    public void clear()
    {
      UserCache.clear();
    }

    @Override
    public void update()
    {
      long nowMillis = System.currentTimeMillis();
      instance.purge(false);
      instance.lastPurgeMillis = nowMillis;
    }
  }
}
