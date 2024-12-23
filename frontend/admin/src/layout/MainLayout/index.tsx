import React, { Suspense } from 'react';
import { Route, Routes } from 'react-router-dom'; // 使用 Routes 来包裹路由
import { App, ConfigProvider, Layout } from 'antd';
import zhCN from 'antd/es/locale/zh_CN';

import MenuComponent from '@/layout/Menu';
import Login from '@/pages/Login';
import routes from '@/routes'; // 引入路由配置
import LogoSvg from './logo.svg';

const { Header, Sider } = Layout;

const MainLayout: React.FC = () => {
	const renderRoutes = (routeList: any[]) => {
		return routeList.map(route => {
			// 在 v6 中，使用 element 属性传递 JSX 组件
			return <Route key={route.path} path={route.path} element={route.element} />;
		});
	};

	return (
		<ConfigProvider
			locale={zhCN}
			theme={{
				token: {
					// Seed Token，影响范围大
					colorPrimary: '#67aadc',
				},
			}}>
			<App>
				<Routes>
					{/* 登录路由 */}
					<Route path="/login" element={<Login />} />

					{/* 其他路由 */}
					<Route
						path="/*"
						element={
							<Layout style={{ minHeight: '100vh' }}>
								{/* 侧边栏 */}
								<Sider
									width={200}
									theme="light"
									style={{ boxShadow: '1px 0 2px rgba(0, 0, 0, 0.05)', zIndex: 100 }}>
									<div
										style={{
											color: '#4d6af1',
											fontSize: '22px',
											padding: '12px 16px',
											textAlign: 'left',
										}}>
										<LogoSvg style={{ height: 22 }} />
									</div>
									<MenuComponent routes={routes} /> {/* 动态生成菜单 */}
								</Sider>

								<Layout>
									{/* 顶部导航 */}
									<Header
										style={{
											background: '#fff',
											padding: 0,
											boxShadow: '0px 1px 4px rgba(0, 21, 41, .118)',
											zIndex: 10,
										}}
										title="3333">
										<div style={{ padding: '0 16px' }}>
											<h2 style={{ margin: 0 }}>管理系统</h2>
										</div>
									</Header>

									<Suspense fallback={<div>Loading...</div>}>
										<Routes>{renderRoutes(routes)}</Routes>
									</Suspense>
								</Layout>
							</Layout>
						}
					/>
				</Routes>
			</App>
		</ConfigProvider>
	);
};

export default MainLayout;
