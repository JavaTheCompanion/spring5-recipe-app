package guru.springframework.commands;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CategoryCommand {

    private Long id;
    private String description;

}
