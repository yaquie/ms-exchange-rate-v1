package pe.com.app.exchange.rate.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name="transaccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaccion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    @Column(name = "total_amount")
    private Double totalAmount;
    @Column(name = "original_currency")
    private String originalCurrency;
    @Column(name = "target_currency")
    private String targetCurrency;
    @Column(name = "exchange_rate")
    private Double exchangeRate;
}
