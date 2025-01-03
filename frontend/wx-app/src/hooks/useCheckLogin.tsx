import React, { useEffect, useState } from "react";
import { serviceConfig } from "../config";
import Taro from "@tarojs/taro";
import { Button } from "@nutui/nutui-react-taro";

const useCheckLogin = () => {
  const [isRegistered, setIsRegistered] = useState(false); // 是否已注册
  const [loading, setLoading] = useState(false); // 按钮加载状态
  const [hasInit, setHasInit] = useState(false);

  // 页面加载时检查是否已注册
  useEffect(() => {
    handleLogin();
  }, []);

  const handleLogin = () => {
    wx.login({
      success: (res) => {
        if (res.code) {
          // 请求后端获取 openid 和 session_key
          wx.request({
            url: serviceConfig.client + "/client/wechat/openid",
            method: "POST",
            data: { code: res.code },
            success: (result: any) => {
              const _data = result.data?.data || {};
              console.log("后端返回数据：", _data);
              wx.setStorageSync("openid", _data.openid);
              wx.setStorageSync("session_key", _data.session_key);

              // 检查是否已注册
              if (_data.user_id) {
                wx.setStorageSync("user_id", _data.user_id);
                setIsRegistered(true); // 已注册
              }

              setHasInit(true);

              //TODO Mock
              setTimeout(() => {
                wx.setStorageSync("phone_number", "18060601823");

                Taro.navigateTo({
                  url: "/pages/registration/index",
                });
              }, 1000);
            },
            fail: (error) => {
              console.error("获取 openid 请求失败：", error);
              wx.showToast({
                title: "网络错误，请稍后重试",
                icon: "none",
                duration: 2000,
              });
            },
          });
        } else {
          console.error("登录失败：", res.errMsg);
        }
      },
      fail: (err) => {
        console.error("调用 wx.login 失败：", err);
      },
    });
  };

  // 点击“去注册”时，获取手机号和用户信息
  const handleRegister = (e: any) => {
    const { encryptedData, iv } = e.detail;
    if (!encryptedData || !iv) {
      wx.showToast({
        title: "未授权获取手机号",
        icon: "none",
        duration: 2000,
      });
      return;
    }

    setLoading(true);

    // 解密手机号
    wx.request({
      url: serviceConfig.client + "/client/wechat/decrypt-phone",
      method: "POST",
      data: {
        data: encryptedData,
        iv,
        session_key: wx.getStorageSync("session_key"),
      },
      success: (response) => {
        const phoneNumber = (response.data as any)?.purePhoneNumber;
        wx.setStorageSync("phone_number", phoneNumber);
        Taro.navigateTo({
          url: "/pages/registration/index",
        });
      },
      fail: (error) => {
        console.error("解密手机号失败：", error);
        wx.showToast({
          title: "获取手机号失败，请重试",
          icon: "none",
          duration: 2000,
        });
        setLoading(false);
      },
    });
  };

  return {
    gotToRegisteredElement:
      !isRegistered && hasInit ? (
        <view className="unregistered-container">
          <text className="prompt-text">
            您尚未注册，请点击下方按钮进行注册。
          </text>
          <Button
            className="register-button"
            type="primary"
            loading={loading}
            openType="getPhoneNumber"
            onGetPhoneNumber={handleRegister}
          >
            {loading ? "处理中..." : "去注册"}
          </Button>
        </view>
      ) : null,
    hasInit,
  };
};
export default useCheckLogin;
