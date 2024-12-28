import React from 'react';
import { PAGES_PRODUCT_CREATE_URL, PAGES_PRODUCT_DETAIL_URL } from '@/pages/product/pages';

const List = React.lazy(() => import('@/pages/product/List'));

const Detail = React.lazy(() => import('@/pages/product/Detail'));
const routes = [
	{
		path: '/product',
		title: '商品管理',
		element: <List />,
	},
	{
		path: PAGES_PRODUCT_CREATE_URL,
		hidden: true,
		title: '创建商品',
		element: <Detail />,
	},
	{
		path: PAGES_PRODUCT_DETAIL_URL,
		hidden: true,
		title: '商品详情',
		element: <Detail />,
	},
];

export default routes;
