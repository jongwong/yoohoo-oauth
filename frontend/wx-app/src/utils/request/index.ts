import { serviceConfig } from "../../config";
import Taro from "@tarojs/taro";
import { isNil, omitBy } from "lodash-es";
import { Toast } from "@antmjs/vantui";

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
const urlList = ["/client/wechat/login"];

const getFormatUrl = (url: string, params: any) => {
  let _params = params || {};
  if (_params) {
    _params = omitBy(_params, (it) => isNil(it));
  }
  if (Object.keys(_params).length === 0) {
    _params = undefined;
  }
  const queryString = _params ? `?${buildQueryString(_params)}` : "";
  return url + queryString;
};

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

    if (!token && !urlList.includes(config.url)) {
      // 获取当前页面地址，需要比对
      const pages = getCurrentPages();
      const pageUrl = pages[0].route;
      if (pageUrl !== "/pages/login/index") {
        wx.navigateTo({
          url: "/pages/login/index",
        });
      }
      return;
    }
    Taro.request({
      header: {
        "Content-Type": "application/json", // 默认请求头
        ...config.header,
        Authorization: token ? `Bearer ${token}` : undefined,
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
          }

          // 获取当前页面地址，需要比对
          const pages = getCurrentPages();
          if (!urlList.includes(config.url)) {
            wx.navigateTo({
              url: "/pages/login/index",
            });
          }
        }

        const _data = {
          ...result?.data,
          success: result?.data?.code === 0,
        };
        if (!_data.success) {
          Toast.fail({
            message: "网络异常",
          });
        }
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
  return request({
    // @ts-ignore
    url: getFormatUrl(url, params),
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
  return request({
    // @ts-ignore
    url: getFormatUrl(url, params),
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
  return request({
    // @ts-ignore
    url: getFormatUrl(url, params),
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

  return request({
    // @ts-ignore
    url: getFormatUrl(url, params),
    method: "DELETE",
    data,
    ...rest,
  });
};

export default { get, post, put, del };
