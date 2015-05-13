package br.com.mpro3.MproEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * In one {@see br.com.mpro3.MproEntity.Entity} noted class,
 * note with @Reference the ArrayList collection field of referenced entities of Entity
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Reference
{
}
