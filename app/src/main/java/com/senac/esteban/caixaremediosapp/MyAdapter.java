package com.senac.esteban.caixaremediosapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vita on 02-12-2016.
 */
public class MyAdapter extends BaseAdapter {

    private List<Remedio> listRemedio;
    private LayoutInflater inflater;
    private Context context;

    public MyAdapter(Context c, List<Remedio> lista){
        this.listRemedio = lista;
        inflater = LayoutInflater.from(c);
        context = c;
    }

    @Override
    public int getCount() {
        return listRemedio.size();
    }

    @Override
    public Object getItem(int position) {
        return listRemedio.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //Resgata remedio
        Remedio remedio = listRemedio.get(position);
        //Resgata layout do remedio
        view = inflater.inflate(R.layout.item_remedios,null);
        //Regata elementos do layout
        ImageView imagem = (ImageView) view.findViewById(R.id.imagemResultado);
        TextView tvDescricao = (TextView) view.findViewById(R.id.descricaoRemedio);

        //inserir dados do objeto produto no layout
        imagem.setImageResource(R.drawable.remedioicon4);
        tvDescricao.setText(context.getString(R.string.txt_Remedio)+ remedio.getNome() + "\n" +
                context.getString(R.string.txt_Dose)+ remedio.getDose() +" "+ remedio.getTipoDose() + "\n" +
                context.getString(R.string.txt_QtDose) + remedio.getDosesContar()+ "/"+remedio.getQtDoses());

        return view;
    }

    public void add(Remedio r){
        listRemedio.add(r);
        getView(getCount()-1,null,null);
    }
}
