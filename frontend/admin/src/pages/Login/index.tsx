import React, { useEffect, useState } from 'react';
import { Button, Form, Input } from 'antd';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { setCookie } from '@/utils/cookie';
import http from '@/utils/http';
import { useNavigate } from 'react-router-dom';
import { getQueryByName } from '@/utils/url';

const Login: React.FC = () => {
	const [loading, setLoading] = useState(false);
	const [lastLoginUsername, setLastLoginUsername] = useState<string>();
	const navigate = useNavigate();

	const onFinish = async (values: { username: string; password: string; remember: boolean }) => {
		setLoading(true);

		// 模拟登录请求
		try {
			const res = await loginRequest(values);

			if (!res.success || !res.data) {
				return;
			}
			// 设置名为 "JWT" 的 Cookie，1 小时过期，HTTPS 下发送，SameSite 为 Strict
			setCookie('access_token', res.data, {
				maxAge: 3600,
			});
			if (process.env.NODE_ENV === 'development') {
				localStorage.setItem('LAST_LOGIN_USERNAME', values.username);
			}

			const url = getQueryByName('redirect_uri') || '/home';
			// 跳转到主页面 (可用 react-router-dom)
			navigate(url);
		} finally {
			setLoading(false);
		}
	};

	const loginRequest = async (data: { username: string; password: string }) => {
		const formData = new URLSearchParams();
		formData.append('grant_type', 'password');
		formData.append('username', data.username);
		formData.append('password', data.password);

		return await http.post('/auth/token', formData, {
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
			},
		}); // 假设返回值中包含 token
	};

	useEffect(() => {
		const savedUsername = localStorage.getItem('LAST_LOGIN_USERNAME');
		if (savedUsername) {
			setLastLoginUsername(savedUsername); // 自动填充用户名
		}
	}, []);

	return (
		<div className="login-container">
			<div
				style={{
					display: 'flex',
					height: '100vh',
					justifyContent: 'center',
					alignItems: 'center',
				}}>
				<Form
					style={{ width: '350px' }}
					name="login_form"
					className="login-form"
					onFinish={onFinish}>
					<Form.Item
						name="username"
						key={lastLoginUsername}
						initialValue={lastLoginUsername}
						rules={[{ required: true, message: '请输入用户名!' }]}>
						<Input prefix={<UserOutlined />} placeholder="用户名" size="large" />
					</Form.Item>

					<Form.Item name="password" rules={[{ required: true, message: '请输入密码!' }]}>
						<Input.Password prefix={<LockOutlined />} placeholder="密码" size="large" />
					</Form.Item>

					<Form.Item>
						<Button
							type="primary"
							htmlType="submit"
							className="login-form-button"
							size="large"
							loading={loading}
							block>
							登录
						</Button>
					</Form.Item>
				</Form>
			</div>
		</div>
	);
};

export default Login;
