package cli.backend.exceptions;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }
}
