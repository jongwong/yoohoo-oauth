import { type ReadonlyEnumMap } from '@/utils/enum';
import { Key, ReactNode } from 'react';
import { BaseFormItemOptionType } from '@/component/pro-component/ProField/types';
import { FormItemLabelProps } from 'antd/es/form/FormItemLabel';

export type BaseProFieldType<T = any> = {
	valueType?: string; // ProField 类型，如 'text', 'select', 'dateRange'
	valueEnum?: ReadonlyEnumMap<number>;
	placeholder?: string; // 输入提示
	fieldProps?: Record<string, any>; // 额外字段属性
	formItemProps?: FormItemLabelProps; // 额外 Form.Item 属性
	renderFormItem?: (t: any, r: T, opt: BaseFormItemOptionType) => ReactNode;
	render?: (t: any, r: T, idx: number, opt: BaseFormItemOptionType) => ReactNode;
	readonly?: boolean;
};

export type BaseFormProFieldType<T = any> = BaseProFieldType<T> & {
	name?: Key | Key[]; // 字段名
	label?: ReactNode; // 显示的标签
	hidden?: boolean; // 是否隐藏
};

export type BaseTableProFieldType<T = any> = BaseProFieldType<T> & {
	dataIndex?: Key | Key[]; // 字段名
	title?: ReactNode; // 显示的标签
};

export type ElementOf<T> = T extends (infer U)[] ? U : never;
