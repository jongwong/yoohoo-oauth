import React from 'react';

import {
	PAGES_DISTRIBUTION_POINT_CREATE_URL,
	PAGES_DISTRIBUTION_POINT_DETAIL_URL,
	PAGES_DISTRIBUTION_POINT_URL,
} from '@/pages/distribution-point/pages';

const List = React.lazy(() => import('@/pages/distribution-point/List'));

const Detail = React.lazy(() => import('@/pages/distribution-point/Detail'));
const routes = [
	{
		path: PAGES_DISTRIBUTION_POINT_URL,
		title: '配送点管理',
		element: <List />,
	},
	{
		path: PAGES_DISTRIBUTION_POINT_CREATE_URL,
		hidden: true,
		title: '创建配送点',
		element: <Detail />,
	},
	{
		path: PAGES_DISTRIBUTION_POINT_DETAIL_URL,
		hidden: true,
		title: '配送点详情',
		element: <Detail />,
	},
];

export default routes;
