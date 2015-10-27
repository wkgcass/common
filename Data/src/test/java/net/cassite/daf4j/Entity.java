package net.cassite.daf4j;

import java.util.ArrayList;
import java.util.List;

public class Entity {
        public final DataComparable<Integer> id = new DataComparable<Integer>(this);
        public final Data<String> name = new Data<String>(this);
        public final Data<String> district = new Data<String>(this);
        public final DataComparable<Integer> age = new DataComparable<Integer>(this);

        public final DataIterable<Entity2, List<Entity2>> oneToMany = new DataIterable<Entity2, List<Entity2>>(new ArrayList<Entity2>(), this);

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
