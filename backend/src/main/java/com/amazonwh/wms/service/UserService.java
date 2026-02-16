package com.amazonwh.wms.service;

import com.amazonwh.wms.dto.UserDto;
import com.amazonwh.wms.exception.BadRequestException;
import com.amazonwh.wms.exception.ResourceNotFoundException;
import com.amazonwh.wms.model.AppUser;
import com.amazonwh.wms.model.Role;
import com.amazonwh.wms.model.enums.UserRoleEnum;
import com.amazonwh.wms.repository.AppUserRepository;
import com.amazonwh.wms.repository.DCRepository;
import com.amazonwh.wms.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DCRepository dcRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        AppUser user = AppUser.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .roles(resolveRoles(dto))
                .build();

        if (dto.getDcId() != null) {
            user.setDc(dcRepository.findById(dto.getDcId())
                    .orElseThrow(() -> new ResourceNotFoundException("DC not found")));
        }

        return toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto dto) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            user.setRoles(resolveRoles(dto));
        }
        if (dto.getDcId() != null) {
            user.setDc(dcRepository.findById(dto.getDcId())
                    .orElseThrow(() -> new ResourceNotFoundException("DC not found")));
        } else {
            user.setDc(null);
        }

        return toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto toggleActive(Long id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(!Boolean.TRUE.equals(user.getActive()));
        return toDto(userRepository.save(user));
    }

    private Set<Role> resolveRoles(UserDto dto) {
        Set<Role> roles = new HashSet<>();
        if (dto.getRoles() != null) {
            for (String roleName : dto.getRoles()) {
                UserRoleEnum roleEnum = UserRoleEnum.valueOf(roleName);
                Role role = roleRepository.findByName(roleEnum)
                        .orElseThrow(() -> new BadRequestException("Role not found: " + roleName));
                roles.add(role);
            }
        }
        if (roles.isEmpty()) {
            roles.add(roleRepository.findByName(UserRoleEnum.RECEIVER)
                    .orElseThrow(() -> new BadRequestException("Default role not found")));
        }
        return roles;
    }

    private UserDto toDto(AppUser user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setDcId(user.getDc() != null ? user.getDc().getId() : null);
        dto.setDcCode(user.getDc() != null ? user.getDc().getCode() : null);
        dto.setRoles(user.getRoles().stream().map(r -> r.getName().name()).toList());
        dto.setActive(user.getActive());
        return dto;
    }
}
