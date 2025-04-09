import { useRequest as _useRequest } from 'ahooks';

import type { Options, Plugin, Result, Service } from 'ahooks/lib/useRequest/src/types';
import { useMemo } from 'react';
import { has } from 'lodash';

// 自定义 Hook：增强 `useRequest` 的功能
const useRequest = <TData, TParams extends any[]>(
	service: Service<TData, TParams>, // service 是一个返回 ApiResponse 的异步函数
	options?: Options<TData, TParams>, // 可选的配置
	plugins?: Plugin<TData, TParams>[] // 可选的插件
): Result<TData, TParams> & {
	data: TData extends { code: number; data: infer U } ? U : TData;
} => {
	const re = _useRequest(service, options, plugins);

	return useMemo(() => {
		let data: any = re.data;
		if (has(data, 'code') && has(data, 'data')) {
			data = data.data;
		}
		re.data = data;
		return re as any;
	}, [re]);
};

export default useRequest;
