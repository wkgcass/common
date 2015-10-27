package net.cassite.daf4j.jpa;

import net.cassite.daf4j.Data;
import net.cassite.daf4j.DataComparable;
import net.cassite.daf4j.DataIterable;
import net.cassite.daf4j.DataUtils;

import javax.persistence.*;
import java.util.*;

@Entity
public class Clinic {
        public DataComparable<Integer> id = new DataComparable<Integer>(this);
        public Data<String> name = new Data<String>(this);
        public Data<String> leader = new Data<String>(this);
        public Data<String> phone = new Data<String>(this);
        public Data<String> address = new Data<String>(this);
        public DataComparable<Integer> number = new DataComparable<Integer>(this);
        public DataComparable<Date> addtime = new DataComparable<Date>(this);
        public DataComparable<Date> deadtime = new DataComparable<Date>(this);
        public DataIterable<Patient, Set<Patient>> patients = new DataIterable<Patient, Set<Patient>>(new HashSet<Patient>(), this);
        public DataIterable<User, Set<User>> users = new DataIterable<User, Set<User>>(new HashSet<User>(), this);
        public DataIterable<Outpatient, Set<Outpatient>> outpatients = new DataIterable<Outpatient, Set<Outpatient>>(new HashSet<Outpatient>(), this);

        @OneToMany(mappedBy = "clinic")
        public Set<Patient> getPatients() {
                return patients.get();
        }

        public void setPatients(Set<Patient> patients) {
                DataUtils.set(this.patients, patients);
        }

        @OneToMany(mappedBy = "clinic")
        public Set<User> getUsers() {
                return users.get();
        }

        public void setUsers(Set<User> users) {
                DataUtils.set(this.users, users);
        }

        @OneToMany(mappedBy = "clinic")
        public Set<Outpatient> getOutpatients() {
                return outpatients.get();
        }

        public void setOutpatients(Set<Outpatient> outpatients) {
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

        public String getName() {
                return name.get();
        }

        public void setName(String name) {
                DataUtils.set(this.name, name);
        }

        public String getLeader() {
                return leader.get();
        }

        public void setLeader(String leader) {
                DataUtils.set(this.leader, leader);
        }

        public String getPhone() {
                return phone.get();
        }

        public void setPhone(String phone) {
                DataUtils.set(this.phone, phone);
        }

        public String getAddress() {
                return address.get();
        }

        public void setAddress(String address) {
                DataUtils.set(this.address, address);
        }

        public Integer getNumber() {
                return number.get();
        }

        public void setNumber(Integer number) {
                DataUtils.set(this.number, number);
        }

        public Date getAddtime() {
                return addtime.get();
        }

        public void setAddtime(Date addtime) {
                DataUtils.set(this.addtime, addtime);
        }

        public Date getDeadtime() {
                return deadtime.get();
        }

        public void setDeadtime(Date deadtime) {
                DataUtils.set(this.deadtime, deadtime);
        }
}
