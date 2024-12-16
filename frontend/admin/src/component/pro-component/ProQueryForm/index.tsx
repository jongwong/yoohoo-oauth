import React, { Key, ReactNode } from 'react';
import { Button, Col, Form, FormInstance, Row } from 'antd';
import ProField from '@ant-design/pro-field';
import { BaseProFieldType } from '@/component/pro-component/types';
import { formatProField } from '@/component/pro-component/utils/render';

export type QueryFormFieldType<T = any> = Omit<
	BaseProFieldType<T>,
	'title' | 'dataIndex' | 'label' | 'name'
> & {
	label: ReactNode;
	name?: Key | Key[];
};

export interface ProQueryFormProps<T = any> {
	fields: QueryFormFieldType<T>[];
	form?: FormInstance;
	onSearch?: (values: any) => void; // 查询按钮的回调
	onReset?: () => void; // 重置按钮的回调
	hideForm?: boolean;
}

const ProQueryForm: React.FC<ProQueryFormProps> = ({
	form: formProp,
	hideForm,
	fields,
	onSearch,
	onReset,
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
		return (
			<Row gutter={[16, 16]} style={{ width: '100%' }}>
				{fields.map(field => (
					<Col key={field.name as string} xs={24} sm={12} md={8} lg={6}>
						<Form.Item name={field.name as string} label={field.label} noStyle>
							<ProField mode={'edit'} plain {...(formatProField(field as any) as any)} />
						</Form.Item>
					</Col>
				))}
				<Col style={{ textAlign: 'right', flex: 'auto' }}>
					<Button type="primary" onClick={handleSearch} style={{ marginRight: 8 }}>
						查询
					</Button>
					<Button onClick={handleReset}>重置</Button>
				</Col>
			</Row>
		);
	};

	if (hideForm) {
		return renderContent();
	}
	return (
		<Form form={form} layout="inline" style={{ marginBottom: 16 }}>
			{renderContent()}
		</Form>
	);
};

export default ProQueryForm;
