import { Key, ReactNode } from 'react';

import { FormInstance, FormItemProps, TableColumnType } from 'antd';

import { type ReadonlyEnumMap } from '@/utils/enum';
import { BaseFormItemOptionType, BaseTableOptionType } from '../ProField/types';

export type BaseProFieldType<T = any> = {
	valueType?: string; // ProField 类型，如 'text', 'select', 'dateRange'
	valueEnum?: ReadonlyEnumMap<number>;
	placeholder?: string; // 输入提示
	fieldProps?: Record<string, any>; // 额外字段属性
	formItemProps?: FormItemProps; // 额外 Form.Item 属性
	extraFieldNames?: string[];
	renderFormItem?: (t: any, r: T, opt: BaseFormItemOptionType) => ReactNode;
	render?: (t: any, r: T, idx: number, opt: BaseFormItemOptionType) => ReactNode;
	editable?: boolean;
};
export type BaseFormProFieldType<T = any> = BaseProFieldType<T> & {
	name?: Key | Key[]; // 字段名
	label?: ReactNode; // 显示的标签
	visible?: boolean; // 是否隐藏
};

export type BaseEditTableProFieldType<T = any> = Omit<
	BaseProFieldType<T>,
	'render' | 'renderFormItem'
> & {
	dataIndex?: Key | Key[]; // 字段名
	title?: ReactNode; // 显示的标签
	visible?: boolean; // 是否隐藏

	render?: (t: any, r: T, idx: number, opt: BaseTableOptionType<T>) => ReactNode;
	renderFormItem?: (t: any, r: T, opt: BaseTableOptionType<T>) => ReactNode;
} & Omit<TableColumnType, 'render'>;

export type BaseFormProFieldFuncType<T = any> =
	| BaseFormProFieldType<T>
	| ((r: T, form: FormInstance<T>) => BaseFormProFieldType<T>);

export type BaseTableProFieldFuncType<T = any> =
	| BaseEditTableProFieldType<T>
	| ((r: T, form: FormInstance<T>) => BaseEditTableProFieldType<T>);

export type BaseTableProFieldType<T = any> = BaseProFieldType<T> & {
	dataIndex?: Key | Key[]; // 字段名
	title?: ReactNode; // 显示的标签
	extraFieldNames?: string[];
};

export type ElementOf<T> = T extends (infer U)[] ? U : never;
