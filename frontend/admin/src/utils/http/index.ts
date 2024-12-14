import axios from 'axios';
import {getCookie} from "@/utils/cookie";
import {Modal} from 'antd'; // 确保正确引入 Modal

// 创建 Axios 实例
const http = axios.create({
    baseURL: 'http://localhost:8080', // 后端 API 基础地址
    timeout: 10000,                  // 请求超时时间
    headers: {
        'Content-Type': 'application/json', // 默认请求头
    },
});


// 请求拦截器：全局设置 Authorization
http.interceptors.request.use(
    (config) => {
        const token = getCookie("access_token");
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器：全局捕获错误
http.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        const data = error?.response?.data
        if (error.status === 401 || data.code === 401) {
            Modal.confirm({
                title: "未登录",
                content: data?.message,
                onOk: () => {
                    window.location.href = '/login'; // 跳转到登录页
                }
            });
        }
        // 返回被拒绝的 Promise，供调用方继续处理
        return Promise.resolve(data);
    }
);

export default http;
