import {RouteConfig} from 'react-router-config';
import React from "react";

const Home = React.lazy(() => import('@/pages/Home'));

const Login = React.lazy(() => import('@/pages/Login'));
const routes: RouteConfig[] = [
    {
        path: '/home',
        component: Home,
        routes: [],
    },
    {
        path: '/login',
        component: Login,
        routes: [],
    },
];

export default routes;
