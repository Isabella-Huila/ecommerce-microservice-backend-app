package com.selimhorri.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.helper.UserMappingHelper;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        log.info("📋 Listando todos los usuarios desde UserServiceImpl...");
        return this.userRepository.findAll()
                .stream()
                .map(UserMappingHelper::map)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public UserDto findById(final Integer userId) {
        log.info("🔍 Buscando usuario con ID: {}", userId);
        return this.userRepository.findById(userId)
                .map(UserMappingHelper::map)
                .orElseThrow(() -> 
                    new UserObjectNotFoundException(
                        String.format("No se encontró ningún usuario con el ID %d.", userId)
                    )
                );
    }

    @Override
    public UserDto save(final UserDto userDto) {
        log.info("💾 Guardando nuevo usuario: {}", userDto.getFirstName());
        return UserMappingHelper.map(
                this.userRepository.save(UserMappingHelper.map(userDto))
        );
    }

    @Override
    public UserDto update(final UserDto userDto) {
        log.info("✏️ Actualizando usuario con ID: {}", userDto.getUserId());
        if (userDto.getUserId() == null || !this.userRepository.existsById(userDto.getUserId())) {
            throw new UserObjectNotFoundException(
                    String.format("No se puede actualizar: el usuario con ID %d no existe.", userDto.getUserId())
            );
        }
        return UserMappingHelper.map(
                this.userRepository.save(UserMappingHelper.map(userDto))
        );
    }

    @Override
    public UserDto update(final Integer userId, final UserDto userDto) {
        log.info("✏️ Actualizando usuario con ID proporcionado: {}", userId);
        UserDto existing = this.findById(userId); // si no existe, lanza excepción 404
        userDto.setUserId(existing.getUserId());
        return UserMappingHelper.map(
                this.userRepository.save(UserMappingHelper.map(userDto))
        );
    }

    @Override
    public void deleteById(final Integer userId) {
        log.info("🗑️ Eliminando usuario con ID: {}", userId);
        if (!this.userRepository.existsById(userId)) {
            throw new UserObjectNotFoundException(
                    String.format("No se puede eliminar: el usuario con ID %d no existe.", userId)
            );
        }
        this.userRepository.deleteById(userId);
    }

    @Override
    public UserDto findByUsername(final String username) {
        log.info("🔍 Buscando usuario con nombre de usuario: {}", username);
        return UserMappingHelper.map(
                this.userRepository.findByCredentialUsername(username)
                        .orElseThrow(() ->
                            new UserObjectNotFoundException(
                                String.format("No se encontró ningún usuario con el nombre de usuario '%s'.", username)
                            )
                        )
        );
    }
}




