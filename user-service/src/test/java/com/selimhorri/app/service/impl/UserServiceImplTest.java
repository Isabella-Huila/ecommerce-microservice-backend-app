package com.selimhorri.app.service.impl;

import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.helper.UserMappingHelper;
import com.selimhorri.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldReturnAllUsers() {
        User user1 = new User();
        user1.setUserId(1);
        user1.setFirstName("Isa");

        User user2 = new User();
        user2.setUserId(2);
        user2.setFirstName("Carlos");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        try (MockedStatic<UserMappingHelper> mocked = mockStatic(UserMappingHelper.class)) {
            UserDto dto1 = UserDto.builder()
                    .userId(1)
                    .firstName("Isa")
                    .build();

            UserDto dto2 = UserDto.builder()
                    .userId(2)
                    .firstName("Carlos")
                    .build();

            mocked.when(() -> UserMappingHelper.map(user1)).thenReturn(dto1);
            mocked.when(() -> UserMappingHelper.map(user2)).thenReturn(dto2);

            List<UserDto> result = userService.findAll();

            assertEquals(2, result.size());
            assertEquals("Isa", result.get(0).getFirstName());
            verify(userRepository, times(1)).findAll();
        }
    }

    @Test
    void shouldReturnUserById() {
        User user = new User();
        user.setUserId(1);
        user.setFirstName("Isa");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        try (MockedStatic<UserMappingHelper> mocked = mockStatic(UserMappingHelper.class)) {
            UserDto dto = UserDto.builder()
                    .userId(1)
                    .firstName("Isa")
                    .build();

            mocked.when(() -> UserMappingHelper.map(user)).thenReturn(dto);

            UserDto result = userService.findById(1);

            assertNotNull(result);
            assertEquals("Isa", result.getFirstName());
        }
    }

    @Test
    void shouldThrowWhenUserNotFoundById() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(UserObjectNotFoundException.class, () -> userService.findById(99));
    }

    @Test
    void shouldSaveUserSuccessfully() {
        UserDto dto = UserDto.builder()
                .userId(1)
                .firstName("Isa")
                .build();

        User user = new User();
        user.setUserId(1);
        user.setFirstName("Isa");

        when(userRepository.save(any(User.class))).thenReturn(user);

        try (MockedStatic<UserMappingHelper> mocked = mockStatic(UserMappingHelper.class)) {
            mocked.when(() -> UserMappingHelper.map(dto)).thenReturn(user);
            mocked.when(() -> UserMappingHelper.map(user)).thenReturn(dto);

            UserDto result = userService.save(dto);

            assertEquals("Isa", result.getFirstName());
            verify(userRepository, times(1)).save(user);
        }
    }

    @Test
    void shouldFindUserByUsername() {
        User user = new User();
        user.setUserId(10);
        user.setFirstName("Isa");

        when(userRepository.findByCredentialUsername("isahc")).thenReturn(Optional.of(user));

        try (MockedStatic<UserMappingHelper> mocked = mockStatic(UserMappingHelper.class)) {
            UserDto dto = UserDto.builder()
                    .userId(10)
                    .firstName("Isa")
                    .build();

            mocked.when(() -> UserMappingHelper.map(user)).thenReturn(dto);

            UserDto result = userService.findByUsername("isahc");

            assertEquals("Isa", result.getFirstName());
            verify(userRepository, times(1)).findByCredentialUsername("isahc");
        }
    }
}
