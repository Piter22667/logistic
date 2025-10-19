package com.example.demo.dto.reuqest;

import com.example.demo.enums.TrailerType;
import com.example.demo.enums.VehicleStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRequestDto {

    @Size(max = 50, message = "Реєстраційний номер не може перевищувати 50 символів")
    @NotBlank(message = "Реєстраційний номер є обов'язковим полем")
    private String licensePlate;

    @NotNull(message = "Тип причепа є обов'язковим полем")
    private TrailerType trailerType;

    @NotNull(message = "Вантажопідйомність є обов'язковим полем")
    @DecimalMin(value = "0.01", message = "Вантажопідйомність повинна бути більше 0")
    @Digits(integer = 10, fraction = 2, message = "Некоректний формат вантажопідйомності")
    private BigDecimal capacity;

    @Builder.Default
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Min(value = 1900, message = "Рік виробництва повинен бути не менше 1900")
    @Max(value = 2100, message = "Рік виробництва не може перевищувати 2100")
    private Integer manufactureYear;

    @PastOrPresent(message = "Дата останнього обслуговування не може бути в майбутньому")
    private LocalDate lastMaintenanceDate;
}
