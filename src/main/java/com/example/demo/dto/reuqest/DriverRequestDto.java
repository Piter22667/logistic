package com.example.demo.dto.reuqest;

import com.example.demo.enums.DriverStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverRequestDto {
    //Поля користувача
    @Email(message = "Некоректний формат email")
    @NotBlank(message = "Email є обов'язковим полем")
    private String email;

    @Size(min = 6, message = "Пароль повинен містити щонайменше 6 символів")
    @NotBlank(message = "Пароль є обов'язковим полем")
    private String password;

    //Поля водія

    @Size(max = 50, message = "Ім'я не повинно перевищувати 50 символів")
    @NotBlank(message = "Ім'я є обов'язковим полем")
    private String firstName;

    @Size(max = 50, message = "Прізвище не повинно перевищувати 50 символів")
    @NotBlank(message = "Прізвище є обов'язковим полем")
    private String lastName;

    @Size(max = 20, message = "Номер ліцензії не повинен перевищувати 20 символів")
    @NotBlank(message = "Номер ліцензії є обов'язковим полем")
    private String licenseNumber;

    @Size(max = 15, message = "Телефон водія не повинен перевищувати 15 символів")
    @NotBlank(message = "Телефон водія є обов'язковим полем")
    private String workPhone;

    @Builder.Default
    private DriverStatus status = DriverStatus.AVAILABLE;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата повинна бути у форматі YYYY-MM-DD")
    private String hireDate;

}
