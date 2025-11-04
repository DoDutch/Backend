package graduation.project.DoDutch_server.domain.kakaopay.entity;


import graduation.project.DoDutch_server.domain.kakaopay.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="payment_order")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String partnerOrderId;

    @Column(nullable=false)
    private String partnerUserId; //받는 사람

    @Column(nullable=false)
    private String payerUserId;  // 보내는 사람

    @Column(nullable=false)
    private String itemName;

    @Column(nullable=false)
    private Long amount;

    private String kakaoTid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // ✅ 생성은 팩토리 메서드로만
    public static PaymentOrder create(String partnerOrderId, String partnerUserId,
                                      String payerUserId, String itemName, Long amount) {
        PaymentOrder order = new PaymentOrder();
        order.partnerOrderId = partnerOrderId;
        order.partnerUserId = partnerUserId;
        order.payerUserId = payerUserId;
        order.itemName = itemName;
        order.amount = amount;
        order.status = PaymentStatus.READY;
        return order;
    }

    // ✅ 상태 변경은 메서드 통해서만
    public void applyTid(String tid) {
        this.kakaoTid = tid;
    }

    public void approve() {
        if (this.status == PaymentStatus.APPROVED) return;
        this.status = PaymentStatus.APPROVED;
        this.processedAt = LocalDateTime.now();
    }

    public boolean isApproved() {
        return this.status == PaymentStatus.APPROVED;
    }
}
