package dev.dharam.authservice.service;

import dev.dharam.authservice.dtos.LoginResponseDto;
import dev.dharam.authservice.dtos.UserResponseDto;
import dev.dharam.authservice.exception.ResourceAlreadyExistException;
import dev.dharam.authservice.exception.ResourceNotFoundExistException;
import dev.dharam.authservice.models.Session;
import dev.dharam.authservice.models.SessionStatus;
import dev.dharam.authservice.models.User;
import dev.dharam.authservice.repository.SessionRepository;
import dev.dharam.authservice.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRepository sessionRepository;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder  passwordEncoder,SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public UserResponseDto signup(String email, String password) {
        userRepository.findByEmail(email).ifPresent(
                user -> {
                    throw new ResourceAlreadyExistException("User with email: "+email +" already exists!");
                }
        );

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);


        return UserResponseDto.from(savedUser);
    }

    @Override
    public LoginResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(
                ()->new ResourceNotFoundExistException("User with email: "+email+" not found!")
        );

        String token = RandomStringUtils.randomAlphanumeric(20);

//        Session session = new Session();
//        session.setToken(token);
//        session.setUser(user);
//        session.setStatus(SessionStatus.ACTIVE);
//        session.setExpiringAt(Instant.now().plus(1, ChronoUnit.MINUTES));
//        sessionRepository.save(session);
        UserResponseDto userDto = UserResponseDto.from(user);

        LoginResponseDto dto = new LoginResponseDto(token,userDto);

        return dto;
    }
}
