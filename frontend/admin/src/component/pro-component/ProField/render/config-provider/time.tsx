/**
 * 通用时间处理渲染函数
 * */
import React from 'react';

import { DatePicker, DatePickerProps } from 'antd';
import dayjs from 'dayjs';
import { get, isNil, isNumber } from 'lodash';

import { getErrorMessageName } from '../../../utils/not-export';

import { EMPTY_TEXT } from '../../../constant';
import { CommonProConfigType } from '../../../ProField/render';
import ProxyWrapped from '../../../ProxyWrapped';
import { ElementOf } from '../../../types';
import { getDefaultPlaceHolder, PlaceHolderType } from '../../render/formatRenderUtil';

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
		render: (t, r, _idx, { field }) => {
			const [startDataIndex, endDataIndex] = field?.extraFieldNames || [];
			if (isNil(startDataIndex) || isNil(endDataIndex)) {
				const name = getErrorMessageName(field);
				throw Error(
					`Error in  ${name}.  missing attributes extraFieldNames, such as {extraFieldNames:[startDataIndex,endDataIndex]} `
				);
			}

			if (get(r, startDataIndex) && get(r, endDataIndex)) {
				// eslint-disable-next-line array-callback-return
				const newVal = [get(r, startDataIndex), get(r, endDataIndex)].map(it => {
					// @ts-ignore
					if (it) {
						return dayjs(it)?.format('YYYY-MM-DD');
					}
					return it;
				});
				return newVal?.join(' ~ ');
			}
			return EMPTY_TEXT;
		},
		renderFormItem: (
			t,
			r: Record<string, any>,
			{ field, form, fieldName = [], index }: Record<string, any>
		) => (
			<ProxyWrapped>
				{(op: any = {}) => {
					const [startDataIndex, endDataIndex] = field?.extraFieldNames || [];
					if (isNil(startDataIndex) || isNil(endDataIndex)) {
						const name = getErrorMessageName(field);
						throw Error(
							`Error in  ${name}.  missing attributes extraFieldNames, such as {extraFieldNames:[startDataIndex,endDataIndex]} `
						);
					}

					const start = [...fieldName];
					const end = [...fieldName];
					start[start.length - 1] = startDataIndex;
					end[end.length - 1] = endDataIndex;

					const val = [form.getFieldValue(start), form.getFieldValue(end)].map(it =>
						it ? dayjs(it) : undefined
					);
					return (
						// @ts-ignore
						<RangePicker
							{...op}
							value={val as any}
							onChange={e => {
								const val1 = e?.[0]?.startOf('date')?.valueOf();
								const val2 = e?.[1]?.endOf('date')?.valueOf();
								const ob = {
									[startDataIndex]: val1,
									[endDataIndex]: val2,
								};
								if (field?._useType === 'useFormatColumns') {
									op?.onChange?.(ob);
								} else {
									form?.setFields?.([
										{
											name: start,
											value: val1,
										},
										{
											name: end,
											value: val2,
										},
									]);
									form.validateFields([start, end]);
								}
							}}
						/>
					);
				}}
			</ProxyWrapped>
		),
	},
};
export default defaultTimeValueTypeMap;
