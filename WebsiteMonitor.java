import java.util.*;

// ================== Observer Interface ==================
interface Observer {
    void update(String url, String message);
}

// ================== Subject Interface ==================
interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers(String message);
}

// ================== Strategy Interface ==================
interface ComparisonStrategy {
    boolean hasChanged(String oldContent, String newContent);
    String describe();
}

// ================== Concrete Strategies ==================
class SizeComparisonStrategy implements ComparisonStrategy {
    public boolean hasChanged(String oldContent, String newContent) {
        return oldContent.length() != newContent.length();
    }

    public String describe() {
        return "Content size comparison";
    }
}

class HtmlComparisonStrategy implements ComparisonStrategy {
    public boolean hasChanged(String oldContent, String newContent) {
        return !oldContent.equals(newContent);
    }

    public String describe() {
        return "Exact HTML content comparison";
    }
}

class TextComparisonStrategy implements ComparisonStrategy {
    public boolean hasChanged(String oldContent, String newContent) {
        String oldText = extractText(oldContent);
        String newText = extractText(newContent);
        return !oldText.equals(newText);
    }

    private String extractText(String html) {
        return html.replaceAll("<[^>]*>", "").trim();
    }

    public String describe() {
        return "Extracted text content comparison";
    }
}

// ================== Concrete Observers ==================
class EmailNotifier implements Observer {
    private String email;

    public EmailNotifier(String email) {
        this.email = email;
    }

    public void update(String url, String message) {
        System.out.println("[EMAIL to " + email + "] Website '" + url + "' update: " + message);
    }
}

class SMSNotifier implements Observer {
    private String phoneNumber;

    public SMSNotifier(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void update(String url, String message) {
        System.out.println("[SMS to " + phoneNumber + "] Website '" + url + "' update: " + message);
    }
}

// ================== Concrete Subject ==================
class WebsiteSubscription implements Subject {
    private String url;
    private String currentContent;
    private ComparisonStrategy strategy;
    private List<Observer> observers = new ArrayList<>();

    public WebsiteSubscription(String url, ComparisonStrategy strategy) {
        this.url = url;
        this.strategy = strategy;
        this.currentContent = fetchInitialContent();
    }

    private String fetchInitialContent() {
        return "<html><body>Initial content</body></html>";
    }

    private String fetchNewContent() {
        // Simulate changing content
        return Math.random() > 0.5
                ? "<html><body>Updated content " + System.currentTimeMillis() + "</body></html>"
                : currentContent;
    }

    public void checkForUpdates() {
        String newContent = fetchNewContent();
        if (strategy.hasChanged(currentContent, newContent)) {
            currentContent = newContent;
            notifyObservers("Change detected using strategy: " + strategy.describe());
        }
    }

    public void registerObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers(String message) {
        for (Observer o : observers) {
            o.update(url, message);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getStrategyName() {
        return strategy.describe();
    }
}

// ================== User Class ==================
class User {
    private String name;
    private String email;
    private String phoneNumber;
    private List<WebsiteSubscription> subscriptions = new ArrayList<>();

    public User(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void addSubscription(WebsiteSubscription sub) {
        subscriptions.add(sub);
    }

    public List<WebsiteSubscription> getSubscriptions() {
        return new ArrayList<>(subscriptions);
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

// ================== Main Application ==================
public class WebsiteMonitor {
    private Scanner scanner = new Scanner(System.in);
    private User currentUser;

    public void start() {
        System.out.println("=== WEBSITE MONITOR ===");
        registerUser();
        mainMenu();
    }

    private void registerUser() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();
        currentUser = new User(name, email, phone);
    }

    private void mainMenu() {
        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Subscribe to website");
            System.out.println("2. Check for updates");
            System.out.println("3. List subscriptions");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> subscribeToWebsite();
                case 2 -> checkUpdates();
                case 3 -> listSubscriptions();
                case 4 -> { System.out.println("Goodbye!"); return; }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void subscribeToWebsite() {
        System.out.print("Enter website URL: ");
        String url = scanner.nextLine();

        System.out.println("Select comparison strategy:");
        System.out.println("1. Identical content size");
        System.out.println("2. Identical HTML content");
        System.out.println("3. Identical text content");
        System.out.print("Your choice: ");
        int stratChoice = scanner.nextInt();
        scanner.nextLine();

        ComparisonStrategy strategy = switch (stratChoice) {
            case 1 -> new SizeComparisonStrategy();
            case 2 -> new HtmlComparisonStrategy();
            case 3 -> new TextComparisonStrategy();
            default -> {
                System.out.println("Invalid choice, defaulting to HTML comparison.");
                yield new HtmlComparisonStrategy();
            }
        };

        WebsiteSubscription subscription = new WebsiteSubscription(url, strategy);

        System.out.println("Select notification methods:");
        System.out.println("1. Email");
        System.out.println("2. SMS");
        System.out.println("3. Both");
        System.out.print("Your choice: ");
        int method = scanner.nextInt();
        scanner.nextLine();

        if (method == 1 || method == 3) {
            subscription.registerObserver(new EmailNotifier(currentUser.getEmail()));
        }
        if (method == 2 || method == 3) {
            subscription.registerObserver(new SMSNotifier(currentUser.getPhoneNumber()));
        }

        currentUser.addSubscription(subscription);
        System.out.println("Successfully subscribed to " + url + " using " + strategy.describe());
    }

    private void checkUpdates() {
        List<WebsiteSubscription> subscriptions = currentUser.getSubscriptions();
        if (subscriptions.isEmpty()) {
            System.out.println("No subscriptions to check");
            return;
        }

        System.out.println("Checking for updates...");
        for (WebsiteSubscription sub : subscriptions) {
            sub.checkForUpdates();
        }
    }

    private void listSubscriptions() {
        List<WebsiteSubscription> subscriptions = currentUser.getSubscriptions();
        if (subscriptions.isEmpty()) {
            System.out.println("No active subscriptions");
            return;
        }

        System.out.println("Your subscriptions:");
        for (int i = 0; i < subscriptions.size(); i++) {
            WebsiteSubscription sub = subscriptions.get(i);
            System.out.println((i + 1) + ". " + sub.getUrl() + " (Strategy: " + sub.getStrategyName() + ")");
        }
    }

    public static void main(String[] args) {
        new WebsiteMonitor().start();
    }
}
