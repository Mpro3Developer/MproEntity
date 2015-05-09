package br.com.mpro3.MproEntity;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Matheus Castello on 18/03/15.
 */

public class SQLBuilder
{
    ObjectInfo objectInfo;

    public SQLBuilder(){}

    public SQLBuilder(ObjectInfo objectInfo)
    {
        this.objectInfo = objectInfo;
    }

    public void setObjectInfo(ObjectInfo objectInfo)
    {
        this.objectInfo = objectInfo;
    }

    public String createTable()
    {
        return "CREATE TABLE IF NOT EXISTS " + this.objectInfo.Name.replaceAll("\\.", "_") + " (cod INTEGER PRIMARY KEY)";
    }

    public ArrayList<String> delete()
    {
        ArrayList<String> sqls = new ArrayList<String>();

        if(this.objectInfo.NameRef.equals(""))
        {
            sqls.add("DELETE FROM " + this.objectInfo.Name.replaceAll("\\.", "_") + " WHERE cod = " + this.objectInfo.Cod + ";");
            sqls.add("DELETE FROM Reference WHERE cod = " + this.objectInfo.Cod + "  AND class = '" + this.objectInfo.Name.replaceAll("\\.", "_") + "';");
        }
        else
        {
            sqls.add("DELETE FROM Reference WHERE cod = " + this.objectInfo.CodRef + " AND codref = " +
                        this.objectInfo.Cod + " AND class = '" +
                        this.objectInfo.Name.replaceAll("\\.", "_") + "' AND classref = '" + this.objectInfo.NameRef.replaceAll("\\.", "_") + "';");
        }

        return sqls;
    }

    public String insertRef()
    {
        return "INSERT INTO Reference VALUES('" +
                this.objectInfo.Name.replaceAll("\\.", "_") + "', '" +
                this.objectInfo.NameRef.replaceAll("\\.", "_") + "', " +
                this.objectInfo.Ix + ", " +
                this.objectInfo.Cod + ", " +
                this.objectInfo.CodRef + ");";
    }

    public String selectRef()
    {
        return "SELECT codref FROM Reference WHERE class = '" + this.objectInfo.Name.replaceAll("\\.", "_") + "' AND classref = '" + this.objectInfo.NameRef.replaceAll("\\.", "_") + "' AND cod = " + this.objectInfo.Cod;
    }

    private String getFields()
    {
        String fields = "";
        for(String f : this.objectInfo.Fields)
        {
            fields += f + ", ";
        }
        fields = fields.replaceFirst(", $", "");
        return fields;
    }

    private String getTypedFields()
    {
        String fields = "";
        for(int i = 0; i < this.objectInfo.FieldsValues.size(); i++)
        {
            fields += this.objectInfo.Types.get(i).equals("TEXT") ? "'" + this.objectInfo.FieldsValues.get(i) + "'" : this.objectInfo.FieldsValues.get(i);
            fields += ", ";
        }
        fields = fields.replaceFirst(", $", "");
        return fields;
    }

    private String getUpdateFields()
    {
        String fields = "";
        for(int i = 0; i < this.objectInfo.Fields.size(); i++)
        {
            fields += this.objectInfo.Fields.get(i) + " = " + (this.objectInfo.Types.get(i).equals("TEXT") ? "'" + this.objectInfo.FieldsValues.get(i) + "'" : this.objectInfo.FieldsValues.get(i));
            fields += ", ";
        }
        fields = fields.replaceFirst(", $", "");
        return fields;
    }

    private String implode(String separator, String... data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length - 1; i++) {
            //data.length - 1 => to not add separator at the end
            if (!data[i].matches(" *")) {//empty string are ""; " "; "  "; and so on
                sb.append(data[i]);
                sb.append(separator);
            }
        }
        sb.append(data[data.length - 1].trim());
        return sb.toString();
    }

    public String insert()
    {
        return "INSERT INTO " + this.objectInfo.Name.replaceAll("\\.", "_") + " VALUES(NULL, " + this.getTypedFields() + ")";
    }

    public String selectAll()
    {
        String sql = "";

        if(this.objectInfo.NameRef.equals(""))
        {
            sql = "SELECT * FROM " + this.objectInfo.Name.replaceAll("\\.", "_") + " "  + (this.objectInfo.Where.equals("") ? "ORDER BY " : (" WHERE (" + this.objectInfo.Where + ") ORDER BY ")) + this.objectInfo.OrderBy;
        }
        else
        {
            sql =  "SELECT * FROM " + this.objectInfo.Name.replaceAll("\\.", "_") + " WHERE cod in " +
            "(SELECT codref FROM Reference WHERE class = '" + this.objectInfo.NameRef.replaceAll("\\.", "_") + "' and cod = " +
                this.objectInfo.CodRef + " AND classref = '" + this.objectInfo.Name.replaceAll("\\.", "_") + "' " + (this.objectInfo.Ix != 2147483647 ? " AND ix = " + this.objectInfo.Ix + " " : "") + ") "
                + " " +
            (this.objectInfo.Where.equals("") ? "ORDER BY " : (" AND (" + this.objectInfo.Where + ") ORDER BY ")) + this.objectInfo.OrderBy;
        }

        if(this.objectInfo.Limiter.length == 2)
        {
            sql += " LIMIT " + this.objectInfo.Limiter[0] + ", " + this.objectInfo.Limiter[1];
        }

        return sql;
    }

    public String selectWhere()
    {
        String sql = "SELECT " + this.objectInfo.Name.replaceAll("\\.", "_") + ".cod, ";
        String sqlInner = "";
        String sqlWhere = "";

        for(int i = 0; i < this.objectInfo.Fields.size(); i++)
        {
            sql += this.objectInfo.Name.replaceAll("\\.", "_") + "." + this.objectInfo.Fields.get(i) + ", ";
        }

        sql = sql.replaceFirst(", $", "");
        sql += " FROM " + this.objectInfo.Name.replaceAll("\\.", "_") + " ";

        // inner joins
        for(int i = 0; i < this.objectInfo.NamesRef.size(); i++)
        {
            if(sqlInner.indexOf(this.objectInfo.Name.replaceAll("\\.", "_")) == -1)
                sqlInner +=     " INNER JOIN Reference ON Reference.cod = " +
                                this.objectInfo.Name.replaceAll("\\.", "_") + ".cod " +
                                "INNER JOIN " + this.objectInfo.NamesRef.get(i).replaceAll("\\.", "_") + " ON " +
                                this.objectInfo.NamesRef.get(i).replaceAll("\\.", "_") + ".cod = Reference.codref AND Reference.classref = '" +
                                this.objectInfo.NamesRef.get(i).replaceAll("\\.", "_") + "' ";

            sqlWhere +=     "" + this.objectInfo.NamesRef.get(i).replaceAll("\\.", "_") + "." + this.objectInfo.FieldsRef.get(i) + " " +
                            this.objectInfo.Comparators.get(i) + " " +
                            (this.objectInfo.LogicVals.get(i).isString() ? "'" +
                            (this.objectInfo.Comparators.get(i).equals(" LIKE ") ? "%" : "")
                            + this.objectInfo.LogicVals.get(i).getValue().toString()
                            + (this.objectInfo.Comparators.get(i).equals(" LIKE ") ? "%" : "") + "'" : this.objectInfo.LogicVals.get(i)) +
                            " " + (((this.objectInfo.LogicNexts.size() > 0) && (this.objectInfo.LogicNexts.get(i) == null)) ? "" : this.objectInfo.LogicNexts.get(i));
        }

        if(!sqlWhere.equals(""))
            sql += sqlInner + " WHERE " + sqlWhere + " " + this.objectInfo.OrderBy;
        else if(!this.objectInfo.Where.equals(""))
            sql += sqlInner + " WHERE " + this.objectInfo.Where;

        if(this.objectInfo.NamesRef.size() > 0)
            sql += " GROUP BY " + this.objectInfo.Name.replaceAll("\\.", "_") + ".cod ";

        if(!this.objectInfo.OrderBy.equals(""))
            sql += " ORDER BY " + this.objectInfo.OrderBy;

        if(this.objectInfo.Limiter.length == 2)
            sql += " LIMIT " + this.objectInfo.Limiter[0] + ", " + this.objectInfo.Limiter[1];

        return sql;
    }

    public String update()
    {
        String update = "UPDATE " + this.objectInfo.Name.replaceAll("\\.", "_") + " SET " + this.getUpdateFields();
        update += " WHERE cod = " + this.objectInfo.Cod;

        return update;
    }

    public ArrayList<String> alterTable(String[][] res)
    {
        ArrayList<String> sqlsField = new ArrayList<String>();
        ArrayList<String> sqlsAlter = new ArrayList<String>();
        boolean canTempDrop = false;

        ArrayList<String> fieldsFromDB = new ArrayList<String>();
        ArrayList<String> fieldsTypesFromDB = new ArrayList<String>();
        HashMap<String, Boolean> mapFromDB = new HashMap<String, Boolean>();

        for(int i = 1; i < res.length; i++)
        {
            mapFromDB.put(res[i][1], true);
        }

        for(int i = 0; i < this.objectInfo.Fields.size(); i++)
        {
            sqlsField.add("ALTER TABLE " + this.objectInfo.Name.replaceAll("\\.", "_") + " ADD " + this.objectInfo.Fields.get(i) + " " + this.objectInfo.Types.get(i));

            if(res.length > 1)
            {
                if (mapFromDB.get(this.objectInfo.Fields.get(i)))
                {
                    fieldsFromDB.add(this.objectInfo.Fields.get(i));
                    fieldsTypesFromDB.add(this.objectInfo.Types.get(i));
                }
                else
                {
                    canTempDrop = true;
                }
            }
        }

        if((res.length -1) > this.objectInfo.Fields.size())
        {
            canTempDrop = true;
        }

        if(canTempDrop)
        {
            sqlsAlter.add("CREATE TABLE back_" + this.objectInfo.Name.replaceAll("\\.", "_") + " (cod INTEGER PRIMARY KEY);");
            for(int i = 0; i < fieldsFromDB.size(); i++)
            {
                sqlsAlter.add("ALTER TABLE back_" + this.objectInfo.Name.replaceAll("\\.", "_") + " ADD " + fieldsFromDB.get(i) + " " + fieldsTypesFromDB.get(i));
            }
            sqlsAlter.add("INSERT INTO back_" + this.objectInfo.Name.replaceAll("\\.", "_") + " SELECT cod, " + (implode(",", (String[])fieldsFromDB.toArray())) + " FROM " + this.objectInfo.Name.replaceAll("\\.", "_") + ";");
            sqlsAlter.add("DROP TABLE " + this.objectInfo.Name.replaceAll("\\.", "_") + ";");
            sqlsAlter.add(this.createTable());
            sqlsAlter.addAll(sqlsField);
            sqlsAlter.add("INSERT INTO " + this.objectInfo.Name.replaceAll("\\.", "_") + "(cod, " + (implode(",", (String[])fieldsFromDB.toArray())) + ") SELECT cod, " + (implode(",", (String[]) fieldsFromDB.toArray())) + " FROM back_" + this.objectInfo.Name.replaceAll("\\.", "_") + ";");
            sqlsAlter.add("DROP TABLE back_" + this.objectInfo.Name.replaceAll("\\.", "_") + ";");
        }

        return sqlsField.size() > sqlsAlter.size() ? sqlsField : sqlsAlter;
    }

    public String createRefTable()
    {
        return "CREATE TABLE IF NOT EXISTS Reference (class TEXT, classref TEXT, ix INTEGER, cod INTEGER, codref INTEGER, PRIMARY KEY(class, classref, ix, cod, codref));";
    }

    public String describeTable()
    {
        return "PRAGMA table_info([" + this.objectInfo.Name.replaceAll("\\.", "_") + "])";
    }
}
