package com.example.wmsnew.user.services;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.Role;
import com.auth0.json.mgmt.users.User;
import com.example.wmsnew.Config.Auth0Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders; // 修改为 Spring 的 HttpHeaders
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class Auth0Service {

  private final Auth0Properties auth0Properties;

  // get the token for machine to machine app
  public String getManagementApiToken() {
    try {
      RestTemplate restTemplate = new RestTemplate();

      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("grant_type", "client_credentials");
      requestBody.put("client_id", auth0Properties.getClientId());
      requestBody.put("client_secret", auth0Properties.getClientSecret());
      requestBody.put("audience", auth0Properties.getAudience());

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

      Map<String, Object> response =
          restTemplate.postForObject(
              "https://" + auth0Properties.getDomain() + "/oauth/token", entity, Map.class);

      if (response.get("access_token") != null) {
        return (String) response.get("access_token");
      } else {
        throw new RuntimeException("Failed to fetch token: " + response.get("error"));
      }

    } catch (Exception ex) {
      // Log the exception here
      throw new RuntimeException("Error retrieving management API token", ex);
    }
  }

  // create the product
  public String createUser(String email, String password, String roleName) throws Auth0Exception {
    String token = getManagementApiToken();
    log.info("token have been created: {}", token);
    ManagementAPI mgmt = new ManagementAPI(auth0Properties.getDomain(), token);

    User user = new User(auth0Properties.getConnection());
    user.setEmail(email);
    user.setPassword(password);
    user.setEmailVerified(true);

    User newUser = mgmt.users().create(user).execute();

    log.info("User created: {}", newUser);

    String userId = newUser.getId();
    String roleId = auth0Properties.getRoleId(roleName);
    List<String> roles = Collections.singletonList(roleId);
    mgmt.users().addRoles(userId, roles).execute();

    log.info("Role added to user: {}", userId);
    return userId;
  }

  // assing the new role to user and remove the old one
  public String updateUserRole(String auth0Id, String newRoleName) throws Auth0Exception {
    String token = getManagementApiToken();
    ManagementAPI mgmt = new ManagementAPI(auth0Properties.getDomain(), token);

    List<Role> currentRoles = mgmt.users().listRoles(auth0Id, null).execute().getItems();

    if (!currentRoles.isEmpty()) {
      List<String> roleIdsToRemove = new ArrayList<>();
      for (Role role : currentRoles) {
        roleIdsToRemove.add(role.getId());
      }
      mgmt.users().removeRoles(auth0Id, roleIdsToRemove).execute();
      log.info("Removed roles from user {}: {}", auth0Id, roleIdsToRemove);
    }

    String newRoleId = auth0Properties.getRoleId(newRoleName);
    List<String> newRoles = Collections.singletonList(newRoleId);
    mgmt.users().addRoles(auth0Id, newRoles).execute();
    log.info("Assigned new role '{}' to user {}", newRoleName, auth0Id);

    return auth0Id;
  }

  // delet the user
  public void deleteUser(String auth0Id) throws Auth0Exception {
    String token = getManagementApiToken();
    ManagementAPI mgmt = new ManagementAPI(auth0Properties.getDomain(), token);

    mgmt.users().delete(auth0Id).execute(); // Deletes user from Auth0
  }
}
