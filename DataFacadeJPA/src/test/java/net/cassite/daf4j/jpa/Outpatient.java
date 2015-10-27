package net.cassite.daf4j.jpa;

import net.cassite.daf4j.Data;
import net.cassite.daf4j.DataComparable;
import net.cassite.daf4j.DataUtils;

import javax.persistence.*;
import java.util.*;

@Entity
public class Outpatient {
    public DataComparable<Integer> id = new DataComparable<Integer>(this);
    public Data<String> judgenote = new Data<String>(this);
    public Data<String> treatnote = new Data<String>(this);
    public DataComparable<Integer> price = new DataComparable<Integer>(this);
    public Data<String> advice = new Data<String>(this);
    public DataComparable<Date> appointment = new DataComparable<Date>(this);
    public DataComparable<Date> addtime = new DataComparable<Date>(this);
    public Data<Patient> patient = new Data<Patient>(this);
    public Data<Clinic> clinic = new Data<Clinic>(this);
    public Data<User> user = new Data<User>(this);


    public Date getAddtime() {
        return addtime.get();
    }

    public void setAddtime(Date addtime) {
        DataUtils.set(this.addtime, addtime);
    }

    public String getJudgenote() {
        return judgenote.get();
    }

    public void setJudgenote(String judgenote) {
        DataUtils.set(this.judgenote, judgenote);
    }

    public String getTreatnote() {
        return treatnote.get();
    }

    public void setTreatnote(String treatnote) {
        DataUtils.set(this.treatnote, treatnote);
    }

    @Id
    @GeneratedValue
    public Integer getId() {
        return id.get();
    }

    public void setId(Integer id) {
        DataUtils.set(this.id, id);
    }

    public Integer getPrice() {
        return price.get();
    }

    public void setPrice(Integer price) {
        DataUtils.set(this.price, price);
    }

    public String getAdvice() {
        return advice.get();
    }

    public void setAdvice(String advice) {
        DataUtils.set(this.advice, advice);
    }

    public Date getAppointment() {
        return appointment.get();
    }

    public void setAppointment(Date appointment) {
        DataUtils.set(this.appointment, appointment);
    }

    @ManyToOne
    public Patient getPatient() {
        return patient.get();
    }

    public void setPatient(Patient patient) {
        DataUtils.set(this.patient, patient);
    }

    @ManyToOne
    public Clinic getClinic() {
        return clinic.get();
    }

    public void setClinic(Clinic clinic) {
        DataUtils.set(this.clinic, clinic);
    }

    @ManyToOne
    public User getUser() {
        return user.get();
    }

    public void setUser(User user) {
        DataUtils.set(this.user, user);
    }
}

	
	