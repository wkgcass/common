package net.cassite.daf4j;

import net.cassite.daf4j.types.XInt;
import net.cassite.daf4j.types.XList;
import net.cassite.daf4j.types.XString;

import java.util.ArrayList;
import java.util.List;

public class Entity {
        public final XInt id = new XInt(this);
        public final XString name = new XString(this);
        public final XString district = new XString(this);
        public final XInt age = new XInt(this);

        public final XList<Entity2> oneToMany = new XList<Entity2>(this);

        public Integer getId() {
                return id.get();
        }

        public String getName() {
                return name.get();
        }

        public Integer getAge() {
                return age.get();
        }

        public void setId(Integer id) {
                DataUtils.set(this.id, id);
        }

        public void setName(String name) {
                DataUtils.set(this.name, name);
        }

        public void setAge(Integer age) {
                DataUtils.set(this.age, age);
        }

        public List<Entity2> getOneToMany() {
                return oneToMany.get();
        }

        public void setOneToMany(List<Entity2> oneToMany) {
                DataUtils.set(this.oneToMany, oneToMany);
        }

        public String getDistrict() {
                return district.get();
        }

        public void setDistrict(String district) {
                DataUtils.set(this.district, district);
        }
}
