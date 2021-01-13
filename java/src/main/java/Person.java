import lombok.Data;

@Data
class Person {

    private String name;
    private String sex;

    Person(String name, String sex){
        this.name = name;
        this.sex = sex;
    }
}
