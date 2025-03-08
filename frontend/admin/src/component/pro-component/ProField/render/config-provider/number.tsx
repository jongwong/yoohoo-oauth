import React from 'react';

import { InputNumber } from 'antd';

import { getDefaultPlaceHolder, PlaceHolderType } from '../../render/formatRenderUtil';
import { ElementOf } from '../../../types';
import { isNumber } from 'lodash';
import { ProxyWrapped } from '@yoo/component';
import { divide, multiply } from '@/utils/number';

export const DefaultNumberValueTypeEnum = {
	Money: 'money',
	Percentage: 'percentage',
	Integer: 'integer',
	Number: 'number',
	PositiveInteger: 'positive-integer',
};

declare const _valueType: ['money', 'percentage', 'integer', 'positive-integer', 'number'];
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
			const num = divide(t, 100);
			const str = isNumber(num) ? `￥${formatNumberToThousands(num.toFixed(2))}` : num;
			return <span className={t < 0 ? 'text-red' : undefined}>{str}</span>;
		},
		renderFormItem: (_t: any, _r: any, opts: any) => {
			return (
				<ProxyWrapped>
					{cfg => (
						<InputNumber
							{...numberPrecision2Config}
							placeholder={getDefaultPlaceHolder(opts.field, PlaceHolderType.Input)}
							value={isNumber(cfg.value) ? divide(cfg.value, 100) : undefined}
							onChange={(e: any) => {
								// 变成整数onchange
								cfg.onChange?.(isNumber(e) ? Math.floor(multiply(e, 100)) : e);
							}}
						/>
					)}
				</ProxyWrapped>
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
					className={'w-1-1'}
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
					className={'w-1-1'}
					precision={0}
					min={1}
					placeholder={getDefaultPlaceHolder(opts.field, PlaceHolderType.Input)}
				/>
			);
		},
	},
	[DefaultNumberValueTypeEnum.Number]: {
		render: (t: number) => {
			return isNumber(t) ? formatNumberToThousands(t) : t;
		},
		renderFormItem: (_t: any, _r: any, opts: any) => {
			return (
				<InputNumber
					className={'w-1-1'}
					placeholder={getDefaultPlaceHolder(opts.field, PlaceHolderType.Input)}
				/>
			);
		},
	},
};
export default defaultNumberValueTypeMap;
