package graduation.project.DoDutch_server.domain.trip.dto.Request;

import java.util.ArrayList;
import java.util.List;

public record ChatGPTRequestDto(
        String model,
        List<MessageDto> messages
) {
    public static ChatGPTRequestDto gptRequest(String model, String prompt) {
        List<MessageDto> messages =  new ArrayList<>();
        messages.add(new MessageDto("user", prompt));
        return new ChatGPTRequestDto(model, messages);
    }
}
