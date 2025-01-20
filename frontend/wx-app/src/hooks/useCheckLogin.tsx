import { useEffect, useState } from "react";
import Taro from "@tarojs/taro";
import { Loading } from "@nutui/nutui-react-taro";
import request from "@/utils/request";

const useCheckLogin = () => {
  const [loading, setLoading] = useState(false); // 按钮加载状态
  const [hasInit, setHasInit] = useState(false);

  // 页面加载时检查是否已注册
  useEffect(() => {
    handleLogin();
  }, []);

  const handleLogin = () => {
    const token = wx.getStorageSync("access_token");
    const userInfo = wx.getStorageSync("userInfo");

    if (token && userInfo) {
      Taro.switchTab({
        url: "/pages/home/index",
      });
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
          // 检查是否已注册
          if (userRes?.data?.name) {
            const userInfo = {
              union_id: _data.union_id,
              session_key: _data.session_key,
              user_id: _data.user_id,
              ...userRes?.data,
            };
            wx.setStorageSync("userInfo", userInfo);

            Taro.switchTab({
              url: "/pages/home/index",
            });
          } else {
            Taro.navigateTo({
              url: "/pages/registration/index",
            });
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
          <Loading direction="vertical">加载中</Loading>
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
