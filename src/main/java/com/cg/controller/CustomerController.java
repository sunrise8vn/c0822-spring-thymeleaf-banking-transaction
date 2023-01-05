package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.service.customer.ICustomerService;
import com.cg.service.deposit.IDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IDepositService depositService;

    @GetMapping
    public String showListPage(Model model) {

        List<Customer> customers = customerService.findAll();

        model.addAttribute("customers", customers);

        return "customer/list";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        model.addAttribute("customer", new Customer());

        return "customer/create";
    }

    @GetMapping("/edit/{id}")
    public String showEditPage(Model model, @PathVariable Long id) {

        Optional<Customer> customerOptional = customerService.findById(id);

        if (!customerOptional.isPresent()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "Customer ID invalid");
        }
        else {
            Customer customer = customerOptional.get();
            model.addAttribute("error", false);
            model.addAttribute("customer", customer);
        }

        return "customer/edit";
    }

    @GetMapping("/deposit/{customerId}")
    public String showDepositPage(@PathVariable Long customerId, Model model) {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "Customer ID invalid");
        }
        else {
            Customer customer = customerOptional.get();
            model.addAttribute("error", null);
            model.addAttribute("customer", customer);
            model.addAttribute("deposit", new Deposit());
        }

        return "customer/deposit";
    }

    @PostMapping("/create")
    public String create(Customer customer) {

        customer.setBalance(BigDecimal.ZERO);
        customerService.save(customer);

        return "redirect:/customers";
    }

    @PostMapping("/edit/{customerId}")
    public String update(Customer customer, @PathVariable Long customerId) {

        customer.setId(customerId);

        customerService.save(customer);

        return "redirect:/customers";
    }

    @PostMapping("/deposit/{customerId}")
    public String deposit(@PathVariable Long customerId, Deposit deposit, Model model) {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "Customer ID invalid");
        }
        else {
            Customer customer = customerOptional.get();
            BigDecimal currentBalance = customer.getBalance();
            BigDecimal transactionAmount = deposit.getTransactionAmount();
            BigDecimal newBalance = currentBalance.add(transactionAmount);
            customer.setBalance(newBalance);

            customerService.deposit(customer, deposit);

            model.addAttribute("error", false);
            model.addAttribute("customer", customer);
            model.addAttribute("deposit", new Deposit());
        }

        return "customer/deposit";
    }
}
