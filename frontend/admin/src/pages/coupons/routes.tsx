import React from 'react';

import {
	PAGES_COUPONS_CREATE_URL,
	PAGES_COUPONS_DETAIL_URL,
	PAGES_COUPONS_URL,
} from '@/pages/coupons/pages';

const List = React.lazy(() => import('@/pages/coupons/List'));

const Detail = React.lazy(() => import('@/pages/coupons/Detail'));
const routes = [
	{
		path: PAGES_COUPONS_URL,
		title: '优惠券管理',
		element: <List />,
	},
	{
		path: PAGES_COUPONS_DETAIL_URL,
		hidden: true,
		title: '优惠券详情',
		element: <Detail />,
	},
	{
		path: PAGES_COUPONS_CREATE_URL,
		hidden: true,
		title: '创建优惠券',
		element: <Detail />,
	},
];

export default routes;
