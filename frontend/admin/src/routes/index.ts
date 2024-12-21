import {RouteConfig} from 'react-router-config';
import React from 'react';
import userRoutes from '@/pages/user/routes';

import productRoutes from '@/pages/product/routes';

const Home = React.lazy(() => import('@/pages/Home'));

const Login = React.lazy(() => import('@/pages/Login'));

const routes: RouteConfig[] = [
	{
		path: '/home',
		component: Home,
		title: '首页',
		routes: [],
	},
	...userRoutes,
	...productRoutes,
];

export default routes;
