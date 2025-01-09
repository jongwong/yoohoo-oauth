import React from 'react';
import { Input, Select } from 'antd';
import { get, has, isArray, isNumber, isObject, omit, pick } from 'lodash';
import { CommonProConfigType, defaultValueTypeMap } from './config-provider';
import {
	findValueEnum,
	getJsonStringByKey,
	isDevelopment,
	throwEmitError,
	validateElement,
} from '../../utils/not-export';
import { EMPTY_TEXT } from '../../constant';
import { DefaultErrorContent } from '../../ErrorBound';

export const getLabelOrTitle = (op = {}) => {
	let val: any = '';
	if (has(op, ['title'])) {
		val = get(op, ['title']);
	} else if (has(op, ['label'])) {
		val = get(op, ['label']);
	}

	if (isNumber(val) || typeof val === 'string') {
		return val;
	}
	return '';
};

export enum PlaceHolderType {
	// eslint-disable-next-line @typescript-eslint/no-shadow
	Input = 10,
	// eslint-disable-next-line @typescript-eslint/no-shadow
	Select = 20,
}

export const getDefaultPlaceHolder = (op: any = {}, type: PlaceHolderType) => {
	let str = '请输入';
	if (type === PlaceHolderType.Select) {
		str = '请选择';
	}
	if (has(op, 'title') && !has(op, 'label')) {
		return str;
	}
	return str + (getLabelOrTitle(op) || '');
};

export function formatRenderFun(
	it: Record<string, any>,
	_valueTypeMap?: CommonProConfigType['valueTypeMap'] | any,
	extraKeys?: string[]
) {
	const valueTypeMap = { ...defaultValueTypeMap, ..._valueTypeMap };

	function getRenderFunc(type: 'render' | 'renderFormItem', enumValue?: any) {
		if (has(it, type)) {
			return it[type];
		}

		if (has(it, 'valueType')) {
			const valueTypeOb = get(valueTypeMap, it.valueType);
			return get(valueTypeOb, type);
		}

		if (has(it, 'valueEnum') && (it?.dataIndex || it?.name)) {
			if (isArray(it.valueEnum && isDevelopment())) {
				formatError(Error('valueEnum not array type'), it);
			}

			if (type === 'render') {
				return () => {
					if (isArray(enumValue) && enumValue.length) {
						return enumValue
							?.map(valIt => findValueEnum(it?.valueEnum, valIt))
							.filter(childIt => childIt)
							.join('、');
					}
					const find = findValueEnum(it?.valueEnum, enumValue);
					return find?.text;
				};
			}
			if (type === 'renderFormItem') {
				return (_op: any, _ins: any) => {
					const opts: any = getOptionFormValueEnum(it?.valueEnum);
					return (
						<Select
							allowClear
							{...{
								options: opts,
								placeholder: getDefaultPlaceHolder(it, PlaceHolderType.Select),
							}}
						/>
					);
				};
			}
		}
	}

	let mergeOb = {};
	if (has(it, 'valueType')) {
		const valueTypeOb = get(valueTypeMap, it.valueType) || {};
		mergeOb = {
			...valueTypeOb,
			formItemProps: { ...(valueTypeOb?.formItemProps || {}), ...(it?.formItemProps || {}) },
		};
	}

	const ob = {
		...mergeOb,
		render: (formatVal: any, ...args: any[]) => {
			const fn = getRenderFunc('render', formatVal);
			const val = typeof fn === 'function' ? fn(formatVal, ...args) : formatVal;

			checkObjectError(val, it);

			return !(val === null || val === undefined || val === '') ? val : EMPTY_TEXT;
		},
		renderFormItem: (...args: any[]) => {
			const fn = getRenderFunc('renderFormItem');
			const labelStr = getDefaultPlaceHolder(it, PlaceHolderType.Input);
			const newFieldProps = { placeholder: labelStr };
			if (typeof fn !== 'function') {
				return <Input {...newFieldProps} allowClear />;
			}

			const val = typeof fn === 'function' ? fn(...args) : null;
			checkObjectError(val, it);
			return val;
		},
	};

	const renderFormItem = ob?.renderFormItem;
	const render = ob?.render;

	const formatRender = (...args: any[]) => {
		// eslint-disable-next-line prefer-const
		let [t, r, idx, _opt = {}] = args || [];
		_opt = {
			column: it,
			..._opt,
		};
		try {
			// 检测就valueType 并本地抛出错误
			checkValueTypeError(it as any);
			return render(t, r, idx, _opt);
		} catch (err) {
			formatError(err, it);
			return <DefaultErrorContent />;
		}
	};

	const formatRenderFormItem = (...args: any[]) => {
		// eslint-disable-next-line prefer-const
		let [t, r, _opt] = args || [];
		_opt = {
			column: it,
			..._opt,
		};
		try {
			// 检测就valueType 并本地抛出错误
			checkValueTypeError(it as any);
			return renderFormItem(t, r, _opt);
		} catch (err) {
			formatError(err, it);
			return <DefaultErrorContent />;
		}
	};
	ob.render = formatRender;
	ob.renderFormItem = formatRenderFormItem;
	return pick(ob, ['render', 'renderFormItem', ...(extraKeys || [])]);
}

const formatError = (err: any, _ob: any) => {
	const str1 = getJsonStringByKey(_ob, ['label', 'title', 'dataIndex', 'name', 'key']);
	const str = str1 ? `{${str1},...}` : '';
	const message = err?.message;
	const formatStr = str ? `${message}, at ${str}` : `${message}`;
	const newError: any = err;
	newError.message = formatStr;
	throwEmitError(newError);
};

const checkObjectError = (val: any, it: any) => {
	if (isObject(val) && !validateElement(val)) {
		const err = new Error(
			'[Error] Objects are not valid as a React child (found: object with keys {content, key, duration}). If you meant to render a collection of children, use an array instead'
		);
		// 非生产环境异步抛出错误
		if (isDevelopment()) {
			throw err;
		} else {
			formatError(err, it);
		}
	}
};

const checkValueTypeError = (it: { valueType: string }) => {
	if (!isDevelopment()) {
		return;
	}
	const _valueType: string = it?.valueType;
};

const getOptionFormValueEnum = (map: any) => {
	const list: any[] = map instanceof Map ? Array.from(map.values()) : Object.values(map);

	return list.map(item => ({
		...omit(item, ['text']),
		label: item.text,
	}));
};
