package gregtech.api.util;

/**
 * Created by matthew on 3/31/17.
 */
final class NotMutableException extends Exception {
    public NotMutableException() {super();}
    public NotMutableException(String message) {super(message);}
}

public class AbstractWrapper<T> {

    private T stored;

    private boolean mutable;

    public AbstractWrapper(T toStore) {
        this(toStore, true);
    }

    public AbstractWrapper(T toStore, boolean mutable) {
        this.stored = toStore;
        this.mutable = mutable;
    }

    public T unwrap() {
        T r = this.stored;
        if (mutable) stored=null;
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return r;
    }

    public T wrap(T newT) throws NotMutableException {
        if (!this.mutable) throw new NotMutableException();
        else {
            this.stored = newT;
            return newT;
        }
    }
}
