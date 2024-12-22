import React from 'react';

const UserList = React.lazy(() => import('@/pages/user/UserList'));

const UserDetail = React.lazy(() => import('@/pages/user/UserDetail'));
const routes = [
    {
        path: '/user/list',
        title: '用户管理',
        element: <UserList/>,

    },
    {
        path: '/user/detail',
        hidden: true,
        title: '用户详情',
        element: <UserDetail/>,
    },
];

export default routes;
