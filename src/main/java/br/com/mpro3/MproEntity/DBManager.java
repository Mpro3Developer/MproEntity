    package br.com.mpro3.MproEntity;


    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteConstraintException;
    import android.database.sqlite.SQLiteDatabase;

    import java.util.Arrays;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * Created by Matheus Castello on 18/03/15.
     */

    public class DBManager
    {
        private SQLiteDatabase connection;
        private Cursor sql;
        private int numRows;
        private int numCollsl;
        private String[] elem;
        private String[][] elems;

        /**
         * Instantiate a SQLite Data Base Manager with methods for execute sql instructions
         * @param context Activity application context
         */
        public DBManager(Context context)
        {
            // DEBUG: create SQLite file in one no root readable and writable folder for debug
            /*File dir = new File(Environment.getExternalStorageDirectory() + "/Android_data/br.com.mpro3/MproEntity/lauses/");
            dir.mkdirs();
            connection = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory() + "/Android_data/br.com.mpro3/MproEntity/lauses/jussa.lau", null);
            connection.execSQL("PRAGMA synchronous=OFF");*/

            // CORRECT WAY: using SQLite Open Helper
            CustomSQLiteOpenHelper customSQLiteOpenHelper = new CustomSQLiteOpenHelper(context);
            this.connection = customSQLiteOpenHelper.getWritableDatabase();
            connection.execSQL("PRAGMA synchronous=OFF");
        }

        /**
         * Execute sql query with return data
         * @param cmd SQL query
         * @return 2D Array with row/column result of sql query
         */
        public String[][] query(String cmd)
        {
            sql = this.connection.rawQuery(cmd, null);
            this.elems = this.fetchAll(sql);
            return this.elems;
        }

        private String[][] fetchAll(Cursor res)
        {
            int count = 0;
            this.numCollsl = res.getColumnCount();
            String[][] elemTmp = new String[1][this.numCollsl];
            if(res.moveToFirst())
            {
                do
                {
                    elemTmp = Arrays.copyOf(elemTmp, count +1);
                    elemTmp[count] = new String[this.numCollsl];
                    for(int i = 0; i < res.getColumnCount(); i++)
                    {
                        elemTmp[count][i] = res.getString(i);
                    }
                    count++;
                }
                while(res.moveToNext());
                this.numRows = count;
            }

            if(count > 0)
                return elemTmp;
            else
                return new String[0][0];
        }

        /**
         * Execute sql query and preserve the cursor returned
         * @param cmd SQL query
         * @return true for successful executed query
         */
        public boolean unQuery(String cmd)
        {
            this.sql = this.connection.rawQuery(cmd, null);
            this.numCollsl = this.sql.getColumnCount();
            return true;
        }

        /**
         * Return the value of one column in one specific row
         * @param row The row of table
         * @param column The column with desired value
         * @return Value of one column in one specific row
         */
        public String row(int row, int column)
        {
            String tmp = "";
            tmp = this.elems[row][column];
            return tmp;
        }

        /**
         * Get the prox row in one query
         * @return The columns Array data of row
         */
        public String[] prox()
        {
            this.elem = new String[this.numCollsl];
            if(this.sql.moveToNext())
            {
                for(int i = 0; i < this.numCollsl; i++)
                {
                    this.elem[i] = sql.getString(i);
                }
                return this.elem;
            }
            return null;
        }

        /**
         * Get the momentum row of one sql query
         * @return The collumns Array data of row
         */
        public String[] getElem()
        {
            return this.elem;
        }

        /**
         * Execute one sql instruction with no return data
         * @param cmd SQL instruction
         */
        public void execute(String cmd)
        {
            try
            {
                this.connection.execSQL(cmd);
            }
            catch (SQLiteConstraintException ex)
            {
                Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Get the last generated identifier
         * @return Last generated identifier
         */
        public int getLastInsertRowId()
        {
            this.unQuery("SELECT last_insert_rowid();");
            if(sql.moveToFirst())
                return this.sql.getInt(0);
            return 0;
        }

        /**
         * Get the number of rows returned from the last sql query
         * @return Number of rows
         */
        public int count()
        {
            return this.numRows;
        }

        /**
         * Close the connection with the data base file
         */
        public void close()
        {
            this.connection.close();
        }
    }
