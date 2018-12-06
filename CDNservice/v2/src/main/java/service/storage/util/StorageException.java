package service.storage.util;

@SuppressWarnings("serial")
public class StorageException extends RuntimeException {

	public StorageException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public StorageException(String msg) {
		super(msg);
	}

}
