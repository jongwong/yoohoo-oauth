import { initial, isObject, last } from 'lodash';

export function getQueryByName(paramName: string, url?: string): string | null {
	// 如果没有传入 URL，使用当前浏览器的 URL
	const fullUrl = url
		? url.startsWith('?')
			? window.location.origin + url
			: url
		: window.location.href;

	// 创建一个 URL 对象
	const urlObj = new URL(fullUrl, window.location.origin); // 以当前域名为基准来解析相对 URL

	// 使用 searchParams 获取查询参数
	return urlObj.searchParams.get(paramName);
}

const validateUrlParams = (routePath: string, args: (string | number)[]) => {
	// 提取 URL 路径中的占位符
	const pathVariables = routePath.split('/').filter(part => part.startsWith(':'));

	// 校验参数数量
	if (pathVariables.length !== args.length) {
		throw Error(
			`路径参数数量不匹配：路径中有 ${pathVariables.length} 个参数，但提供了 ${args.length} 个参数`
		);
	}
};

// 第一个重载签名: 路径和一个路径变量
export function transformUrlByRoutePath<T extends Record<string, string | number>>(
	routePath: string,
	pathVariable: (string | number)[],
	query?: T
): string;

// 第二个重载签名: 路径和两个路径变量
export function transformUrlByRoutePath<T extends Record<string, string | number>>(
	routePath: string,
	pathVariable1: (string | number)[],
	pathVariable2: (string | number)[],
	query?: T
): string;

// 第三个重载签名: 仅路径和查询参数
export function transformUrlByRoutePath<T extends Record<string, string | number>>(
	routePath: string,
	query?: T
): string;

export function transformUrlByRoutePath<T extends Record<string, string | number>>(
	routePath: string,
	...args: any[]
): string {
	let url = routePath;

	// 去掉最后一个参数，如果它是对象
	const pathVariableList = isObject(last(args)) ? initial(args) : args;
	const query = isObject(last(args)) ? last(args) : undefined;

	// 校验路径参数数量
	validateUrlParams(routePath, pathVariableList);

	// 获取路径变量的占位符
	const pathVariables = routePath.split('/').filter(part => part.startsWith(':'));

	// 替换路径中的路径变量
	pathVariables.forEach((variable, index) => {
		// 获取对应的路径参数
		const paramValue = pathVariableList[index];
		// 动态替换占位符（例如 :couponsId 替换为相应的值）
		url = url.replace(variable, paramValue.toString());
	});

	// 添加查询参数
	if (query) {
		const queryString = new URLSearchParams(query).toString();
		url += (url.includes('?') ? '&' : '?') + queryString;
	}

	return url;
}
