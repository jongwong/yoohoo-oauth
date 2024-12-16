import React from 'react';
import { Layout } from 'antd';
import { BrowserRouter as Router } from 'react-router-dom';
import { renderRoutes } from 'react-router-config';
import MenuComponent from '@/layout/Menu';
import routes from '@/routes'; // 引入路由配置

const { Header, Content, Footer, Sider } = Layout;

const MainLayout: React.FC = () => {
	return (
		<Router>
			<Layout style={{ minHeight: '100vh' }}>
				{/* 侧边栏 */}
				<Sider
					width={200}
					theme="light"
					style={{ boxShadow: '1px 0 2px rgba(0, 0, 0, 0.05)', zIndex: 100 }}>
					<div style={{ color: '#4d6af1', fontSize: '24px', padding: 12, textAlign: 'center' }}>
						LOGO
					</div>
					<MenuComponent routes={routes} /> {/* 动态生成菜单 */}
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
						<div style={{ padding: '0 16px' }}>
							<h2 style={{ margin: 0 }}>管理系统</h2>
						</div>
					</Header>
					{renderRoutes(routes)}
				</Layout>
			</Layout>
		</Router>
	);
};

export default MainLayout;
