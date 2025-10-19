package com.example.demo.dto.reuqest;

import com.example.demo.enums.DriverStatus;
import jakarta.validation.constraints.Email;
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
public class UpdateDriverDto {
    @Size(max = 50, message = "Ім'я не повинно перевищувати 50 символів")
    private String firstName;

    @Size(max = 50, message = "Прізвище не повинно перевищувати 50 символів")
    private String lastName;

    @Email(message = "Некоректний формат email")
    private String email;

    @Size(max = 15, message = "Телефон водія не повинен перевищувати 15 символів")
    private String workPhone;

    private DriverStatus status;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата повинна бути у форматі YYYY-MM-DD")
    private String hireDate;}
