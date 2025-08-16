package com.example.wmsnew.Config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class Auth0Convertor implements Converter<Jwt, Collection<GrantedAuthority>> {

  public Collection<GrantedAuthority> convert(Jwt source) {
    ArrayList<String> roles =
        (ArrayList<String>) source.getClaims().get("https://my-app.example.com/roles");
    Collection<GrantedAuthority> authorities =
        roles.stream()
            .map(roleName -> "ROLE_" + roleName)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    return authorities;
  }
}
