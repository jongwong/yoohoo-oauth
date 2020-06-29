package cn.jongwong.oauth.common;

public class ResponseResultBuilder {

    //成功，不返回具体数据
    public static <T> ResponseResult<T> success(ResultCode code) {
        ResponseResult<T> result = new ResponseResult<T>();
        result.setCode(code.getCode());
        result.setMsg(code.getMsg());
        return result;
    }

    //成功，返回数据
    public static <T> ResponseResult<T> success(T t, ResultCode code) {
        ResponseResult<T> result = new ResponseResult<T>();
        result.setCode(code.getCode());
        result.setMsg(code.getMsg());
        result.setData(t);
        return result;
    }

    //失败，返回失败信息
    public static <T> ResponseResult<T> faile(ResultCode code) {
        ResponseResult<T> result = new ResponseResult<T>();
        result.setCode(code.getCode());
        result.setMsg(code.getMsg());
        return result;
    }

}
