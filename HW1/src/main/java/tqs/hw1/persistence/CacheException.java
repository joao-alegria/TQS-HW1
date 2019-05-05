package tqs.hw1.persistence;

/**
 * Personal implementation of the Exception object.
 */
public class CacheException extends Exception{

    /**
     * Simple creator.
     * @param string the message the exception should contain.
     */
    public CacheException(String string) {
        super(string);
    }
    
}
