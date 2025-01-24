package ee.ivkhk.NPTV23Store;

import ee.ivkhk.NPTV23Store.entity.Customer;
import ee.ivkhk.NPTV23Store.entity.Product;
import ee.ivkhk.NPTV23Store.entity.Purchase;
import ee.ivkhk.NPTV23Store.helpers.CustomerHelper;
import ee.ivkhk.NPTV23Store.helpers.ProductHelper;
import ee.ivkhk.NPTV23Store.helpers.PurchaseHelper;
import ee.ivkhk.NPTV23Store.interfaces.AppHelper;
import ee.ivkhk.NPTV23Store.interfaces.Input;
import ee.ivkhk.NPTV23Store.services.CustomerService;
import ee.ivkhk.NPTV23Store.services.ProductService;
import ee.ivkhk.NPTV23Store.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StoreSpringJPA implements CommandLineRunner {

	@Autowired
	private Input input;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ProductService productService;

	@Autowired
	private PurchaseService purchaseService;

	private AppHelper<Product> productHelper;
	private AppHelper<Customer> customerHelper;
	private AppHelper<Purchase> purchaseHelper;

	public static void main(String[] args) {
		SpringApplication.run(StoreSpringJPA.class, args);
	}

	@Override
	public void run(String... args) {
		productHelper = new ProductHelper(input, productService);
		customerHelper = new CustomerHelper(input, customerService);
		purchaseHelper = new PurchaseHelper(input, purchaseService, customerService, productService);

		System.out.println("------ Магазин товаров для домашних животных ------");
		boolean repeat = true;
		while (repeat) {
			try {
				System.out.println("Список задач:");
				System.out.println("0. Выйти из программы");
				System.out.println("1. Добавить товар");
				System.out.println("2. Список товаров");
				System.out.println("3. Редактировать атрибуты товара");
				System.out.println("4. Добавить покупателя");
				System.out.println("5. Список покупателей");
				System.out.println("6. Редактировать атрибуты покупателя");
				System.out.println("7. Купить товар");
				System.out.println("8. Доход магазина за каждый период");

				System.out.print("Введите номер задачи: ");
				int task = input.getInt();
				switch (task) {
					case 0:
						repeat = false;
						break;
					case 1:
						productHelper.addEntity();
						break;
					case 2:
						productHelper.showEntities();
						break;
					case 3:
						productHelper.editEntity();
						break;
					case 4:
						customerHelper.addEntity();
						break;
					case 5:
						customerHelper.showEntities();
						break;
					case 6:
						customerHelper.editEntity();
						break;
					case 7:
						purchaseHelper.addEntity();
						break;
					case 8:
						purchaseHelper.showIncome();
						break;
					default:
						System.out.println("Выберите задачу из списка!");
				}
				System.out.println("----------------------------------------");
			} catch (Exception e) {
				System.out.println("Ошибка: " + e.getMessage());
				e.printStackTrace();
			}
		}
		System.out.println("До свидания :)");
	}
}