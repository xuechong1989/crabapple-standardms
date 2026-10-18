package xc.crabapple.standardms.common.domain.dto;

/**
 * xuechong 2025-01-16
 */
public class S2cDto extends AppDto{

    public S2cDto(){

    }

    public static final S2cDto okay(){
        final S2cDto s2cDto = okay("success", "success", "yes");
        return s2cDto;
    }

    public static final S2cDto okay(final String message) {
        final S2cDto s2cDto = okay(message, "success", "yes");
        return s2cDto;
    }

    public static final S2cDto okay(final String message, final String code) {
        final S2cDto s2cDto = okay(message, "success", "yes");
        return s2cDto;
    }

    public static final S2cDto okay(final String message, final String code, final String cipher) {
        S2cDto s2cDto = new S2cDto();
        s2cDto.setSuccess("yes");
        s2cDto.setMessage(message);
        s2cDto.setCode(code);
        s2cDto.setCipher(cipher);
        return s2cDto;
    }

    public static final S2cDto failure(){
        final S2cDto s2cDto = failure("error", "error", "yes");
        return s2cDto;
    }

    public static final S2cDto failure(final String message) {
        final S2cDto s2cDto = failure(message, "error", "yes");
        return s2cDto;
    }

    public static final S2cDto failure(final String message, final String code) {
        final S2cDto s2cDto = failure(message, code, "yes");
        return s2cDto;
    }

    public static final S2cDto failure(final String message, final String code, final String cipher) {
        S2cDto s2cDto = new S2cDto();
        s2cDto.setSuccess("no");
        s2cDto.setMessage(message);
        s2cDto.setCode(code);
        s2cDto.setCipher(cipher);
        return s2cDto;
    }

}
