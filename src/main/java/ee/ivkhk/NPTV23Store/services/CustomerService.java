package ee.ivkhk.NPTV23Store.services;

import ee.ivkhk.NPTV23Store.entity.Customer;
import ee.ivkhk.NPTV23Store.interfaces.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public void addCustomer(String firstName, String lastName, double balance) {
        Customer newCustomer = new Customer(firstName, lastName, balance);
        customerRepository.save(newCustomer);
    }

    public Iterable<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public void editCustomer(Long customerId, String firstName, String lastName, Double balance) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Нет покупателя с таким ID: " + customerId));

        if (firstName != null && !firstName.trim().isEmpty()) {
            customer.setFirstName(firstName);
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            customer.setLastName(lastName);
        }
        if (balance != null && balance >= 0) {
            customer.setBalance(balance);
        }
        customerRepository.save(customer);
    }
}
