import React from 'react';
import { BaseProFieldType } from '@/component/pro-component/types';
import { get, isNumber } from 'lodash';
import { Form } from 'antd';
import { getKeyList } from '@/component/pro-component/utils/not-export';

type ProFieldProps = {
	mode: 'edit' | 'readonly';
	value: any;
	name: any;
	index?: number;
} & BaseProFieldType;
const ProField: React.FC<ProFieldProps> = props => {
	const { mode, label, name, index, value, formItemProps = {}, ...rest } = props;
	const form = Form.useFormInstance();

	const readonly = mode === 'readonly';
	const formName = getKeyList(name);
	if (readonly) {
		const t = get(value, name);
		const idx = isNumber(index) ? index : -1;
		return props?.render?.(t, value, idx, {
			form,
			index: idx,
			formName,
			field: props,
		});
	}
	if (mode === 'edit') {
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
			<Form.Item name={formName as any} label={label} {...formItemProps}>
				{_curRender()}
			</Form.Item>
		);
	}
};
export default ProField;
