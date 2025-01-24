import React, { useState } from "react";
import Taro from "@tarojs/taro";
import { Button, Field, Form, FormItem } from "@antmjs/vantui";

import Layout from "../../component/Layout";

import request from "@/utils/request";

import styles from "./index.module.less";

const Registration: React.FC = () => {
  const [loading, setLoading] = useState(false); // 加载状态

  const form = Form.useForm();
  // 提交注册信息
  const handleRegister = async (e) => {
    const { encryptedData, iv } = e.detail;
    if (!form) {
      return;
    }
    setLoading(true);
    form.validateFields(async (errorMess) => {
      if (errorMess?.length) {
        return;
      }
      const values = form.getFieldsValue();
      const _data = {
        ...values, // 提交表单数据
        encrypted_data: encryptedData,
        iv,
        union_id: wx.getStorageSync("union_id"),
        session_key: wx.getStorageSync("session_key"),
      };
      const res = await request
        .post("/client/wechat/register", _data)
        .finally(() => {
          setLoading(false);
        });
      if (res.success) {
        wx.showToast({
          title: "注册成功",
          icon: "success",
          duration: 2000,
        });
        Taro.reLaunch({
          url: "/pages/login/index",
        });
      } else {
        wx.showToast({
          title: "注册失败，请重试",
          icon: "none",
          duration: 2000,
        });
      }
    });
  };

  /*  // 校验密码：检查是否包含大写字母、小写字母、数字和特殊字符
    const validatePassword = (_rule: any, value: string) => {
      if (!value) {
        return Promise.reject("请输入密码");
      }
      if (value.length < 10) {
        return Promise.reject("密码长度不能小于10位");
      }
      if (!/[a-z]/.test(value)) {
        return Promise.reject("密码必须包含至少一个小写字母");
      }
      if (!/[A-Z]/.test(value)) {
        return Promise.reject("密码必须包含至少一个大写字母");
      }
      if (!/\d/.test(value)) {
        return Promise.reject("密码必须包含至少一个数字");
      }
      // 更新后的特殊字符校验，包含更多特殊字符
      if (!/[!@#$%^&*()_+[\]{}|;:'",.<>?/\\`~\-._=|]/.test(value)) {
        return Promise.reject("密码必须包含至少一个特殊字符");
      }
      return Promise.resolve();
    };*/

  return (
    <Layout
      style={{
        backgroundColor: "#f8faf6",
      }}
    >
      <Form className={styles.form} form={form}>
        <FormItem
          label="姓名"
          name="name"
          layout={"vertical"}
          rules={[
            {
              rule: (value, call) => {
                if (!value) {
                  call("姓名不能为空");
                } else if (value.length < 2) {
                  call("姓名长度不能小于 2 个字符");
                } else if (value.length > 8) {
                  call("姓名长度不能超过 8 个字符");
                }
              },
            },
          ]}
        >
          <Field placeholder="请输入姓名" className={styles.input} />
        </FormItem>

        <FormItem
          label="昵称"
          name="nickname"
          layout={"vertical"}
          rules={[
            {
              rule: (value, call) => {
                if (!value) {
                  call("昵称不能为空");
                } else if (value.length < 2) {
                  call("昵称长度不能小于 2 个字符");
                } else if (value.length > 12) {
                  call("昵称长度不能超过 12 个字符");
                }
              },
            },
          ]}
        >
          <Field placeholder="请输入昵称" className={styles.input} />
        </FormItem>
      </Form>
      <Button
        className={styles.button}
        type="primary"
        formType="submit"
        size={"large"}
        block
        openType="getPhoneNumber"
        onGetPhoneNumber={handleRegister}
        loading={loading}
      >
        注册
      </Button>
    </Layout>
  );
};

export default Registration;
