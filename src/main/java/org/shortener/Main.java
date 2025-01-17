package org.shortener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final FileUrlRepository fileRepository = new FileUrlRepository();
    private static final UrlService urlService = new UrlService(fileRepository);
    private static String userId;

    public static void main(String[] args) {
        showLoginMenu();

        while (true) {
            printMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                processChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            } catch (UrlException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private static void showLoginMenu() {
        while (true) {
            System.out.println("=== Welcome to URL Shortener ===");
            System.out.println("1. Login with UUID");
            System.out.println("2. Continue as new user");
            System.out.print("Choose option (1-2): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        loginWithUUID();
                        return;
                    case 2:
                        createNewUser();
                        return;
                    default:
                        System.out.println("Invalid option");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    private static void loginWithUUID() {
        while (true) {
            System.out.print("Enter your UUID (or press Enter to cancel): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                createNewUser();
                return;
            }

            try {
                UUID uuid = UUID.fromString(input);
                userId = uuid.toString();
                fileRepository.loadUserData(userId);
                System.out.println("Successfully logged in with UUID: " + userId);
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid UUID format. Please try again.");
            }
        }
    }

    private static void createNewUser() {
        userId = new User().getId();
        fileRepository.loadUserData(userId);
        System.out.println("New user created with UUID: " + userId);
        System.out.println("Please save this UUID for future logins!");
    }

    private static void printMenu() {
        System.out.println("\n=== URL Shortener ===");
        System.out.println("Current user UUID: " + userId);
        System.out.println("1. Create short URL");
        System.out.println("2. Get original URL");
        System.out.println("3. List my URLs");
        System.out.println("4. Remove URL");
        System.out.println("5. Open URL in browser");
        System.out.println("6. Switch user");
        System.out.println("7. Exit");
        System.out.print("Choose option (1-7): ");
    }

    private static void processChoice(int choice) {
        switch (choice) {
            case 1:
                createShortUrl();
                break;
            case 2:
                getOriginalUrl();
                break;
            case 3:
                listUrls();
                break;
            case 4:
                removeUrl();
                break;
            case 5:
                openUrl();
                break;
            case 6:
                showLoginMenu();
                break;
            case 7:
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option");
        }
    }

    private static void createShortUrl() {
        System.out.print("Enter URL to shorten: ");
        String originalUrl = scanner.nextLine();

        System.out.print("Enter click limit (0 for unlimited): ");
        int clickLimit = Integer.parseInt(scanner.nextLine());

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1); // Срок жизни - 1 день

        ShortUrl shortUrl = urlService.createShortUrl(originalUrl, userId, expiresAt, clickLimit);
        System.out.println("Created short URL: " + shortUrl.getShortUrl());
        System.out.println("Expires at: " + shortUrl.getExpiresAt());
        System.out.println("Click limit: " + (shortUrl.getClickLimit() > 0 ? shortUrl.getClickLimit() : "unlimited"));
    }

    private static void openUrl() {
        System.out.print("Enter short URL code: ");
        String shortUrl = scanner.nextLine();
        urlService.openUrl(shortUrl);
        System.out.println("URL opened in browser");
    }

    private static void getOriginalUrl() {
        System.out.print("Enter short URL code: ");
        String shortUrl = scanner.nextLine();

        String originalUrl = urlService.getOriginalUrl(shortUrl);
        System.out.println("Original URL: " + originalUrl);
    }

    private static void listUrls() {
        List<ShortUrl> urls = urlService.getUserUrls(userId);
        if (urls.isEmpty()) {
            System.out.println("No URLs found");
            return;
        }

        System.out.println("\nYour URLs:");
        for (ShortUrl url : urls) {
            System.out.println("Short: " + url.getShortUrl());
            System.out.println("Original: " + url.getOriginalUrl());
            System.out.println("Expires: " + url.getExpiresAt());
            System.out.println("Clicks: " + url.getClickCount() + "/" +
                    (url.getClickLimit() > 0 ? url.getClickLimit() : "unlimited"));
            System.out.println("---");
        }
    }

    private static void removeUrl() {
        System.out.print("Enter short URL to remove: ");
        String shortUrl = scanner.nextLine();

        urlService.removeUrl(shortUrl, userId);
        System.out.println("URL removed successfully");
    }
}