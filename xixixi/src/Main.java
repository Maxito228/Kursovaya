import java.util.*;
import java.util.stream.Collectors;

class Product {
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("%-15s | %6.2f тг | %3d шт", name, price, quantity);
    }
}

class Order {
    private static int counter = 1;
    private int id;
    private User user;
    private List<Product> items = new ArrayList<>();
    private String status = "Новый";

    public Order(User user) {
        this.user = user;
        this.id = counter++;
    }

    public int getId() { return id; }
    public User getUser() { return user; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<Product> getItems() { return items; }

    public void addProduct(Product p) {
        items.add(p);
    }

    public double getTotal() {
        return items.stream().mapToDouble(Product::getPrice).sum();
    }

    @Override
    public String toString() {
        return String.format("Заказ #%d от %s | Статус: %s | Сумма: %.2f тг", id, user.getLogin(), status, getTotal());
    }
}

class User {
    protected String name;
    protected String login;
    protected String password;
    protected List<Order> orders = new ArrayList<>();

    public User(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public String getName() { return name; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public List<Order> getOrders() { return orders; }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public boolean isAdmin() {
        return false;
    }
}

class Admin extends User {
    public Admin(String name, String login, String password) {
        super(name, login, password);
    }

    @Override
    public boolean isAdmin() {
        return true;
    }
}

class WarehouseSystem {
    private List<User> users = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public WarehouseSystem() {
        users.add(new Admin("Admin", "admin", "admin"));

        products.add(new Product("Laptop", 300000, 20));
        products.add(new Product("Mouse", 10000, 70));
        products.add(new Product("Phone", 230000, 40));
    }

    public void register() {
        System.out.print("Имя: ");
        String name = scanner.nextLine();
        System.out.print("Логин: ");
        String login = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        users.add(new User(name, login, password));
        System.out.println("Регистрация прошла успешно!");
    }

    public User login() {
        System.out.print("Логин: ");
        String login = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        for (User u : users) {
            if (u.getLogin().equals(login) && u.getPassword().equals(password)) {
                System.out.println("Успешный вход!");
                return u;
            }
        }
        System.out.println("ERROR: Неверный логин или пароль.");
        return null;
    }

    public void search() {
        System.out.print("Введите название товара: ");
        String query = scanner.nextLine().toLowerCase();
        List<Product> found = products.stream()
                .filter(p -> p.getName().toLowerCase().contains(query))
                .collect(Collectors.toList());
        if (found.isEmpty()) {
            System.out.println("Товар не найден.");
        } else {
            found.forEach(System.out::println);
        }
    }

    public void userMenu(User user) {
        while (true) {
            System.out.println("\n--- USER MENU ---");
            System.out.println("1. Поиск товаров");
            System.out.println("2. Создать заказ");
            System.out.println("3. Мои заказы");
            System.out.println("0. Выход");
            System.out.print("Выбор: ");
            String c = scanner.nextLine();

            switch (c) {
                case "1": search(); break;
                case "2": createOrder(user); break;
                case "3": orders(user); break;
                case "0": return;
                default: System.out.println("Неверный ввод");
            }
        }
    }

    private void createOrder(User user) {
        Order order = new Order(user);
        while (true) {
            System.out.println("\nДоступные товары:");
            for (int i = 0; i < products.size(); i++) {
                System.out.println((i + 1) + ". " + products.get(i));
            }
            System.out.print("Введите номер товара (0 - закончить): ");
            int n = Integer.parseInt(scanner.nextLine());
            if (n == 0) break;
            if (n > 0 && n <= products.size()) {
                order.addProduct(products.get(n - 1));
            }
        }
        orders.add(order);
        user.addOrder(order);
        System.out.println("Заказ создан: " + order);
    }

    private void orders(User user) {
        if (user.getOrders().isEmpty()) {
            System.out.println("У вас нет заказов.");
        } else {
            user.getOrders().forEach(System.out::println);
        }
    }


    public void adminMenu(Admin admin) {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Список пользователей");
            System.out.println("2. Управление заказами");
            System.out.println("3. Управление товарами");
            System.out.println("4. Статистика заказов");
            System.out.println("0. Выход");
            System.out.print("Выбор: ");
            String c = scanner.nextLine();

            switch (c) {
                case "1": listUsers(); break;
                case "2": manageOrders(); break;
                case "3": manageProducts(); break;
                case "4": stats(); break;
                case "0": return;
                default: System.out.println("Неверный ввод");
            }
        }
    }

    private void listUsers() {
        System.out.println("\nПользователи:");
        users.forEach(u -> System.out.println("- " + u.getLogin() + (u.isAdmin() ? " (Admin)" : "")));
    }

    private void manageOrders() {
        System.out.println("\nВсе заказы:");
        for (Order o : orders) {
            System.out.println(o);
        }
        System.out.print("Введите ID заказа для изменения статуса (0 - выход): ");
        int id = Integer.parseInt(scanner.nextLine());
        if (id == 0) return;
        for (Order o : orders) {
            if (o.getId() == id) {
                System.out.print("Введите новый статус: ");
                o.setStatus(scanner.nextLine());
                System.out.println("Статус изменён!");
                return;
            }
        }
        System.out.println("Заказ не найден.");
    }

    private void manageProducts() {
        System.out.println("\nТовары:");
        products.forEach(System.out::println);
        System.out.println("1. Добавить товар\n2. Изменить\n3. Удалить\n0. Назад");
        String c = scanner.nextLine();

        switch (c) {
            case "1":
                System.out.print("Название: ");
                String name = scanner.nextLine();
                System.out.print("Цена: ");
                double price = Double.parseDouble(scanner.nextLine());
                System.out.print("Количество: ");
                int qty = Integer.parseInt(scanner.nextLine());
                products.add(new Product(name, price, qty));
                System.out.println("Товар добавлен!");
                break;
            case "2":
                System.out.print("Название товара: ");
                String n = scanner.nextLine();
                for (Product p : products) {
                    if (p.getName().equalsIgnoreCase(n)) {
                        System.out.print("Новая цена: ");
                        p.setPrice(Double.parseDouble(scanner.nextLine()));
                        System.out.print("Новое количество: ");
                        p.setQuantity(Integer.parseInt(scanner.nextLine()));
                        System.out.println("Изменения сохранены!");
                        return;
                    }
                }
                System.out.println("Товар не найден.");
                break;
            case "3":
                System.out.print("Название товара: ");
                String del = scanner.nextLine();
                products.removeIf(p -> p.getName().equalsIgnoreCase(del));
                System.out.println("Товар удалён!");
                break;
        }
    }

    private void stats() {
        System.out.println("\nСтатистика:");
        System.out.println("Всего заказов: " + orders.size());
        double total = orders.stream().mapToDouble(Order::getTotal).sum();
        System.out.printf("Общая сумма заказов: %.2f тг%n", total);
    }
}

public class Main {
    public static void main(String[] args) {
        WarehouseSystem system = new WarehouseSystem();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nWAREHOUSE ACCOUNTING SYSTEM");
            System.out.println("1. Регистрация");
            System.out.println("2. Вход");
            System.out.println("0. Выход");
            System.out.print("Выбор: ");
            String c = sc.nextLine();

            switch (c) {
                case "1": system.register(); break;
                case "2":
                    User u = system.login();
                    if (u != null) {
                        if (u.isAdmin())
                            system.adminMenu((Admin) u);
                        else
                            system.userMenu(u);
                    }
                    break;
                case "0":
                    System.out.println("Выход");
                    return;
                default:
                    System.out.println("Неверный ввод");
            }
        }
    }
}
