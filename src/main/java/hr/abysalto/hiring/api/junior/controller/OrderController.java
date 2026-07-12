package hr.abysalto.hiring.api.junior.controller;

import hr.abysalto.hiring.api.junior.components.DatabaseInitializer;
import hr.abysalto.hiring.api.junior.dto.CreateOrderRequest;
import hr.abysalto.hiring.api.junior.dto.OrderResponse;
import hr.abysalto.hiring.api.junior.dto.OrderTotalResponse;
import hr.abysalto.hiring.api.junior.dto.UpdateStatusRequest;
import hr.abysalto.hiring.api.junior.manager.OrderManager;
import hr.abysalto.hiring.api.junior.model.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST kontroler za narudzbe. @RestController = radi s JSON-om (za razliku od
 * BuyerControllera koji vraca HTML). Najlakse se testira kroz Swagger UI.
 */
@Tag(name = "Orders", description = "upravljanje narudzbama restorana")
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderManager orderManager;
    @Autowired
    private DatabaseInitializer databaseInitializer;

    @Operation(summary = "Dodaj novu narudzbu")
    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest request) {
        // Tablice se kreiraju tek na /init-data/, pa bez toga nema smisla spremati.
        if (!this.databaseInitializer.isDataInitialized()) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Baza nije inicijalizirana. Prvo pozovi POST /init-data/");
        }
        try {
            Order created = this.orderManager.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 = uspjesno stvoreno
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());       // 400 = krivi unos
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage()); // 500 = neocekivano
        }
    }

    @Operation(summary = "Dohvati sve narudzbe (opcionalno ?sort=asc|desc po iznosu)")
    @GetMapping("/")
    public ResponseEntity<?> getAll(@RequestParam(name = "sort", required = false) String sort) {
        if (!this.databaseInitializer.isDataInitialized()) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Baza nije inicijalizirana. Prvo pozovi POST /init-data/");
        }
        try {
            return ResponseEntity.ok(this.orderManager.getAllOrders(sort));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @Operation(summary = "Dohvati jednu narudzbu po broju")
    @GetMapping("/{orderNr}")
    public ResponseEntity<?> getOne(@PathVariable("orderNr") Long orderNr) {
        if (!this.databaseInitializer.isDataInitialized()) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Baza nije inicijalizirana. Prvo pozovi POST /init-data/");
        }
        OrderResponse order = this.orderManager.getOrderByNr(orderNr);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Narudzba " + orderNr + " ne postoji");
        }
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Promijeni status narudzbe")
    @PatchMapping("/{orderNr}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("orderNr") Long orderNr,
                                          @RequestBody UpdateStatusRequest request) {
        if (!this.databaseInitializer.isDataInitialized()) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Baza nije inicijalizirana. Prvo pozovi POST /init-data/");
        }
        try {
            OrderResponse updated = this.orderManager.updateStatus(orderNr, request.getStatus());
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Narudzba " + orderNr + " ne postoji");
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @Operation(summary = "Izracunaj ukupni iznos racuna narudzbe")
    @GetMapping("/{orderNr}/total")
    public ResponseEntity<?> getTotal(@PathVariable("orderNr") Long orderNr) {
        if (!this.databaseInitializer.isDataInitialized()) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Baza nije inicijalizirana. Prvo pozovi POST /init-data/");
        }
        OrderTotalResponse total = this.orderManager.getOrderTotal(orderNr);
        if (total == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Narudzba " + orderNr + " ne postoji");
        }
        return ResponseEntity.ok(total);
    }
}