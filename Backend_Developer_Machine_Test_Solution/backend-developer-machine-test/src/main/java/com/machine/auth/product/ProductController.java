package com.machine.auth.product;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
    private final ProductRepository products;
    public ProductController(ProductRepository products){this.products=products;}

    @GetMapping
    public Page<Product> list(@RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="id") String sortBy){
        if(page<0 || size<1 || size>100) throw new IllegalArgumentException("Invalid page or size (max size 100)");
        if(!java.util.Set.of("id", "name", "price", "createdAt").contains(sortBy))
            throw new IllegalArgumentException("sortBy must be one of: id, name, price, createdAt");
        return products.findAll(PageRequest.of(page,size,Sort.by(sortBy).descending()));
    }
    @GetMapping("/{id}") public Product get(@PathVariable Long id){return products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));}
    @PostMapping public ResponseEntity<Product> create(@Valid @RequestBody ProductDtos.ProductRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(products.save(new Product(req.name(),req.description(),req.price())));
    }
    @PutMapping("/{id}") public Product update(@PathVariable Long id,@Valid @RequestBody ProductDtos.ProductRequest req){
        Product p=get(id);p.update(req.name(),req.description(),req.price());return products.save(p);
    }
    @DeleteMapping("/{id}") public Map<String,String> delete(@PathVariable Long id){products.delete(get(id));return Map.of("message","Product deleted");}
}
