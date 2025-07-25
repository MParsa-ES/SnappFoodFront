package org.example.snappfoodfront.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class TransactionDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequestDTO {
        Long order_id;
        TransactionMethod method;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponseDTO {
        Long id;
        Long order_id;
        Long user_id;
        String method;
        String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopUpRequestDTO {
        private BigDecimal amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionsList {
        Set<PaymentResponseDTO> transactions;
    }

}
