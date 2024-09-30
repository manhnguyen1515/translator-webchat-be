package com.translator.webchat.service.impl;

import com.translator.webchat.config.UserAuthProvider;
import com.translator.webchat.dto.request.*;
import com.translator.webchat.dto.response.ResponseGetUser;
import com.translator.webchat.dto.response.UserAuthResponse;
import com.translator.webchat.dto.response.UserSignInResponse;
import com.translator.webchat.entities.Session;
import com.translator.webchat.entities.User;
import com.translator.webchat.exception.ResourceNotFoundException;
import com.translator.webchat.repositories.SessionRepository;
import com.translator.webchat.repositories.SessionUsersRepository;
import com.translator.webchat.repositories.UserRepository;
import com.translator.webchat.service.UserService;
import com.translator.webchat.util.GenerateOTP;
import com.translator.webchat.util.HashedPassword;
import com.translator.webchat.util.PasswordUtil;
import com.translator.webchat.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final SessionUsersRepository sessionUsersRepository;
    private final UserAuthProvider userAuthProvider;
    private final GenerateOTP generateOTP;

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public String signUp(UserRequestDTO userDto) {
        Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser.isPresent()) {
            //UserName already exist
            throw new ResourceNotFoundException(HttpStatus.CONFLICT.value(), "UserName already exist", "1");
        } else if (userDto.getUsername().toLowerCase().contains("admin")) {
            //Can't create username with characters like admin
            throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST.value(),
                    "Can't create username with characters like admin", "2");
        } else if (userDto.getPassword().length() < 8) {
            //Password must be at least 8 characters
            throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST.value(),
                    "Password must be at least 8 characters", "3");
        } else if (userDto.getNickname().toLowerCase().contains("admin")) {
            //Can't create nickname with characters like admin
            throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST.value(),
                    "Can't create nickname with characters like admin", "4");
        } else if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            //Email already exist
            throw new ResourceNotFoundException(HttpStatus.CONFLICT.value(), "Email already exist", "5");
        }

        String password = userDto.getPassword();
        HashedPassword hashedPassword = PasswordUtil.hashAndSaltPassword(password);
        java.util.UUID userId = UUID.GenerateUUID();
        User newUser = User.builder()
                .id(userId)
                .username(userDto.getUsername())
                .password(hashedPassword.getHashedPassword())
                .email(userDto.getEmail())
                .nickname(userDto.getNickname())
                .regionCountry(userDto.getRegionCountry())
                .createdAt(new java.sql.Timestamp(System.currentTimeMillis()))
                .build();
        userRepository.save(newUser);
        return "true";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthResponse signIn(UserRequestSignInDTO userRequestDto) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String usernameOrEmail = userRequestDto.getUsernameOrEmail();
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);

        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
            if (userOpt.isEmpty()) {
                throw new ResourceNotFoundException(HttpStatus.NOT_FOUND.value(), "User Not Found", "1");
            }
        }

        User userFounded = userOpt.get();
        if (PasswordUtil.verifyPassword(userRequestDto.getPassword(), userFounded.getPassword())) {
            String accessToken = userAuthProvider.createAccessToken(userFounded.getUsername());
            String refreshToken = userAuthProvider.createRefreshToken(userFounded.getId());
            return new UserAuthResponse(accessToken, refreshToken, new UserSignInResponse(userFounded.getId(),
                    userFounded.getUsername(), userFounded.getEmail(), userFounded.getNickname(),
                    userFounded.getRegionCountry()));
        }
        throw new ResourceNotFoundException(HttpStatus.UNAUTHORIZED.value(), "Invalid password", "2");
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String email = changePasswordRequestDTO.getEmail();

        User userDB = userRepository.findByEmail(email).get();

        if (changePasswordRequestDTO.getOldPassword().length() < 8) {
            throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST.value(),
                    "Password must be at least 8 characters", "1");
        } else if (changePasswordRequestDTO.getNewPassword().length() < 8) {
            throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST.value(),
                    "New password must be at least 8 characters", "2");
        } else if (!PasswordUtil.verifyPassword(changePasswordRequestDTO.getOldPassword(), userDB.getPassword())) {
            throw new ResourceNotFoundException(HttpStatus.UNAUTHORIZED.value(),
                    "Old password is incorrect", "3");
        } else if (PasswordUtil.isPasswordMatch(changePasswordRequestDTO.getOldPassword(),
                changePasswordRequestDTO.getNewPassword())) {
            throw new ResourceNotFoundException(HttpStatus.CONFLICT.value(),
                    "Old password and new password can't be the same", "4");
        }

        HashedPassword hashedPassword = PasswordUtil.hashAndSaltPassword(changePasswordRequestDTO.getNewPassword());
        userDB.setPassword(hashedPassword.getHashedPassword());
        userDB.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        userRepository.save(userDB);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public ResponseGetUser getUser(String usernameOrNickname) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Optional<User> getUser = userRepository.findByUsername(usernameOrNickname);
        if (getUser.isEmpty()) {
            getUser = userRepository.findByNickname(usernameOrNickname);
            if (getUser.isEmpty()) {
                throw new ResourceNotFoundException(HttpStatus.NOT_FOUND.value(), "User Not Found", "1");
            }
        }
        User userFounded = getUser.get();
        return new ResponseGetUser(userFounded.getUsername(), userFounded.getNickname());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createSession(CreateSessionDTO createSessionDTO) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String username1 = createSessionDTO.getUsername1();
        String username2 = createSessionDTO.getUsername2();
        // Check if username1 exist
        Optional<User> getUser1 = userRepository.findByUsername(username1);
        if (getUser1.isEmpty()) {
                throw new ResourceNotFoundException(HttpStatus.NOT_FOUND.value(), "User1 Not Found", "1");
        }
        // Check if username2 exist
        Optional<User> getUser2 = userRepository.findByUsername(username2);
        if (getUser2.isEmpty()) {
                throw new ResourceNotFoundException(HttpStatus.NOT_FOUND.value(), "User2 Not Found", "1");
        }

        User userFounded1 = getUser1.get();
        User userFounded2 = getUser2.get();

        Optional<Long> idSU = sessionUsersRepository.findSessionIdsByUserId(userFounded1.getId(), userFounded2.getId());
        if (idSU.isEmpty()) {
            Session newSession = Session.builder()
                    .createdAt(LocalDateTime.now())
                    .build();

            Set<User> users = new HashSet<>();
            users.add(userFounded1);
            users.add(userFounded2);
            newSession.setUsers(users);
            Session sessionCreated = sessionRepository.save(newSession);
            return sessionCreated.getId().toString();
        }

        return idSU.get().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String verifyOtpAndChangePassword(VerifyOTPAndChangePasswordRequestDTO verifyOTPAndChangePasswordRequestDTO)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String email = verifyOTPAndChangePasswordRequestDTO.getEmail();
        String otp = verifyOTPAndChangePasswordRequestDTO.getOtp();
        String newPassword = verifyOTPAndChangePasswordRequestDTO.getNewPassword();

        if (newPassword.length() < 8) {
            throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST.value(),
                    "New password must be at least 8 characters", "1");
        } else if (!generateOTP.isOtpValid(email, otp)) {
            throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST.value(),
                    "OTP or Email is incorrect", "2");
        }
        User user = userRepository.findByEmail(email).get();
        HashedPassword hashedPassword = PasswordUtil.hashAndSaltPassword(newPassword);
        user.setPassword(hashedPassword.getHashedPassword());
        user.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        return "true";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserSignInResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserSignInResponse(user.getId(), user.getUsername(), user.getEmail(), user.getNickname(), user.getRegionCountry()))
                .collect(Collectors.toList());
    }
}
