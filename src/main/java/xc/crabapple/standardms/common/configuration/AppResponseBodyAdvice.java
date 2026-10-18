package xc.crabapple.standardms.common.configuration;

import xc.crabapple.standardms.common.domain.dto.S2cDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import xc.crabapple.core.collection.StringUtil;
import xc.crabapple.core.data.dto.DtoBase;
import xc.crabapple.core.json.jackson.JacksonUtil;
import xc.crabapple.core.log.LogUtil;
import xc.crabapple.core.servlet.ServletUtil;
import xc.crabapple.ssm.security.SecurityUtil;
import xc.crabapple.ssm.security.transmitc.TransmitCipherUtil;
import xc.crabapple.ssm.spring.SsmResponseBodyAdvice;

/**
 * xuechong 2024-12-28
 */
@ControllerAdvice
public class AppResponseBodyAdvice extends SsmResponseBodyAdvice {

    @InitBinder
    @Override
    public void initBinder(final WebDataBinder webDataBinder) {
        super.initBinder(webDataBinder);
    }

    /**
     * xuechong 2025-01-21
     * 系统全局异常处理、全局错误处理
     * @param request
     * @param throwable
     * @return
     */
    @ExceptionHandler(value=Throwable.class)
    @ResponseBody
    public S2cDto exceptionHandler(HttpServletRequest request, Throwable throwable) {
        String throwableStr = throwable.toString();
        if(StringUtil.isBlank(throwableStr)){
            return null;
        }
        if(StringUtil.containOf(throwableStr, "ClientAbortException")){
            return null;
        }
        //
        final String requestURI = ServletUtil.getRequestURI(request);
        LogUtil.error("AppResponseBodyAdvice_exceptionHandler_1", throwable, requestURI);
        S2cDto s2cDto = S2cDto.failure("系统异常");
        s2cDto.setParam("msg", "全局异常");
        return s2cDto;
    }

    /**
     * xuechong 2024-12-28
     * 服务器响应给客户端数据时统一执行入口，执行完controller方法后再执行此方法。
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (!(body instanceof S2cDto)) {
            return body;
        }
        // 解密后推送给客户端
        S2cDto s2cDto = (S2cDto) body;
        DtoBase cipherDtoBase = SecurityUtil.tcS2cEncrypt(s2cDto);
        S2cDto cipherS2cDto = (S2cDto) cipherDtoBase;
        return cipherS2cDto;
    }

    /**
     * xuechong 2025-01-15
     * @param type 消息类型：true-正确，false-错误
     * @param msg
     * @return
     */
    public static final boolean sendJson2client(final boolean type, final String code, final String msg){
        final String requestURI = ServletUtil.getRequestURI();
        final String transmitEnable = TransmitCipherUtil.getEnable();
        //
        if(type){
            S2cDto s2cDto = S2cDto.okay(msg, code);
            s2cDto.setCipher(transmitEnable);
            s2cDto.setParam("requestURI", requestURI);
            final String json = JacksonUtil.obj2json(s2cDto);
            ServletUtil.sendJson2client(json);
            return true;
        }
        //
        S2cDto s2cDto = S2cDto.failure(msg, code);
        s2cDto.setCipher(transmitEnable);
        s2cDto.setParam("requestURI", requestURI);
        final String json = JacksonUtil.obj2json(s2cDto);
        ServletUtil.sendJson2client(json);
        return true;
    }

}