package ee.ivkhk.NPTV23Store.helpers;

import ee.ivkhk.NPTV23Store.entity.Product;
import ee.ivkhk.NPTV23Store.interfaces.AppHelper;
import ee.ivkhk.NPTV23Store.interfaces.Input;
import ee.ivkhk.NPTV23Store.services.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductHelper implements AppHelper<Product> {

    private final Input input;
    private final ProductService productService;

    public ProductHelper(Input input, ProductService productService) {
        this.input = input;
        this.productService = productService;
    }

    @Override
    public Product createEntity(Object... args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Недостаточно аргументов для создания Product.");
        }
        String name = (String) args[0];
        double price = (double) args[1];
        int quantity = (int) args[2];

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название продукта должно быть заполнено.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Цена продукта должна быть положительной.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Количество продукта не может быть отрицательным.");
        }

        return new Product(name, price, quantity);
    }

    @Override
    public boolean isValid(Product product) {
        return product != null
                && product.getName() != null && !product.getName().trim().isEmpty()
                && product.getPrice() > 0
                && product.getQuantity() >= 0;
    }

    @Override
    public List<String> formatEntities(List<Product> products) {
        return products.stream()
                .map(product -> String.format(
                        "ID: %d, Название: %s, Цена: %.2f, Количество: %d",
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void addEntity() {
        try {
            System.out.print("Введите название товара: ");
            String name = input.getString();
            System.out.print("Введите цену товара: ");
            double price = input.getDouble();
            System.out.print("Введите количество товара: ");
            int quantity = input.getInt();

            Product p = createEntity(name, price, quantity);
            if (!isValid(p)) {
                throw new IllegalArgumentException("Данные товара некорректны.");
            }

            productService.addProduct(p.getName(), p.getPrice(), p.getQuantity());
            System.out.println("Товар успешно добавлен!");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void showEntities() {
        try {
            Iterable<Product> allProductsIterable = productService.getAllProducts();
            List<Product> allProductsList = new ArrayList<>();
            allProductsIterable.forEach(allProductsList::add);

            List<String> lines = formatEntities(allProductsList);
            System.out.println("Список всех товаров:");
            lines.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void editEntity() {
        try {
            System.out.print("Введите ID товара для редактирования: ");
            Long productId = input.getLong();

            System.out.print("Введите новое название товара (или оставьте пустым): ");
            String name = input.getString();

            System.out.print("Введите новую цену товара (или введите 0 для пропуска): ");
            double price = input.getDouble();

            System.out.print("Введите новое количество товара (или введите -1 для пропуска): ");
            int quantity = input.getInt();

            productService.editProduct(
                    productId,
                    name.isEmpty() ? null : name,
                    price,
                    quantity
            );
            System.out.println("Товар успешно обновлен!");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
