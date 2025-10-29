package com.selimhorri.app.service.impl;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.domain.Favourite;
import com.selimhorri.app.domain.id.FavouriteId;
import com.selimhorri.app.dto.FavouriteDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.FavouriteNotFoundException;
import com.selimhorri.app.helper.FavouriteMappingHelper;
import com.selimhorri.app.repository.FavouriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavouriteServiceImplTest {

    private FavouriteRepository favouriteRepository;
    private RestTemplate restTemplate;
    private FavouriteServiceImpl favouriteService;

    @BeforeEach
    void setUp() {
        favouriteRepository = mock(FavouriteRepository.class);
        restTemplate = mock(RestTemplate.class);
        favouriteService = new FavouriteServiceImpl(favouriteRepository, restTemplate);
    }

    @Test
    void shouldReturnAllFavourites() {
        // Mock favourites - no necesitamos setters, solo usar el mock
        Favourite favourite1 = mock(Favourite.class);
        Favourite favourite2 = mock(Favourite.class);

        UserDto userDto1 = UserDto.builder().userId(1).firstName("Isa").build();
        UserDto userDto2 = UserDto.builder().userId(2).firstName("Carlos").build();
        ProductDto productDto1 = ProductDto.builder().productId(101).productTitle("Product 1").build();
        ProductDto productDto2 = ProductDto.builder().productId(102).productTitle("Product 2").build();

        when(favouriteRepository.findAll()).thenReturn(Arrays.asList(favourite1, favourite2));
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/1", UserDto.class))
                .thenReturn(userDto1);
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/2", UserDto.class))
                .thenReturn(userDto2);
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/101", ProductDto.class))
                .thenReturn(productDto1);
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/102", ProductDto.class))
                .thenReturn(productDto2);

        FavouriteDto dto1 = FavouriteDto.builder()
                .userId(1)
                .productId(101)
                .build();

        FavouriteDto dto2 = FavouriteDto.builder()
                .userId(2)
                .productId(102)
                .build();

        try (MockedStatic<FavouriteMappingHelper> mocked = mockStatic(FavouriteMappingHelper.class)) {
            mocked.when(() -> FavouriteMappingHelper.map(favourite1)).thenReturn(dto1);
            mocked.when(() -> FavouriteMappingHelper.map(favourite2)).thenReturn(dto2);

            List<FavouriteDto> result = favouriteService.findAll();

            assertEquals(2, result.size());
            assertEquals(1, result.get(0).getUserId());
            assertEquals(101, result.get(0).getProductId());
            assertEquals("Isa", result.get(0).getUserDto().getFirstName());
            assertEquals("Product 1", result.get(0).getProductDto().getProductTitle());
            verify(favouriteRepository, times(1)).findAll();
            verify(restTemplate, times(2)).getForObject(contains(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL), eq(UserDto.class));
            verify(restTemplate, times(2)).getForObject(contains(AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL), eq(ProductDto.class));
        }
    }

    @Test
    void shouldReturnFavouriteById() {
        FavouriteId favouriteId = new FavouriteId(1, 101,null);
        Favourite favourite = mock(Favourite.class);

        UserDto userDto = UserDto.builder().userId(1).firstName("Isa").build();
        ProductDto productDto = ProductDto.builder().productId(101).productTitle("Product 1").build();

        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.of(favourite));
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/1", UserDto.class))
                .thenReturn(userDto);
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/101", ProductDto.class))
                .thenReturn(productDto);

        FavouriteDto dto = FavouriteDto.builder()
                .userId(1)
                .productId(101)
                .build();

        try (MockedStatic<FavouriteMappingHelper> mocked = mockStatic(FavouriteMappingHelper.class)) {
            mocked.when(() -> FavouriteMappingHelper.map(favourite)).thenReturn(dto);

            FavouriteDto result = favouriteService.findById(favouriteId);

            assertNotNull(result);
            assertEquals(1, result.getUserId());
            assertEquals(101, result.getProductId());
            assertEquals("Isa", result.getUserDto().getFirstName());
            assertEquals("Product 1", result.getProductDto().getProductTitle());
            verify(favouriteRepository, times(1)).findById(favouriteId);
        }
    }

    @Test
    void shouldThrowWhenFavouriteNotFoundById() {
        FavouriteId favouriteId = new FavouriteId(99, 999,null);
        
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.empty());

        assertThrows(FavouriteNotFoundException.class, 
            () -> favouriteService.findById(favouriteId));
        
        verify(favouriteRepository, times(1)).findById(favouriteId);
    }

    @Test
    void shouldSaveFavouriteSuccessfully() {
        FavouriteDto dto = FavouriteDto.builder()
                .userId(1)
                .productId(101)
                .build();

        Favourite favourite = mock(Favourite.class);

        when(favouriteRepository.save(any(Favourite.class))).thenReturn(favourite);

        try (MockedStatic<FavouriteMappingHelper> mocked = mockStatic(FavouriteMappingHelper.class)) {
            mocked.when(() -> FavouriteMappingHelper.map(dto)).thenReturn(favourite);
            mocked.when(() -> FavouriteMappingHelper.map(favourite)).thenReturn(dto);

            FavouriteDto result = favouriteService.save(dto);

            assertEquals(1, result.getUserId());
            assertEquals(101, result.getProductId());
            verify(favouriteRepository, times(1)).save(any(Favourite.class));
        }
    }

    @Test
    void shouldUpdateFavouriteSuccessfully() {
        FavouriteDto dto = FavouriteDto.builder()
                .userId(1)
                .productId(101)
                .likeDate(java.time.LocalDateTime.now())
                .build();

        Favourite favourite = mock(Favourite.class);

        when(favouriteRepository.save(any(Favourite.class))).thenReturn(favourite);

        try (MockedStatic<FavouriteMappingHelper> mocked = mockStatic(FavouriteMappingHelper.class)) {
            mocked.when(() -> FavouriteMappingHelper.map(dto)).thenReturn(favourite);
            mocked.when(() -> FavouriteMappingHelper.map(favourite)).thenReturn(dto);

            FavouriteDto result = favouriteService.update(dto);

            assertNotNull(result);
            assertEquals(1, result.getUserId());
            assertEquals(101, result.getProductId());
            verify(favouriteRepository, times(1)).save(any(Favourite.class));
        }
    }

    @Test
    void shouldDeleteFavouriteById() {
        FavouriteId favouriteId = new FavouriteId(1, 101,null);
        
        doNothing().when(favouriteRepository).deleteById(favouriteId);

        favouriteService.deleteById(favouriteId);

        verify(favouriteRepository, times(1)).deleteById(favouriteId);
    }

    @Test
    void shouldHandleEmptyFavouritesList() {
        when(favouriteRepository.findAll()).thenReturn(Arrays.asList());

        List<FavouriteDto> result = favouriteService.findAll();

        assertEquals(0, result.size());
        verify(favouriteRepository, times(1)).findAll();
        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    void shouldFetchUserAndProductDataForEachFavourite() {
        Favourite favourite = mock(Favourite.class);

        UserDto userDto = UserDto.builder().userId(5).firstName("Maria").build();
        ProductDto productDto = ProductDto.builder().productId(205).productTitle("Special Product").build();

        when(favouriteRepository.findAll()).thenReturn(Arrays.asList(favourite));
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/5", UserDto.class))
                .thenReturn(userDto);
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/205", ProductDto.class))
                .thenReturn(productDto);

        FavouriteDto dto = FavouriteDto.builder()
                .userId(5)
                .productId(205)
                .build();

        try (MockedStatic<FavouriteMappingHelper> mocked = mockStatic(FavouriteMappingHelper.class)) {
            mocked.when(() -> FavouriteMappingHelper.map(favourite)).thenReturn(dto);

            List<FavouriteDto> result = favouriteService.findAll();

            assertEquals(1, result.size());
            assertNotNull(result.get(0).getUserDto());
            assertNotNull(result.get(0).getProductDto());
            assertEquals("Maria", result.get(0).getUserDto().getFirstName());
            assertEquals("Special Product", result.get(0).getProductDto().getProductTitle());
        }
    }
}