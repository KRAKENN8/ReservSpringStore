package ee.ivkhk.NPTV23Store.services;

import ee.ivkhk.NPTV23Store.entity.Customer;
import ee.ivkhk.NPTV23Store.entity.Product;
import ee.ivkhk.NPTV23Store.entity.Purchase;
import ee.ivkhk.NPTV23Store.interfaces.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    public void purchaseProduct(Long customerId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть положительным.");
        }

        Customer customer = customerService.findCustomerById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Покупатель с ID " + customerId + " не найден."));

        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Товар с ID " + productId + " не найден."));

        double totalCost = product.getPrice() * quantity;
        if (customer.getBalance() < totalCost) {
            throw new IllegalArgumentException("Недостаточно средств на счёте покупателя.");
        }

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Недостаточно товара на складе.");
        }

        Purchase purchase = new Purchase();
        purchase.setCustomer(customer);
        purchase.setProduct(product);
        purchase.setQuantity(quantity);
        purchase.setPurchaseDate(LocalDateTime.now());

        purchaseRepository.save(purchase);

        customer.setBalance(customer.getBalance() - totalCost);
        customerService.editCustomer(customerId, null, null, customer.getBalance());

        product.setQuantity(product.getQuantity() - quantity);
        productService.editProduct(productId, null, 0, product.getQuantity());
    }

    public Iterable<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public List<Purchase> getAllPurchasesAsList() {
        Iterable<Purchase> iterable = purchaseRepository.findAll();
        List<Purchase> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    public double getIncome(java.time.LocalDate startDate, java.time.LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end   = endDate.plusDays(1).atStartOfDay();
        double totalIncome = 0.0;

        for (Purchase p : purchaseRepository.findAll()) {
            if (!p.getPurchaseDate().isBefore(start) && p.getPurchaseDate().isBefore(end)) {
                double itemCost = p.getProduct().getPrice() * p.getQuantity();
                totalIncome += itemCost;
            }
        }
        return totalIncome;
    }
}
