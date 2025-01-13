import React from 'react';
import { Button, Col, Form, FormInstance, Row, Space } from 'antd';
import { BaseFormProFieldFuncType } from '../../pro-component/types';
import ProField from '../ProField';
import { isFunction } from 'lodash';

export type QueryFormFieldType<T = any> = BaseFormProFieldFuncType<T>;

export interface ProQueryFormProps<T = any> {
	fields: QueryFormFieldType<T>[];
	form?: FormInstance;
	onSearch?: (values: any) => void; // 查询按钮的回调
	onReset?: () => void; // 重置按钮的回调
	hideForm?: boolean;
	operations?: (el: React.ReactNode[]) => React.ReactNode;
	extraOperation?: React.ReactNode;
}

const ProQueryForm: React.FC<ProQueryFormProps> = ({
	form: formProp,
	hideForm,
	fields,
	onSearch,
	onReset,
	operations,
	extraOperation,
}) => {
	const [form] = Form.useForm(formProp);

	// 提交查询表单
	const handleSearch = () => {
		form.validateFields().then(values => {
			onSearch?.(values);
		});
	};

	// 重置表单
	const handleReset = () => {
		form.resetFields();
		onReset?.();
	};

	const renderContent = () => {
		const els = (
			<>
				{extraOperation}
				<Button key={'reset'} onClick={handleReset}>
					重置
				</Button>
				<Button key={'search'} type="primary" onClick={handleSearch} style={{ marginRight: 8 }}>
					查询
				</Button>
			</>
		);

		return (
			<Row gutter={[16, 16]} style={{ width: '100%' }}>
				{fields.map((field: any) => (
					<Col key={field.key || field.name} xs={24} sm={12} md={8} lg={8} xl={8} xxl={6}>
						<ProField
							{...field}
							fieldFunc={isFunction(field) ? field : undefined}
							allEditable={true}
						/>
					</Col>
				))}
				<Col style={{ textAlign: 'right', flex: 'auto' }}>
					<Space>{operations ? operations(React.Children.toArray(els)) : els}</Space>
				</Col>
			</Row>
		);
	};

	if (hideForm) {
		return renderContent();
	}
	return (
		<Form component={'div'} form={form} layout="inline" style={{ marginBottom: 16 }}>
			{renderContent()}
		</Form>
	);
};

export default ProQueryForm;
