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

	const defaultOb = {
		renderFormItem: (t: any, fieldProps: any) => {
			if (has(field, 'valueEnum')) {
				return (
					<Select
						allowClear
						placeholder={`请选择${field.title || field.label}`}
						options={field?.valueEnum?.options() || []}
						{...fieldProps}
					/>
				);
			}

			return (
				<Input allowClear placeholder={`请输入${field.title || field.label}`} {...fieldProps} />
			);
		},
		render: (t: any, r: any, idx: number) => {
			if (has(field, 'valueEum')) {
				return field?.valueEnum?.get(t)?.text || '--';
			}
			return isNumber(t) || t ? t : '--';
		},
	};
	let ob: any = {
		fieldProps,
		valueType: field.valueType,
		placeholder: field?.placeholder,
	};
	if (has(field, 'valueType') && !field?.renderFormItem) {
		ob = omit(field, ['renderFormItem']);
	}
	if (has(field, 'valueType') && !has(field, 'render')) {
		ob = omit(field, ['render']);
	}
	if (isTable) {
		return {
			...defaultOb,
			...ob,
			...omit(field, ['name', 'name']),
			title: _field?.title,
			dataIndex: _field?.dataIndex,
		};
	} else {
		return {
			...defaultOb,
			...ob,
			...omit(field, ['title', 'dataIndex']),
			name: _field?.name,
			label: _field?.label,
		};
	}
};
