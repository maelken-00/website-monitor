import java.util.*;

// === Notifier Interface + Implementierungen ===
interface Notifier {
    void notify(User user, String message);
}

class EmailNotifier implements Notifier {
    public void notify(User user, String message) {
        System.out.println("[EMAIL] To " + user.getEmail() + ": " + message);
    }
}

class SMSNotifier implements Notifier {
    public void notify(User user, String message) {
        System.out.println("[SMS] To " + user.getPhoneNumber() + ": " + message);
    }
}

// === Benutzerklasse ===
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
        return subscriptions;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }
}

// === WebsiteSubscription ===
class WebsiteSubscription {
    private User user;
    private String url;
    private Notifier notifier;
    private String lastContent = "";

    public WebsiteSubscription(User user, String url, Notifier notifier) {
        this.user = user;
        this.url = url;
        this.notifier = notifier;
    }

    public void checkForUpdate() {
        String newContent = Math.random() > 0.5 ? "changed" : lastContent;
        if (!newContent.equals(lastContent)) {
            lastContent = newContent;
            notifier.notify(user, "Website updated: " + url);
        } else {
            System.out.println("No update found on: " + url);
        }
    }

    public String getUrl() {
        return url;
    }

    public static WebsiteSubscription subscribeWebsite(User user, Scanner scanner) {
        System.out.print("URL der Website: ");
        String url = scanner.nextLine();

        System.out.println("Benachrichtigungsmethode:");
        System.out.println("1. E-Mail");
        System.out.println("2. SMS");
        System.out.print("Wahl: ");
        int method = scanner.nextInt();
        scanner.nextLine(); // \n schlucken

        Notifier notifier = (method == 2) ? new SMSNotifier() : new EmailNotifier();
        WebsiteSubscription sub = new WebsiteSubscription(user, url, notifier);

        System.out.println("Website erfolgreich abonniert.");
        return sub;
    }
}

// === Monitor Klasse ===
public class Monitor {
    private Scanner scanner = new Scanner(System.in);
    private User currentUser;

    public void start() {
        System.out.println("=== WEBSITE MONITOR ===");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefonnummer: ");
        String phone = scanner.nextLine();

        currentUser = new User(name, email, phone);

        boolean running = true;
        while (running) {
            System.out.println("\n--- Menü ---");
            System.out.println("1. Website abonnieren");
            System.out.println("2. Abonnements anzeigen");
            System.out.println("3. Nach Updates prüfen");
            System.out.println("4. Beenden");
            System.out.print("Auswahl: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // \n schlucken

            switch (choice) {
                case 1 -> addSubscription();
                case 2 -> showSubscriptions();
                case 3 -> checkUpdates();
                case 4 -> running = false;
                default -> System.out.println("Ungültige Auswahl.");
            }
        }

        System.out.println("Programm beendet.");
    }

    private void addSubscription() {
        WebsiteSubscription sub = WebsiteSubscription.subscribeWebsite(currentUser, scanner);
        currentUser.addSubscription(sub);
    }

    private void showSubscriptions() {
        List<WebsiteSubscription> subs = currentUser.getSubscriptions();
        if (subs.isEmpty()) {
            System.out.println("Keine Abonnements vorhanden.");
        } else {
            System.out.println("Deine abonnierten Websites:");
            for (int i = 0; i < subs.size(); i++) {
                System.out.println((i + 1) + ". " + subs.get(i).getUrl());
            }
        }
    }

    private void checkUpdates() {
        List<WebsiteSubscription> subs = currentUser.getSubscriptions();
        if (subs.isEmpty()) {
            System.out.println("Keine Abos zum Prüfen.");
            return;
        }
        for (WebsiteSubscription sub : subs) {
            sub.checkForUpdate();
        }
    }

    public static void main(String[] args) {
        new Monitor().start();
    }
}

