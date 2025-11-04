package graduation.project.DoDutch_server.domain.trip.dto.Response;


import graduation.project.DoDutch_server.domain.trip.dto.Request.MessageDto;

import java.util.List;

public record ChatGPTResponseDto(
        List<Choice> choices
) {
    public record Choice(
            int index,
            MessageDto message
    ){}
}
