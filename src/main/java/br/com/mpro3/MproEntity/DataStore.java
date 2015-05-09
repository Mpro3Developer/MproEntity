package br.com.mpro3.MproEntity;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matheus Castello on 18/03/15.
 */

public class DataStore
{
    private DBManager dbManager;

    /**
     * Instantiate a DataStore object
     * Instantiate a SQLite Data Base Manager
     * @param context Activity context of application
     */
    public DataStore(Context context)
    {
        this.dbManager = new DBManager(context);
    }

    /**
     * Return the instance of SQLite Data Base Manager for direct sql instructions
     * @return Instance of SQLite Data Base Manager
     */
    public DBManager getDbManager()
    {
        return this.dbManager;
    }

    /**
     * Create a DataStore Query for find saved objects
     * @return query object for filter
     */
    public <T> Query query(Class<T> c)
    {
        try {
            return new Query<T>(c.newInstance(), this.dbManager);
        } catch (InstantiationException e) {
            Log.e("DataStore", "DataStore Exception", e);
        } catch (IllegalAccessException e) {
            Log.e("DataStore", "DataStore Exception", e);
        }
        throw new MproEntityError("DataStore Error");
    }

    /**
     * Delete object with Entity annotation.
     * Delete all reference of referenced entities collection.
     * @param obj Annotate MproEntity object
     */
    public void delete(Object obj)
    {
        ObjectInfo objectInfo = getInformation(obj);
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.setObjectInfo(objectInfo);

        ArrayList<String> cmds = sqlBuilder.delete();
        for(String cmd : cmds)
        {
            this.dbManager.execute(cmd);
        }

        try
        {
            Field fieldId = obj.getClass().getDeclaredField(objectInfo.FieldId);
            fieldId.setAccessible(true);
            fieldId.set(obj, 0);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save objects with Entity annotation
     * Save objects linked in array lists
     * @param objects Collection of annotate MproEntities objects
     */
    public void save(Object ... objects)
    {
        for(Object obj : objects)
        {
            this.save(obj);
        }
    }

    /**
     * Save object with Entity annotation, get information by reflection
     * and save objects linked in array lists
     * @param obj Object with Entity annotation
     * @return primary key generated by SQLite
     */
    public int save(Object obj)
    {
        ObjectInfo objectInfo = getInformation(obj);
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.setObjectInfo(objectInfo);

        // Create Table
        this.dbManager.execute(sqlBuilder.createTable());
        String[][] res = this.dbManager.query(sqlBuilder.describeTable());
        ArrayList<String> createCmds = sqlBuilder.alterTable(res);

        for (String cmd : createCmds)
        {
            try
            {
                this.dbManager.execute(cmd);
            }
            catch (SQLiteException ex)
            {
                Log.e("DataStore", "DataStore Exception", ex);
            }
        }

        // Save Instance
        objectInfo.FieldsValues = new ArrayList<String>();

        for(String field : objectInfo.Fields)
        {
            try
            {
                Field pfield = obj.getClass().getDeclaredField(field);
                pfield.setAccessible(true);
                objectInfo.FieldsValues.add(pfield.get(obj).toString());
            }
            catch (IllegalAccessException e)
            {
                Log.e("DataStore", "DataStore Exception", e);
            }
            catch (NoSuchFieldException e)
            {
                Log.e("DataStore", "DataStore Exception", e);
            }
        }

        String cmd = "";
        int ret = 0;

        try
        {
            Field pfield = obj.getClass().getDeclaredField(objectInfo.FieldId);
            pfield.setAccessible(true);
            objectInfo.Cod = pfield.getInt(obj);

            if(objectInfo.Cod == 0)
            {
                cmd = sqlBuilder.insert();
                this.dbManager.execute(cmd);
                ret = this.dbManager.getLastInsertRowId();
                pfield.set(obj, ret);
            }
            else
            {
                cmd = sqlBuilder.update();
                this.dbManager.execute(cmd);
                ret = objectInfo.Cod;
            }

            //objectInfo.NameRef = objectInfo.Name;
            objectInfo.Cod = ret;
            saveReference(obj, objectInfo);
        }
        catch (IllegalAccessException e)
        {
            Log.e("DataStore", "DataStore Exception", e);
        }
        catch (NoSuchFieldException e)
        {
            Log.e("DataStore", "DataStore Exception", e);
        }

        return ret;
    }

    /**
     * Save objects referenced in arrays from objects saved
     * @param obj Object with Entity annotation
     * @param objectInfo
     */
    private void saveReference(Object obj, ObjectInfo objectInfo)
    {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.setObjectInfo(objectInfo);

        this.dbManager.execute(sqlBuilder.createRefTable());

        Object[] keys = objectInfo.References.keySet().toArray();
        for(int i = 0; i < keys.length; i++)
        {
            try
            {
                Field pfield = obj.getClass().getDeclaredField((String) keys[i]);
                pfield.setAccessible(true);

                HashMap<Integer, Object> codMappeds = new HashMap<Integer, Object>();

                ArrayList<Object> refArray = (ArrayList<Object>) pfield.get(obj);
                for(Object object : refArray)
                {
                    int cod = this.save(object);
                    objectInfo.CodRef = cod;
                    codMappeds.put(cod, object);
                    objectInfo.NameRef = object.getClass().getName();
                    this.dbManager.execute(sqlBuilder.insertRef());
                }

                String[][] res = this.dbManager.query(sqlBuilder.selectRef());

                if(res != null)
                {
                    for (int j = 0; j < res.length; j++)
                    {
                        if (!codMappeds.containsKey(Integer.parseInt(res[j][0])))
                        {
                            objectInfo.Cod = Integer.parseInt(res[j][0]);
                            ArrayList<String> cmds = sqlBuilder.delete();
                            for (String cmd : cmds)
                            {
                                this.dbManager.execute(cmd);
                            }
                        }
                    }
                }
            }
            catch (IllegalAccessException e)
            {
                Log.e("DataStore", "DataStore Exception", e);
            }
            catch (NoSuchFieldException e)
            {
                Log.e("DataStore", "DataStore Exception", e);
            }
        }
    }

    /**
     * Take information from fields using reflection.
     * @param obj Object with Entity annotation
     * @return object with fields names and field types, reference objects in arrays
     */
    protected static ObjectInfo getInformation(Object obj)
    {
        ObjectInfo oi = new ObjectInfo();
        Entity e = obj.getClass().getAnnotation(Entity.class);

        if(e != null)
        {
            oi.Name = obj.getClass().getName();
            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field f : fields)
            {
                Transient t = f.getAnnotation(Transient.class);
                if(t == null)
                {
                    Reference r = f.getAnnotation(Reference.class);
                    if(r == null)
                    {
                        Id id = f.getAnnotation(Id.class);
                        if(id == null)
                        {
                            String type = getTypeName(f.getType());
                            if (type.equals(""))
                                throw new Error("Field " + f.getName() + " not contain a primitive type and is not a reference.");
                            oi.Fields.add(f.getName());
                            oi.Types.add(type);
                        }
                        else
                        {
                            oi.FieldId = f.getName();
                            f.setAccessible(true);
                            try
                            {
                                oi.Cod = Integer.parseInt(f.get(obj).toString());
                            }
                            catch (IllegalAccessException er)
                            {
                                er.printStackTrace();
                            }
                        }
                    }
                    else
                    {
                        ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
                        oi.References.put(f.getName(), ((Class)(parameterizedType.getActualTypeArguments()[0])).getName());
                        //Log.d("SAFERA", ((Class)(parameterizedType.getActualTypeArguments()[0])).getSimpleName());
                    }
                }
            }
        }
        else
            throw new MproEntityError("Object instance is not an Entity");

        if(oi.FieldId.equals(""))
            throw new MproEntityError("An Entity must have a @Id integer field mapped");

        return oi;
    }

    /**
     * Resolve a SQLite type for a class type input
     * @param type class input
     * @return SQLite type string
     */
    protected static String getTypeName(Class type)
    {
        if(type == String.class)
            return "TEXT";
        else if(type == double.class || type == float.class ||  type == int.class)
            return  "NUMERIC";

        return "";
    }
}