import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {



    public static void main(String[] args) {
        Person person1 = new Person("dai","male");
        Person person2 = new Person("dai","female");
        List<Person> personList = new ArrayList<>();
        personList.add(person1);
        personList.add(person2);
        List<String> names = personList.stream().map(Person::getName).collect(Collectors.toList());
        System.out.println(111);

    }

}
