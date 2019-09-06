package com.codegym.service;

import com.codegym.model.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerServiceImpl implements CustomerService {
    private Map<Integer, Customer> customers;

    {
        customers = new HashMap<>();
        customers.put(1, new Customer(1, "John", "john@codegym.vn", "Hanoi","1-1494467429_660x0.jpg"));
        customers.put(2, new Customer(2, "Bill", "bill@codegym.vn", "Danang","ngoc-trinh-7-6846-1552530069-9335-1552791273.jpg"));
        customers.put(3, new Customer(3, "Bill", "alex@codegym.vn", "Saigon","a"));
        customers.put(4, new Customer(4, "Adam", "adam@codegym.vn", "Beijin","a"));
        customers.put(5, new Customer(5, "Sophia", "sophia@codegym.vn", "Miami","a"));
        customers.put(6, new Customer(6, "Rose", "rose@codegym.vn", "Newyork","a"));
    }

    @Override
    public List<Customer> findAll() {

        return new ArrayList<>(customers.values());
    }

    @Override
    public void save(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    @Override
    public Customer findById(int id) {
        return customers.get(id);

    }

    @Override
    public void update(int id, Customer customer) {
        customers.put(id, customer);
    }

    @Override
    public void remove(int id) {
        customers.remove(id);
    }
}
