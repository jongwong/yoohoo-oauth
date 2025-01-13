import React from 'react';

import {
	PAGES_PURCHASE_GROUP_CREATE_URL,
	PAGES_PURCHASE_GROUP_DETAIL_URL,
	PAGES_PURCHASE_GROUP_PRODUCT_URL,
	PAGES_PURCHASE_GROUP_URL,
} from './pages'; // 使用相对路径

const List = React.lazy(() => import('./List')); // 使用相对路径

const GroupList = React.lazy(() => import('./GroupList')); // 使用相对路径

const Detail = React.lazy(() => import('./Detail')); // 使用相对路径

const Create = React.lazy(() => import('./Create')); // 使用相对路径

const routes = [
	{
		path: PAGES_PURCHASE_GROUP_PRODUCT_URL,
		title: '团购商品',
		element: <GroupList />,
	},
	{
		path: PAGES_PURCHASE_GROUP_URL,
		title: '团购管理',
		element: <List />,
	},
	{
		path: PAGES_PURCHASE_GROUP_CREATE_URL,
		hidden: true,
		title: '团购管理创建',
		element: <Create />,
	},
	{
		path: PAGES_PURCHASE_GROUP_DETAIL_URL,
		hidden: true,
		title: '团购管理详情',
		element: <Detail />,
	},
];

export default routes;
