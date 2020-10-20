//package p1.preload;
//
//import org.springframework.stereotype.Component;
//import p1.entity.Address;
//import p1.entity.Person;
//import p1.service.PersonCrud;
//
//@Component
//public class DataLoader {
//
//    private final PersonCrud personCrud;
//
//    public DataLoader(PersonCrud personCrud) {
//        this.personCrud= personCrud;
//        LoadData();
//    }
//
//    private void LoadData() {
//        Person p1 = new Person(0L, "caleb", "caleb@test", 24, true, new Address("1", "main street", "ny", "usa"));
//        Person p2 = new Person(1L, "mario", "nintendo@test", 56, false, new Address("11", "kamitoba", "kyoto", "jp") );
//        Person p3 = new Person(2L, "oreo", "kraft@test", 108, false, null);
//        personCrud.putPerson(p1.getId(), p1);
//        personCrud.putPerson(p2.getId(), p2);
//        personCrud.putPerson(p3.getId(), p3);
//        System.out.println("Data Loaded");
//    }
//}
