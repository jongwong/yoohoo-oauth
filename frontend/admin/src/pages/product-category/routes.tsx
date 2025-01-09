import React from 'react';

import {
	PAGES_PRODUCT_CATEGORY_CREATE_URL,
	PAGES_PRODUCT_CATEGORY_DETAIL_URL,
	PAGES_PRODUCT_CATEGORY_URL,
} from '@/pages/product-category/pages';

const List = React.lazy(() => import('@/pages/product-category/List'));

const Detail = React.lazy(() => import('@/pages/product-category/Detail'));

const Create = React.lazy(() => import('@/pages/product-category/Create'));
const routes = [
	{
		path: PAGES_PRODUCT_CATEGORY_URL,
		title: '商品类别',
		element: <List />,
	},
	{
		path: PAGES_PRODUCT_CATEGORY_CREATE_URL,
		hidden: true,
		title: '商品类别创建',
		element: <Create />,
	},
	{
		path: PAGES_PRODUCT_CATEGORY_DETAIL_URL,
		hidden: true,
		title: '商品类别详情',
		element: <Detail />,
	},
];

export default routes;
