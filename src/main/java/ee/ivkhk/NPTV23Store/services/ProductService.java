package ee.ivkhk.NPTV23Store.services;

import ee.ivkhk.NPTV23Store.entity.Product;
import ee.ivkhk.NPTV23Store.interfaces.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void addProduct(String name, double price, int quantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название продукта не может быть пустым.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Цена продукта должна быть положительной.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Количество продукта не может быть отрицательным.");
        }

        Product product = new Product(name, price, quantity);
        productRepository.save(product);
    }

    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    public void editProduct(Long productId, String newName, double newPrice, int newQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Нет продукта с таким ID: " + productId));

        if (newName != null && !newName.trim().isEmpty()) {
            product.setName(newName);
        }
        if (newPrice > 0) {
            product.setPrice(newPrice);
        }
        if (newQuantity >= 0) {
            product.setQuantity(newQuantity);
        }

        productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}
