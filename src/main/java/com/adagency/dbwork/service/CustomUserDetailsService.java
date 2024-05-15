package com.adagency.dbwork.service;

import com.adagency.model.dto.security.AuthorizePersonDTO;
import com.adagency.model.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final BaseModelPersonService baseModelPersonService;


    @Autowired
    public CustomUserDetailsService(BaseModelPersonService baseModelPersonService){
        this.baseModelPersonService = baseModelPersonService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthorizePersonDTO authorize = baseModelPersonService.authorize(email);
        //return new org.springframework.security.core.userdetails.User(authorize.getEmail(), authorize.getPassword(), getAuthorities(authorize.getRole().getName()));
        return new CustomUserDetails(
                authorize.getEmail(),
                authorize.getPassword(),
                getAuthorities(authorize.getRole().getName()),
                authorize.getId() //
        );

    }

    private static List<SimpleGrantedAuthority> getAuthorities(String role) {
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        authList.add(new SimpleGrantedAuthority("ROLE_"+role));
        return authList;
    }


}
