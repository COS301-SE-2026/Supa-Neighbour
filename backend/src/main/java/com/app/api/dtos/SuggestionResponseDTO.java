package com.app.api.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.app.api.services.SuggestionService.ViolationType;
import com.app.api.services.SuggestionService.Severity;
import com.app.api.services.SuggestionService.SuggestedAction;

@Data
@AllArgsConstructor
public class SuggestionResponseDTO {
    private  ViolationType violationType;
    private Severity severity;
    private SuggestedAction suggestedAction;
}
