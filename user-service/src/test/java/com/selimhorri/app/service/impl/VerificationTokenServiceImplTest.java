package com.selimhorri.app.service.impl;

import com.selimhorri.app.domain.VerificationToken;
import com.selimhorri.app.dto.VerificationTokenDto;
import com.selimhorri.app.exception.wrapper.VerificationTokenNotFoundException;
import com.selimhorri.app.helper.VerificationTokenMappingHelper;
import com.selimhorri.app.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationTokenServiceImplTest {

    private VerificationTokenRepository verificationTokenRepository;
    private VerificationTokenServiceImpl verificationTokenService;

    @BeforeEach
    void setUp() {
        verificationTokenRepository = mock(VerificationTokenRepository.class);
        verificationTokenService = new VerificationTokenServiceImpl(verificationTokenRepository);
    }

    @Test
    void shouldReturnAllVerificationTokens() {
        VerificationToken token1 = new VerificationToken();
        token1.setVerificationTokenId(1);
        token1.setToken("token123");

        VerificationToken token2 = new VerificationToken();
        token2.setVerificationTokenId(2);
        token2.setToken("token456");

        when(verificationTokenRepository.findAll()).thenReturn(Arrays.asList(token1, token2));

        try (MockedStatic<VerificationTokenMappingHelper> mocked = mockStatic(VerificationTokenMappingHelper.class)) {
            VerificationTokenDto dto1 = VerificationTokenDto.builder()
                    .verificationTokenId(1)
                    .token("token123")
                    .build();

            VerificationTokenDto dto2 = VerificationTokenDto.builder()
                    .verificationTokenId(2)
                    .token("token456")
                    .build();

            mocked.when(() -> VerificationTokenMappingHelper.map(token1)).thenReturn(dto1);
            mocked.when(() -> VerificationTokenMappingHelper.map(token2)).thenReturn(dto2);

            List<VerificationTokenDto> result = verificationTokenService.findAll();

            assertEquals(2, result.size());
            assertEquals("token123", result.get(0).getToken());
            assertEquals("token456", result.get(1).getToken());
            verify(verificationTokenRepository, times(1)).findAll();
        }
    }

    @Test
    void shouldReturnVerificationTokenById() {
        VerificationToken token = new VerificationToken();
        token.setVerificationTokenId(1);
        token.setToken("token123");

        when(verificationTokenRepository.findById(1)).thenReturn(Optional.of(token));

        try (MockedStatic<VerificationTokenMappingHelper> mocked = mockStatic(VerificationTokenMappingHelper.class)) {
            VerificationTokenDto dto = VerificationTokenDto.builder()
                    .verificationTokenId(1)
                    .token("token123")
                    .build();

            mocked.when(() -> VerificationTokenMappingHelper.map(token)).thenReturn(dto);

            VerificationTokenDto result = verificationTokenService.findById(1);

            assertNotNull(result);
            assertEquals("token123", result.getToken());
            verify(verificationTokenRepository, times(1)).findById(1);
        }
    }

    @Test
    void shouldThrowWhenVerificationTokenNotFoundById() {
        when(verificationTokenRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(VerificationTokenNotFoundException.class, 
            () -> verificationTokenService.findById(99));
        
        verify(verificationTokenRepository, times(1)).findById(99);
    }

    @Test
    void shouldSaveVerificationTokenSuccessfully() {
        VerificationTokenDto dto = VerificationTokenDto.builder()
                .verificationTokenId(1)
                .token("token123")
                .build();

        VerificationToken token = new VerificationToken();
        token.setVerificationTokenId(1);
        token.setToken("token123");

        when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(token);

        try (MockedStatic<VerificationTokenMappingHelper> mocked = mockStatic(VerificationTokenMappingHelper.class)) {
            mocked.when(() -> VerificationTokenMappingHelper.map(dto)).thenReturn(token);
            mocked.when(() -> VerificationTokenMappingHelper.map(token)).thenReturn(dto);

            VerificationTokenDto result = verificationTokenService.save(dto);

            assertEquals("token123", result.getToken());
            verify(verificationTokenRepository, times(1)).save(token);
        }
    }

    @Test
    void shouldUpdateVerificationTokenSuccessfully() {
        VerificationTokenDto dto = VerificationTokenDto.builder()
                .verificationTokenId(1)
                .token("updatedToken")
                .build();

        VerificationToken token = new VerificationToken();
        token.setVerificationTokenId(1);
        token.setToken("updatedToken");

        when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(token);

        try (MockedStatic<VerificationTokenMappingHelper> mocked = mockStatic(VerificationTokenMappingHelper.class)) {
            mocked.when(() -> VerificationTokenMappingHelper.map(dto)).thenReturn(token);
            mocked.when(() -> VerificationTokenMappingHelper.map(token)).thenReturn(dto);

            VerificationTokenDto result = verificationTokenService.update(dto);

            assertEquals("updatedToken", result.getToken());
            verify(verificationTokenRepository, times(1)).save(token);
        }
    }

    @Test
    void shouldUpdateVerificationTokenWithIdSuccessfully() {
        VerificationToken existingToken = new VerificationToken();
        existingToken.setVerificationTokenId(1);
        existingToken.setToken("existingToken");

        VerificationTokenDto existingDto = VerificationTokenDto.builder()
                .verificationTokenId(1)
                .token("existingToken")
                .build();

        VerificationTokenDto updatedDto = VerificationTokenDto.builder()
                .verificationTokenId(1)
                .token("updatedToken")
                .build();

        when(verificationTokenRepository.findById(1)).thenReturn(Optional.of(existingToken));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(existingToken);

        try (MockedStatic<VerificationTokenMappingHelper> mocked = mockStatic(VerificationTokenMappingHelper.class)) {
            mocked.when(() -> VerificationTokenMappingHelper.map(existingToken)).thenReturn(existingDto);
            mocked.when(() -> VerificationTokenMappingHelper.map(existingDto)).thenReturn(existingToken);

            VerificationTokenDto result = verificationTokenService.update(1, updatedDto);

            assertNotNull(result);
            verify(verificationTokenRepository, times(1)).findById(1);
            verify(verificationTokenRepository, times(1)).save(existingToken);
        }
    }

    @Test
    void shouldDeleteVerificationTokenById() {
        doNothing().when(verificationTokenRepository).deleteById(1);

        verificationTokenService.deleteById(1);

        verify(verificationTokenRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentToken() {
        when(verificationTokenRepository.findById(99)).thenReturn(Optional.empty());

        VerificationTokenDto dto = VerificationTokenDto.builder()
                .verificationTokenId(99)
                .token("token")
                .build();

        assertThrows(VerificationTokenNotFoundException.class,
            () -> verificationTokenService.update(99, dto));

        verify(verificationTokenRepository, times(1)).findById(99);
        verify(verificationTokenRepository, never()).save(any());
    }
}