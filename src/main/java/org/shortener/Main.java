package org.shortener;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final FileUrlRepository fileRepository = new FileUrlRepository();
    private static final UrlService urlService = new UrlService(fileRepository);
    private static String userId;

    public static void main(String[] args) {
        System.out.println("Working directory: " + System.getProperty("user.dir"));

        showLoginMenu();

        while (true) {
            printMenu();
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }
                int choice = Integer.parseInt(input);
                processChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            } catch (UrlException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
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
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }
                int choice = Integer.parseInt(input);
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
            System.out.print("Enter your UUID (or press Enter to go back): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                showLoginMenu();
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
        userId = UUID.randomUUID().toString();
        fileRepository.loadUserData(userId);
        System.out.println("New user created with UUID: " + userId);
        System.out.println("Please save this UUID for future logins!");
    }

    private static void printMenu() {
        System.out.println("\n=== URL Shortener ===");
        System.out.println("Current user UUID: " + userId);
        System.out.println("1. Create short URL");
        System.out.println("2. Open URL in browser");
        System.out.println("3. List my URLs");
        System.out.println("4. Remove URL");
        System.out.println("5. Switch user");
        System.out.println("6. Exit");
        System.out.print("Choose option (1-6): ");
    }

    private static void processChoice(int choice) {
        switch (choice) {
            case 1:
                createShortUrl();
                break;
            case 2:
                getAndOpenUrl();
                break;
            case 3:
                listUrls();
                break;
            case 4:
                removeUrl();
                break;
            case 5:
                showLoginMenu();
                break;
            case 6:
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option");
        }
    }

    private static void createShortUrl() {
        System.out.print("Enter URL to shorten: ");
        String originalUrl = scanner.nextLine().trim();
        if (originalUrl.isEmpty()) {
            System.out.println("URL cannot be empty");
            return;
        }

        Integer lifetimeDays = null;
        while (true) {
            System.out.print("Enter lifetime in days (press Enter for default): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                break;
            }
            try {
                lifetimeDays = Integer.parseInt(input);
                if (lifetimeDays > 0) {
                    break;
                }
                System.out.println("Lifetime must be positive");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }

        Integer clickLimit = null;
        while (true) {
            System.out.print("Enter click limit (press Enter for default): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                break;
            }
            try {
                clickLimit = Integer.parseInt(input);
                if (clickLimit > 0) {
                    break;
                }
                System.out.println("Click limit must be positive");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }

        try {
            ShortUrl shortUrl = urlService.createShortUrl(originalUrl, userId, lifetimeDays, clickLimit);
            System.out.println("\nURL successfully shortened:");
            System.out.println("Short URL: " + shortUrl.getShortUrl());
            System.out.println("Expires at: " + shortUrl.getExpiresAt());
            System.out.println("Click limit: " + shortUrl.getClickLimit());
        } catch (Exception e) {
            System.out.println("Failed to create short URL: " + e.getMessage());
        }
    }

    private static void getAndOpenUrl() {
        System.out.print("Enter short URL code: ");
        String shortUrl = scanner.nextLine().trim();
        if (shortUrl.isEmpty()) {
            System.out.println("URL code cannot be empty");
            return;
        }

        try {
            urlService.openUrl(shortUrl);
            System.out.println("URL opened in browser");
        } catch (Exception e) {
            System.out.println("Failed to open URL: " + e.getMessage());
        }
    }

    private static void listUrls() {
        try {
            List<ShortUrl> urls = urlService.getUserUrls(userId);
            if (urls.isEmpty()) {
                System.out.println("No URLs found");
                return;
            }

            System.out.println("\nYour URLs:");
            for (ShortUrl url : urls) {
                System.out.println("\nShort URL: " + url.getShortUrl());
                System.out.println("Original URL: " + url.getOriginalUrl());
                System.out.println("Expires at: " + url.getExpiresAt());
                System.out.println("Clicks: " + url.getClickCount() + "/" + url.getClickLimit());
                System.out.println("---");
            }
        } catch (Exception e) {
            System.out.println("Failed to list URLs: " + e.getMessage());
        }
    }

    private static void removeUrl() {
        System.out.print("Enter short URL to remove: ");
        String shortUrl = scanner.nextLine().trim();
        if (shortUrl.isEmpty()) {
            System.out.println("URL code cannot be empty");
            return;
        }

        try {
            urlService.removeUrl(shortUrl, userId);
            System.out.println("URL removed successfully");
        } catch (Exception e) {
            System.out.println("Failed to remove URL: " + e.getMessage());
        }
    }
}