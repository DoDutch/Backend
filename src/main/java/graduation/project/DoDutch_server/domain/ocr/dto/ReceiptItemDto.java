package graduation.project.DoDutch_server.domain.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptItemDto {
    private String name;   // 상품명
    private int unitPrice; // 단가
    private int quantity;  // 수량
    private int totalPrice; // 금액
}
