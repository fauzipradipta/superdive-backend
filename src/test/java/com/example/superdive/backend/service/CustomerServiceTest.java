package com.example.superdive.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.superdive.backend.dto.CustomerDTO;
import com.example.superdive.backend.dto.DivingDataDTO;
import com.example.superdive.backend.dto.ReferenceDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.entity.DivingData;
import com.example.superdive.backend.entity.Reference;
import com.example.superdive.backend.entity.User;
import com.example.superdive.backend.exception.CustomerAlreadyExistException;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.repository.CustomerRepository;
import com.example.superdive.backend.security.AuthenticatedUserProvider;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private CustomerService customerService;


    private CustomerDTO validRequest() {
        CustomerDTO request = new CustomerDTO();
        request.setName("John Doe");
        request.setPhoneNum("1234567890");
        request.setDob(Date.valueOf("1990-01-01"));
        request.setDiver(true);
        request.setDivingData(Collections.emptyList());
        request.setReference(Collections.emptyList());
        return request;
    }



    @Test
    void add_customer_shouldReturnCustomer() throws CustomerAlreadyExistException {
        CustomerDTO request = validRequest();

        User currentUser = new User();
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(currentUser);
        when(customerRepository.findByName("John Doe")).thenReturn(Collections.emptyList());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer saved = customerService.createCustomer(request);

        assertNotNull(saved);
        assertEquals("John Doe", saved.getName());
        assertEquals("1234567890", saved.getPhoneNum());
        assertEquals(Date.valueOf("1990-01-01"), saved.getDob());
        assertTrue(saved.diver());
        assertSame(currentUser, saved.getUser());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void add_customer_shouldMapDiverFlagFalse() throws CustomerAlreadyExistException {
        CustomerDTO request = validRequest();
        request.setDiver(false);

        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());
        when(customerRepository.findByName("John Doe")).thenReturn(Collections.emptyList());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer saved = customerService.createCustomer(request);

        assertFalse(saved.diver());
    }

    @Test
    void add_customer_withDivingData() throws CustomerAlreadyExistException {
        CustomerDTO request = validRequest();

        DivingDataDTO divingDTO = new DivingDataDTO();
        divingDTO.setAgencyName("PADI");
        divingDTO.setLevel("Open Water");
        divingDTO.setReference(true);
        request.setDivingData(Collections.singletonList(divingDTO));

        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());
        when(customerRepository.findByName("John Doe")).thenReturn(Collections.emptyList());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer saved = customerService.createCustomer(request);

        assertEquals(1, saved.getDivingData().size());
        DivingData divingData = saved.getDivingData().get(0);
        assertEquals("PADI", divingData.getAgencyName());
        assertEquals("Open Water", divingData.getLevel());
        assertTrue(divingData.getReference());
        // the back-reference must point at the customer being saved
        assertSame(saved, divingData.getCustomer());
    }

    @Test
    void add_customer_withReferences() throws CustomerAlreadyExistException {
        CustomerDTO request = validRequest();
        request.setReference(Collections.singletonList(new ReferenceDTO("Jane Ref", "0899999999")));

        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());
        when(customerRepository.findByName("John Doe")).thenReturn(Collections.emptyList());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer saved = customerService.createCustomer(request);

        assertEquals(1, saved.getReferences().size());
        Reference reference = saved.getReferences().get(0);
        assertEquals("Jane Ref", reference.getReferenceName());
        assertEquals("0899999999", reference.getReferencePhoneNum());
        assertSame(saved, reference.getCustomer());
    }

    @Test
    void add_customer_allowsNullReferenceList() throws CustomerAlreadyExistException {
        CustomerDTO request = validRequest();
        request.setReference(null);

        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());
        when(customerRepository.findByName("John Doe")).thenReturn(Collections.emptyList());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer saved = customerService.createCustomer(request);

        assertTrue(saved.getReferences().isEmpty());
    }

    @Test
    void add_customer_throwsWhenNameAlreadyExists() {
        CustomerDTO request = validRequest();

        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());
        when(customerRepository.findByName("John Doe")).thenReturn(Collections.singletonList(new Customer()));

        CustomerAlreadyExistException thrown = assertThrows(CustomerAlreadyExistException.class,
                () -> customerService.createCustomer(request));

        assertTrue(thrown.getMessage().contains("John Doe"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // ---- findCustomerByNameAndPhoneNum ----

    @Test
    void findCustomerByNameAndPhoneNum_returnsFirstMatch() throws MessageErrorException {
        Customer first = new Customer();
        first.setName("John Doe");
        Customer second = new Customer();

        when(customerRepository.findByNameAndPhoneNum("John Doe", "1234567890"))
                .thenReturn(Arrays.asList(first, second));

        assertSame(first, customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890"));
    }

    @Test
    void findCustomerByNameAndPhoneNum_throwsWhenNoMatch() {
        when(customerRepository.findByNameAndPhoneNum("Nobody", "0000"))
                .thenReturn(Collections.emptyList());

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> customerService.findCustomerByNameAndPhoneNum("Nobody", "0000"));

        assertTrue(thrown.getMessage().contains("Nobody"));
        assertTrue(thrown.getMessage().contains("0000"));
    }

    // ---- getCustomerById ----

    @Test
    void getCustomerById_returnsCustomer() {
        Customer customer = new Customer();
        customer.setId(7L);
        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));

        assertSame(customer, customerService.getCustomerById(7L));
    }

    @Test
    void getCustomerById_throwsWhenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> customerService.getCustomerById(99L));
        assertEquals("Customer not found", thrown.getMessage());
    }

    // ---- read-through methods ----

    @Test
    void getAllCustomerByPagination_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Customer> page = new PageImpl<>(Collections.singletonList(new Customer()));
        when(customerRepository.findAll(pageable)).thenReturn(page);

        assertSame(page, customerService.getAllCustomerByPagination(pageable));
    }

    @Test
    void getAllCustomer_delegatesToRepository() {
        List<Customer> customers = Arrays.asList(new Customer(), new Customer());
        when(customerRepository.findAll()).thenReturn(customers);

        assertEquals(customers, customerService.getAllCustomer());
    }

    @Test
    void deleteCustomer_delegatesToRepository() {
        customerService.deleteCustomer(5L);

        verify(customerRepository).deleteById(5L);
    }

    // ---- updateCustomer ----

    @Test
    void updateCustomer_overwritesScalarFields() throws MessageErrorException {
        Customer existing = new Customer();
        existing.setId(3L);
        existing.setName("Old Name");
        existing.setPhoneNum("0000000000");

        CustomerDTO request = new CustomerDTO();
        request.setName("New Name");
        request.setPhoneNum("1111111111");
        request.setDob(Date.valueOf("1985-05-05"));
        request.setDiver(true);

        when(customerRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updated = customerService.updateCustomer(3L, request);

        assertEquals("New Name", updated.getName());
        assertEquals("1111111111", updated.getPhoneNum());
        assertEquals(Date.valueOf("1985-05-05"), updated.getDob());
        assertTrue(updated.diver());
    }

    @Test
    void updateCustomer_replacesChildCollectionsInPlace() throws MessageErrorException {
        Customer existing = new Customer();
        existing.setId(3L);

        DivingData staleDiving = new DivingData();
        staleDiving.setAgencyName("SSI");
        existing.getDivingData().add(staleDiving);

        Reference staleReference = new Reference();
        staleReference.setReferenceName("Stale");
        existing.getReferences().add(staleReference);

        // hold on to the original list instances: updateCustomer must mutate, not replace,
        // otherwise Hibernate orphan removal breaks
        List<DivingData> divingList = existing.getDivingData();
        List<Reference> referenceList = existing.getReferences();

        DivingDataDTO divingDTO = new DivingDataDTO();
        divingDTO.setAgencyName("PADI");
        divingDTO.setLevel("Rescue Diver");
        divingDTO.setReference(false);

        CustomerDTO request = new CustomerDTO();
        request.setName("New Name");
        request.setDivingData(Collections.singletonList(divingDTO));
        request.setReference(Collections.singletonList(new ReferenceDTO("Fresh Ref", "0812345678")));

        when(customerRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updated = customerService.updateCustomer(3L, request);

        assertSame(divingList, updated.getDivingData());
        assertSame(referenceList, updated.getReferences());
        assertEquals(1, updated.getDivingData().size());
        assertEquals("PADI", updated.getDivingData().get(0).getAgencyName());
        assertSame(existing, updated.getDivingData().get(0).getCustomer());
        assertEquals(1, updated.getReferences().size());
        assertEquals("Fresh Ref", updated.getReferences().get(0).getReferenceName());
    }

    @Test
    void updateCustomer_clearsCollectionsWhenDtoHasNone() throws MessageErrorException {
        Customer existing = new Customer();
        existing.getDivingData().add(new DivingData());
        existing.getReferences().add(new Reference());

        CustomerDTO request = new CustomerDTO();
        request.setName("New Name");
        // divingData and reference left null

        when(customerRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updated = customerService.updateCustomer(3L, request);

        assertTrue(updated.getDivingData().isEmpty());
        assertTrue(updated.getReferences().isEmpty());
    }

    @Test
    void updateCustomer_throwsWhenCustomerNotFound() {
        when(customerRepository.findById(404L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> customerService.updateCustomer(404L, new CustomerDTO()));

        assertEquals("Customer not found", thrown.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_savesTheManagedEntity() throws MessageErrorException {
        Customer existing = new Customer();
        existing.setId(3L);

        CustomerDTO request = new CustomerDTO();
        request.setName("New Name");

        when(customerRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        customerService.updateCustomer(3L, request);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        assertSame(existing, captor.getValue());
    }
}
