package org.ex.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Primary;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tg_trans_bot_user")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @NotNull
    @Column(name = "id")
    String id;
    @Column(name = "langfrom")
    @NotNull
    String langFrom;
    @NotNull
    @Column(name = "langto")
    String langTo;
    @NotNull
    @Column(name = "reqfile")
    String reqFile;
    @NotNull
    @Column(name = "resfile")
    String resFile;

}
