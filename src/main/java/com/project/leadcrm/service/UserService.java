package com.project.leadcrm.service;

import com.project.leadcrm.dto.UserRequest;
import com.project.leadcrm.dto.UserResponse;
import com.project.leadcrm.dto.UserUpdateRequest;
import com.project.leadcrm.exception.ConflictException;
import com.project.leadcrm.exception.ResourceNotFoundException;
import com.project.leadcrm.model.User;
import com.project.leadcrm.model.UserRole;
import com.project.leadcrm.model.UserStatus;
import com.project.leadcrm.repository.UserRepository;
import com.project.leadcrm.util.EmailUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return toResponse(getUser(id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("User email is already registered");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(EmailUtils.normalize(request.email()));
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role() == null ? UserRole.SALES_REP : request.role());
        user.setStatus(request.status() == null ? UserStatus.ACTIVE : request.status());

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = getUser(id);
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new ConflictException("User email is already registered");
        }

        user.setFullName(request.fullName());
        user.setEmail(EmailUtils.normalize(request.email()));
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        user.setRole(request.role() == null ? user.getRole() : request.role());
        user.setStatus(request.status() == null ? user.getStatus() : request.status());

        return toResponse(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(getUser(id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User %d was not found".formatted(id)));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
