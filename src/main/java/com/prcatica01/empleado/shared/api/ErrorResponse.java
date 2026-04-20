package com.prcatica01.empleado.shared.api;

import java.util.List;

public record ErrorResponse(String code, String message, List<String> details) {
}
