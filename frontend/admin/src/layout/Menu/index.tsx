// MenuComponent.jsx
import React from 'react';
import {Menu} from 'antd';
import {RouteConfig} from "react-router-config";

const MenuComponent: React.FC<{
    routes: RouteConfig
}> = ({routes}) => {
    const items = (routes || [])
        .filter((route: any) => !route.hidden) // 过滤掉隐藏的路由
        .map((route: any) => ({
            key: route.path, // 路径作为菜单项的唯一标识
            icon: route.icon, // 如果有 icon 属性，使用它；否则使用默认的 icon
            label: route.title, // 菜单项的文字
            children: route?.routes?.lenght ? route.routes.map((childRoute: any) => ({
                key: childRoute.path,
                label: childRoute.title,
                icon: childRoute.icon,
                // 递归处理子路由
            })) : undefined,
        }));
    return (
        <Menu items={items}/>
    );
};

export default MenuComponent;
