import React, { Suspense } from 'react';
import { Route, Routes } from 'react-router-dom'; // 使用 Routes 来包裹路由
import { App, ConfigProvider, Layout } from 'antd';
import zhCN from 'antd/es/locale/zh_CN';

import MenuComponent from '@/layout/Menu';
import Login from '@/pages/Login';
import routes from '@/routes'; // 引入路由配置
import LogoSvg from './logo.svg';
import { CaretRightFilled } from '@ant-design/icons';
import { useLocalStorageState } from 'ahooks';

const { Header, Sider } = Layout;

const MainLayout: React.FC = () => {
	const renderRoutes = (routeList: any[]) => {
		return routeList.map(route => {
			// 在 v6 中，使用 element 属性传递 JSX 组件
			return <Route key={route.path} path={route.path} element={route.element} />;
		});
	};

	const [menuVisible, _setMenuVisible] = useLocalStorageState('YOOHOO_ADMIN_MENU_VISIBLE', {
		defaultValue: true,
	});
	const setMenuVisible = (e: boolean) => {
		_setMenuVisible(e);
	};

	return (
		<ConfigProvider
			locale={zhCN}
			theme={{
				token: {
					// Seed Token，影响范围大
					colorPrimary: '#446fef',
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
									width={menuVisible ? 200 : 0}
									theme="light"
									style={{ border: '1px solid #eee', zIndex: 100 }}>
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
									<div
										onClick={() => {
											setMenuVisible(!menuVisible);
										}}
										style={{
											position: 'absolute',
											background: '#fff',
											width: 12,
											height: 56,
											right: -12,
											display: 'flex',
											alignItems: 'center',
											justifyContent: 'end',
											cursor: 'pointer',
											boxShadow: '2px 2px 4px rgba(0, 0, 0, 1)',
											clipPath: 'polygon(0 0, 100% 10%, 100% 90%, 0 100%)', // 对称梯形
											zIndex: 10, // 确保主内容在伪元素之上
										}}>
										{/* 图标内容 */}
										<CaretRightFilled
											rotate={menuVisible ? 180 : 0}
											style={{
												position: 'absolute',
												left: -3,
												color: '#666',
											}}
										/>
									</div>
								</Sider>

								<Layout>
									{/* 顶部导航 */}
									{/*<Header*/}
									{/*	style={{*/}
									{/*		background: '#fff',*/}
									{/*		padding: 0,*/}
									{/*		boxShadow: '0px 1px 4px rgba(0, 21, 41, .118)',*/}
									{/*		zIndex: 10,*/}
									{/*	}}>*/}
									{/*	<div style={{ padding: '0 16px' }}>*/}
									{/*		<h2 style={{ margin: 0 }}>333管理系统</h2>*/}
									{/*	</div>*/}
									{/*</Header>*/}
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
