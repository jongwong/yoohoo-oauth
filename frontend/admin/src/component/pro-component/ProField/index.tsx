import React from 'react';
import { BaseFormProFieldType } from '../types';
import { get, isNil, isNumber } from 'lodash';
import { Form, FormInstance } from 'antd';
import { getKeyList } from '../utils/not-export';
import { formatRenderFun } from '../ProField/render/formatRenderUtil';

type ProFieldProps<T = any> = {
	editable: boolean;
	value: any;
	name: any;
	index?: number;
} & BaseFormProFieldType<T>;
const InerProField: React.FC<ProFieldProps> = props => {
	const {
		label,
		visible = true,
		fieldProps,
		editable = false,
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

	if (editable) {
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
				hidden={!visible}
				label={isNil(props?.label) ? (props as any)?.title : label}
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
	return (
		<Form.Item
			shouldUpdate={!editable ? true : undefined}
			name={editable ? formName : undefined}
			label={label}
			hidden={!visible}
			{...formItemProps}>
			{formatRender()}
		</Form.Item>
	);
};

const ProField: React.FC<
	| ProFieldProps
	| {
			fieldFunc: (r: any, form: FormInstance) => ProFieldProps;
			getRecord?: () => any;
			allEditable?: boolean;
	  }
> = props => {
	const _props = props as any;
	const _allEditable = !!_props?.allEditable;
	const getTransformField = (field: any) => {
		return {
			...field,
			...formatRenderFun(field, {}),
		};
	};

	const render = (field: any) => {
		const formatField = getTransformField(field);
		const { editable = true } = formatField;
		return <InerProField {...formatField} editable={editable && _allEditable} />;
	};
	if (_props?.fieldFunc) {
		return (
			<Form.Item noStyle shouldUpdate={true}>
				{form => {
					const r = _props?.getRecord?.() || form.getFieldsValue(true);
					const _field = (props as any).fieldFunc(r, form);
					return render(_field);
				}}
			</Form.Item>
		);
	}

	return render(props);
};
export default ProField;
