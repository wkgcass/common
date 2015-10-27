package net.cassite.daf4j.jpa;

import net.cassite.daf4j.Data;
import net.cassite.daf4j.DataComparable;
import net.cassite.daf4j.DataIterable;
import net.cassite.daf4j.DataUtils;

import javax.persistence.*;
import java.util.*;

@Entity
public class Patient {
    public DataComparable<Integer> id = new DataComparable<Integer>(this);
    public Data<String> sn = new Data<String>(this);
    public Data<String> name = new Data<String>(this);
    public Data<String> mn = new Data<String>(this);
    public Data<String> gender = new Data<String>(this);
    public DataComparable<Integer> age = new DataComparable<Integer>(this);
    public Data<String> phone = new Data<String>(this);
    public Data<String> address = new Data<String>(this);
    public Data<String> qq = new Data<String>(this);
    public DataComparable<Date> addtime = new DataComparable<Date>(this);
    public Data<Clinic> clinic = new Data<Clinic>(this);
    public DataIterable<Outpatient, Set<Outpatient>> outpatients = new DataIterable<Outpatient, Set<Outpatient>>(new HashSet<Outpatient>(), this);


    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        DataUtils.set(this.address, address);
    }

    public void setOutpatients(Set<Outpatient> outpatients) {
        DataUtils.set(this.outpatients, outpatients);
    }

    @ManyToOne
    public Clinic getClinic() {
        return clinic.get();
    }

    public void setClinic(Clinic clinic) {
        DataUtils.set(this.clinic, clinic);
    }

    @OneToMany(mappedBy = "patient")
    public Set<Outpatient> getOutpatients() {
        return outpatients.get();
    }

    public void setOutpatient(Set<Outpatient> outpatients) {
        DataUtils.set(this.outpatients, outpatients);
    }

    @Id
    @GeneratedValue
    public Integer getId() {
        return id.get();
    }

    public void setId(Integer id) {
        DataUtils.set(this.id, id);
    }

    public String getSn() {
        return sn.get();
    }

    public void setSn(String sn) {
        DataUtils.set(this.sn, sn);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        DataUtils.set(this.name, name);
    }

    public String getMn() {
        return mn.get();
    }

    public void setMn(String mn) {
        DataUtils.set(this.mn, mn);
    }

    public String getGender() {
        return gender.get();
    }

    public void setGender(String gender) {
        DataUtils.set(this.gender, gender);
    }

    public Integer getAge() {
        return age.get();
    }

    public void setAge(Integer age) {
        DataUtils.set(this.age, age);
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        DataUtils.set(this.phone, phone);
    }

    public String getQq() {
        return qq.get();
    }

    public void setQq(String qq) {
        DataUtils.set(this.qq, qq);
    }

    public Date getAddtime() {
        return addtime.get();
    }

    public void setAddtime(Date addtime) {
        DataUtils.set(this.addtime, addtime);
    }


}
