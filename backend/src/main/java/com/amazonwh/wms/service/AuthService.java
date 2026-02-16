package com.amazonwh.wms.service;

import com.amazonwh.wms.dto.*;
import com.amazonwh.wms.exception.BadRequestException;
import com.amazonwh.wms.model.AppUser;
import com.amazonwh.wms.model.Role;
import com.amazonwh.wms.model.enums.UserRoleEnum;
import com.amazonwh.wms.repository.AppUserRepository;
import com.amazonwh.wms.repository.RoleRepository;
import com.amazonwh.wms.repository.DCRepository;
import com.amazonwh.wms.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DCRepository dcRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));
        String token = jwtProvider.generateToken(user.getUsername());
        var roles = user.getRoles().stream().map(r -> r.getName().name()).toList();
        return new AuthResponse(token, user.getUsername(), user.getFullName(), roles);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        UserRoleEnum roleEnum = request.getRole() != null ? UserRoleEnum.valueOf(request.getRole()) : UserRoleEnum.RECEIVER;
        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new BadRequestException("Role not found"));
        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .roles(Set.of(role))
                .build();
        if (request.getDcId() != null) {
            user.setDc(dcRepository.findById(request.getDcId()).orElse(null));
        }
        userRepository.save(user);
        String token = jwtProvider.generateToken(user.getUsername());
        var roles = user.getRoles().stream().map(r -> r.getName().name()).toList();
        return new AuthResponse(token, user.getUsername(), user.getFullName(), roles);
    }
}
