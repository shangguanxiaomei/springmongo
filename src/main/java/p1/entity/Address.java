package p1.entity;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
//@EqualsAndHashCode
public class Address {
    String houseNumber;
    String street;
    String city;
    String country;
}