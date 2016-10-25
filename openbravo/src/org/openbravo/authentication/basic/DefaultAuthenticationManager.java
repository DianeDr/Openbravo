/*
 ************************************************************************************
 * Copyright (C) 2001-2016 Openbravo S.L.U.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */

package org.openbravo.authentication.basic;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.openbravo.authentication.AuthenticationException;
import org.openbravo.authentication.AuthenticationExpirationPasswordException;
import org.openbravo.authentication.AuthenticationManager;
import org.openbravo.base.HttpBaseUtils;
import org.openbravo.base.secureApp.LoginUtils;
import org.openbravo.base.secureApp.VariablesHistory;
import org.openbravo.base.secureApp.VariablesSecureApp;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.erpCommon.utility.Utility;
import org.openbravo.model.ad.access.User;

/**
 * 
 * @author adrianromero
 * @author iperdomo
 */
public class DefaultAuthenticationManager extends AuthenticationManager {

  private static final Logger log4j = Logger.getLogger(DefaultAuthenticationManager.class);

  public DefaultAuthenticationManager() {
  }

  public DefaultAuthenticationManager(HttpServlet s) throws AuthenticationException {
    super(s);
  }

  @Override
  protected String doAuthenticate(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, ServletException, IOException {

    final VariablesSecureApp vars = new VariablesSecureApp(request, false);
    final Boolean resetPassword = Boolean.parseBoolean(vars.getStringParameter("resetPassword"));
    final String sUserId;
    if (resetPassword) {
      sUserId = vars.getSessionValue("#AD_User_ID");

    } else {
      sUserId = (String) request.getSession().getAttribute("#Authenticated_user");
    }

    final String strAjax = vars.getStringParameter("IsAjaxCall");
    if (!StringUtils.isEmpty(sUserId) && !resetPassword) {
      return sUserId;
    }

    VariablesHistory variables = new VariablesHistory(request);
    final String user;
    final String pass;
    // Begins code related to login process
    if (resetPassword) {
      User userOB = OBDal.getInstance().get(User.class, sUserId);
      user = userOB.getUsername();
    } else {
      user = vars.getStringParameter("user");
    }
    pass = vars.getStringParameter("password");
    username = user;
    if (StringUtils.isEmpty(user)) {
      // redirects to the menu or the menu with the target
      setTargetInfoInVariables(request, variables);
      return null; // just give up, return null
    }

    final String userId = LoginUtils.getValidUserId(conn, user, pass);
    final String sessionId = createDBSession(request, user, userId);

    if (userId == null) {

      OBError errorMsg = new OBError();
      errorMsg.setType("Error");

      if (LoginUtils.checkUserPassword(conn, user, pass) == null) {
        log4j.debug("Failed user/password. Username: " + user + " - Session ID:" + sessionId);
        errorMsg.setTitle("IDENTIFICATION_FAILURE_TITLE");
        errorMsg.setMessage("IDENTIFICATION_FAILURE_MSG");
      } else {
        log4j.debug(user + " is locked cannot activate session ID " + sessionId);
        errorMsg.setTitle("LOCKED_USER_TITLE");
        errorMsg.setMessage("LOCKED_USER_MSG");
      }

      throw new AuthenticationException("IDENTIFICATION_FAILURE_TITLE", errorMsg);
    }

    vars.setSessionValue("#AD_User_ID", userId);

    checkIfPasswordExpired(userId, variables.getLanguage());

    // Using the Servlet API instead of vars.setSessionValue to avoid breaking code
    // vars.setSessionValue always transform the key to upper-case
    request.getSession(true).setAttribute("#Authenticated_user", userId);

    vars.setSessionValue("#AD_SESSION_ID", sessionId);
    vars.setSessionValue("#LogginIn", "Y");

    if (!StringUtils.isEmpty(strAjax) && StringUtils.isEmpty(userId)) {
      bdErrorAjax(response, "Error", "",
          Utility.messageBD(this.conn, "NotLogged", variables.getLanguage()));
      return null;
    } else {
      // redirects to the menu or the menu with the target
      setTargetInfoInVariables(request, variables);
    }

    return userId;
  }

  private void setTargetInfoInVariables(HttpServletRequest request, VariablesHistory variables) {
    // redirects to the menu or the menu with the target
    String strTarget = request.getRequestURL().toString();
    String qString = request.getQueryString();
    String strDireccionLocal = HttpBaseUtils.getLocalAddress(request);

    if (!strTarget.endsWith("/security/Menu.html")) {
      variables.setSessionValue("targetmenu", strTarget);
    }

    // Storing target string to redirect after a successful login
    variables.setSessionValue("target", strDireccionLocal + "/security/Menu.html"
        + (qString != null && !qString.equals("") ? "?" + qString : ""));
    if (qString != null && !qString.equals("")) {
      variables.setSessionValue("targetQueryString", qString);
    }
  }

  @Override
  protected void doLogout(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    if (!response.isCommitted()) {
      response.sendRedirect(HttpBaseUtils.getLocalAddress(request));
    }
  }

  /**
   * Checks the expiration password date from userId, throws
   * AuthenticationExpirationPasswordException in case that expiration date is reached
   * 
   * @param userId
   *          The userId of the user to check expiration password date
   * @param language
   *          Default language for the user
   * @throws AuthenticationExpirationPasswordException
   *           AuthenticationExpirationPasswordException is thrown in case that expiration date is
   *           reached
   * 
   */
  private void checkIfPasswordExpired(String userId, String language)
      throws AuthenticationExpirationPasswordException {

    Date total = null;

    final OBCriteria<User> obc = OBDal.getInstance().createCriteria(User.class);
    obc.setFilterOnReadableClients(false);
    obc.setFilterOnReadableOrganization(false);
    obc.add(Restrictions.eq(User.PROPERTY_ID, userId));
    final User userOB = (User) obc.uniqueResult();
    Date lastUpdatePassword = userOB.getLastPasswordUpdate();
    Long validityDays = userOB.getClient().getDaysToPasswordExpiration();
    if (validityDays != null && validityDays > 0) {
      Calendar expirationDate = Calendar.getInstance();
      expirationDate.setTimeInMillis(lastUpdatePassword.getTime());
      expirationDate.add(Calendar.DATE, validityDays.intValue());
      total = expirationDate.getTime();
    }

    Date today = new Date();
    if (total != null && total.compareTo(today) <= 0) {
      OBError errorMsg = new OBError();
      errorMsg.setType("Error");
      errorMsg.setTitle(Utility.messageBD(conn, "CPExpirationPassword", language));
      errorMsg.setMessage(Utility.messageBD(conn, "CPUpdatePassword", language));
      throw new AuthenticationExpirationPasswordException(errorMsg.getTitle(), errorMsg, true);
    }

  }
}
