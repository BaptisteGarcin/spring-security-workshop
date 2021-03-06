package com.serli.security.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AppAuthProvider extends DaoAuthenticationProvider {


    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AppAuthProvider(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;

        String name = auth.getName();
        String password = auth.getCredentials()
                .toString();


        // Il faut vérifier que l'utilisateur dans auth existe à l'aide de la méthode getUserDetailsService().
        // Mais aussi que le mot de passe correspond

        //Puis renvoyer un UsernamePasswordAuthenticationToken si l'utilisateur correspond une exception sinon de type BadCredentialsException
        return null;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;

    }
}