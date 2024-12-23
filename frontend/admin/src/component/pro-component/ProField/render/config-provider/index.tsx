import type { Consumer, Provider } from 'react';
import React from 'react';

import type { DefaultInputValueType } from './input';
import defaultInputValueTypeMap, { DefaultInputValueTypeEnum } from './input';

import type { DefaultTimeValueType } from './time';
import defaultTimeValueTypeMap, { DefaultTimeValueTypeEnum } from './time';

import { BaseFormItemOptionType } from '@/component/pro-component/ProField/types';

export type UppcaseKey<Str extends string> =
	Str extends `${infer First}-${infer Second}${infer Other}`
		? `${First}${Uppercase<Second>}${UppcaseKey<Other>}`
		: Str;
export type UppcaseFirstKey<Str extends string> = Str extends `${infer First}${infer Other}`
	? `${Uppercase<First>}${Other}`
	: Str;

export type DefaultValueTypeEnumAll = {
	[K in DefaultValueType as UppcaseFirstKey<UppcaseKey<K>>]: K;
};

export type DefaultValueType = DefaultInputValueType | DefaultTimeValueType;

export const EDefaultValueType: DefaultValueTypeEnumAll = {
	...DefaultInputValueTypeEnum,
	...DefaultTimeValueTypeEnum,
} as any;

export interface CommonProConfigType {
	valueTypeMap: Record<
		DefaultValueType | string,
		{
			/** 只在表格的时候生效 */
			align?: 'left' | 'right';
			render?: (
				t: any,
				r: any,
				idx: undefined | number,
				opts: Pick<BaseFormItemOptionType<any>, 'field'>
			) => React.ReactNode;
			/**
			 * @description 编辑态渲染方法
			 */
			renderFormItem?: (
				val: any,
				r: any,
				opts: Pick<BaseFormItemOptionType<any>, 'field'>
			) => React.ReactNode;
		}
	>;
}

export const defaultValueTypeMap: CommonProConfigType['valueTypeMap'] = {
	...defaultInputValueTypeMap,
	...defaultTimeValueTypeMap,
};

const CommonProConfigContext = React.createContext<CommonProConfigType>({
	valueTypeMap: defaultValueTypeMap,
});

export const CommonProConfigConsumer: Consumer<CommonProConfigType> =
	CommonProConfigContext.Consumer;
export const CommonProConfigProvider: Provider<CommonProConfigType> =
	CommonProConfigContext.Provider;
export default CommonProConfigContext;
