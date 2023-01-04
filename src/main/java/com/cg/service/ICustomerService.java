package com.cg.service;

import com.cg.model.Customer;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {

    List<Customer> findAll();

    Optional<Customer> findById(Long id);

    Customer save(Customer customer);

    void delete(Long id);

    void delete(Customer customer);
}
