import React, {useState} from "react";
import {Button, Form, Input, message} from "antd";
import {LockOutlined, UserOutlined} from "@ant-design/icons";
// @ts-ignore
import {useHistory} from "react-router";
import {setCookie} from "@/utils/cookie";
import http from "@/utils/http";

const Login: React.FC = () => {
    const [loading, setLoading] = useState(false);

    const h = useHistory(); // 引入 useNavigate 钩子

    const onFinish = async (values: { username: string; password: string; remember: boolean }) => {
        setLoading(true);

        // 模拟登录请求
        try {
            const response = await loginRequest(values);
            message.success("登录成功！");

            // 设置名为 "JWT" 的 Cookie，1 小时过期，HTTPS 下发送，SameSite 为 Strict
            setCookie("access_token", response.data, {
                maxAge: 3600,
                secure: true,
                sameSite: "Strict",
            });
            // 跳转到主页面 (可用 react-router-dom)
            h.replace("/home");
        } catch (error) {
            message.error("登录失败，请检查用户名或密码！");
        } finally {
            setLoading(false);
        }
    };


    const loginRequest = async (data: { username: string, password: string }) => {
        const formData = new URLSearchParams();
        formData.append("grant_type", "password");
        formData.append("username", data.username);
        formData.append("password", data.password);

        const response = await http.post("/auth/token", formData, {
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
        });
        return response.data; // 假设返回值中包含 token
    };

    return (
        <div className="login-container">
            <div style={{display: 'flex', height: '100vh', justifyContent: 'center', alignItems: 'center'}}>
                <Form
                    style={{width: "350px"}}
                    name="login_form"
                    className="login-form"
                    onFinish={onFinish}
                >
                    <Form.Item
                        name="username"
                        rules={[{required: true, message: "请输入用户名!"}]}
                    >
                        <Input
                            prefix={<UserOutlined/>}
                            placeholder="用户名"
                            size="large"
                        />
                    </Form.Item>

                    <Form.Item
                        name="password"
                        rules={[{required: true, message: "请输入密码!"}]}
                    >
                        <Input.Password
                            prefix={<LockOutlined/>}
                            placeholder="密码"
                            size="large"
                        />
                    </Form.Item>


                    <Form.Item>
                        <Button
                            type="primary"
                            htmlType="submit"
                            className="login-form-button"
                            size="large"
                            loading={loading}
                            block
                        >
                            登录
                        </Button>
                    </Form.Item>
                </Form>
            </div>

        </div>
    );
};

export default Login;
