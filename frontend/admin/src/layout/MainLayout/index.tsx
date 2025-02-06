import React, { Suspense } from 'react';
import { Route, Routes } from 'react-router-dom'; // 使用 Routes 来包裹路由
import { App, ConfigProvider, DatePicker, Layout } from 'antd';

import MenuComponent from '@/layout/Menu';
import Login from '@/pages/Login';
import routes from '@/routes'; // 引入路由配置
import LogoSvg from './logo.svg';
import { useLocalStorageState } from 'ahooks';
import dayjs from 'dayjs';
import locale from 'antd/locale/zh_CN';

import 'dayjs/locale/zh-cn';
import { CaretRightFilled } from '@ant-design/icons';

dayjs.locale('zh-cn');

const { Header, Sider } = Layout;

const ranges = {
	明天: [dayjs().add(1, 'day').startOf('date'), dayjs().add(1, 'day').endOf('date')],
	下一周: [dayjs().startOf('date'), dayjs().add(7, 'day').endOf('date')],
	下个月: [dayjs().add(1, 'month').startOf('month'), dayjs().add(1, 'month').endOf('month')],
	昨天: [dayjs().subtract(1, 'day').startOf('date'), dayjs().subtract(1, 'day').endOf('date')],
	最近7天: [dayjs().subtract(6, 'day').startOf('date'), dayjs().endOf('date')],
	最近30天: [dayjs().subtract(29, 'day').startOf('date'), dayjs().endOf('date')],
	今天: [dayjs().startOf('date'), dayjs().endOf('date')],
	本周: [dayjs().startOf('week'), dayjs().endOf('date')],
	本月: [dayjs().startOf('month'), dayjs().endOf('date')],
};

// 全局配置默认 props
DatePicker.RangePicker.defaultProps = {
	ranges,
} as any;

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
			locale={locale}
			theme={{
				token: {
					// Seed Token，影响范围大
					colorPrimary: '#446fef',
				},
			}}>
			<App className={'h-1-1 w-1-1'}>
				<Routes>
					{/* 登录路由 */}
					<Route path="/login" element={<Login />} />

					{/* 其他路由 */}
					<Route
						path="/*"
						element={
							<Layout style={{ minHeight: '100vh' }}>
								<div
									style={{
										position: 'relative',
										height: '100vh',
										width: menuVisible ? 200 : 0,
									}}></div>
								{/* 侧边栏 */}
								<div
									style={{
										border: '1px solid #eee',
										zIndex: 1,
										position: 'fixed',
										height: '100vh',
										left: 0,
										top: 0,

										width: menuVisible ? 200 : 0,
									}}>
									<div
										onClick={() => {
											setMenuVisible(!menuVisible);
										}}
										style={{
											position: 'absolute',
											width: 12,
											height: 56,
											top: '50%',
											right: -12,
											cursor: 'pointer',
											boxShadow: '0x 2px 4px rgba(0, 0, 0, 0.05)',
											zIndex: 1000, // 确保主内容在伪元素之上
										}}>
										<div
											style={{
												background: '#fff',
												width: '100%',
												height: '100%',
												display: 'flex',
												alignItems: 'center',
												justifyContent: 'end',

												clipPath: 'polygon(0 0, 100% 10%, 100% 90%, 0 100%)', // 对称梯形
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
									</div>
								</div>
								<Sider
									width={menuVisible ? 200 : 0}
									theme="light"
									style={{
										border: '1px solid #eee',
										zIndex: 1,
										position: 'fixed',
										height: '100vh',
										left: 0,
										top: 0,
										overflowY: 'auto',
									}}>
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
