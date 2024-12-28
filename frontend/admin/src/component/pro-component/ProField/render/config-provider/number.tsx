import React from 'react';

import { InputNumber } from 'antd';

import {
	getDefaultPlaceHolder,
	PlaceHolderType,
} from '@/component/pro-component/ProField/render/formatRenderUtil';
import { ElementOf } from '@/component/pro-component/types';
import { isNumber } from 'lodash';

export const DefaultNumberValueTypeEnum = {
	Money: 'money',
	Percentage: 'percentage',
	Integer: 'integer',
	PositiveInteger: 'positive-integer',
};

declare const _valueType: ['money', 'percentage', 'integer', 'positive-integer'];
export type DefaultNumberValueType = ElementOf<typeof _valueType>;

function formatNumberToThousands(value: number | string): string {
	if (value == null || value === '') return '';

	// 确保输入为字符串，兼容数字或字符串输入
	const [integerPart, decimalPart] = value.toString().split('.');

	// 使用正则为整数部分添加千分位
	const formattedInteger = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',');

	// 如果有小数部分，拼接回去
	return decimalPart !== undefined ? `${formattedInteger}.${decimalPart}` : formattedInteger;
}

const numberPrecision2Config = {
	precision: 2,
	formatter: e => (e ? e.toString() : ''),
	parser: e => e!.replace(/￥\s?|(,*)/g, ''),
};
const defaultNumberValueTypeMap = {
	[DefaultNumberValueTypeEnum.Money]: {
		render: (t: number) => {
			if (!isNumber(t)) {
				return undefined;
			}
			const str = `￥${formatNumberToThousands(t.toFixed(2))}`;
			return <span className={t < 0 ? 'text-red' : undefined}>{str}</span>;
		},
		renderFormItem: (_t: any, _r: any, opts: any) => {
			return (
				<InputNumber
					{...numberPrecision2Config}
					placeholder={getDefaultPlaceHolder(opts.field, PlaceHolderType.Input)}
				/>
			);
		},
	},
	[DefaultNumberValueTypeEnum.Percentage]: {
		render: (t: number) => {
			return isNumber(t) ? `${formatNumberToThousands(t)}%` : t;
		},
		renderFormItem: (_t: any, _r: any, opts: any) => {
			return (
				<InputNumber
					{...numberPrecision2Config}
					step={1}
					min={0}
					max={100}
					placeholder={getDefaultPlaceHolder(opts.field, PlaceHolderType.Input)}
				/>
			);
		},
	},
	[DefaultNumberValueTypeEnum.Integer]: {
		render: (t: number) => {
			return isNumber(t) ? formatNumberToThousands(t) : t;
		},
		renderFormItem: (_t: any, _r: any, opts: any) => {
			return (
				<InputNumber
					precision={0}
					placeholder={getDefaultPlaceHolder(opts.field, PlaceHolderType.Input)}
				/>
			);
		},
	},
	[DefaultNumberValueTypeEnum.PositiveInteger]: {
		render: (t: number) => {
			return isNumber(t) ? formatNumberToThousands(t) : t;
		},
		renderFormItem: (_t: any, _r: any, opts: any) => {
			return (
				<InputNumber
					precision={0}
					min={1}
					placeholder={getDefaultPlaceHolder(opts.field, PlaceHolderType.Input)}
				/>
			);
		},
	},
};
export default defaultNumberValueTypeMap;
