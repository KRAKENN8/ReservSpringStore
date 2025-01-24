package ee.ivkhk.NPTV23Store.helpers;

import ee.ivkhk.NPTV23Store.entity.Customer;
import ee.ivkhk.NPTV23Store.interfaces.AppHelper;
import ee.ivkhk.NPTV23Store.interfaces.Input;
import ee.ivkhk.NPTV23Store.services.CustomerService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerHelper implements AppHelper<Customer> {

    private final Input input;
    private final CustomerService customerService;

    public CustomerHelper(Input input, CustomerService customerService) {
        this.input = input;
        this.customerService = customerService;
    }

    @Override
    public Customer createEntity(Object... args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Недостаточно аргументов для создания Customer.");
        }
        String firstName = (String) args[0];
        String lastName = (String) args[1];
        double balance = (double) args[2];

        if (firstName == null || firstName.trim().isEmpty()
                || lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя и фамилия клиента должны быть заполнены.");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Баланс клиента не может быть отрицательным.");
        }
        return new Customer(firstName, lastName, balance);
    }

    @Override
    public boolean isValid(Customer customer) {
        return customer != null
                && customer.getFirstName() != null && !customer.getFirstName().trim().isEmpty()
                && customer.getLastName() != null && !customer.getLastName().trim().isEmpty()
                && customer.getBalance() >= 0;
    }

    @Override
    public List<String> formatEntities(List<Customer> customers) {
        return customers.stream()
                .map(c -> String.format(
                        "ID: %d, Имя: %s %s, Баланс: %.2f",
                        c.getId(),
                        c.getFirstName(),
                        c.getLastName(),
                        c.getBalance()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void addEntity() {
        try {
            System.out.print("Введите имя покупателя: ");
            String firstName = input.getString();

            System.out.print("Введите фамилию покупателя: ");
            String lastName = input.getString();

            System.out.print("Введите баланс покупателя: ");
            double balance = input.getDouble();

            Customer c = createEntity(firstName, lastName, balance);

            if (!isValid(c)) {
                throw new IllegalArgumentException("Данные покупателя некорректны.");
            }

            customerService.addCustomer(c.getFirstName(), c.getLastName(), c.getBalance());
            System.out.println("Покупатель успешно добавлен!");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void showEntities() {
        try {
            Iterable<Customer> allCustomersIterable = customerService.getAllCustomers();

            List<Customer> allCustomersList = new ArrayList<>();
            allCustomersIterable.forEach(allCustomersList::add);

            List<String> lines = formatEntities(allCustomersList);

            System.out.println("Список всех покупателей:");
            lines.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void editEntity() {
        try {
            System.out.print("Введите ID покупателя для редактирования: ");
            Long customerId = input.getLong();

            System.out.print("Введите новое имя покупателя (или оставьте пустым): ");
            String firstName = input.getString();

            System.out.print("Введите новую фамилию покупателя (или оставьте пустым): ");
            String lastName = input.getString();

            System.out.print("Введите новый баланс покупателя (или введите -1 для пропуска): ");
            double balanceInput = input.getDouble();
            Double newBalance = (balanceInput >= 0) ? balanceInput : null;

            customerService.editCustomer(
                    customerId,
                    firstName.isEmpty() ? null : firstName,
                    lastName.isEmpty() ? null : lastName,
                    newBalance
            );
            System.out.println("Покупатель успешно обновлен!");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
