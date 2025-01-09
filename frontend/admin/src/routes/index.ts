import { RouteConfig } from 'react-router-config';
import React from 'react';
import userRoutes from '@/pages/user/routes';

import productRoutes from '@/pages/product/routes';

import couponsRoutes from '@/pages/coupons/routes';
import distributionPointRoutes from '@/pages/distribution-point/routes';

import productCategoryRoutes from '@/pages/product-category/routes';

const Home = React.lazy(() => import('@/pages/Home'));

const routes: RouteConfig[] = [
	{
		path: '/home',
		component: Home,
		title: '首页',
		routes: [],
	},
	...couponsRoutes,
	...userRoutes,
	...productRoutes,
	...distributionPointRoutes,
	...productCategoryRoutes,
];

export default routes;
