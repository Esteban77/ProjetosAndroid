package com.senac.esteban.caixaremediosapp;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by vita on 22-11-2016.
 */
@DatabaseTable(tableName = "remedio")
public class Remedio implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String nome;

    @DatabaseField
    private String tipoDose;

    @DatabaseField
    private Double dose;

    @DatabaseField
    private Integer horaInicio;

    @DatabaseField
    private Integer minutosInicio;

    @DatabaseField
    private Double tempoLembrar;

    @DatabaseField
    private Integer qtDoses;

    @DatabaseField
    private Integer dosesContar;

    @DatabaseField
    private Integer contaNot;

    public Remedio(){};

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipoDose() {
        return tipoDose;
    }

    public void setTipoDose(String tipoDose) {
        this.tipoDose = tipoDose;
    }

    public Double getDose() {
        return dose;
    }

    public Integer getQtDoses() {
        return qtDoses;
    }

    public void setQtDoses(Integer qtDoses) {
        this.qtDoses = qtDoses;
    }

    public void setDose(Double dose) {
        this.dose = dose;
    }

    public Double getTempoLembrar() {
        return tempoLembrar;
    }

    public void setTempoLembrar(Double tempoLembrar) {
        this.tempoLembrar = tempoLembrar;
    }

    public Integer getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Integer horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Integer getMinutosInicio() {
        return minutosInicio;
    }

    public void setMinutosInicio(Integer minutosInicio) {
        this.minutosInicio = minutosInicio;
    }

    public Integer getDosesContar() {
        return dosesContar;
    }

    public void setDosesContar(Integer dosesContar) {
        this.dosesContar = dosesContar;
    }

    public Integer getContaNot() {
        return contaNot;
    }

    public void setContaNot(Integer contaNot) {
        this.contaNot = contaNot;
    }

    @Override
    public String toString() {
        return "Remedio: " + nome + "\n" +
                "Dose: " + dose +" "+ tipoDose + "\n" +
                "Qtd Doses: " + dosesContar+ "/"+qtDoses;
    }
}
