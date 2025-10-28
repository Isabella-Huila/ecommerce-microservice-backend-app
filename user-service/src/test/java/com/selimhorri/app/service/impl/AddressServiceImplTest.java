package com.selimhorri.app.service.impl;

import com.selimhorri.app.domain.Address;
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.exception.wrapper.AddressNotFoundException;
import com.selimhorri.app.helper.AddressMappingHelper;
import com.selimhorri.app.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {

    private AddressRepository addressRepository;
    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        addressRepository = mock(AddressRepository.class);
        addressService = new AddressServiceImpl(addressRepository);
    }

    @Test
    void shouldReturnAllAddresses() {
        Address a1 = new Address();
        a1.setAddressId(1);
        a1.setCity("Cali");

        Address a2 = new Address();
        a2.setAddressId(2);
        a2.setCity("Bogotá");

        when(addressRepository.findAll()).thenReturn(Arrays.asList(a1, a2));

        try (MockedStatic<AddressMappingHelper> mocked = mockStatic(AddressMappingHelper.class)) {
            AddressDto dto1 = AddressDto.builder().addressId(1).city("Cali").build();
            AddressDto dto2 = AddressDto.builder().addressId(2).city("Bogotá").build();

            mocked.when(() -> AddressMappingHelper.map(a1)).thenReturn(dto1);
            mocked.when(() -> AddressMappingHelper.map(a2)).thenReturn(dto2);

            List<AddressDto> result = addressService.findAll();

            assertEquals(2, result.size());
            assertEquals("Cali", result.get(0).getCity());
            verify(addressRepository, times(1)).findAll();
        }
    }

    @Test
    void shouldFindAddressById() {
        Address address = new Address();
        address.setAddressId(1);
        address.setCity("Cali");

        when(addressRepository.findById(1)).thenReturn(Optional.of(address));

        try (MockedStatic<AddressMappingHelper> mocked = mockStatic(AddressMappingHelper.class)) {
            AddressDto dto = AddressDto.builder().addressId(1).city("Cali").build();
            mocked.when(() -> AddressMappingHelper.map(address)).thenReturn(dto);

            AddressDto result = addressService.findById(1);

            assertNotNull(result);
            assertEquals("Cali", result.getCity());
        }
    }

    @Test
    void shouldThrowExceptionWhenAddressNotFound() {
        when(addressRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(AddressNotFoundException.class, () -> addressService.findById(99));
    }

    @Test
    void shouldSaveAddressSuccessfully() {
        AddressDto dto = AddressDto.builder().addressId(1).city("Medellín").build();
        Address address = new Address();
        address.setAddressId(1);
        address.setCity("Medellín");

        when(addressRepository.save(any(Address.class))).thenReturn(address);

        try (MockedStatic<AddressMappingHelper> mocked = mockStatic(AddressMappingHelper.class)) {
            mocked.when(() -> AddressMappingHelper.map(dto)).thenReturn(address);
            mocked.when(() -> AddressMappingHelper.map(address)).thenReturn(dto);

            AddressDto result = addressService.save(dto);

            assertEquals("Medellín", result.getCity());
            verify(addressRepository, times(1)).save(address);
        }
    }

    @Test
    void shouldDeleteAddressById() {
        doNothing().when(addressRepository).deleteById(1);
        addressService.deleteById(1);
        verify(addressRepository, times(1)).deleteById(1);
    }
}
