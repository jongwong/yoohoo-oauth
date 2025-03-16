import { BaseFormProFieldFuncType } from '../../types';
import { FormItemProps } from 'antd';

export type ProFormItemsFieldType<T = any> = BaseFormProFieldFuncType<T>;

export type ProFormItemsProps<T = any> = {
	fields: BaseFormProFieldFuncType<T>[];
	editable?: boolean;
	labelCol?: FormItemProps['labelCol'];
	wrapperCol?: FormItemProps['wrapperCol'];
};
