import React, {Suspense} from 'react';
import {ConfigProvider, Layout} from 'antd';
import {BrowserRouter as Router, Route} from 'react-router-dom';
import {renderRoutes} from 'react-router-config';
import MenuComponent from '@/layout/Menu';
import routes from '@/routes'; // 引入路由配置
import LogoSvg from './logo.svg';
import Login from "@/pages/Login";

const { Header, Content, Footer, Sider } = Layout;

const MainLayout: React.FC = () => {
	return (
		<Router>
			<ConfigProvider
				theme={{
					token: {
						// Seed Token，影响范围大
						colorPrimary: '#67aadc',
					},
				}}>


				<Route path={'/login'} component={Login}></Route>

				<Route path={'/'}>
					<Layout style={{minHeight: '100vh'}}>
						{/* 侧边栏 */}
						<Sider
							width={200}
							theme="light"
							style={{boxShadow: '1px 0 2px rgba(0, 0, 0, 0.05)', zIndex: 100}}>
							<div
								style={{
									color: '#4d6af1',
									fontSize: '22px',
									padding: '12px 16px',
									textAlign: 'left',
								}}>
								<LogoSvg style={{height: 22}}/>
							</div>
							<MenuComponent routes={routes}/> {/* 动态生成菜单 */}
						</Sider>

						<Layout>
							{/* 顶部导航 */}
							<Header
								style={{
									background: '#fff',
									padding: 0,
									// height: 48,
									// lineHeight: '48px',
									boxShadow: '0px 1px 4px rgba(0, 21, 41, .118)',
									zIndex: 10,
								}}
								title={'3333'}>
								<div style={{padding: '0 16px'}}>
									<h2 style={{margin: 0}}>管理系统</h2>
								</div>
							</Header>
							<Suspense>
								{renderRoutes(routes)}
							</Suspense>

						</Layout>
					</Layout>

				</Route>

			</ConfigProvider>
		</Router>
	);
};

export default MainLayout;
