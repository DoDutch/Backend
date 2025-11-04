package graduation.project.DoDutch_server.domain.kakaopay.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="wallets")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private Long userId;

    private Long balance = 0L;

    public Wallet(Long userId) {
        this.userId = userId;
    }

    // ✅ 반드시 메서드를 통해 금액 변경
    public void addBalance(Long amount) {
        if (amount <= 0) throw new IllegalArgumentException("금액이 올바르지 않습니다.");
        this.balance += amount;
    }
}
