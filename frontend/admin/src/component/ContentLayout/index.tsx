import React, { ReactNode } from 'react';
import { matchPath } from 'react-router';
import { useLocation } from 'react-router-dom';

import { Breadcrumb, Layout, Spin, Tabs, TabsProps } from 'antd';

import routes from '@/routes';

import './index.less';

import styles from './index.module.less';
import HeaderInfo, { HeaderInfoProps } from '@/component/HeaderInfo';

const { Content, Footer } = Layout;

export type LayoutHeaderProps = {
	footer?: ReactNode;
	extra?: ReactNode;
	tabsProps?: TabsProps;
	info?: Omit<HeaderInfoProps, 'extra'>;
};
type LayoutProps = {
	footer?: React.ReactNode;
	children?: React.ReactNode;
	loading?: boolean;
	header?: LayoutHeaderProps;
};
const ContentLayout: React.FC<LayoutProps> = props => {
	const { footer, header, loading, children, ...rest } = props;
	const headerFooter = header?.footer;
	const tabsProps = header?.tabsProps;
	const info = header?.info;

	const extra = header?.extra;
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
		<Spin spinning={!!loading}>
			{/* 内容区域 */}
			<Content
				className={footer ? 'yh-layout__has-footer yh-layout' : 'yh-layout'}
				style={{ minHeight: minHeight }}>
				<div className={styles['yh-layout-header']}>
					{/* 动态渲染面包屑 */}
					<Breadcrumb className={'mb-4'}>
						{breadcrumbItems.map(item => (
							<Breadcrumb.Item key={item.path}>{item.title}</Breadcrumb.Item>
						))}
					</Breadcrumb>
					<h2 className="yh-header-heading-title">
						{breadcrumbItems[breadcrumbItems.length - 1].title}
					</h2>
					<HeaderInfo {...info} extra={extra} />
					<div className={!tabsProps ? 'mb-4' : undefined}>{headerFooter}</div>
					{tabsProps ? (
						<Tabs
							{...tabsProps}
							items={tabsProps?.items?.map(it => ({
								...it,
								children: (
									<div
										style={{
											background: '#f5f5f5',
											padding: 24,
										}}
										key={it.key}>
										{it.children}
									</div>
								),
							}))}
							size={'small'}
						/>
					) : null}
				</div>
				<div>{children}</div>
			</Content>

			{footer ? <Footer style={{ background: '#fff', padding: '8px' }}>{footer}</Footer> : null}
		</Spin>
	);
};
export default ContentLayout;
