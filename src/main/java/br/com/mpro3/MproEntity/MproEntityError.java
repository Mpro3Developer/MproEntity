package br.com.mpro3.MproEntity;

/**
 * Custom error class to differentiate java errors from MproEntities errors
 */
public class MproEntityError extends Error
{
    public MproEntityError(String message)
    {
        super(message);
    }
}
