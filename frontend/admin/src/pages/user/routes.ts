import { RouteConfig } from 'react-router-config';
import React from 'react';
import userList from '@/pages/user/UserList';

const UserList = React.lazy(() => import('@/pages/user/UserList'));

const UserDetail = React.lazy(() => import('@/pages/user/UserDetail'));
const routes: RouteConfig[] = [
	{
		path: '/user/list',
		title: '用户管理',
		component: userList,
		routes: [
			{
				path: '/user/detail',
				hidden: true,
				title: '用户详情',
				component: UserDetail,
			},
		],
	},
];

export default routes;
