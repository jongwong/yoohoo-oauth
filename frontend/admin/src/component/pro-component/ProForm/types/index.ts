import { BaseFormProFieldType } from '@/component/pro-component/types';
import { DependencyList } from 'react';

export type ProFormItemsFieldType<T = any> = BaseFormProFieldType<T>;

export type ProFormItemsProps<T = any> = {
	fields: ProFormItemsFieldType<T>[];
	readonly?: boolean;
	fieldsDeps?: DependencyList;
};
