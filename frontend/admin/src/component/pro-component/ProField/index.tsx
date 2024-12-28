import React from 'react';
import { BaseFormProFieldType } from '@/component/pro-component/types';
import { get, isNumber } from 'lodash';
import { Form } from 'antd';
import { getKeyList } from '@/component/pro-component/utils/not-export';

type ProFieldProps<T = any> = {
	readonly: boolean;
	value: any;
	name: any;
	index?: number;
} & BaseFormProFieldType<T>;
const ProField: React.FC<ProFieldProps> = props => {
	const {
		label,
		hidden,
		fieldProps,
		readonly,
		name,
		index,
		value,
		formItemProps = {},
		...rest
	} = props;
	const form = Form.useFormInstance();

	const formName = getKeyList(name);

	const formatRender = () => {
		const r = form.getFieldsValue(true);
		const t = get(r, name);
		const idx = isNumber(index) ? index : -1;
		return props?.render?.(t, r, idx, {
			form,
			index: idx,
			formName,
			field: props,
		});
	};
	if (readonly) {
		return (
			<Form.Item name={formName as any} label={label} hidden={hidden} {...formItemProps}>
				{formatRender()}
			</Form.Item>
		);
	}
	if (!readonly) {
		const idx = isNumber(index) ? index : -1;

		const _curRender = () => {
			return (
				<>
					{props?.renderFormItem?.(value, props, {
						form,
						index: idx,
						formName,
						field: props,
					})}
				</>
			);
		};
		return (
			<Form.Item
				name={formName as any}
				hidden={hidden}
				label={label}
				{...formItemProps}
				getValueProps={e => {
					const merge = formItemProps?.getValueProps?.(e) || {};
					return {
						value: e,
						...fieldProps,
						...merge,
					};
				}}>
				{_curRender()}
			</Form.Item>
		);
	}
	return null;
};
export default ProField;
