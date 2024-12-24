/**
 * 通用时间处理渲染函数
 * */
import React from 'react';
import dayjs from 'dayjs';
import { isNumber } from 'lodash';

import { DatePicker, DatePickerProps } from 'antd';
import {
	getDefaultPlaceHolder,
	PlaceHolderType,
} from '@/component/pro-component/ProField/render/formatRenderUtil';
import ProxyWrapped from '@/component/pro-component/ProxyWrapped';
import { CommonProConfigType } from '@/component/pro-component/ProField/render';
import { EMPTY_TEXT } from '@/component/pro-component/constant';
import { ElementOf } from '@/component/pro-component/types';

const { RangePicker } = DatePicker;

export enum DefaultTimeValueTypeEnum {
	DateTime = 'date-time',
	DateTimeMinutes = 'date-time-minutes',
	Date = 'date',
	// eslint-disable-next-line @typescript-eslint/no-shadow
	RangePicker = 'range-picker',
}

declare const _valueType: [
	'date-time',
	'date-time-minutes',
	'date',
	'range-picker',
	'range-picker-object'
];
export type DefaultTimeValueType = ElementOf<typeof _valueType>;

const renderTime = (
	extraProps: Partial<DatePickerProps> | any,
	opts: any,
	type?: 'end' | 'start' | 'none'
): any => {
	const Component: any = DatePicker;
	const formatFn: any = (e: any) => {
		if (isNumber(e)) {
			return dayjs(e);
		}
		if (e?.valueOf) {
			return dayjs(e?.valueOf());
		}
		return undefined;
	};

	return (
		<ProxyWrapped>
			{op => (
				<Component
					placeholder={getDefaultPlaceHolder(opts.field, PlaceHolderType.Input)}
					{...extraProps}
					{...op}
					value={(op?.value ? formatFn(op?.value) : op?.value) as any}
					onChange={(e: any) => {
						let _val = e ? formatFn(e) : undefined;

						if (type === 'start') {
							_val = _val?.startOf('date')?.valueOf();
						} else if (type === 'end') {
							_val = _val?.endOf('date')?.valueOf();
						} else {
							_val = _val?.valueOf();
						}
						op?.onChange?.(_val);
					}}
				/>
			)}
		</ProxyWrapped>
	);
};
/**
 * @ts-ignore
 */
const defaultTimeValueTypeMap: CommonProConfigType['valueTypeMap'] = {
	[DefaultTimeValueTypeEnum.DateTime]: {
		render: (val: any) => (val ? dayjs(val).format('YYYY-MM-DD HH:mm:ss') : EMPTY_TEXT),
		renderFormItem: (v, r, opts: Record<string, any>) => renderTime({ showTime: 'HH:mm:ss' }, opts),
	},
	[DefaultTimeValueTypeEnum.DateTimeMinutes]: {
		render: (val: any) => val && dayjs(val).format('YYYY-MM-DD HH:mm'),
		renderFormItem: (v, r, opts: Record<string, any>) => renderTime({ showTime: 'HH:mm' }, opts),
	},
	[DefaultTimeValueTypeEnum.Date]: {
		render: (val: any) => val && dayjs(val).format('YYYY-MM-DD'),
		renderFormItem: (v, r, opts: Record<string, any>) => renderTime({}, opts, 'start'),
	},
	[DefaultTimeValueTypeEnum.RangePicker]: {
		render: val => {
			let newVal = val;
			if (Array.isArray(val)) {
				// eslint-disable-next-line array-callback-return
				newVal = val.map(it => {
					if (it) {
						return dayjs(it)?.format('YYYY-MM-DD');
					}
					return it;
				});
				return newVal?.join(' ~ ');
			}
			return EMPTY_TEXT;
		},
		renderFormItem: (t, r: Record<string, any>, opt) => (
			<ProxyWrapped>
				{op => {
					const val = Array.isArray(op?.value)
						? op?.value.map(it => (it ? dayjs(it) : it))
						: undefined;
					return (
						<RangePicker
							{...op}
							value={val as any}
							onChange={e => {
								const newVal = Array.isArray(e)
									? e.map((it, idx) => {
											if (!it) {
												return it;
											}
											if (idx === 0) {
												return it?.startOf('date').valueOf();
											}
											if (idx === e?.length - 1) {
												return it?.endOf('date').valueOf();
											}
											return it?.valueOf();
									  })
									: undefined;
								op?.onChange?.(newVal);
							}}
						/>
					);
				}}
			</ProxyWrapped>
		),
	},
};
export default defaultTimeValueTypeMap;
