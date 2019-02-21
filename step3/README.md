# Troisième étape

Le but de cette étape est de mettre en place une connexion par **token**. Nous utiliserons ici un type de token éprouvé, le token JWT.
Le principe de se genre d'authentification réside dans le fait que chaque requête, chaque appel au serveur doit être accompagné d'un token. Celui-ci doit contenir des informations suffisantes afin que le backend soit en mesure de savoir qui fait l'appel et s'il a le droit.


## Mise en place
* Informer Spring de ne pas utiliser de session (Aucune session ne sera créée par Spring cf [SessionCreationPolicy](https://www.baeldung.com/spring-security-session))

        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        
* Informer ensuite Spring de la démarche à suivre une fois la connexion est établie
    * Pour cela compléter la config par 
            
            .formLogin()
                .loginPage("/")
                .loginProcessingUrl("/login")
                .successHandler(getAuthSuccessHandler())
                
        > Comme précedemment on informe Spring de la page de login et de l'url de login. On ajoute un successHandler qui nous permettra d'effectuer une action après connexion

    * Le but de cet handler est de créer un jeton jwt que nous passerons ensuite dans la requête. Pour ce faire nous allons utiliser la librairie **[jjwt](https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt)**, je vous invites donc à ajouter la dépendance dans le pom.xml
    
            //L'objet Claims permet de stocker des informations utile pour la connection
            Claims claims = Jwts.claims().setSubject(username);
        
            // Génération du token à partir du claims ci-dessus 
            // avec une date d'expiration 
            // signé par une clé secrète
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                    .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret())
                    .compact();
    * une fois cette étape terminé il ne reste plus qu'a stocker ce token côté front afin de pouvoir le passer à toute les requêtes. Nous utiliserons pour ceci l'objet **sessionStorage**
    
            //permet de stocker dans le storage du navigateur
            sessionStorage.setItem("mon-token", token);      
            
            // permet de récupérer la variable stockée
            let monToken = sessionStorage.getItem("monToken");          
                    
* Nous pourrons ensuite mettre en place un filtre qui s'executera avant chaque requête afin de vérifier que celle-ci porte bien le token, et que celui-ci est valide.
    * Créer une classe qui hérites de **OncePerRequestFilter**
    * Implémenter la méthode **doFilterInternal**. 
        * Cette méthode devra vérifier que la requête contient le jeton de connexion. Le header est généralement de la forme `'Authorization': Bearer le-token`
        
                String header = request.getHeader(headerName);
                
                if (header == null || !header.startsWith(prefix)) {
                    chain.doFilter(request, response);
                    return;
                }
        
                String token = header.replace(prefix, "");
                
        * Que celui-ci est valide. 
        
                Claims claims = Jwts.parser()
                                    .setSigningKey(jwtConfig.getSecret())
                                    .parseClaimsJws(token)
                                    .getBody();
                                    
        * Puis stocker dans le context Spring le user 
        
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("{} -> authenticated user {} session expired {}", request.getRequestURI(), username, claims.getExpiration());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
        * on peut maintenant dire à Spring que chaque requête nécessitant l'authentification devra au préalable être filtrée.
        
                .anyRequest().authenticated()
                .and().addFilterBefore(new JwtTokenAuthenticationFilter(jwtConfig, userDetailsService()), UsernamePasswordAuthenticationFilter.class)
            > On précise ici que notre filtre s'executera juste avant le filtre d'authentification **UsernamePasswordAuthenticationFilter** ainsi on pourra garantir que l'on aura remplie ou non le context utilisateur de spring et ainsi laisser spring géré le droit d'accès au différents points d'entrée
            
        * Depuis l'application il faudra alors alimenter nos appel au serveur avec le header
        
                headers: {
                    'Authorization': `Bearer ${this.token}`
                }                 
