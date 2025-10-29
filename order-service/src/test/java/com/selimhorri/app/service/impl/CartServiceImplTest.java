package com.selimhorri.app.service.impl;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.CartNotFoundException;
import com.selimhorri.app.helper.CartMappingHelper;
import com.selimhorri.app.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    private CartRepository cartRepository;
    private RestTemplate restTemplate;
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        restTemplate = mock(RestTemplate.class);
        cartService = new CartServiceImpl(cartRepository, restTemplate);
    }

    @Test
    void shouldReturnAllCarts() {
        Cart cart1 = mock(Cart.class);
        Cart cart2 = mock(Cart.class);

        UserDto userDto1 = UserDto.builder().userId(1).firstName("Isa").build();
        UserDto userDto2 = UserDto.builder().userId(2).firstName("Carlos").build();

        CartDto dto1 = CartDto.builder()
                .cartId(1)
                .userDto(UserDto.builder().userId(1).build())
                .build();

        CartDto dto2 = CartDto.builder()
                .cartId(2)
                .userDto(UserDto.builder().userId(2).build())
                .build();

        when(cartRepository.findAll()).thenReturn(Arrays.asList(cart1, cart2));
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/1", UserDto.class))
                .thenReturn(userDto1);
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/2", UserDto.class))
                .thenReturn(userDto2);

        try (MockedStatic<CartMappingHelper> mocked = mockStatic(CartMappingHelper.class)) {
            mocked.when(() -> CartMappingHelper.map(cart1)).thenReturn(dto1);
            mocked.when(() -> CartMappingHelper.map(cart2)).thenReturn(dto2);

            List<CartDto> result = cartService.findAll();

            assertEquals(2, result.size());
            assertEquals("Isa", result.get(0).getUserDto().getFirstName());
            assertEquals("Carlos", result.get(1).getUserDto().getFirstName());
            verify(cartRepository, times(1)).findAll();
            verify(restTemplate, times(2)).getForObject(contains(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL), eq(UserDto.class));
        }
    }

    @Test
    void shouldReturnCartById() {
        Cart cart = mock(Cart.class);
        UserDto userDto = UserDto.builder().userId(1).firstName("Isa").build();

        CartDto dto = CartDto.builder()
                .cartId(1)
                .userDto(UserDto.builder().userId(1).build())
                .build();

        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/1", UserDto.class))
                .thenReturn(userDto);

        try (MockedStatic<CartMappingHelper> mocked = mockStatic(CartMappingHelper.class)) {
            mocked.when(() -> CartMappingHelper.map(cart)).thenReturn(dto);

            CartDto result = cartService.findById(1);

            assertNotNull(result);
            assertEquals(1, result.getCartId());
            assertEquals("Isa", result.getUserDto().getFirstName());
            verify(cartRepository, times(1)).findById(1);
        }
    }

    @Test
    void shouldThrowWhenCartNotFoundById() {
        when(cartRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartService.findById(99));
        
        verify(cartRepository, times(1)).findById(99);
    }

    @Test
    void shouldSaveCartSuccessfully() {
        CartDto dto = CartDto.builder()
                .cartId(1)
                .userDto(UserDto.builder().userId(1).build())
                .build();

        Cart cart = mock(Cart.class);

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        try (MockedStatic<CartMappingHelper> mocked = mockStatic(CartMappingHelper.class)) {
            mocked.when(() -> CartMappingHelper.map(dto)).thenReturn(cart);
            mocked.when(() -> CartMappingHelper.map(cart)).thenReturn(dto);

            CartDto result = cartService.save(dto);

            assertEquals(1, result.getCartId());
            verify(cartRepository, times(1)).save(any(Cart.class));
        }
    }

    @Test
    void shouldUpdateCartSuccessfully() {
        CartDto dto = CartDto.builder()
                .cartId(1)
                .userDto(UserDto.builder().userId(1).build())
                .build();

        Cart cart = mock(Cart.class);

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        try (MockedStatic<CartMappingHelper> mocked = mockStatic(CartMappingHelper.class)) {
            mocked.when(() -> CartMappingHelper.map(dto)).thenReturn(cart);
            mocked.when(() -> CartMappingHelper.map(cart)).thenReturn(dto);

            CartDto result = cartService.update(dto);

            assertNotNull(result);
            assertEquals(1, result.getCartId());
            verify(cartRepository, times(1)).save(any(Cart.class));
        }
    }

    @Test
    void shouldUpdateCartWithIdSuccessfully() {
        Cart existingCart = mock(Cart.class);
        UserDto userDto = UserDto.builder().userId(1).firstName("Isa").build();

        CartDto existingDto = CartDto.builder()
                .cartId(1)
                .userDto(UserDto.builder().userId(1).build())
                .build();

        CartDto updatedDto = CartDto.builder()
                .cartId(1)
                .userDto(UserDto.builder().userId(1).build())
                .build();

        when(cartRepository.findById(1)).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(existingCart);
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/1", UserDto.class))
                .thenReturn(userDto);

        try (MockedStatic<CartMappingHelper> mocked = mockStatic(CartMappingHelper.class)) {
            mocked.when(() -> CartMappingHelper.map(existingCart)).thenReturn(existingDto);
            mocked.when(() -> CartMappingHelper.map(existingDto)).thenReturn(existingCart);

            CartDto result = cartService.update(1, updatedDto);

            assertNotNull(result);
            verify(cartRepository, times(1)).findById(1);
            verify(cartRepository, times(1)).save(any(Cart.class));
        }
    }

    @Test
    void shouldDeleteCartById() {
        doNothing().when(cartRepository).deleteById(1);

        cartService.deleteById(1);

        verify(cartRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentCart() {
        when(cartRepository.findById(99)).thenReturn(Optional.empty());

        CartDto dto = CartDto.builder()
                .cartId(99)
                .userDto(UserDto.builder().userId(1).build())
                .build();

        assertThrows(CartNotFoundException.class, () -> cartService.update(99, dto));

        verify(cartRepository, times(1)).findById(99);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void shouldHandleEmptyCartsList() {
        when(cartRepository.findAll()).thenReturn(Arrays.asList());

        List<CartDto> result = cartService.findAll();

        assertEquals(0, result.size());
        verify(cartRepository, times(1)).findAll();
        verify(restTemplate, never()).getForObject(anyString(), any());
    }
}