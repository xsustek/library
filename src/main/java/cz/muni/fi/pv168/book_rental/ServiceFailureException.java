package cz.muni.fi.pv168.book_rental;

/**
 * Created by Milan on 20.03.2016.
 */
public class ServiceFailureException extends RuntimeException {
    public ServiceFailureException(String msg) {
        super(msg);
    }

    public ServiceFailureException(Throwable tr) {
        super(tr);
    }

    public ServiceFailureException(String msg, Throwable tr) {
        super(msg, tr);
    }
}
