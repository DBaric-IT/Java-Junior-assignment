package hr.abysalto.hiring.api.junior.dto;

import lombok.Data;

/**
 * Tijelo zahtjeva za promjenu statusa. Klijent posalje npr. { "status": "PREPARING" }.
 * Dozvoljeno: WAITING_FOR_CONFIRMATION, PREPARING, DONE.
 */
@Data
public class UpdateStatusRequest {
    private String status;
}