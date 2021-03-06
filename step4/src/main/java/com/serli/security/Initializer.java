package com.serli.security;

import com.serli.security.model.Comment;
import com.serli.security.model.CommentRepository;
import com.serli.security.model.User;
import com.serli.security.model.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
class Initializer implements CommandLineRunner {

    private final UserRepository repository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Initializer(UserRepository repository, CommentRepository repo, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.commentRepository = repo;
        this.bCryptPasswordEncoder = encoder;
    }

    @Override
    public void run(String... strings) {

        for (int i = 1; i < 5; i++) {
            User user = new User("test" + i, "test" + i + "@test.com", this.bCryptPasswordEncoder.encode("test" + i));
            if(i==1){
                user.setAdmin(true);
            }
            repository.save(user);
            Comment comment = new Comment(null, "test de message "+i, user);
            commentRepository.save(comment);
        }

    }
}