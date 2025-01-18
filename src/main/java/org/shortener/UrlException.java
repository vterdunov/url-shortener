package org.shortener;

//  Base exception for URL shortener application
public class UrlException extends RuntimeException {
    public UrlException(String message) {
        super(message);
    }

    public UrlException(String message, Throwable cause) {
        super(message, cause);
    }
}

//  Thrown when URL validation fails
class UrlValidationException extends UrlException {
    public UrlValidationException(String message) {
        super(message);
    }
}

//  Thrown when URL is not found or expired
class UrlNotFoundException extends UrlException {
    public UrlNotFoundException(String message) {
        super(message);
    }
}

//  Thrown when user has no access rights to URL
class UrlAccessDeniedException extends UrlException {
    public UrlAccessDeniedException(String message) {
        super(message);
    }
}

//  Thrown when there are problems with URL storage
class UrlStorageException extends UrlException {
    public UrlStorageException(String message) {
        super(message);
    }

    public UrlStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
