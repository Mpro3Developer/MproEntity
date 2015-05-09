package br.com.mpro3.MproEntity.model;

import java.util.ArrayList;

import br.com.mpro3.MproEntity.Entity;
import br.com.mpro3.MproEntity.Id;
import br.com.mpro3.MproEntity.Reference;

/**
 * Created by matheus on 23/03/15.
 */

@Entity
public class Loan
{
    @Id
    private int cod;
    private String loanDate;
    private String returnDate;
    @Reference
    private ArrayList<User> Users = new ArrayList<User>();
    @Reference
    private ArrayList<Book> Books = new ArrayList<Book>();

    public Loan(){}

    public Loan(String loanDate, String returnDate)
    {
        loanDate = loanDate;
        returnDate = returnDate;
    }

    public int getCod()
    {
        return cod;
    }

    public void setCod(int cod)
    {
        this.cod = cod;
    }

    public void setLoanDate(String loanDate)
    {
        this.loanDate = loanDate;
    }

    public void setReturnDate(String returnDate)
    {
        this.returnDate = returnDate;
    }

    public String getLoanDate()
    {
        return loanDate;
    }

    public String getReturnDate()
    {
        return returnDate;
    }

    public ArrayList<User> getUsers()
    {
        return Users;
    }

    public ArrayList<Book> getBooks()
    {
        return Books;
    }
}
