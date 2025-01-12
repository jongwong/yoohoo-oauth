import { serviceConfig } from "../../config";
import Taro from "@tarojs/taro";

type ResponseData<T = any> = {
  code: number;
  message: string;
  data: T;
  success: boolean;
  total: number;
};

export type RequestOption<T = any, U = any> = Omit<
  // @ts-ignore
  Taro.request.Option<T, U> & {
    params?: Record<string, any>; // 查询参数
  },
  "method" | "url"
>;

const request = <T = any, U = any>(
  config: RequestOption<T, U> & {
    method: Taro.request.Option<T>["method"];
    url: string;
  }
): Promise<ResponseData<T>> => {
  return new Promise((resolve, reject) => {
    const _url = config.url.startsWith("/")
      ? serviceConfig.client + config.url
      : config.url;

    const token = wx.getStorageSync("access_token");
    Taro.request({
      header: {
        "Content-Type": "application/json", // 默认请求头
        ...config.header,
        Authorization: `Bearer ${token}`,
      },
      fail: (error) => {
        reject({
          message: "网络错误，请检查网络连接",
          error,
        });
      },
      success: (result) => {
        if (result?.data?.code === 401) {
          if (
            result?.data?.message.startsWith("Token Invalid: JWT expired at")
          ) {
            wx.removeStorageSync("access_token");
            wx.removeStorageSync("refresh_token");
            console.log("=====222=====", 222);
          }

          wx.navigateTo({
            url: "/pages/login/index",
          });
        }

        const _data = {
          ...result?.data,
          success: result?.data?.code === 0,
        };
        resolve(_data);
        config?.success?.(_data);
      },
      ...config,
      // @ts-ignore
      url: _url,
    });
  }) as any;
};

// 辅助函数：将 params 转为查询字符串
const buildQueryString = (params: Record<string, any>): string => {
  return Object.entries(params)
    .map(
      ([key, value]) =>
        `${encodeURIComponent(key)}=${encodeURIComponent(value)}`
    )
    .join("&");
};

// 封装通用的 HTTP 方法
const get = <T = any, U = any>(
  url: string,
  config: RequestOption<T, U> = {}
) => {
  const { params, ...rest } = config;
  const queryString = params ? `?${buildQueryString(params)}` : "";
  return request({
    // @ts-ignore
    url: url + queryString,
    method: "GET",
    ...rest,
  });
};

const post = <T = any, U = any>(
  url: string,
  data: Partial<T>,
  config: RequestOption<T, U> = {}
) => {
  const { params, ...rest } = config;
  const queryString = params ? `?${buildQueryString(params)}` : "";
  return request({
    // @ts-ignore
    url: url + queryString,
    method: "POST",
    data,
    ...rest,
  });
};

const put = <T = any, U = any>(
  url: string,
  config: RequestOption<T, U> = {}
) => {
  const { params, data, ...rest } = config;
  const queryString = params ? `?${buildQueryString(params)}` : "";
  return request({
    // @ts-ignore
    url: url + queryString,
    method: "PUT",
    data,
    ...rest,
  });
};

const del = <T = any, U = any>(
  url: string,
  config: RequestOption<T, U> = {}
) => {
  const { params, data, ...rest } = config;
  const queryString = params ? `?${buildQueryString(params)}` : "";

  return request({
    // @ts-ignore
    url: url + queryString,
    method: "DELETE",
    data,
    ...rest,
  });
};

export default { get, post, put, del };
