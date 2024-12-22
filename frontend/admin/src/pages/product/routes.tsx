import React from 'react';

const List = React.lazy(() => import('@/pages/product/List'));

const Detail = React.lazy(() => import('@/pages/product/Detail'));
const routes = [
	{
		path: '/product',
		title: '商品管理',
		element: <List />,
	},
	{
		path: '/product/:productId',
		hidden: true,
		title: '商品详情',
		element: <Detail />,
	},
];

export default routes;
