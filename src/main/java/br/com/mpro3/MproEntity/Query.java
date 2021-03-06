package br.com.mpro3.MproEntity;

import android.util.Log;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Class that builds queries.
 * <p>Simple Example: Select all Books where name like java, and author like james:  <pre>
 * {@code
 * ArrayList<Book> books = new DataStore()
 *                              .query(Book.class)
 *                                    .where("name").like("java")
 *                                    .and("author").like("james")
 *                              .execute();
 * }
 * </pre> Reference Example: Select all Loans where name of Book in the Loan is like java
 * and author of Book in the loan is like james: <br><br><pre>
 * {@code
 * ArrayList<Book> books = new DataStore()
 *                              .query(Loan.class)
 *                                    .where(Book.class, "name").like("java")
 *                                    .and(Book.class, "author").like("james")
 *                              .execute();
 * }
 * </pre></p>
 * @param <T> The Entity class type from entities that will be returned from query
 */
public class Query<T>
{
    private DBManager dbManager;
    private SQLBuilder sqlBuilder;
    private Class c;
    private ArrayList<String> steps = new ArrayList<String>();
    private String queryString = "";
    private ObjectInfo objectInfo;

    /**
     * Instantiate one MproEntity automatic query builder
     * @param c Class of query Entity
     * @param dbManager SQLite data base manager
     */
    public Query(T c, DBManager dbManager)
    {
        this.dbManager = dbManager;
        this.c = c.getClass();
        getInfoT();
    }

    /**
     * Instantiate one MproEntity automatic query builder
     * @param dbManager SQLite data base manager
     */
    public Query(DBManager dbManager)
    {
        this.dbManager = dbManager;
        getInfoT();
    }

    private void getInfoT()
    {
        try
        {
            T c = newInstanceOfT();
            this.sqlBuilder = new SQLBuilder();
            this.objectInfo = DataStore.getInformation(c.getClass().newInstance());
            this.sqlBuilder.setObjectInfo(this.objectInfo);
        } catch (InstantiationException e) {
            Log.e("DataStore", "DataStore Exception", e);
        } catch (IllegalAccessException e) {
            Log.e("DataStore", "DataStore Exception", e);
        }
    }

    private T newInstanceOfT() throws InstantiationException, IllegalAccessException
    {
        return (T) this.c.newInstance();
    }

    private boolean checkThat()
    {
        return  steps.get(steps.size() -1).equals("eq") ||
                steps.get(steps.size() -1).equals("neq") ||
                steps.get(steps.size() -1).equals("like") ||
                steps.get(steps.size() -1).equals("bigger") ||
                steps.get(steps.size() -1).equals("less") ||
                steps.get(steps.size() -1).equals("biggerEq") ||
                steps.get(steps.size() -1).equals("lessEq") ||
                steps.get(steps.size() -1).equals("between");
    }

    private boolean checkOperator()
    {
        return  steps.get(steps.size() -1).equals("and") ||
                steps.get(steps.size() -1).equals("or");
    }

    /**
     * Add WHERE filter instruction in one query
     * @param cl The class of referenced Entity filtered
     * @param field The desired field for filter in referenced Entity
     * @return The query with where filter added
     * @throws NoSuchFieldException
     */
    public Query where(Class cl, String field) throws NoSuchFieldException
    {
        if(steps.size() == 0)
        {
            steps.add("whereo");
            this.objectInfo.NamesRef.add(cl.getName());
            cl.getDeclaredField(field);
            this.objectInfo.FieldsRef.add(field);
        }
        else
        {
            throw new MproEntityError("(where) can not be called after other method");
        }
        return this;
    }

    /**
     * Add WHERE filter instruction in one query
     * @param field The desired field for filter in Entity
     * @return The query with where filter added
     */
    public Query where(String field)
    {
        if(steps.size() == 0)
        {
            steps.add("where");
            queryString += "" + field + " ";
        }
        else
        {
            throw new MproEntityError("(where) can not be called after other method");
        }
        return this;
    }

    /**
     * Add EQUAL comparator in one query, use after {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)}
     * @param value The value filter for {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} seted field
     * @return The query with equal filter added
     * @throws MproEntityError in case of use without call (where, and, or) before
     */
    public Query equal(Object value)
    {
        if(steps.size() > 0 && (steps.get(steps.size() -1).equals("where") || checkOperator()) && !steps.contains("whereo"))
        {
            queryString += " = " + (value.getClass().getName().equals("String") ? "'" + value.toString() + "'" : value.toString()) + " ";
        }
        else if(steps.contains("whereo") && !checkThat())
        {
            this.objectInfo.Comparators.add(" = ");
            this.objectInfo.LogicVals.add(new Value(value));
            this.objectInfo.LogicNexts.add(null);
        }
        else
            throw new MproEntityError("(equal) only can be called after where or after (or, and)");

        steps.add("eq");

        return this;
    }

    /**
     * Add NOT EQUAL comparator in one query, use after {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)}
     * @param value The value filter for {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} seted field
     * @return The query with not equal filter added
     * @throws MproEntityError in case of use without call (where, and, or) before
     */
    public Query notEqual(Object value)
    {
        if(steps.size() > 0 && (steps.get(steps.size() -1).equals("where") || checkOperator()) && !steps.contains("whereo"))
        {
            queryString += " <> " + (value.getClass().getName().equals("String") ? "'" + value.toString() + "'" : value.toString()) + " ";
        }
        else if(steps.contains("whereo") && !checkThat())
        {
            this.objectInfo.Comparators.add(" <> ");
            this.objectInfo.LogicVals.add(new Value(value));
            this.objectInfo.LogicNexts.add(null);
        }
        else
            throw new MproEntityError("(notEqual) only can be called after where or after (or, and)");

        steps.add("neq");

        return this;
    }

    /**
     * Add LIKE comparator in one query, use after {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)}
     * @param value The value filter for {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} seted field
     * @return The query with like filter added
     * @throws MproEntityError in case of use without call (where, and, or) before
     */
    public Query like(Object value)
    {
        if(steps.size() > 0 && (steps.get(steps.size() -1).equals("where") || checkOperator()) && !steps.contains("whereo"))
        {
            queryString += " LIKE " + (value.getClass().getSimpleName().equals("String") ? "'%" + value.toString() + "%'" : value.toString()) + " ";
        }
        else if(steps.contains("whereo") && !checkThat())
        {
            this.objectInfo.Comparators.add(" LIKE ");
            this.objectInfo.LogicVals.add(new Value(value));
            this.objectInfo.LogicNexts.add(null);
        }
        else
            throw new MproEntityError("(like) only can be called after where or after (or, and)");

        steps.add("like");

        return this;
    }

    /**
     * Add BETWEEN comparator in one query, use after {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)}
     * @param value1 The first value filter for {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} seted field
     * @param value2 The second value filter for {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} seted field
     * @return The query with between filter added
     * @throws MproEntityError in case of use without call (where, and, or) before
     */
    public Query between(Object value1, Object value2)
    {
        if(steps.size() > 0 && (steps.get(steps.size() -1).equals("where") || checkOperator()) && !steps.contains("whereo"))
        {
            queryString += " BETWEEN " +
                    (value1.getClass().getName().equals("String") ? "'%" + value1.toString() + "%'" : value1.toString()) +
                    " AND " +
                    (value2.getClass().getName().equals("String") ? "'%" + value1.toString() + "%'" : value2.toString()) + " ";
        }
        else if(steps.contains("whereo") && !checkThat())
        {
            this.objectInfo.Comparators.add(" BETWEEN " +
                    (value1.getClass().getName().equals("String") ? "'%" + value1.toString() + "%'" : value1.toString()) +
                    " AND ");
            this.objectInfo.LogicVals.add(new Value(value2));
            this.objectInfo.LogicNexts.add(null);
        }
        else
            throw new MproEntityError("(between) only can be called after where or after (or, and)");

        steps.add("between");

        return this;
    }

    /**
     * Add EQUAL comparator in one query, use after {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)}
     * @param value The value filter for {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} seted field
     * @return The query with equal filter added
     * @throws MproEntityError in case of use without call {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} before
     */
    public Query bigger(Object value)
    {
        if(steps.size() > 0 && (steps.get(steps.size() -1).equals("where") || checkOperator()) && !steps.contains("whereo"))
        {
            queryString += " > " + (value.getClass().getName().equals("String") ? "'" + value.toString() + "'" : value.toString()) + " ";
        }
        else if(steps.contains("whereo") && !checkThat())
        {
            this.objectInfo.Comparators.add(" > ");
            this.objectInfo.LogicVals.add(new Value(value));
            this.objectInfo.LogicNexts.add(null);
        }
        else
            throw new MproEntityError("(bigger) only can be called after where or after (or, and)");

        steps.add("bigger");

        return this;
    }

    /**
     * Add LESS comparator in one query, use after {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)}
     * @param value The value filter for {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} seted field
     * @return The query with less filter added
     * @throws MproEntityError in case of use without call {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} before
     */
    public Query less(Object value)
    {
        if(steps.size() > 0 && (steps.get(steps.size() -1).equals("where") || checkOperator()) && !steps.contains("whereo"))
        {
            queryString += " < " + (value.getClass().getName().equals("String") ? "'" + value.toString() + "'" : value.toString()) + " ";
        }
        else if(steps.contains("whereo") && !checkThat())
        {
            this.objectInfo.Comparators.add(" < ");
            this.objectInfo.LogicVals.add(new Value(value));
            this.objectInfo.LogicNexts.add(null);
        }
        else
            throw new MproEntityError("(less) only can be called after where or after (or, and)");

        steps.add("less");

        return this;
    }

    /**
     * Add BIGGER AND EQUAL comparator in one query, use after {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)}
     * @param value The value filter for {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} seted field
     * @return The query with bigger and equal filter added
     * @throws MproEntityError in case of use without call {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} before
     */
    public Query biggerAndEqual(Object value)
    {
        if(steps.size() > 0 && (steps.get(steps.size() -1).equals("where") || checkOperator()) && !steps.contains("whereo"))
        {
            queryString += " >= " + (value.getClass().getName().equals("String") ? "'" + value.toString() + "'" : value.toString()) + " ";
        }
        else if(steps.contains("whereo") && !checkThat())
        {
            this.objectInfo.Comparators.add(" >= ");
            this.objectInfo.LogicVals.add(new Value(value));
            this.objectInfo.LogicNexts.add(null);
        }
        else
            throw new MproEntityError("(biggerAndEqual) only can be called after where or after (or, and)");

        steps.add("biggerEq");

        return this;
    }

    /**
     * Add LESS AND EQUAL comparator in one query, use after {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)}
     * @param value The value filter for {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} seted field
     * @return The query with less and equal filter added
     * @throws MproEntityError in case of use without call {@see br.com.mpro3.MproEntity.Query#where(String)},
     * {@see br.com.mpro3.MproEntity.Query#and(String)}, {@see br.com.mpro3.MproEntity.Query#or(String)} before
     */
    public Query lessAndEqual(Object value)
    {
        if(steps.size() > 0 && (steps.get(steps.size() -1).equals("where") || checkOperator()) && !steps.contains("whereo"))
        {
            queryString += " <= " + (value.getClass().getName().equals("String") ? "'" + value.toString() + "'" : value.toString()) + " ";
        }
        else if(steps.contains("whereo") && !checkThat())
        {
            this.objectInfo.Comparators.add(" <= ");
            this.objectInfo.LogicVals.add(new Value(value));
            this.objectInfo.LogicNexts.add(null);
        }
        else
            throw new MproEntityError("(lessAndEqual) only can be called after where or after (or, and)");

        steps.add("lessEq");

        return this;
    }

    /**
     * Add AND operator in one query, user after {@see br.com.mpro3.MproEntity.Query#like(Object)},
     * {@see br.com.mpro3.MproEntity.Query#equal(Object)}, {@see br.com.mpro3.MproEntity.Query#bigger(Object)},
     * {@see br.com.mpro3.MproEntity.Query#less(Object)} etc ...
     * @param field The desired field for filter in Entity
     * @return The query with "and" operator added
     * @throws MproEntityError in case of use without call {@see br.com.mpro3.MproEntity.Query#like(Object)},
     * {@see br.com.mpro3.MproEntity.Query#equal(Object)}, {@see br.com.mpro3.MproEntity.Query#bigger(Object)},
     * {@see br.com.mpro3.MproEntity.Query#less(Object)} etc ... before
     */
    public Query and(String field)
    {
        if(steps.size() > 0 && checkThat() && !steps.contains("whereo"))
        {
            queryString += "AND " + field + " ";
        }
        else if(checkThat() && steps.contains("whereo"))
        {
            this.objectInfo.LogicNexts.set(this.objectInfo.LogicNexts.size() -1, " AND ");
        }
        else
            throw new MproEntityError("(and) only can be called after (like, equal, bigger, less etc ...)");

        steps.add("and");

        return this;
    }

    /**
     * Add AND operator in one query, user after {@see br.com.mpro3.MproEntity.Query#like(Object)},
     * {@see br.com.mpro3.MproEntity.Query#equal(Object)}, {@see br.com.mpro3.MproEntity.Query#bigger(Object)},
     * {@see br.com.mpro3.MproEntity.Query#less(Object)} etc ...
     * @param cl The Entity class type for query in referenced entities from the Entity passed in this instance of {@see br.com.mpro3.MproEntity.Query}
     * @param field The desired field for filter in referenced Entity
     * @return The query with "and" operator added
     * @throws MproEntityError in case of use without call {@see br.com.mpro3.MproEntity.Query#like(Object)},
     * {@see br.com.mpro3.MproEntity.Query#equal(Object)}, {@see br.com.mpro3.MproEntity.Query#bigger(Object)},
     * {@see br.com.mpro3.MproEntity.Query#less(Object)} etc ... before
     * @throws NoSuchFieldException in case of field name passed as parameter not exist in the Entity referenced
     */
    public Query and(Class cl, String field) throws NoSuchFieldException
    {
        if(checkThat() && steps.contains("whereo"))
        {
            this.objectInfo.LogicNexts.set(this.objectInfo.LogicNexts.size() -1, " AND ");
            this.objectInfo.NamesRef.add(cl.getName());
            cl.getDeclaredField(field);
            this.objectInfo.FieldsRef.add(field);
        }
        else
            throw new MproEntityError("(and) only can be called after (like, equal, bigger, less etc ...)");

        steps.add("or");

        return this;
    }

    /**
     * Add OR operator in one query, user after {@see br.com.mpro3.MproEntity.Query#like(Object)},
     * {@see br.com.mpro3.MproEntity.Query#equal(Object)}, {@see br.com.mpro3.MproEntity.Query#bigger(Object)},
     * {@see br.com.mpro3.MproEntity.Query#less(Object)} etc ...
     * @param field The desired field for filter in Entity
     * @return The query with "or" operator added
     * @throws MproEntityError in case of use without call {@see br.com.mpro3.MproEntity.Query#like(Object)},
     * {@see br.com.mpro3.MproEntity.Query#equal(Object)}, {@see br.com.mpro3.MproEntity.Query#bigger(Object)},
     * {@see br.com.mpro3.MproEntity.Query#less(Object)} etc ... before
     */
    public Query or(String field)
    {
        if(steps.size() > 0 && checkThat() && !steps.contains("whereo"))
        {
            queryString += "OR " + field + " ";
        }
        else if(checkThat() && steps.contains("whereo"))
        {
            throw new MproEntityError("(or) used without class reference in (where) with reference");
        }
        else
            throw new MproEntityError("(and) only can be called after (like, equal, bigger, less etc ...)");

        steps.add("or");

        return this;
    }

    /**
     * Add OR operator in one query, user after {@see br.com.mpro3.MproEntity.Query#like(Object)},
     * {@see br.com.mpro3.MproEntity.Query#equal(Object)}, {@see br.com.mpro3.MproEntity.Query#bigger(Object)},
     * {@see br.com.mpro3.MproEntity.Query#less(Object)} etc ...
     * @param cl The Entity class type for query in referenced entities from the Entity passed in this instance of {@see br.com.mpro3.MproEntity.Query}
     * @param field The desired field for filter in referenced Entity
     * @return The query with "or" operator added
     * @throws MproEntityError in case of use without call {@see br.com.mpro3.MproEntity.Query#like(Object)},
     * {@see br.com.mpro3.MproEntity.Query#equal(Object)}, {@see br.com.mpro3.MproEntity.Query#bigger(Object)},
     * {@see br.com.mpro3.MproEntity.Query#less(Object)} etc ... before
     * @throws NoSuchFieldException in case of field name passed as parameter not exist in the Entity referenced
     */
    public Query or(Class cl, String field) throws NoSuchFieldException
    {
        if(checkThat() && steps.contains("whereo"))
        {
            this.objectInfo.LogicNexts.set(this.objectInfo.LogicNexts.size() -1, " OR ");
            this.objectInfo.NamesRef.add(cl.getName());
            cl.getDeclaredField(field);
            this.objectInfo.FieldsRef.add(field);
        }
        else
            throw new MproEntityError("(or) only can be called after (like, equal, bigger, less etc ...)");

        steps.add("or");

        return this;
    }

    /**
     * Add ORDER BY command in one query, use {@see br.com.mpro3.MproEntity.Query#asc()} to order by field ascending
     * or {@see br.com.mpro3.MproEntity.Query#desc()} to order by field descending after
     * @param field The desired field to order
     * @return The query with "orderBy" command added
     */
    public Query orderBy(String field)
    {
        this.objectInfo.OrderBy = " " + field;
        steps.add("order");
        return this;
    }

    /**
     * Add ASC operator in one query, for order by ascending.
     * Use after {@see br.com.mpro3.MproEntity.Query#orderBy(String)}
     * @return The query with "asc" operator added
     * @throws MproEntityError in case of use without call {@see br.com.mpro3.MproEntity.Query#orderBy(String)} before
     */
    public Query asc()
    {
        if(steps.get(steps.size() -1).equals("order"))
        {
            this.objectInfo.OrderBy += " ASC";
        }
        else
            throw new MproEntityError("(asc) only can be called after (orderBy)");
        steps.add("asc");
        return this;
    }

    /**
     * Add DESC operator in one query, for order by descending.
     * Use after {@see br.com.mpro3.MproEntity.Query#orderBy(String)}
     * @return The query with "asc" operator added
     * @throws MproEntityError in case of use without call {@see br.com.mpro3.MproEntity.Query#orderBy(String)} before
     */
    public Query desc()
    {
        if(steps.get(steps.size() -1).equals("order"))
        {
            this.objectInfo.OrderBy += " DESC";
        }
        else
            throw new MproEntityError("(desc) only can be called after (orderBy)");
        steps.add("desc");
        return this;
    }

    /**
     * Add LIMIT operator in one query, limiting the begin and the size of query result between the inferior and superior limit
     * @param limitInf The inferior limit
     * @param limitSup The superior limit
     * @return The query with "limit" operator added
     */
    public Query limit(int limitInf, int limitSup)
    {
        this.objectInfo.Limiter = new int[2];
        this.objectInfo.Limiter[0] = limitInf;
        this.objectInfo.Limiter[1] = limitSup;
        steps.add("limit");
        return this;
    }

    private boolean checkTuples()
    {
        if(steps.size() > 0)
        {
            if(steps.size() % 2 == 0)
            {
                return true;
            }
            else if(steps.get(steps.size() -1).equals("limit"))
            {
                return true;
            }
            else
                return false;
        }
        return true;
    }

    /**
     * Translate the query calls in sql commands and execute
     * @return The collection of entities that satisfy the query executed
     * @throws MproEntityError in case of incomplete call order from the query methods
     */
    public ArrayList<T> execute()
    {
        if(checkTuples())
        {
            this.objectInfo.Where = this.queryString;
            ArrayList<T> res = new ArrayList<T>();
            String sql = this.sqlBuilder.selectWhere();
            String[][] sqlRes = this.dbManager.query(sql);

            for(int i = 0; i < sqlRes.length; i++)
            {
                try
                {
                    T o = newInstanceOfT();

                    Field fieldid = o.getClass().getDeclaredField(this.objectInfo.FieldId);
                    fieldid.setAccessible(true);
                    fieldid.set(o, Integer.parseInt(sqlRes[i][0]));
                    this.objectInfo.Cod = Integer.parseInt(sqlRes[i][0]);

                    for(int j = 0; j < this.objectInfo.Fields.size(); j++)
                    {
                        Field field = o.getClass().getDeclaredField(this.objectInfo.Fields.get(j));
                        field.setAccessible(true);
                        field.set(o, (new Value(field.getType().getSimpleName(), sqlRes[i][(j+1)])).getValue());
                    }

                    Object[] keys = objectInfo.References.keySet().toArray();
                    for(int k = 0; k < keys.length; k++)
                    {
                        ArrayList<Object> refObjects = new ArrayList<Object>();
                        this.objectInfo.NameRef = objectInfo.References.get(keys[k]);
                        String[][] refCodes =  this.dbManager.query(this.sqlBuilder.selectRef());

                        for(int x = 0; x < refCodes.length; x++)
                        {
                            refObjects.add
                            (
                                (
                                    new Query(Class.forName(this.objectInfo.NameRef).newInstance(), this.dbManager)
                                        .where("cod")
                                        .equal(Integer.parseInt(refCodes[x][0]))
                                        .execute()
                                ).get(0)
                            );
                        }

                        Field field = o.getClass().getDeclaredField(keys[k].toString());
                        field.setAccessible(true);
                        field.set(o, refObjects);
                    }

                    res.add(o);

                } catch (InstantiationException e) {
                    Log.e("DataStore", "DataStore Exception", e);
                } catch (IllegalAccessException e) {
                    Log.e("DataStore", "DataStore Exception", e);
                } catch (NoSuchFieldException e) {
                    Log.e("DataStore", "DataStore Exception", e);
                } catch (ClassNotFoundException e){
                    Log.e("DataStore", "DataStore Exception", e);
                }
            }

            return res;
        }

        throw new MproEntityError("Your query appears incomplete. Make sure you are not missing after (where, or, and) one comparator (like, equal, notEqual, bigger, less, between) or missing after (orderBy) one order (asc, desc)");
    }
}
