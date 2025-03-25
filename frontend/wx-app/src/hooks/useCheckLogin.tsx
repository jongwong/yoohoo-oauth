import { useEffect, useState } from "react";
import Taro from "@tarojs/taro";
import { Loading } from "@antmjs/vantui";
import request from "@/utils/request";
import { navigateTo } from "@/utils/navigate";

const useCheckLogin: (op?: { notRedirect?: boolean }) => {
  hasInit: boolean;
  gotToRegisteredElement: JSX.Element;
} = (op) => {
  const { notRedirect } = op || {};
  const [loading, setLoading] = useState(false); // 按钮加载状态
  const [hasInit, setHasInit] = useState(false);

  // 页面加载时检查是否已注册
  useEffect(() => {
    handleLogin();
  }, []);

  const handleLogin = () => {
    const pages = getCurrentPages();
    const pageUrl = pages[0].route;
    const token = wx.getStorageSync("access_token");
    const userInfo = wx.getStorageSync("userInfo");
    const openId = wx.getStorageSync("open_id");
    const isHomePage = pageUrl === "pages/home/index";
    if (token && userInfo && openId) {
      setHasInit(true);
      if (!notRedirect) {
        if (!hasUserBaseInfo(userInfo)) {
          navigateTo({
            url: "/pages/registration/index",
          });
          return;
        }

        if (!isHomePage) {
          Taro.switchTab({
            url: "/pages/home/index",
          });
        }
      }

      return;
    }

    wx.login({
      success: async (res) => {
        setLoading(true);
        if (!res.code) {
          setLoading(false);
          console.error("登录失败：", res.errMsg);
          return;
        }

        // 请求后端获取 openid 和 session_key
        const openidRes = await request
          .post(
            "/client/wechat/login",
            { code: res.code },
            {
              fail: (error) => {
                console.error("获取 openid 请求失败：", error);
                wx.showToast({
                  title: "网络错误，请稍后重试",
                  icon: "none",
                  duration: 2000,
                });
                setLoading(false);
              },
            }
          )
          .finally(() => {
            setLoading(false);
          });
        setLoading(false);

        if (!openidRes?.success) {
          wx.showToast({
            title: "网络错误，请稍后重试",
            icon: "none",
            duration: 2000,
          });

          return;
        }
        const _data = openidRes?.data || {};

        // wx.setStorageSync("user", userInfo);
        wx.setStorageSync("access_token", _data.access_token);
        wx.setStorageSync("refresh_token", _data.refresh_token);

        const userRes = await request.get(`/client/user/${_data.user_id}`);

        if (userRes.success) {
          const userInfo = {
            union_id: _data?.union_id,
            session_key: _data?.session_key,
            user_id: _data?.user_id,
            ...userRes?.data,
          };
          wx.setStorageSync("userInfo", userInfo);

          wx.setStorageSync("open_id", _data?.open_id);

          if (!notRedirect) {
            // 检查是否已注册
            if (hasUserBaseInfo(userRes?.data)) {
              if (!isHomePage) {
                Taro.switchTab({
                  url: "/pages/home/index",
                });
              }
            } else {
              navigateTo({
                url: "/pages/registration/index",
              });
            }
          }

          setLoading(false);
          setHasInit(true);
        }
      },
      fail: (err) => {
        console.error(err);
        setLoading(false);
        wx.showToast({
          title: "登录失败",
          type: "error",
          message: err.errMsg,
        });
      },
    });
  };

  const hasUserBaseInfo = (user: Record<string, any> = {}) => {
    return !!(user?.name && user?.nickname && user?.mobile && user?.avatar);
  };

  const wrapperStyle = {
    display: "flex",
    height: "100%",
    alignItems: "center",
    justifyContent: "center",
  };
  const renderContent = () => {
    if (loading) {
      return (
        <div className="wrapper" style={wrapperStyle}>
          <Loading vertical>加载中</Loading>
        </div>
      );
    }
  };

  return {
    gotToRegisteredElement: renderContent(),
    hasInit,
  };
};
export default useCheckLogin;
