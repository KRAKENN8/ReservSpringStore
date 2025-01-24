package ee.ivkhk.NPTV23Store.helpers;

import ee.ivkhk.NPTV23Store.entity.Customer;
import ee.ivkhk.NPTV23Store.entity.Product;
import ee.ivkhk.NPTV23Store.entity.Purchase;
import ee.ivkhk.NPTV23Store.interfaces.AppHelper;
import ee.ivkhk.NPTV23Store.interfaces.Input;
import ee.ivkhk.NPTV23Store.services.CustomerService;
import ee.ivkhk.NPTV23Store.services.ProductService;
import ee.ivkhk.NPTV23Store.services.PurchaseService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PurchaseHelper implements AppHelper<Purchase> {

    private final Input input;
    private final PurchaseService purchaseService;
    private final CustomerService customerService;
    private final ProductService productService;

    public PurchaseHelper(
            Input input,
            PurchaseService purchaseService,
            CustomerService customerService,
            ProductService productService
    ) {
        this.input = input;
        this.purchaseService = purchaseService;
        this.customerService = customerService;
        this.productService = productService;
    }

    @Override
    public Purchase createEntity(Object... args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Недостаточно аргументов для создания Purchase.");
        }
        Long customerId = (Long) args[0];
        Long productId  = (Long) args[1];
        int quantity    = (int) args[2];

        Customer customer = customerService.findCustomerById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Покупатель с ID " + customerId + " не найден.")
                );

        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Товар с ID " + productId + " не найден.")
                );

        Purchase p = new Purchase();
        p.setCustomer(customer);
        p.setProduct(product);
        p.setQuantity(quantity);
        p.setPurchaseDate(LocalDateTime.now());

        return p;
    }

    @Override
    public boolean isValid(Purchase purchase) {
        if (purchase == null) return false;
        if (purchase.getCustomer() == null) return false;
        if (purchase.getProduct() == null) return false;
        return (purchase.getQuantity() > 0);
    }

    @Override
    public List<String> formatEntities(List<Purchase> purchases) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return purchases.stream()
                .map(p -> String.format(
                        "ID: %d, Клиент: %s %s, Продукт: %s, Кол-во: %d, Дата: %s",
                        p.getId(),
                        p.getCustomer().getFirstName(),
                        p.getCustomer().getLastName(),
                        p.getProduct().getName(),
                        p.getQuantity(),
                        p.getPurchaseDate().format(formatter)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void addEntity() {
        try {
            System.out.print("Введите ID покупателя: ");
            Long customerId = input.getLong();

            System.out.print("Введите ID товара: ");
            Long productId = input.getLong();

            System.out.print("Введите количество для покупки: ");
            int quantity = input.getInt();

            Purchase p = createEntity(customerId, productId, quantity);

            if (!isValid(p)) {
                throw new IllegalArgumentException("Данные для покупки некорректны.");
            }

            purchaseService.purchaseProduct(customerId, productId, quantity);

            System.out.println("Покупка успешно завершена!");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void showEntities() {
        try {
            Iterable<Purchase> iterable = purchaseService.getAllPurchases();
            List<Purchase> purchases = new ArrayList<>();
            iterable.forEach(purchases::add);

            List<String> lines = formatEntities(purchases);

            System.out.println("Список всех покупок:");
            lines.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void editEntity() {
        throw new UnsupportedOperationException("Редактирование покупки не предусмотрено.");
    }

    @Override
    public void showIncome() {
        try {
            System.out.print("Введите год (например, 2025): ");
            int year = input.getInt();
            System.out.print("Введите месяц (1-12): ");
            int month = input.getInt();
            System.out.print("Введите день (1-31): ");
            int day = input.getInt();

            LocalDate date = LocalDate.of(year, month, day);
            double dailyIncome = purchaseService.getIncome(date, date);
            System.out.printf("Доход магазина за %s: %.2f%n", date, dailyIncome);

            System.out.print("Введите месяц для расчета дохода (1-12): ");
            month = input.getInt();

            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
            double monthlyIncome = purchaseService.getIncome(startOfMonth, endOfMonth);
            System.out.printf("Доход магазина за месяц %d/%d: %.2f%n", month, year, monthlyIncome);

            System.out.print("Введите год для расчета дохода (например, 2025): ");
            year = input.getInt();

            LocalDate startOfYear = LocalDate.of(year, 1, 1);
            LocalDate endOfYear = LocalDate.of(year, 12, 31);
            double yearlyIncome = purchaseService.getIncome(startOfYear, endOfYear);
            System.out.printf("Доход магазина за год %d: %.2f%n", year, yearlyIncome);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
