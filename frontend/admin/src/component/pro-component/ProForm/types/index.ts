import { BaseFormProFieldFuncType } from '../../types';

export type ProFormItemsFieldType<T = any> = BaseFormProFieldFuncType<T>;

export type ProFormItemsProps<T = any> = {
	fields: BaseFormProFieldFuncType<T>[];
	editable?: boolean;
};
