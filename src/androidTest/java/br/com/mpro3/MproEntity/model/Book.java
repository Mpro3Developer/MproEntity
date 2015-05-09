package br.com.mpro3.MproEntity.model;

import br.com.mpro3.MproEntity.Entity;
import br.com.mpro3.MproEntity.Id;

/**
 * Created by matheus on 23/03/15.
 */

@Entity
public class Book
{
    @Id
    private int cod;
    private String name;
    private String isbn;
    private int year;

    public Book(){}

    public Book(String name, String isbn, int year)
    {
        this.name = name;
        this.isbn = isbn;
        this.year = year;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getIsbn()
    {
        return isbn;
    }

    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }
}
