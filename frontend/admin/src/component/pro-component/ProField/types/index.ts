import { FormInstance } from 'antd';
import { Key } from 'react';

export type ProFieldType<T = any> = {};

export type BaseFormItemOptionType<T = any> = {
	field: ProFieldType<T>;
	form: FormInstance<T>;
	formName: Key[];
	index?: number;
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
