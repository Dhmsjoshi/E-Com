package dev.dharam.authservice.service;

import dev.dharam.authservice.config.appconfig.ApplicationConstants;
import dev.dharam.authservice.dtos.InternalLoginResultDto;
import dev.dharam.authservice.dtos.LoginResponseDto;
import dev.dharam.authservice.dtos.UserResponseDto;
import dev.dharam.authservice.exception.ResourceAlreadyExistException;
import dev.dharam.authservice.exception.ResourceNotFoundExistException;
import dev.dharam.authservice.models.Role;
import dev.dharam.authservice.models.Session;
import dev.dharam.authservice.models.SessionStatus;
import dev.dharam.authservice.models.User;
import dev.dharam.authservice.repository.RoleRepository;
import dev.dharam.authservice.repository.SessionRepository;
import dev.dharam.authservice.repository.UserRepository;
import dev.dharam.authservice.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRepository sessionRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;



    @Override
    @Transactional
    public UserResponseDto signup(String email, String password) {
        userRepository.findByEmail(email).ifPresent(
                user -> {
                    throw new ResourceAlreadyExistException("User with email: "+email +" already exists!");
                }
        );
        Role customRole = roleRepository.findByName(ApplicationConstants.ROLE_CUSTOMER).orElseThrow(
                ()-> new ResourceNotFoundExistException("Default role not found!")
        );

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(customRole));
        User savedUser = userRepository.save(user);


        return UserResponseDto.from(savedUser);
    }

    @Override
    @Transactional
    public InternalLoginResultDto login(String email, String password) {
        Authentication resultAuthentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email, password));

        var loggedInUser =(User) resultAuthentication.getPrincipal();

        invalidateOldSessions(loggedInUser);

        String accessToken=jwtUtil.generateJwtToken(resultAuthentication);
        String refreshToken = UUID.randomUUID().toString();


        saveSession(loggedInUser,refreshToken);

        UserResponseDto userResponseDto = UserResponseDto.from(loggedInUser); // Mapper use karein
        return new InternalLoginResultDto(accessToken, refreshToken, userResponseDto);
    }

    @Override
    public void logout(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken).orElseThrow(
                ()->new ResourceNotFoundExistException("Session not found!")
        );
        session.setStatus(SessionStatus.EXPIRED);
        sessionRepository.save(session);
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken).orElseThrow(
                ()->new ResourceNotFoundExistException("Invalid refresh token!")
        );

        if(!session.getStatus().equals(SessionStatus.ACTIVE)){
            throw new RuntimeException("Session has expired or logged out!");
        }
        if(session.getExpiringAt().isBefore(Instant.now())){
            session.setStatus(SessionStatus.EXPIRED);
            sessionRepository.save(session);
            throw new RuntimeException("Refresh token has expired!");
        }

        User user = session.getUser();
        return jwtUtil.generateJwtTokenFromUser(user);

    }

    private void saveSession(User user, String refreshToken) {
        Session session = new Session();
        session.setUser(user);
        session.setRefreshToken(refreshToken);
        session.setStatus(SessionStatus.ACTIVE);
        session.setExpiringAt(Instant.now().plus(30, ChronoUnit.DAYS));
        sessionRepository.save(session);
    }
    private void invalidateOldSessions(User user) {
        List<Session> activeSessions = sessionRepository.findByUserAndStatus(user, SessionStatus.ACTIVE);
        for(Session session : activeSessions){
            session.setStatus(SessionStatus.EXPIRED);
        }
        sessionRepository.saveAll(activeSessions);
    }
}
