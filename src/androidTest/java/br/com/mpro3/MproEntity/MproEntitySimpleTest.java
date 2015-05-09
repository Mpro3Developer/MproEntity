package br.com.mpro3.MproEntity;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import java.util.ArrayList;
import br.com.mpro3.MproEntity.model.User;

/**
 * Created by Matheus Castello on 01/04/15.
 */

public class MproEntitySimpleTest extends AndroidTestCase
{
    private DataStore dataStore;

    private User userFactory()
    {
        User u = new User();
        u.setName("User Test");
        u.setNickName("Test");
        u.setBirthDate("14/11/1990");
        return u;
    }

    private void userFactoryAndSave(int size)
    {
        for(int i = 0; i < size; i++)
        {
            User u = userFactory();
            this.dataStore.save(u);
        }
    }

    /**
     * Instantiate one Entity then save, test the return identifier
     * Instantiate other Entity then save, test the identifier auto increment
     */
    public void testSaveEntity()
    {
        User u = userFactory();

        assertEquals(1, this.dataStore.save(u));
        assertEquals(1, u.getCod());

        u = userFactory();

        assertEquals(2, this.dataStore.save(u));
        assertEquals(2, u.getCod());
    }

    /**
     * Instantiate one Entity then save, test the return identifier
     * Query Entity and check the field
     * With the same instance change the field value then save, update by cod, test if the identifier remains the same
     * Query Entity and check the updated field value
     */
    public void testUpdateEntity()
    {
        User u = userFactory();

        assertEquals(1, this.dataStore.save(u));
        assertEquals(1, u.getCod());

        ArrayList<User> users = this.dataStore.query(User.class).execute();
        assertEquals("Test", users.get(0).getNickName());

        u.setNickName("Modified");
        this.dataStore.save(u);

        assertEquals(1, this.dataStore.save(u));
        assertEquals(1, u.getCod());

        users = this.dataStore.query(User.class).execute();
        assertEquals("Modified", users.get(0).getNickName());
    }

    /**
     * Instantiate one Entity then save, test the object identifier
     * Delete the Entity saved, test the object identifier deleted
     */
    public void testDeleteEntity()
    {
        User u = userFactory();

        this.dataStore.save(u);
        assertEquals(1, u.getCod());

        this.dataStore.delete(u);
        assertEquals(0, u.getCod());
    }

    /**
     * Instantiate and save 10 Entities
     * Query all Entity in data base, test the size of collection query
     */
    public void testQueryEntities()
    {
        userFactoryAndSave(10);

        ArrayList<User> users = this.dataStore.query(User.class).execute();
        assertEquals(10, users.size());
    }

    /**
     * Instantiate and save 10 Entities
     * Query one Entity WHERE identifier EQUAL 5
     */
    public void testQueryWhereEqual()
    {
        userFactoryAndSave(10);

        ArrayList<User> users = this.dataStore.query(User.class).where("cod").equal(5).execute();
        assertEquals(1, users.size());
        assertEquals(5, users.get(0).getCod());
    }

    /**
     * Instantiate and save 2 Entities
     * Query all Entities WHERE identifier NOT EQUAL 1
     */
    public void testQueryWhereNotEqual()
    {
        userFactoryAndSave(2);

        ArrayList<User> users = this.dataStore.query(User.class).where("cod").notEqual(1).execute();
        assertEquals(1, users.size());
        assertEquals(2, users.get(0).getCod());
    }

    /**
     * Instantiate and save 10 Entities
     * Query all Entities WHERE identifier BIGGER than 5
     */
    public void testQueryWhereBigger()
    {
        userFactoryAndSave(10);

        ArrayList<User> users = this.dataStore.query(User.class).where("cod").bigger(5).execute();
        assertEquals(5, users.size());
        assertEquals(6, users.get(0).getCod());
    }

    /**
     * Instantiate and save 10 Entities
     * Query all Entities WHERE identifier BIGGER AND EQUAL 5
     */
    public void testQueryWhereBiggerAndEqual()
    {
        userFactoryAndSave(10);

        ArrayList<User> users = this.dataStore.query(User.class).where("cod").biggerAndEqual(5).execute();
        assertEquals(6, users.size());
        assertEquals(5, users.get(0).getCod());
    }

    /**
     * Instantiate and save 10 Entities
     * Query all Entities WHERE identifier LESS 5
     */
    public void testQueryWhereLess()
    {
        userFactoryAndSave(10);

        ArrayList<User> users = this.dataStore.query(User.class).where("cod").less(5).execute();
        assertEquals(4, users.size());
        assertEquals(1, users.get(0).getCod());
    }

    /**
     * Instantiate and save 10 Entities
     * Query all Entities WHERE identifier LESS AND EQUAL 5
     */
    public void testQueryWhereLessAndEqual()
    {
        userFactoryAndSave(10);

        ArrayList<User> users = this.dataStore.query(User.class).where("cod").lessAndEqual(5).execute();
        assertEquals(5, users.size());
        assertEquals(1, users.get(0).getCod());
    }

    /**
     * Instantiate and save 10 Entities
     * Query all Entities WHERE identifier BETWEEN 3 and 6
     */
    public void testQueryWhereBetween()
    {
        userFactoryAndSave(10);

        ArrayList<User> users = this.dataStore.query(User.class).where("cod").between(3, 6).execute();
        assertEquals(4, users.size());
        assertEquals(3, users.get(0).getCod());
    }

    /**
     * Instantiate 4 Entities with distinct field values, save
     * Query all Entities WHERE field LIKE value, test the result
     */
    public void testQueryWhereLike()
    {
        User user1 = userFactory();
        user1.setName("Linus Benedict Torvalds");

        User user2 = userFactory();
        user2.setName("Charles William Bachman III");

        User user3 = userFactory();
        user3.setName("Charles Babbage");

        User user4 = userFactory();
        user4.setName("Leonard Max Adleman");

        this.dataStore.save(user1, user2, user3, user4);

        ArrayList<User> users = this.dataStore.query(User.class).where("name").like("charles").execute();
        assertEquals(2, users.size());
        assertEquals("Charles William Bachman III", users.get(0).getName());
        assertEquals("Charles Babbage", users.get(1).getName());
    }

    /**
     * Instantiate 4 Entities with distinct field values, save
     * Query all Entities WHERE field LIKE value OR field LIKE value, test the result
     */
    public void testQueryWhereOr()
    {
        User user1 = userFactory();
        user1.setName("Linus Benedict Torvalds");

        User user2 = userFactory();
        user2.setName("Charles William Bachman III");

        User user3 = userFactory();
        user3.setName("Charles Babbage");

        User user4 = userFactory();
        user4.setName("Leonard Max Adleman");

        this.dataStore.save(user1, user2, user3, user4);

        ArrayList<User> users = this.dataStore.query(User.class).where("name").like("charles").or("name").like("max").execute();
        assertEquals(3, users.size());
        assertEquals("Charles William Bachman III", users.get(0).getName());
        assertEquals("Charles Babbage", users.get(1).getName());
        assertEquals("Leonard Max Adleman", users.get(2).getName());
    }

    /**
     * Instantiate 5 Entities with distinct field values, save
     * Query all Entities WHERE field LIKE value AND field LIKE value, test the result
     */
    public void testQueryWhereAnd()
    {
        User user1 = userFactory();
        user1.setName("Linus Benedict Torvalds");

        User user2 = userFactory();
        user2.setName("Charles William Bachman III");

        User user3 = userFactory();
        user3.setName("Charles Babbage");

        User user4 = userFactory();
        user4.setName("Leonard Max Adleman");

        User user5 = userFactory();
        user5.setName("Charles Asimov Max Tinder");

        this.dataStore.save(user1, user2, user3, user4, user5);

        ArrayList<User> users = this.dataStore.query(User.class).where("name").like("charles").and("name").like("max").execute();
        assertEquals(1, users.size());
        assertEquals("Charles Asimov Max Tinder", users.get(0).getName());
    }

    /**
     * Instantiate 5 Entities with distinct fields values in wrong order, save
     * Query all Entities ORDER BY field DESC, test the order
     */
    public void testQueryOrderDesc()
    {
        User user;
        user = new User("A", "1", "14/11/1990");
        this.dataStore.save(user);
        user = new User("C", "3", "14/11/1990");
        this.dataStore.save(user);
        user = new User("B", "2", "14/11/1990");
        this.dataStore.save(user);
        user = new User("E", "5", "14/11/1990");
        this.dataStore.save(user);
        user = new User("D", "4", "14/11/1990");
        this.dataStore.save(user);

        ArrayList<User> users = this.dataStore.query(User.class).orderBy("name").desc().execute();
        assertEquals(5, users.size());
        assertEquals("E", users.get(0).getName());
        assertEquals("D", users.get(1).getName());
        assertEquals("C", users.get(2).getName());
        assertEquals("B", users.get(3).getName());
        assertEquals("A", users.get(4).getName());
    }

    /**
     * Instantiate 5 Entities with distinct field values in wrong order, save
     * Query all Entities ORDER BY field ASC, test the order
     */
    public void testQueryOrderAsc()
    {
        User user;
        user = new User("A", "1", "14/11/1990");
        this.dataStore.save(user);
        user = new User("C", "3", "14/11/1990");
        this.dataStore.save(user);
        user = new User("B", "2", "14/11/1990");
        this.dataStore.save(user);
        user = new User("E", "5", "14/11/1990");
        this.dataStore.save(user);
        user = new User("D", "4", "14/11/1990");
        this.dataStore.save(user);

        ArrayList<User> users = this.dataStore.query(User.class).orderBy("name").asc().execute();
        assertEquals(5, users.size());
        assertEquals("A", users.get(0).getName());
        assertEquals("B", users.get(1).getName());
        assertEquals("C", users.get(2).getName());
        assertEquals("D", users.get(3).getName());
        assertEquals("E", users.get(4).getName());
    }

    /**
     * Instantiate and save 10 Entities
     * Query all Entities with 5 LIMIT Entities
     */
    public void testQueryLimit()
    {
        userFactoryAndSave(10);

        ArrayList<User> users = this.dataStore.query(User.class).limit(1, 5).execute();
        assertEquals(5, users.size());
    }

    /**
     * Instantiate and save 10 Entities
     * Query all Entities WHERE identifier BIGGER than 3 and with 5 LIMIT
     */
    public void testQueryWhereLimit()
    {
        userFactoryAndSave(10);

        ArrayList<User> users = this.dataStore.query(User.class).where("cod").bigger(3).limit(3, 5).execute();
        assertEquals(4, users.size());
        assertEquals(7, users.get(0).getCod());
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
