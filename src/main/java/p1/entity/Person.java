package p1.entity;

import lombok.*;
import org.springframework.data.annotation.Id;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Person {
    @Id
    private Long id;
    private String name;
    private String email;
    private int age;
    private boolean isDeveloper;
    private Address address;
}
