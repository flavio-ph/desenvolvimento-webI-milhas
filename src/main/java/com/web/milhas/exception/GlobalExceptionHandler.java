package com.web.milhas.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, WebRequest req) {
        return buildProblemDetail(HttpStatus.NOT_FOUND, ex.getMessage(), "Recurso Não Encontrado", req);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(ForbiddenException ex, WebRequest req) {
        return buildProblemDetail(HttpStatus.FORBIDDEN, ex.getMessage(), "Acesso Negado", req);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailExists(EmailAlreadyExistsException ex, WebRequest req) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, ex.getMessage(), "Conflito de Dados", req);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex, WebRequest req) {
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.", "Falha de Autenticação", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex, WebRequest req) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ProblemDetail problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, "A requisição contém parâmetros inválidos.", "Erro de Validação", req);
        problemDetail.setProperty("invalid_params", errors);
        return problemDetail;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwt(ExpiredJwtException ex, WebRequest req) {
        log.warn("Token JWT expirado: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, "Sessão expirada. Faça login novamente.", "Token Expirado", req);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxSizeException(MaxUploadSizeExceededException ex, WebRequest req) {
        return buildProblemDetail(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo enviado excede o tamanho máximo permitido.", "Payload Muito Grande", req);
    }

    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ProblemDetail handleAccessDenied(Exception ex, WebRequest req) {
        return buildProblemDetail(HttpStatus.FORBIDDEN, "Acesso negado: Somente administradores podem realizar esta ação.", "Acesso Proibido", req);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ProblemDetail handleRegraNegocio(RegraNegocioException ex, WebRequest req) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, ex.getMessage(), "Violação de Regra de Negócio", req);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericError(Exception ex, WebRequest req) {
        log.error("Erro crítico não tratado ao acessar {}: ", req.getDescription(false), ex);
        return buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno. Por favor, tente novamente mais tarde ou entre em contato com o suporte.",
                "Erro Interno do Servidor",
                req
        );
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String detail, String title, WebRequest req) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(req.getDescription(false).replace("uri=", "")));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }
}