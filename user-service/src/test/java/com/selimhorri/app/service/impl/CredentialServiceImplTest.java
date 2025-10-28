package com.selimhorri.app.service.impl;

import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.domain.*;

import com.selimhorri.app.exception.wrapper.CredentialNotFoundException;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.helper.CredentialMappingHelper;
import com.selimhorri.app.repository.CredentialRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CredentialServiceImplTest {

    @Mock
    private CredentialRepository credentialRepository;

    @InjectMocks
    private CredentialServiceImpl credentialService;

    private Credential credential;
    private CredentialDto credentialDto;

    @BeforeEach
    void setup() {
        credential = new Credential();
        credential.setCredentialId(1);
        credential.setUsername("selim");
        credential.setPassword("1234");

        credentialDto = CredentialDto.builder()
                .credentialId(1)
                .username("selim")
                .password("1234")
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();
    }

    @Test
    void shouldReturnAllCredentials() {
        when(credentialRepository.findAll()).thenReturn(List.of(credential));

        try (MockedStatic<CredentialMappingHelper> mocked = Mockito.mockStatic(CredentialMappingHelper.class)) {
            mocked.when(() -> CredentialMappingHelper.map(any(Credential.class))).thenReturn(credentialDto);

            List<CredentialDto> result = credentialService.findAll();

            assertEquals(1, result.size());
            assertEquals("selim", result.get(0).getUsername());
            verify(credentialRepository).findAll();
        }
    }

    @Test
    void shouldReturnCredentialById() {
        when(credentialRepository.findById(1)).thenReturn(Optional.of(credential));

        try (MockedStatic<CredentialMappingHelper> mocked = Mockito.mockStatic(CredentialMappingHelper.class)) {
            mocked.when(() -> CredentialMappingHelper.map(any(Credential.class))).thenReturn(credentialDto);

            CredentialDto result = credentialService.findById(1);

            assertEquals("selim", result.getUsername());
            verify(credentialRepository).findById(1);
        }
    }

    @Test
    void shouldThrowCredentialNotFoundExceptionWhenIdMissing() {
        when(credentialRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(CredentialNotFoundException.class, () -> credentialService.findById(99));
    }

    @Test
    void shouldSaveCredentialSuccessfully() {
        when(credentialRepository.save(any(Credential.class))).thenReturn(credential);

        try (MockedStatic<CredentialMappingHelper> mocked = Mockito.mockStatic(CredentialMappingHelper.class)) {
            mocked.when(() -> CredentialMappingHelper.map(any(CredentialDto.class))).thenReturn(credential);
            mocked.when(() -> CredentialMappingHelper.map(any(Credential.class))).thenReturn(credentialDto);

            CredentialDto result = credentialService.save(credentialDto);

            assertEquals("selim", result.getUsername());
            verify(credentialRepository).save(any(Credential.class));
        }
    }

    @Test
    void shouldFindByUsernameSuccessfully() {
        when(credentialRepository.findByUsername("selim")).thenReturn(Optional.of(credential));

        try (MockedStatic<CredentialMappingHelper> mocked = Mockito.mockStatic(CredentialMappingHelper.class)) {
            mocked.when(() -> CredentialMappingHelper.map(any(Credential.class))).thenReturn(credentialDto);

            CredentialDto result = credentialService.findByUsername("selim");

            assertEquals("selim", result.getUsername());
            verify(credentialRepository).findByUsername("selim");
        }
    }

    @Test
    void shouldThrowUserObjectNotFoundExceptionWhenUsernameMissing() {
        when(credentialRepository.findByUsername("missing")).thenReturn(Optional.empty());
        assertThrows(UserObjectNotFoundException.class, () -> credentialService.findByUsername("missing"));
    }

    @Test
    void shouldDeleteByIdSuccessfully() {
        doNothing().when(credentialRepository).deleteById(1);
        credentialService.deleteById(1);
        verify(credentialRepository).deleteById(1);
    }
}
