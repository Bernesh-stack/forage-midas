

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private LocalDateTime timestamp;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User recipient;

    // Constructors, Getters, Setters
    public TransactionRecord() {}

    public TransactionRecord(User sender, User recipient, BigDecimal amount, LocalDateTime timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Getters and setters omitted for brevity
}
