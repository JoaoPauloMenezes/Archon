package com.joao.archon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ArchonDb.db";
    public static final String DATABASE_ID_NAME = "codigo";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE Pedidos " +
                        "(codigo integer primary key, estado integer)");
        db.execSQL("CREATE TABLE Estado_Pedido " +
                        "(codigo integer primary key, nome_estado varchar)");

        //Inicializando Tabela ESTADO_PEDIDO
        ContentValues contentValues = new ContentValues();
        contentValues.put("codigo", 0);
        contentValues.put("nome_estado", "Aguardando Inicio Entrega");
        db.insert("Estado_Pedido", null, contentValues);

        contentValues.put("codigo", 1);
        contentValues.put("nome_estado", "A Caminho do Destinatário");
        db.insert("Estado_Pedido", null, contentValues);

        contentValues.put("codigo", 2);
        contentValues.put("nome_estado", "Produto Entregue");
        db.insert("Estado_Pedido", null, contentValues);

    }

    public int numberOfRowsEstadoPedido(SQLiteDatabase db){
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "Estado_Pedido");
        if(numberOfRowsEstadoPedido(db) == 0) {
            numRows = (int) DatabaseUtils.queryNumEntries(db, "Estado_Pedido");
        }
        return numRows;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        onCreate(db);
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Pedidos where codigo="+id+"", null );
        return res;
    }

    public int getEstadoPedido(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Pedidos where codigo="+id+";", null );
        res.moveToFirst();
        return res.getInt(res.getColumnIndex("estado"));
    }

    public String getNomeEstadoPedido(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select Estado_Pedido.nome_estado, Pedidos.codigo from Estado_Pedido inner join Pedidos on Pedidos.estado = Estado_Pedido.codigo;", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            if(res.getInt(res.getColumnIndex("codigo")) == id)
                return res.getString(res.getColumnIndex("nome_estado"));
            res.moveToNext();
        }

        return "Não Encontrado";
    }

    public boolean setEstadoPedido(int id, int estado) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("estado",estado);

        db.update("Pedidos",contentValues,"codigo = "+id,null);
        return true;
    }

    public Cursor getUltimoPedido() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT codigo FROM Pedidos ORDER BY codigo DESC LIMIT 1",null);
        return res;
    }

    public boolean inserirPedido() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (numberOfRowsPedidos() == 0) {
            contentValues.put("codigo",1);
            contentValues.put("estado",0);
            db.insert("Pedidos", null, contentValues);
            return true;
        } else {
            contentValues.put("codigo", numberOfRowsPedidos()+1);
            contentValues.put("estado",0);
            db.insert("Pedidos", null, contentValues);
            return true;
        }
    }

    public boolean inserirPedido(Integer codigoPedido) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

            contentValues.put("codigo", codigoPedido);
            contentValues.put("estado",0);
            db.insert("Pedidos", null, contentValues);
            setEstadoPedido(codigoPedido, 0);
            return true;

    }

    public int numberOfRowsPedidos(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "Pedidos");
        return numRows;
    }



    public ArrayList<String> getTodosPedidos() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Pedidos", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("codigo")));
            res.moveToNext();
        }
        return array_list;
    }
}


