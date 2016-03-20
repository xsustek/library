package cz.muni.fi.pv168.book_rental;

/**
 * Created by Milan on 20.03.2016.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String msg) {
        super(msg);
    }
}
