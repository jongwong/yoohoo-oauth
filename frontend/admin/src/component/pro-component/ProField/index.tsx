import React from 'react';

import { Form, FormInstance } from 'antd';
import { get, isNumber, take } from 'lodash';

import { formatRenderFun } from '../ProField/render/formatRenderUtil';
import { BaseFormProFieldType } from '../types';
import { getKeyList } from '../utils/not-export';

type ProFieldProps<T = any> = {
	editable: boolean;
	value: any;
	name: any;
	index?: number;
} & BaseFormProFieldType<T>;
const InerProField: React.FC<
	ProFieldProps & {
		_allEditable?: boolean;
		getArgs?: (e: boolean) => any;
		getValue?: () => any;
	}
> = props => {
	const {
		label,
		visible = true,
		fieldProps,
		editable = false,
		name,
		index,
		getArgs,
		_allEditable,
		formItemProps = {},
		getValue,
		...rest
	} = props;
	const form = Form.useFormInstance();
	const fieldName = getKeyList(name);

	const formatRender = () => {
		const idx = isNumber(index) ? index : -1;

		if (getArgs) {
			const [t, r, idx, cfg] = getArgs(false);

			return props?.render?.(t, r, idx, {
				form,
				index: idx,
				fieldName,
				field: props,
				...cfg,
			});
		}
		const r = form.getFieldsValue(true);
		const t = get(r, name);
		return props?.render?.(t, r, idx, {
			form,
			index: idx,
			fieldName,
			field: props,
		});
	};

	const _curRender = () => {
		if (label === '开始配送时间') {
			console.log('=====editable=====', editable);
		}
		let idx = -1;
		let r = form.getFieldsValue(true);

		let val = get(r, name);
		let cfg = {};
		if (getArgs) {
			const [t, _record, _idx, _cfg] = getArgs(false);

			const pre = take(_cfg?.fieldName, _cfg?.fieldName?.length - 1);
			r = form.getFieldValue(pre);

			val = form.getFieldValue(_cfg?.fieldName);
			idx = _idx;

			cfg = _cfg;
		}
		return props?.renderFormItem?.(val, r, {
			form,
			index: idx,
			fieldName,
			field: props,
			...cfg,
		});
	};

	if (editable) {
		return (
			<Form.Item
				name={fieldName as any}
				hidden={!visible}
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
	return (
		<Form.Item
			shouldUpdate={!editable ? true : undefined}
			name={editable || _allEditable ? fieldName : undefined}
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
			getArgs?: (e: boolean) => any;
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
		return (
			<InerProField
				{...formatField}
				editable={editable && _allEditable}
				_allEditable={_allEditable}
			/>
		);
	};
	if (_props?.fieldFunc) {
		return (
			<Form.Item noStyle shouldUpdate={true}>
				{form => {
					const r = _props?.getRecord ? _props?.getRecord?.() : form.getFieldsValue(true);
					const _field = (props as any).fieldFunc(r, form);
					return render({ ..._field, getRecord: _props?.r });
				}}
			</Form.Item>
		);
	}

	return render(props);
};
export default ProField;
