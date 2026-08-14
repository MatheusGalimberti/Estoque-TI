package br.com.cnec.estoqueti.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String mensagem,
        String path,
        Map<String, String> campos
) {
}