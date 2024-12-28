import { BaseFormProFieldFuncType } from '@/component/pro-component/types';
import { DependencyList } from 'react';

export type ProFormItemsFieldType<T = any> = BaseFormProFieldFuncType<T>;

export type ProFormItemsProps<T = any> = {
	fields: BaseFormProFieldFuncType<T>[];
	editable?: boolean;
	fieldsDeps?: DependencyList;
};
