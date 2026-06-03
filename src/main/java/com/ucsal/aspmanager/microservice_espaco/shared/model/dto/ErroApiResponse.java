package com.ucsal.aspmanager.microservice_espaco.shared.model.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ErroApiResponse {
    private LocalDateTime timestamp;
    private Integer codigo;
    private String status;
    private List<String> erros;
}