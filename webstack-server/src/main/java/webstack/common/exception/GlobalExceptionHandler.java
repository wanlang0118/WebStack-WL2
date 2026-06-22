package webstack.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import webstack.common.result.Result;
import webstack.common.result.ResultCode;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常：直接把可读的业务原因返回给前端
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * @Valid 校验失败（请求体）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("请求参数错误");
        return Result.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * @RequestParam / @PathVariable 上的约束校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        return Result.error(ResultCode.BAD_REQUEST.getCode(), e.getMessage());
    }

    /**
     * 缺少必填请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.error(ResultCode.BAD_REQUEST.getCode(),
                "缺少必填参数：" + e.getParameterName() + "，请补全后重试");
    }

    /**
     * 参数类型不匹配（如把字母传给 Long 类型）
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return Result.error(ResultCode.BAD_REQUEST.getCode(),
                "参数 [" + e.getName() + "] 类型不正确，期望类型为 "
                        + (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "正确类型")
                        + "，请检查后重试");
    }

    /**
     * 请求体无法解析（JSON 格式错误等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException e) {
        return Result.error(ResultCode.BAD_REQUEST.getCode(),
                "请求体格式错误或为空，请检查 JSON 是否合法");
    }

    /**
     * 请求方法不被支持（如对只支持 POST 的接口用 GET）
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return Result.error(ResultCode.BAD_REQUEST.getCode(),
                "不支持的请求方法 [" + e.getMethod() + "]，该接口支持的方法为："
                        + String.join("、", e.getSupportedMethods() != null ? e.getSupportedMethods() : new String[0]));
    }

    /**
     * 数据库约束异常：尽量给出可操作的原因
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<Void> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("数据库约束异常", e);
        String msg = e.getMessage();
        if (msg != null && msg.contains("foreign key constraint fails")) {
            if (msg.contains("fk_site_category")) {
                return Result.error(ResultCode.ERROR.getCode(), "删除失败：该分类下存在关联的网站数据，请先删除或转移网站后再操作。");
            }
            return Result.error(ResultCode.ERROR.getCode(), "操作失败：存在关联数据，无法直接删除，请先处理关联数据。");
        }
        if (msg != null && msg.contains("Duplicate entry")) {
            return Result.error(ResultCode.ERROR.getCode(), "数据已存在（唯一键冲突），请勿重复提交或更换唯一字段的值。");
        }
        return Result.error(ResultCode.ERROR.getCode(), "数据不符合约束要求，请检查必填项、长度与格式是否正确。");
    }

    /**
     * 上传文件超过大小限制
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return Result.error(ResultCode.BAD_REQUEST.getCode(), "上传文件大小超出限制，请压缩或更换更小的文件后重试");
    }

    /**
     * 兜底异常：完整堆栈记录到服务端日志，向前端返回具体原因与排查建议
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统未捕获异常", e);
        String reason = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        return Result.error(ResultCode.ERROR.getCode(),
                "系统处理出错，原因：" + reason + "。建议：请检查请求参数是否正确；若问题持续，请联系管理员并提供操作时间以便排查日志。");
    }
}
