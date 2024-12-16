import React from 'react';
import { Breadcrumb, Layout } from 'antd';
import './index.less';
// @ts-ignore
import { matchPath, useLocation } from 'react-router';
import routes from '@/routes';

const { Header, Content, Footer } = Layout;

type LayoutProps = {
	footer?: React.ReactNode;
	children?: React.ReactNode;
};
const ContentLayout: React.FC<LayoutProps> = props => {
	const { footer, children, ...rest } = props;
	const minHeight = footer ? 'calc(100vh - 64px - 48px)' : 'calc(100vh - 64px)';
	const location = useLocation();
	// 递归查找当前路径对应的路由层级
	const generateBreadcrumbItems = (routes: any, pathname: string) => {
		const breadcrumbItems: any[] = [];
		const findRoute = (routeList: any[], path: string) => {
			for (const route of routeList) {
				const match = matchPath(path, { path: route.path, exact: route.exact });
				if (match) {
					breadcrumbItems.push(route);
					if (route.routes) {
						findRoute(route.routes, path); // 递归查找子路由
					}
					break;
				}
			}
		};
		findRoute(routes, pathname);
		return breadcrumbItems;
	};
	const breadcrumbItems = generateBreadcrumbItems(routes, location.pathname);

	return (
		<>
			{/* 内容区域 */}
			<Content
				className={footer ? 'yh-layout-content__has-footer yh-layout-content' : 'yh-layout-content'}
				style={{ minHeight: minHeight }}>
				<div style={{ padding: '8px 24px', background: '#fff' }}>
					{/* 动态渲染面包屑 */}
					<Breadcrumb>
						{breadcrumbItems.map(item => (
							<Breadcrumb.Item key={item.path}>{item.title}</Breadcrumb.Item>
						))}
					</Breadcrumb>
					<h2 className="yh-header-heading-title">
						{breadcrumbItems[breadcrumbItems.length - 1].title}
					</h2>
				</div>
				<div>{children}</div>
			</Content>

			{footer ? <Footer style={{ background: '#fff', padding: '8px' }}>{footer}</Footer> : null}
		</>
	);
};
export default ContentLayout;
