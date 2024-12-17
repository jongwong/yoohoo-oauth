import { RouteConfig } from 'react-router-config';
import React from 'react';

const List = React.lazy(() => import('@/pages/product/List'));

const Detail = React.lazy(() => import('@/pages/product/Detail'));
const routes: RouteConfig[] = [
	{
		path: '/product/list',
		title: '商品管理',
		component: List,
		routes: [
			{
				path: '/product/detail',
				hidden: true,
				title: '商品详情',
				component: Detail,
			},
		],
	},
];

export default routes;
