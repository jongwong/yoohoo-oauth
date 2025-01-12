import { FormInstance } from 'antd';
import { Key } from 'react';
import { BaseProFieldType } from '@/component/pro-component/types';

export type ProFieldType<T = any> = BaseProFieldType<T>;

export type BaseFormItemOptionType<T = any> = {
	field: ProFieldType<T>;
	form: FormInstance<T>;
	fieldName: Key[];
	index?: number;
};

export type BaseTableOptionType<T = any> = {
	field: ProFieldType<T>;
	form: FormInstance<T>;
	fieldName: Key[];
	index: number;
	operations: { remove: () => void };
};

export type GetColumnPropsFnType<T = any, P = any, K = any> = (
	// 所有的getColumnProps 参数要统一
	t: any,
	r: T,
	opts: {
		formKey: Key[];
		form: FormInstance<T>;
		field: ProFieldType<T>;
	} & K
) => Partial<P>;
