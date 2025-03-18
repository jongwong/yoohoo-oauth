import axios, { AxiosError, AxiosRequestConfig, AxiosResponse } from 'axios';
import { deleteCookie, getCookie } from '@/utils/cookie';
import { Modal } from 'antd';
import { isNil, omitBy } from 'lodash';
import axiosRetry from 'axios-retry';

const getRedirectUrl = () => {
	const pathname = window.location.pathname; // 获取当前页面路径（不包含域名和查询参数）
	const search = window.location.search; // 获取查询参数部分（包括问号“?”）
	// 跳转到登录页，并附加当前路径和查询参数
	return '/login?redirect_uri=' + encodeURIComponent(pathname + search);
};
const openLoginConfirm = (message: string) => {
	Modal.confirm({
		title: '未登录',
		content: message,
		onOk: () => {
			window.location.href = getRedirectUrl();
		},
	});
};

const tryConfirm = (error: AxiosError): Promise<boolean> => {
	return new Promise(resolve => {
		const data = error?.response?.data || {
			code: -1,
			success: false,
			message: '',
		};
		if (error?.status === 401 || data?.code === 401) {
			deleteCookie('access_token');
			openLoginConfirm(data?.message);
			return Promise.resolve(data);
		}
		Modal.confirm({
			title: '请求超时，请重试',
			content: error.data?.message,
			onOk: async () => {
				// 实现重试逻辑
				return resolve(true);
			},
			onCancel: async () => {
				// 实现重试逻辑
				return resolve(false);
			},
		});
	});
};

// 创建 Axios 实例
const http = axios.create({
	baseURL: 'http://dev.api.yoohoo.cn', // 后端 API 基础地址
	timeout: 10000, // 请求超时时间
	headers: {
		'Content-Type': 'application/json', // 默认请求头
	},
});

// 配置 axios-retry：对于失败的请求进行重试
axiosRetry(http, {
	retries: 10, // 重试次数
	retryDelay: axiosRetry.exponentialDelay, // 使用指数回退延迟
	retryCondition: error => {
		// 在这里可以根据错误的类型来决定是否重试
		return tryConfirm(error);
	},
});

// 请求拦截器：全局设置 Authorization
http.interceptors.request.use(
	config => {
		const token = getCookie('access_token')?.trim();

		if (!token && window.location.pathname !== '/login') {
			window.location.href = getRedirectUrl();
			return;
		}
		if (token?.length) {
			config.headers['Authorization'] = `Bearer ${token}`;
		}
		return config;
	},
	error => {
		return Promise.reject(error);
	}
);

// 响应拦截器：全局捕获错误
http.interceptors.response.use(
	response => {
		const ob = response?.data || {};
		return { ...ob, success: ob?.code === 0 };
	},
	async error => {
		const data = error?.response?.data || {
			code: -1,
			success: false,
			message: '',
		};

		if (error?.status === 401 || data?.code === 401) {
			deleteCookie('access_token');
			openLoginConfirm(data?.message);
			return Promise.resolve(data);
		}

		return Promise.resolve(data);
	}
);

type ResponseData<T = any> = {
	code: number;
	message: string;
	data: T;
	success: boolean;
	total: number;
};

export default {
	request: (config: AxiosRequestConfig) => {
		return http.request(config).catch(error => {
			console.error('Global Error Handling (request):', error);
			// 可以在这里统一处理错误
			return Promise.reject(error); // 必须返回 rejection，否则会继续执行
		});
	},

	get: (url: string, config?: AxiosRequestConfig) => {
		const params = omitBy(config?.params || {}, it => {
			return it === -1 || it === '' || isNil(it);
		});
		try {
			return http
				.get(url, {
					...config,
					params,
				})
				.catch(error => {});
		} catch (error) {
			console.error('Global Error Handling (GET):', error);
			// 可以在这里统一处理错误
			return Promise.resolve({
				success: false,
				code: -1,
			});
		} // 必须返回 rejection
	},

	delete: (url: string, config?: AxiosRequestConfig) => {
		return http.delete(url, config).catch(error => {
			console.error('Global Error Handling (DELETE):', error);
			// 可以在这里统一处理错误
			return Promise.reject(error); // 必须返回 rejection，否则会继续执行
		});
	},

	post: (url: string, data?: any, config?: AxiosRequestConfig) => {
		return http.post(url, data, config).catch(error => {
			console.error('Global Error Handling (POST):', error);
			// 可以在这里统一处理错误
			return Promise.reject(error); // 必须返回 rejection，否则会继续执行
		});
	},

	put: (url: string, data?: any, config?: AxiosRequestConfig) => {
		return http.put(url, data, config).catch(error => {
			console.error('Global Error Handling (PUT):', error);
			// 可以在这里统一处理错误
			return Promise.reject(error); // 必须返回 rejection，否则会继续执行
		});
	},
} as {
	request<T = any, R = AxiosResponse<T>, D = any>(
		config: AxiosRequestConfig<D>
	): Promise<ResponseData<T>>;
	get<T = any, R = AxiosResponse<T>, D = any>(
		url: string,
		config?: AxiosRequestConfig<D>
	): Promise<ResponseData<T>>;
	delete<T = any, R = AxiosResponse<T>, D = any>(
		url: string,
		config?: AxiosRequestConfig<D>
	): Promise<ResponseData<T>>;
	post<T = any, R = AxiosResponse<T>, D = any>(
		url: string,
		data?: D,
		config?: AxiosRequestConfig<D>
	): Promise<ResponseData<T>>;
	put<T = any, R = AxiosResponse<T>, D = any>(
		url: string,
		data?: D,
		config?: AxiosRequestConfig<D>
	): Promise<ResponseData<T>>;
};
