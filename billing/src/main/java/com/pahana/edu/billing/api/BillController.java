// api/BillController.java
package com.pahana.edu.billing.api;

import com.pahana.edu.billing.domain.dto.bill.*;
import com.pahana.edu.billing.service.interfaces.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/bills") @RequiredArgsConstructor
public class BillController {
  private final BillingService billing;

  @PostMapping public ResponseEntity<BillResponse> create(@Valid @RequestBody BillCreateRequest r){
    return ResponseEntity.ok(billing.create(r));
  }
  @GetMapping("/{id}") public ResponseEntity<BillResponse> get(@PathVariable Long id){ return ResponseEntity.ok(billing.get(id)); }
  @GetMapping(params="customerId")
  public ResponseEntity<List<BillResponse>> listByCustomer(@RequestParam Long customerId){
    return ResponseEntity.ok(billing.listByCustomer(customerId));
  }
  @PostMapping("/{id}/pay") public ResponseEntity<BillResponse> markPaid(@PathVariable Long id){
    return ResponseEntity.ok(billing.markPaid(id));
  }
}
