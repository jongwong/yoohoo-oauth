/**
 * 通用输入组件渲染函数
 * */

import React from 'react';
import { FormInstance, Input, Switch, Upload } from 'antd';
import {
	getDefaultPlaceHolder,
	PlaceHolderType,
} from '@/component/pro-component/ProField/render/formatRenderUtil';
import { EMPTY_TEXT } from '@/component/pro-component/constant';
import ProxyWrapped from '@/component/pro-component/ProxyWrapped';
import { ElementOf } from '@/component/pro-component/types';
import OssUpload from '@/component/OssUpload';

export const DefaultInputValueTypeEnum = {
	Input: 'input',
	Textarea: 'textarea',
	Switch: 'switch',
	RadioBool: 'radio-bool',
	File: 'file',
	Image: 'image',
};

declare const _valueType: ['input', 'textarea', 'switch', 'radio-bool', 'file', 'image'];
export type DefaultInputValueType = ElementOf<typeof _valueType>;

const defaultInputValueTypeMap: {} = {
	[DefaultInputValueTypeEnum.Input]: {
		render: (val: any, record: any, index: number) => val,
		renderFormItem: (t: any, r: any, opts: any, ins: FormInstance) => (
			<Input
				className="w-1-1"
				allowClear
				placeholder={getDefaultPlaceHolder(opts.field, PlaceHolderType.Input)}
			/>
		),
	},
	[DefaultInputValueTypeEnum.Textarea]: {
		render: (val: any, record: any, index: number) =>
			val ? <span className="whitespace-pre-line break-words">{val}</span> : EMPTY_TEXT,
		renderFormItem: (t: any, r: any, opts: Record<string, any>, ins: FormInstance) => (
			<Input.TextArea
				className="w-1-1"
				autoSize={{
					minRows: opts?.field?._useType === 'table' ? 2 : 4,
				}}
				placeholder={getDefaultPlaceHolder(opts?.field, PlaceHolderType.Input)}
			/>
		),
	},
	[DefaultInputValueTypeEnum.Switch]: {
		render: (val: any) => <Switch disabled checked={val} />,
		renderFormItem: (t: any, r: any, opts: Record<string, any>, ins: FormInstance) => (
			<ProxyWrapped>
				{inputProps => <Switch {...inputProps} checked={inputProps?.value} />}
			</ProxyWrapped>
		),
	},
	// [DefaultInputValueTypeEnum.RadioBool]: {
	// 	render: (val: any, record: any, index: number) => EGlobalBoolMap?.get(val)?.text || EMPTY_TEXT,
	// 	renderFormItem: (t:any, r:any, opts: Record<string, any>, ins: FormInstance) => (
	// 		<Radio.Group options={toValEnumList(EGlobalBoolMap, { type: 'label' }) as any} />
	// 	),
	// },
	[DefaultInputValueTypeEnum.File]: {
		renderFormItem: (t: any, r: any) => {
			return (
				<ProxyWrapped>
					{(op: any) => (
						<Upload multiple {...op} fileList={Array.isArray(op?.value) ? op?.value : []} />
					)}
				</ProxyWrapped>
			);
		},
		render: (e: any) => {
			const val = Array.isArray(e) ? e : [];
			return val?.length ? <OssUpload readonly value={val} multiple /> : null;
		},
	},
	[DefaultInputValueTypeEnum.Image]: {
		renderFormItem: (t: any, r: any) => {
			return (
				<ProxyWrapped>
					{(op: any) => (
						<OssUpload
							multiple
							listType={'picture-card'}
							{...op}
							onChange={(e: any) => {
								op?.onChange?.(e);
							}}
							value={Array.isArray(op?.value) ? op?.value : []}></OssUpload>
					)}
				</ProxyWrapped>
			);
		},
		render: (e: any) => {
			const val = Array.isArray(e) ? e : [];
			return val?.length ? (
				<OssUpload value={val} listType={'picture-card'} readonly multiple />
			) : null;
		},
	},
};
export default defaultInputValueTypeMap;
