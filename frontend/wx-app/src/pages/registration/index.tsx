import React, { useEffect, useState } from "react";
import Taro from "@tarojs/taro";
import { serviceConfig } from "../../config";

import { Button, Form, Input, Radio } from "@nutui/nutui-react-taro";
import Layout from "../../component/Layout";
import styles from "./index.module.less";

const Registration: React.FC = () => {
  const [phoneNumber, setPhoneNumber] = useState<string | null>(null); // 存储手机号
  const [loading, setLoading] = useState(false); // 加载状态

  const [form] = Form.useForm();
  useEffect(() => {
    const val = wx.getStorageSync("phone_number");
    setPhoneNumber(val); // 从本地缓存获取手机号
  }, []);

  // 提交注册信息
  const handleRegister = () => {
    setLoading(true);

    // 将数据发送到后端进行注册
    wx.request({
      url: serviceConfig.client + "/client/register", // 后端注册接口
      method: "POST",
      data: {
        phone_number: phoneNumber,
      },
      success: (res) => {
        setLoading(false);
        if (res.data.success) {
          wx.showToast({
            title: "注册成功",
            icon: "success",
            duration: 2000,
          });
          // 跳转到首页或其他页面
          Taro.navigateBack();
        } else {
          wx.showToast({
            title: res.data.message || "注册失败，请重试",
            icon: "none",
            duration: 2000,
          });
        }
      },
      fail: (error) => {
        setLoading(false);
        console.error("注册请求失败：", error);
        wx.showToast({
          title: "网络错误，请稍后重试",
          icon: "none",
          duration: 2000,
        });
      },
    });
  };

  return (
    <Layout
      style={{
        backgroundColor: "#f8faf6",
      }}
    >
      <Form
        form={form}
        className={styles.form}
        labelPosition={"top"}
        footer={
          <Button
            className={styles.button}
            type="primary"
            formType="submit"
            size={"large"}
            block
          >
            注册
          </Button>
        }
      >
        <Form.Item label={"姓名"} name={"name"}>
          <Input placeholder="请输入姓名" className={styles.input} />
        </Form.Item>
        <Form.Item label={"昵称"} name={"nickname"}>
          <Input
            name="nickname"
            placeholder="请输入昵称"
            className={styles.input}
          />
        </Form.Item>
        <Form.Item label={"性别"} name={"sex"}>
          <Radio.Group defaultValue={1} direction={"horizontal"}>
            <Radio value={1}>男 </Radio>
            <Radio value={2}>女</Radio>
          </Radio.Group>
        </Form.Item>

        <Form.Item label={"登录密码"} name="password">
          <Input
            name="password"
            type="password"
            placeholder="请输入密码"
            className={styles.input}
          />
        </Form.Item>
        <Form.Item label={"确认密码"} name="confirmPassword">
          <Input
            name="confirmPassword"
            type="password"
            placeholder="请输入确认密码"
            className={styles.input}
          />
        </Form.Item>
      </Form>
    </Layout>
  );
};

export default Registration;
