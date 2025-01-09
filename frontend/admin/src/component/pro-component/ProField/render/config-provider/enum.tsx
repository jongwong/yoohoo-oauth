/**
 * 通用输入组件渲染函数
 * */

import React from 'react';
import { getDefaultPlaceHolder, PlaceHolderType } from '../../render/formatRenderUtil';
import { ElementOf } from '../../../types';
import { Select, Tag } from 'antd';
import { findValueEnum, toValEnumList } from '../../../utils/not-export';
import { StatusText } from '@yoo/component';

export const DefaultEnumValueTypeEnum = {
	EnumStatusTag: 'enum-status-tag',
	EnumStatusDot: 'enum-status-dot',
	EnumStatusText: 'enum-status-text',
};

declare const _valueType: ['enum-status-tag', 'enum-status-dot', 'enum-status-text'];
export type DefaultEnumValueType = ElementOf<typeof _valueType>;

const selectFormItem = (_t: any, _r: any, opts: any) => {
	const { column } = opts;
	const _options: any = toValEnumList(column?.valueEnum).map((it: any) => ({
		value: it.value,
		label: it.text,
	}));
	if (_options.length < 0) {
		console.warn(
			`when valueType is 'enum-status',must offer effective valueEnum,in label: ${
				column?.label || column?.title
			}`
		);
	}
	return (
		<Select
			allowClear
			options={_options}
			placeholder={getDefaultPlaceHolder(opts.column, PlaceHolderType.Select)}
		/>
	);
};
const defaultEnumValueTypeMap = {
	[DefaultEnumValueTypeEnum.EnumStatusTag]: {
		render: (val: any, r: any, _idx: number, { field }) => {
			const find = findValueEnum(field?.valueEnum || {}, val);
			return find ? <Tag color={find?.color || find?.status}>{find?.text}</Tag> : undefined;
		},
		renderFormItem: selectFormItem,
	},
	[DefaultEnumValueTypeEnum.EnumStatusDot]: {
		render: (val: any, r: any, _idx: number, { field }) => {
			const find = findValueEnum(field?.valueEnum || {}, val);
			return find ? <StatusText {...find} /> : undefined;
		},
		renderFormItem: selectFormItem,
	},

	[DefaultEnumValueTypeEnum.EnumStatusText]: {
		render: (val: any, r: any, _idx: number, { field }) => {
			const find = findValueEnum(field?.valueEnum || {}, val);
			return find ? <StatusText {...find} dot={false} /> : undefined;
		},
		renderFormItem: selectFormItem,
	},
};
export default defaultEnumValueTypeMap;
