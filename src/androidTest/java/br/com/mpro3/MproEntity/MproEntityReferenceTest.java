package br.com.mpro3.MproEntity;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.util.ArrayList;

import br.com.mpro3.MproEntity.model.Book;
import br.com.mpro3.MproEntity.model.Loan;
import br.com.mpro3.MproEntity.model.User;

/**
 * Created by Matheus Castello on 03/04/15.
 */
public class MproEntityReferenceTest extends AndroidTestCase
{
    private DataStore dataStore;

    private Loan loanFactory()
    {
        Loan loan = new Loan();
        loan.setLoanDate("03/04/2015");
        loan.setReturnDate("17/04/2015");

        Book book = new Book();
        book.setName("Android for Dummies");
        book.setIsbn("2038420398409");
        book.setYear(2010);

        loan.getBooks().add(book);

        book = new Book();
        book.setName("Android Studio Development");
        book.setIsbn("037849878");
        book.setYear(2014);

        loan.getBooks().add(book);

        User user = new User();
        user.setName("Charles Babbage");
        user.setNickName("Mpro3");
        user.setBirthDate("14/11/1990");

        loan.getUsers().add(user);

        return loan;
    }

    /**
     * Instantiate one Entity with referenced Entities, save
     * Test the identifier returned and the identifiers of Entities saved by cascade
     */
    public void testSaveEntity()
    {
        Loan loan = loanFactory();
        assertEquals(1, this.dataStore.save(loan));

        assertEquals(1, loan.getCod());
        assertEquals(1, loan.getUsers().get(0).getCod());
        assertEquals(1, loan.getBooks().get(0).getCod());
        assertEquals(2, loan.getBooks().get(1).getCod());
    }

    /**
     * Instantiate one Entity with referenced Entities, save
     * Query the saved Entity and test the size of entities referenced
     * Add one new reference entity in instantiated Entity, save
     * Query the saved Entity and test the new size of entities referenced
     */
    public void testAddReferencedObject()
    {
        Loan loan = loanFactory();
        this.dataStore.save(loan);

        ArrayList<Loan> loans = this.dataStore.query(Loan.class).execute();
        assertEquals(2, loans.get(0).getBooks().size());

        loan.getBooks().add(new Book("How To Use MproEntity", "983729187", 2015));
        this.dataStore.save(loan);

        loans = this.dataStore.query(Loan.class).execute();
        assertEquals(3, loans.get(0).getBooks().size());
    }

    /**
     * Instantiate one Entity with referenced Entities, save
     * Query Entity test the size of referenced objects collection
     * Remove one referenced object from collection, save update, remove referenced
     * Query Entity test the new size of referenced objects collection
     */
    public void testRemoveReferencedObject()
    {
        Loan loan = loanFactory();
        assertEquals(1, this.dataStore.save(loan));

        ArrayList<Loan> loans = this.dataStore.query(Loan.class).execute();
        assertEquals(2, loans.get(0).getBooks().size());

        loan.getBooks().remove(1);

        assertEquals(1, this.dataStore.save(loan));

        loans = this.dataStore.query(Loan.class).execute();
        assertEquals(1, loans.get(0).getBooks().size());
    }

    /**
     * Instantiate and save two Entities with referenced Entities
     * Query Entity WHERE field of referenced Entity equal
     * Test distinct result for differed name
     */
    public void testQueryWhereEqual()
    {
        Loan loan = loanFactory();
        loan.getBooks().add(new Book("How To Use MproEntity", "983729187", 2015));
        this.dataStore.save(loan);

        Loan loan1 = loanFactory();
        this.dataStore.save(loan1);

        try
        {
            ArrayList<Loan> loans = this.dataStore.query(Loan.class)
                                    .where(Book.class, "name")
                                    .equal("How To Use MproEntity")
                                    .execute();

            assertEquals(1, loans.size());
            assertEquals(3, loans.get(0).getBooks().size());

            loans = this.dataStore.query(Loan.class)
                    .where(Book.class, "name")
                    .equal("Android for Dummies")
                    .execute();

            assertEquals(2, loans.size());
            assertEquals(3, loans.get(0).getBooks().size());
            assertEquals(2, loans.get(1).getBooks().size());
        }
        catch (NoSuchFieldException e)
        {
            assertEquals("", "NoSuch Field Exception");
        }
    }

    /**
     * Instantiate and save two Entities with referenced Entities
     * Query Entity WHERE field of referenced Entity NOT EQUAL
     * Test the result
     */
    public void testQueryWhereNotEqual()
    {
        Loan loan = loanFactory();
        loan.getBooks().clear();
        loan.getBooks().add(new Book("How To Use MproEntity", "983729187", 2015));
        this.dataStore.save(loan);

        Loan loan1 = loanFactory();
        this.dataStore.save(loan1);

        try
        {
            ArrayList<Loan> loans = this.dataStore.query(Loan.class)
                    .where(Book.class, "name")
                    .notEqual("How To Use MproEntity")
                    .execute();

            assertEquals(1, loans.size());
            assertEquals(2, loans.get(0).getBooks().size());
        }
        catch (NoSuchFieldException e)
        {
            assertEquals("", "NoSuch Field Exception");
        }
    }

    /**
     * Instantiate and save two Entities with referenced Entities
     * Query Entity WHERE field of referenced Entity BIGGER than
     * Test the result
     */
    public void testQueryWhereBigger()
    {
        Loan loan = loanFactory();
        loan.getBooks().add(new Book("How To Use MproEntity", "983729187", 2015));
        this.dataStore.save(loan);

        Loan loan1 = loanFactory();
        this.dataStore.save(loan1);

        try
        {
            ArrayList<Loan> loans = this.dataStore.query(Loan.class)
                    .where(Book.class, "year")
                    .bigger(2014)
                    .execute();

            assertEquals(1, loans.size());
            assertEquals(1, loans.get(0).getCod());
        }
        catch (NoSuchFieldException e)
        {
            assertEquals("", "NoSuch Field Exception");
        }
    }

    /**
     * Instantiate and save two Entities with referenced Entities
     * Query Entity WHERE field of referenced Entity BIGGER AND EQUAL than
     * Test the result
     */
    public void testQueryWhereBiggerAndEqual()
    {
        Loan loan = loanFactory();
        loan.getBooks().add(new Book("How To Use MproEntity", "983729187", 2015));
        this.dataStore.save(loan);

        Loan loan1 = loanFactory();
        this.dataStore.save(loan1);

        try
        {
            ArrayList<Loan> loans = this.dataStore.query(Loan.class)
                    .where(Book.class, "year")
                    .biggerAndEqual(2014)
                    .execute();

            assertEquals(2, loans.size());
        }
        catch (NoSuchFieldException e)
        {
            assertEquals("", "NoSuch Field Exception");
        }
    }

    /**
     * Instantiate and save two Entities with referenced Entities
     * Query Entity WHERE field of referenced Entity LESS than
     * Test the result
     */
    public void testQueryWhereLess()
    {
        Loan loan = loanFactory();
        loan.getBooks().clear();
        loan.getBooks().add(new Book("How To Use MproEntity", "983729187", 2015));
        this.dataStore.save(loan);

        Loan loan1 = loanFactory();
        this.dataStore.save(loan1);

        try
        {
            ArrayList<Loan> loans = this.dataStore.query(Loan.class)
                    .where(Book.class, "year")
                    .less(2015)
                    .execute();

            assertEquals(1, loans.size());
            assertEquals(2, loans.get(0).getCod());
        }
        catch (NoSuchFieldException e)
        {
            assertEquals("", "NoSuch Field Exception");
        }
    }

    /**
     * Instantiate and save two Entities with referenced Entities
     * Query Entity WHERE field of referenced Entity LESS AND EQUAL than
     * Test the result
     */
    public void testQueryWhereLessAndEqual()
    {
        Loan loan = loanFactory();
        loan.getBooks().remove(0);
        loan.getBooks().remove(0);
        loan.getBooks().add(new Book("How To Use MproEntity", "983729187", 2015));
        this.dataStore.save(loan);

        Loan loan1 = loanFactory();
        this.dataStore.save(loan1);

        try
        {
            ArrayList<Loan> loans = this.dataStore.query(Loan.class)
                    .where(Book.class, "year")
                    .lessAndEqual(2015)
                    .execute();

            assertEquals(2, loans.size());
            assertEquals(1, loans.get(0).getCod());
        }
        catch (NoSuchFieldException e)
        {
            assertEquals("", "NoSuch Field Exception");
        }
    }

    /**
     * Instantiate and save three Entities with referenced Entities
     * Query Entity WHERE field of referenced Entity BETWEEN values
     * Test the result
     */
    public void testQueryWhereBetween()
    {
        Loan loan = loanFactory();
        loan.getBooks().clear();
        loan.getBooks().add(new Book("How To Use MproEntity", "983729187", 2015));
        this.dataStore.save(loan);

        Loan loan1 = loanFactory();
        loan1.getBooks().clear();
        loan1.getBooks().add(new Book("The End of Eternity", "86639693", 1955));
        this.dataStore.save(loan1);

        Loan loan2 = loanFactory();
        loan2.getBooks().clear();
        loan2.getBooks().add(new Book("I, Robot", "8687694836", 1950));
        this.dataStore.save(loan2);

        try
        {
            ArrayList<Loan> loans = this.dataStore.query(Loan.class)
                    .where(Book.class, "year")
                    .between(1950, 1990)
                    .execute();

            assertEquals(2, loans.size());
            assertEquals(2, loans.get(0).getCod());
        }
        catch (NoSuchFieldException e)
        {
            assertEquals("", "NoSuch Field Exception");
        }
    }

    /**
     * Instantiate and save two Entities with referenced Entities
     * Query Entity WHERE field of referenced Entity LIKE
     * Test the result
     */
    public void testQueryWhereLike()
    {
        Loan loan = loanFactory();
        loan.getBooks().clear();
        loan.getBooks().add(new Book("How To Use MproEntity", "983729187", 2015));
        this.dataStore.save(loan);

        Loan loan1 = loanFactory();
        this.dataStore.save(loan1);

        try
        {
            ArrayList<Loan> loans = this.dataStore.query(Loan.class)
                    .where(Book.class, "name")
                    .like("Android")
                    .execute();

            assertEquals(1, loans.size());
            assertEquals(2, loans.get(0).getCod());
        }
        catch (NoSuchFieldException e)
        {
            assertEquals("", "NoSuch Field Exception");
        }
    }

    /**
     * Instantiate and save two Entities with referenced Entities
     * Query Entity WHERE field of referenced Entity LIKE value *{OR LIKE value}
     * Test the result
     */
    public void testQueryWhereOr()
    {
        Loan loan = loanFactory();
        loan.getBooks().clear();
        loan.getBooks().add(new Book("How To Use MproEntity", "983729187", 2015));
        this.dataStore.save(loan);

        Loan loan1 = loanFactory();
        this.dataStore.save(loan1);

        try
        {
            ArrayList<Loan> loans = this.dataStore.query(Loan.class)
                    .where(Book.class, "name")
                    .like("Android")
                    .or(Book.class, "name")
                    .like("MproEntity")
                    .execute();

            assertEquals(2, loans.size());
        }
        catch (NoSuchFieldException e)
        {
            assertEquals("", "NoSuch Field Exception");
        }
    }

    /**
     * Instantiate and save two Entities with referenced Entities
     * Query Entity WHERE field of referenced Entity LIKE value *{AND LIKE value}
     * Test the result
     */
    public void testQueryWhereAnd()
    {
        Loan loan = loanFactory();
        loan.getBooks().clear();
        loan.getBooks().add(new Book("How To Use MproEntity", "983729187", 2015));
        this.dataStore.save(loan);

        Loan loan1 = loanFactory();
        this.dataStore.save(loan1);

        try
        {
            ArrayList<Loan> loans = this.dataStore.query(Loan.class)
                    .where(Book.class, "name")
                    .like("Android")
                    .and(Book.class, "name")
                    .like("Dummies")
                    .execute();

            assertEquals(1, loans.size());
        }
        catch (NoSuchFieldException e)
        {
            assertEquals("", "NoSuch Field Exception");
        }
    }

    @Override
    protected void setUp() throws Exception
    {
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        this.dataStore = new DataStore(context);
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception
    {
        this.dataStore.getDbManager().close();
        super.tearDown();
    }
}
