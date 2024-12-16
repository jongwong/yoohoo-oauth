import { has, isNumber, omit } from 'lodash';
import { Input, Select } from 'antd';
import React from 'react';
import { BaseProFieldType } from '@/component/pro-component/types';

export const formatProField = (field: BaseProFieldType, isTable?: boolean) => {
	const _field: any = field;
	const fieldProps = {
		placeholder: field?.placeholder,
		...field.fieldProps,
	};
	let ob: any = {
		fieldProps,
		valueType: field.valueType,
		placeholder: field?.placeholder,
		renderFormItem: (t: any, fieldProps: any) => {
			if (has(field, 'valueEnum')) {
				return (
					<Select
						placeholder={`请选择${field.title || field.label}`}
						options={field?.valueEnum?.options() || []}
						{...fieldProps}
					/>
				);
			}

			return <Input placeholder={`请输入${field.title || field.label}`} {...fieldProps} />;
		},
		render: (t: any, r: any, idx: number) => {
			if (has(field, 'valueEum')) {
				return field?.valueEnum?.get(t)?.text || '--';
			}
			return isNumber(t) || t ? t : '--';
		},
	};
	if (has(field, 'valueType') && !field?.renderFormItem) {
		ob = omit(field, ['renderFormItem']);
	}
	if (has(field, 'valueType') && !has(field, 'render')) {
		ob = omit(field, ['render']);
	}
	if (isTable) {
		return {
			...ob,
			title: _field?.title,
			dataIndex: _field?.dataIndex,
		};
	} else {
		return {
			...ob,
			name: _field?.name,
			label: _field?.label,
		};
	}
};
