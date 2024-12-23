import { has, isArray, isFunction, isMap, isNil, isString } from 'lodash';
import { ValueEnumType } from '@/utils/enum';
import React, { Key } from 'react';

export const findValueEnum = (valueEnum: ValueEnumType<any>, enumValue: any) => {
	if (isMap(valueEnum)) {
		return valueEnum?.get(enumValue);
	}
	return (valueEnum as any)[enumValue];
};
export const isDevelopment = () => {
	try {
		if ((window as any)?.process && (window as any)?.process.env?.NODE_ENV === 'development') {
			return true;
		}
	} catch (e) {
		return false;
	}
	return false;
};
export const checkFormDataSourceConflict = (props: Record<string, any>, isState?: boolean) => {
	if (!isDevelopment()) {
		return '';
	}
	// 这个会引起Uncaught Error: Rendered fewer hooks than expected
	if (isState && props?.editable && !has(props, 'dataSource') && !has(props, 'onChange')) {
		return `warning:\`dataSource | onChange\` required. don't worry,only throw  error when development environment ${JSON.stringify(
			props
		)}`;
	}

	if (has(props, 'form') && (has(props, 'dataSource') || has(props, 'onChange'))) {
		return `warning:\`dataSource | onChange\` and \`form\` shouldn't be used together. don't worry,only throw  error when development environment ${JSON.stringify(
			props
		)}`;
	}

	if (isFunction(props?.columns)) {
		return `warning:\`columns\` not support type is 'function',please use getColumnProps instead . don't worry,only throw  error when development environment ${JSON.stringify(
			props
		)}`;
	}
	return '';
};

export const checkDeprecated = (props: Record<string, any>, key: string) => {
	if (!isDevelopment()) {
		return false;
	}
	if (has(props, key)) {
		const str = JSON.stringify(props || {});
		throw Error(
			`${key} has deprecated， .don't worry,only throw  error when development environment. ${str} `
		);
	}
	return false;
};
export const getJsonStringByKey = (ob: any, keys: string[]) => {
	const list: string[] = [];
	keys.forEach(it => {
		const item = ob[it];
		if (item !== null && item !== undefined) {
			list.push([it, `"${String(item)}"`].join(': '));
		}
	});
	return `${list.join(',')}`;
};
export const getErrorMessageName = (ob: Record<string, any>) =>
	getJsonStringByKey(ob, ['label', 'title', 'dataIndex', 'key']);

/**
 * 抛异常
 * @param err 错误信息
 * @param column  列信息
 * @param isReportAsWarning 是否上报为 warning
 */
export const throwAndFormatColumnError = (
	err: Error | string,
	column: any,
	isReportAsWarning?: boolean
) => {
	let str1 = getJsonStringByKey(column, ['label', 'title', 'dataIndex', 'key']);
	try {
		str1 = JSON.stringify(column);
	} catch (e) {
		console.error(e);
	}
	const str = str1 ? `${str1}` : '';
	let newError;
	let formatStr;

	if (isString(err)) {
		formatStr = str ? `Warning in  ${str} ${err}` : `${err}`;
		newError = new Error(formatStr);
	} else {
		const message = err?.message;
		formatStr = str ? `Warning in  ${str} ${message}` : `${message}`;
		newError = err;
		newError.message = formatStr;
	}

	if (isReportAsWarning) {
		console.error(formatStr);
	} else {
		// eventEmitterInstance.$emit(PRO_THROW_ERROR_EMITTER_EVENT_NAME, newError);
	}
};

export const throwEmitError = (e: Error) => {
	// eventEmitterInstance.$emit(PRO_THROW_ERROR_EMITTER_EVENT_NAME, e);
};
/**
 *
 * @param val 需要检测的元素
 * @param len 检测深度,0时就不检测 默认3
 *
 * */
export const validateElement = (val: any, len = 3): boolean => {
	if (typeof val === 'number' || typeof val === 'string' || len < 0) {
		return true;
	}
	return Array.isArray(val)
		? !val.some(it => !validateElement(it, len - 1))
		: React.isValidElement(val);
};
export const getKeyList = (key: Key | Key[]): Key[] => {
	if (isNil(key) || (isArray(key) && !key?.length)) {
		return [];
	}
	if (isArray(key) && key?.length) {
		return key;
	}
	return [key] as any;
};

export const formatGetColumns = (origin: any, merge: any) => ({
	...origin,
	...merge,
});
