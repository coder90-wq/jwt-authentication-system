package com.machine.auth.product;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public class ProductDtos {
    public record ProductRequest(@NotBlank @Size(max=150) String name,
        @Size(max=2000) String description,@NotNull @DecimalMin("0.0") BigDecimal price){}
}
